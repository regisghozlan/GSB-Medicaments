package fr.euroforma.gsb_medicaments;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServiceWorker extends Worker {

    public WebServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String codeV = getInputData().getString("codeV");
        String secureKey = getInputData().getString("secureKey de test");
        String token = getInputData().getString("token");


        String url = "http://ppe.formationsiparis.fr/sendmail.php?codeV=" + codeV + "&secureKey=" + secureKey + "&Token=" + token;
        HttpURLConnection httpURLConnection = null;
        try {
            // Configuration de la connexion
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
          //  httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
           // httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setConnectTimeout(5000);

            // Réception de la réponse
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                StringBuilder stringBuilder = new StringBuilder();
                int c;
                while ((c = inputStreamReader.read()) != -1) {
                    stringBuilder.append((char) c);
                }
                String response = stringBuilder.toString();

                // Traitement de la réponse
                // ...

                return Result.success();
            } else {
                Log.e("WebService", "Erreur : " + responseCode);
                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
