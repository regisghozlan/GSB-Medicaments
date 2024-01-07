package fr.euroforma.gsb_medicaments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class authentification extends AppCompatActivity {
    private EditText editTextCodeV;
    private static final String SECURETOKEN = "BethElicheva5";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);

        editTextCodeV = findViewById(R.id.editTextCodeV);

        Button buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClick();
            }
        });
    }

    private void onSendButtonClick() {
        // Récupérer le codeV saisi dans l'EditText
        String codeV = editTextCodeV.getText().toString();

        // Vous pouvez maintenant utiliser la méthode sendKeyByEmail
        // avec le codeV, secureKey, et token comme paramètres
        String secureKey = "votre_secureKey";
        String token = SECURETOKEN;
        SendKeyTask sendKeyTask = new SendKeyTask(getApplicationContext());
        sendKeyTask.execute(codeV, secureKey, token);
        //sendKeyByEmail(codeV, secureKey, token);
    }


    }
