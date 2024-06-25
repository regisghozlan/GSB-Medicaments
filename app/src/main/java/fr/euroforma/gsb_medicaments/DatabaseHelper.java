package fr.euroforma.gsb_medicaments;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Pattern;

public class DatabaseHelper extends SQLiteOpenHelper {


    private final Context mycontext;
    private static final String DATABASE_NAME = "medicaments.db";
    private static final int DATABASE_VERSION = 2;
    private String DATABASE_PATH;
    private static final String LOG_FILE_NAME = "application_log.txt";
    private static final String LOG_FILE_PATH = "logs/";
    private static final String PREMIERE_VOIE = "Séléctionnez une voie d'administration";
    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkdatabase()) {
            // copy db de 'assets' vers DATABASE_PATH
            Log.d("APP", "BDD a copier");
            copydatabase();

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Define the tables and necessary structures
        // Note: You should execute the appropriate CREATE TABLE queries here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            //Log.d("debug", "onUpgrade() : oldVersion=" + oldVersion + ",newVersion=" + newVersion);
            mycontext.deleteDatabase(DATABASE_NAME);
            copydatabase();
        }
    } // onUpgrade

    // TODO: Implement methods to interact with the database, such as fetching distinct Voies_dadministration
    // and searching for medicaments based on criteria

    public List<String> getDistinctVoiesAdmin() {
        List<String> voiesAdminList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT upper(Voies_dadministration) FROM CIS_bdpm WHERE Voies_dadministration NOT LIKE '%;%' ORDER BY Voies_dadministration", null);
        voiesAdminList.add(PREMIERE_VOIE);
        if (cursor.moveToFirst()) {
            do {

                String voieAdmin = cursor.getString(0).toString();
                voiesAdminList.add(voieAdmin);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return voiesAdminList;
    }

    private boolean checkdatabase() {
        // retourne true/false si la bdd existe dans le dossier de l'app
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);

        return dbfile.exists();
    }

    public List<Medicament> searchMedicaments(String denomination, String formePharmaceutique, String titulaires, String denominationSubstance, String voiesAdmin) {
        List<Medicament> medicamentList = new ArrayList<>();
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("%" + denomination + "%");
        selectionArgs.add("%" + formePharmaceutique + "%");
        selectionArgs.add("%" + titulaires + "%");
        selectionArgs.add("%" + denominationSubstance + "%");
        SQLiteDatabase db = this.getReadableDatabase();
        String finSQL = "";
        // String Sql_nbmolecule ="" ;

        if (!voiesAdmin.equals(PREMIERE_VOIE)) {
            finSQL = "AND  Voies_dadministration LIKE ?";
            selectionArgs.add("%" + voiesAdmin + "%");
        }
        String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(upper(Denomination_substance), 'Â','A'),'Ä','A'),'À','A'),'É','E'),'Á','A'),'Ï','I'), 'Ê','E'),'È','E'),'Ô','O'),'Ü','U'), 'Ç','C' ) LIKE ?";
//String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE Denomination_substance COLLATE latin1_general_cs_ai LIKE ?" ;

        // La requête SQL de recherche
        String query = "SELECT *,(select count(*) from CIS_COMPO_bdpm c where c.Code_CIS=m.Code_CIS) as nb_molecule FROM CIS_bdpm m  WHERE " +
                "Denomination_du_medicament LIKE ? AND " +
                "Forme_pharmaceutique LIKE ? AND " +
                "Titulaires LIKE ? AND " +
                "Code_CIS IN (" + SQLSubstance + ")" +
                finSQL;

        // Les valeurs à remplacer dans la requête


        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));


        if (cursor.moveToFirst()) {
            do {
                // Récupérer les valeurs de la ligne actuelle
                int codeCIS = cursor.getInt(cursor.getColumnIndex("Code_CIS"));
                String denominationMedicament = cursor.getString(cursor.getColumnIndex("Denomination_du_medicament"));
                String formePharmaceutiqueMedicament = cursor.getString(cursor.getColumnIndex("Forme_pharmaceutique"));
                String voiesAdminMedicament = cursor.getString(cursor.getColumnIndex("Voies_dadministration"));
                String titulairesMedicament = cursor.getString(cursor.getColumnIndex("Titulaires"));
                String statutAdministratif = cursor.getString(cursor.getColumnIndex("Statut_administratif_de_lAMM"));
                String CountMolecule = cursor.getString(cursor.getColumnIndex("nb_molecule"));

                // Créer un objet Medicament avec les valeurs récupérées
                Medicament medicament = new Medicament();
                medicament.setCodeCIS(codeCIS);
                medicament.setDenomination(denominationMedicament);
                medicament.setFormePharmaceutique(formePharmaceutiqueMedicament);
                medicament.setVoiesAdmin(voiesAdminMedicament);
                medicament.setTitulaires(titulairesMedicament);
                medicament.setStatutAdministratif(statutAdministratif);
                // medicament.setNb_molecule(CountMolecule.toString());
                medicament.setNb_molecule(String.valueOf(getNombreMolecules(codeCIS)));
                // Ajouter l'objet Medicament à la liste
                medicamentList.add(medicament);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(mycontext, "Aucun résultat", Toast.LENGTH_LONG).show();


        }

        cursor.close();
        db.close();

        return medicamentList;
    }

    public int getNombreMolecules(int codeCIS) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from CIS_COMPO_bdpm  where Code_CIS=?", new String[]{String.valueOf(codeCIS)});
        cursor.moveToFirst();
        int nb = cursor.getInt(0);
        return (nb);
    }

    public List<String> getCompositionMedicament(int codeCIS) {
        List<String> compositionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CIS_compo_bdpm WHERE Code_CIS = ?", new String[]{String.valueOf(codeCIS)});
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                i++;
                String substance = cursor.getString(cursor.getColumnIndex("Denomination_substance"));
                String dosage = cursor.getString(cursor.getColumnIndex("Dosage_substance"));
                compositionList.add(i + ":" + substance + "(" + dosage + ")");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return compositionList;
    }

    public List<String> getPresentationMedicament(int codeCIS) {
        List<String> presentationList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CIS_CIP_bdpm WHERE Code_CIS = ?", new String[]{String.valueOf(codeCIS)});
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                i++;
                String libellePresentation = cursor.getString(cursor.getColumnIndex("Libelle_presentation"));
                presentationList.add(i + ":" + libellePresentation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return presentationList;
    }

    public Cursor performQuery(String cisCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT gener.id _id, m.Code_CIS,Libelle_generic from CIS_bdpm m inner JOIN CIS_GENER_bdpm gener on m.Code_CIS = gener.Code_CIS WHERE Statut_administratif_de_lAMM='Autorisation active' AND gener.Code_CIS= ?";
        return db.rawQuery(query, new String[]{cisCode});
    }

    private void copydatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        //AssetManager assetManager = mycontext.getAssets();
        InputStream myInput;

        try {
            // Ouvre le fichier de la  bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if (!pathFile.exists()) {
                if (!pathFile.mkdirs()) {
                    Toast.makeText(mycontext, "Erreur : copydatabase(), pathFile.mkdirs()", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            Log.d("APP", "BDD copiée");
            myOutput.flush();
            myOutput.close();
            myInput.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR", "erreur copie de la base");
            Toast.makeText(mycontext, "Erreur : copydatabase()", Toast.LENGTH_SHORT).show();
        }

        // on greffe le numéro de version
        try {
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        } catch (SQLiteException e) {
            // bdd n'existe pas
        }

    }

    public void writeToLogFile(String logEntry) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        // Ajouter le timestamp au début de l'entrée de log
        File logDirectory = new File(mycontext.getFilesDir(), LOG_FILE_PATH);
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }

        File logFile = new File(logDirectory, LOG_FILE_NAME);
        try {
            FileOutputStream outputStream = new FileOutputStream(logFile, true);
            outputStream.write((timestamp + " - " + logEntry + "\n").getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e("LogWriter", "Error writing to log file", e);
        }
    }

    private String readLogFile() {
        File logDirectory = new File(mycontext.getFilesDir(), LOG_FILE_PATH);
        File logFile = new File(logDirectory, LOG_FILE_NAME);
        StringBuilder logContent = new StringBuilder();

        try {
            FileInputStream inputStream = new FileInputStream(logFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Log.e("LogReader", "Error reading log file", e);
        }

        return logContent.toString();
    }
}



