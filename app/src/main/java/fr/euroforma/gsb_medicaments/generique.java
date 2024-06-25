package fr.euroforma.gsb_medicaments;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class generique extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generique);

        EditText cisEditText = findViewById(R.id.cisEditText);
        Button searchButton = findViewById(R.id.searchButton);
        ListView listView = findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);

        // Définir l'adaptateur initialement vide
        String[] fromColumns = {"Libelle_generic"};
        int[] toViews = {R.id.libelleTextView};
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.generique_item,
                null,
                fromColumns,
                toViews,
                0
        );
        listView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cisCode = cisEditText.getText().toString();
                performQuery(cisCode);
            }
        });
    }

    private void performQuery(String cisCode) {
        // Exécuter la requête via DBHelper
        Cursor cursor = dbHelper.performQuery(cisCode);
        // Mettre à jour l'adaptateur avec le nouveau curseur
        adapter.changeCursor(cursor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Fermer la base de données et le curseur lorsqu'ils ne sont plus nécessaires
        if (dbHelper != null) {
            dbHelper.close();
        }
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
}