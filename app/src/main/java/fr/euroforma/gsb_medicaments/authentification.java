package fr.euroforma.gsb_medicaments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class authentification extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_STATUS = "userStatus";
    private static final String KEY_USER_NAME = "username";
    private static final String USER_STATUS_OK = "  Authentifié";
    private static final String SECURETOKEN = "euroforma@5785";
    private EditText codeVisiteur, saisieCode, NomUtilisateur;
    private Button buttonValider, buttonOK;
    private String secureKey;
    private WebServiceCaller webServiceCaller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        setUserStatus("KO");

        codeVisiteur = findViewById(R.id.codeVisiteur);
        buttonValider = findViewById(R.id.buttonValider);
        saisieCode = findViewById(R.id.saisieCode);
        buttonOK = findViewById(R.id.buttonOk);
        NomUtilisateur = findViewById(R.id.UserName);

    }

    public void clickValider(View v) {
        findViewById(R.id.partieDeux).setVisibility(View.VISIBLE);
        //findViewById(R.id.partieDeux).isVisible(true);

        String codeV = codeVisiteur.getText().toString();

        // Vous pouvez maintenant utiliser la méthode sendKeyByEmail
        // avec le codeV, secureKey, et token comme paramètres


        secureKey = generateRandomCode();
        Log.d("CODE", secureKey);
        webServiceCaller = new WebServiceCaller(this);
        // NomUtilisateur.setText(secureKey);
        String token = SECURETOKEN;
        // SendKeyTask sendEmail = new SendKeyTask(getApplicationContext());
        appellerWebService(codeV, secureKey);
        // sendEmail.execute(codeV, secureKey, token);

    }


    public void clickOk(View v) {
        //String str1 = secureKey;
        String str2 = saisieCode.getText().toString();
        if (secureKey.equals(str2)) {
            //String status1 = "Authentifié";
            setUserName(NomUtilisateur.getText().toString());
            setUserStatus(USER_STATUS_OK);
            //Log.d("COMPARE", "OK");
            affiche("Authentification réussie");

            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();

        } else {
            affiche("la clé est incorrecte");

        }
    }

    private void setUserStatus(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_STATUS, status);
        // editor.putString("NOM",)
        editor.apply();
    }

    private void setUserName(String lenom) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, lenom);
        // editor.putString("NOM",)
        editor.apply();
    }

    private String generateRandomCode() {
        // Caractères possibles dans le code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Longueur du code souhaitée
        int codeLength = 12;

        // Utilisation de SecureRandom pour une génération sécurisée
        SecureRandom random = new SecureRandom();

        // StringBuilder pour construire le code
        StringBuilder codeBuilder = new StringBuilder(codeLength);

        // Boucle pour construire le code caractère par caractère
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }

        // Retourne le code généré
        return codeBuilder.toString();
    }

    public void clickQuitter(View v) {
        finish();
    }


    private void appellerWebService(String CV, String SK) {
        // Paramètres pour l'appel
        String url = "https://auth.euroforma.site/authent2.php";

        String codeVisiteur = CV;
        String cleAuthent = SK;

        // Appel au webservice via la classe WebServiceCaller
        String key = sha256(cleAuthent + codeVisiteur + SECURETOKEN);
        webServiceCaller.appelWebService(
                url,
                key,
                codeVisiteur,
                cleAuthent,
                new WebServiceCaller.WebServiceCallback() {
                    @Override
                    public void onSuccess(String jsonResponse) {
                        // Affichage du JSON reçu dans un toast
                        affiche(jsonResponse);

                        // Décommentez ce bloc pour traiter le JSON
                    /*
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        // Traitement du JSON ici
                    } catch (JSONException e) {
                        Toast.makeText(
                            MainActivity.this,
                            "Erreur de traitement JSON: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                    */
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Affichage du message d'erreur dans un toast
                        affiche("Erreur: " + errorMessage);
                    }
                }
        );
    }

    private void affiche(String msg) {

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();

    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null; // ou tu peux retourner "" ou un message d'erreur
        }
    }
}