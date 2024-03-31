package fr.euroforma.gsb_medicaments;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class activity_detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
    public void toggleSection(View view) {
        // Obtenez l'ID de la section correspondante à l'en-tête cliqué
        int sectionId = 0;
/*
        switch (view.getId()) {
            case R.id.header1:
                sectionId = R.id.section1Content;
                break;
            case R.id.header2:
                sectionId = R.id.section2Content;
                break;
            // Ajoutez d'autres cas pour chaque en-tête
        }
*/
        // Obtenez la vue de contenu de la section
        LinearLayout sectionContent = findViewById(sectionId);

        // Basculer la visibilité du contenu
        if (sectionContent.getVisibility() == View.VISIBLE) {
            sectionContent.setVisibility(View.GONE);
        } else {
            sectionContent.setVisibility(View.VISIBLE);
        }
    }
}