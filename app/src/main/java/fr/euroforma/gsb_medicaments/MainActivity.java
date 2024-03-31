package fr.euroforma.gsb_medicaments;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDenomination, editTextFormePharmaceutique, editTextTitulaires, editTextDenominationSubstance;
    private Spinner spinnerVoiesAdmin;
    private Button btnSearch;
    private ListView listViewResults;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        editTextDenomination = findViewById(R.id.editTextDenomination);
        editTextFormePharmaceutique = findViewById(R.id.editTextFormePharmaceutique);
        editTextTitulaires = findViewById(R.id.editTextTitulaires);
        editTextDenominationSubstance = findViewById(R.id.editTextDenominationSubstance);
        spinnerVoiesAdmin = findViewById(R.id.spinnerVoiesAdmin);
        btnSearch = findViewById(R.id.btnSearch);
        listViewResults = findViewById(R.id.listViewResults);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Set up the spinner with Voies_dadministration data
        setupVoiesAdminSpinner();

        // Set up the click listener for the search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the search and update the ListView
                performSearch();
            }
        });

        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the selected item
                Medicament selectedMedicament = (Medicament) adapterView.getItemAtPosition(position);
                // Show composition of the selected medicament
                afficherCompositionMedicament(selectedMedicament);
            }
        }
        );
    }

    private void setupVoiesAdminSpinner() {
        // Fetch distinct Voies_dadministration data from the database and populate the spinner
        List<String> voiesAdminList = dbHelper.getDistinctVoiesAdmin();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiesAdminList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoiesAdmin.setAdapter(spinnerAdapter);
    }


    private void performSearch() {
        // TODO: Implement the search logic using the entered criteria and update the ListView
        String denomination = editTextDenomination.getText().toString().trim();
        String formePharmaceutique = editTextFormePharmaceutique.getText().toString().trim();
        String titulaires = editTextTitulaires.getText().toString().trim();
        String denominationSubstance = removeAccents(editTextDenominationSubstance.getText().toString().trim());
        String voiesAdmin = spinnerVoiesAdmin.getSelectedItem().toString();
        cacherClavier();
        // TODO: Use dbHelper to fetch search results and update the ListView
        List<Medicament> searchResults = dbHelper.searchMedicaments(denomination, formePharmaceutique, titulaires, denominationSubstance, voiesAdmin);

        // TODO: Create and set an adapter for the ListView to display search results
        MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
        listViewResults.setAdapter(adapter);
    }

    private void cacherClavier() {
        // Obtenez le gestionnaire de fenêtre
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Obtenez la vue actuellement focalisée, qui devrait être la vue avec le clavier
        View vueCourante = getCurrentFocus();

        // Vérifiez si la vue est non nulle pour éviter les erreurs
        if (vueCourante != null) {
            // Masquez le clavier
            imm.hideSoftInputFromWindow(vueCourante.getWindowToken(), 0);
        }
    }
    private String removeAccents(String input) {
        if (input == null) {
            return null;
        }

        // Normalisation en forme de décomposition (NFD)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Remplacement des caractères diacritiques
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
    private void afficherCompositionMedicament_old(Medicament medicament) {
        // Ici, vous pouvez implémenter la logique pour afficher la composition du médicament
        // Vous pouvez utiliser une boîte de dialogue, une nouvelle activité ou une autre méthode selon vos besoins.
        // Par exemple, une boîte de dialogue simple pour afficher la composition :

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Composition de " + medicament.getDenomination());
        builder.setMessage("Code CIS: " + medicament.getCodeCIS() + "\n" +
                "Forme pharmaceutique: " + medicament.getFormePharmaceutique() + "\n" +
                "Voies d'administration: " + medicament.getVoiesAdmin() + "\n" +
                "Titulaires: " + medicament.getTitulaires() + "\n" +
                "Statut administratif: " + medicament.getStatutAdministratif());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void afficherCompositionMedicament(Medicament medicament) {
        List<String> composition = dbHelper.getCompositionMedicament(medicament.getCodeCIS());

        // Afficher la composition du médicament dans une boîte de dialogue ou autre méthode d'affichage
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Composition de " + medicament.getCodeCIS());
        if (composition.isEmpty()) {
            builder.setMessage("Aucune composition disponible pour ce médicament.");
        } else {
            StringBuilder compositionText = new StringBuilder();
            for (String item : composition) {
                compositionText.append(item).append("\n");
            }
            builder.setMessage(compositionText.toString());
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}