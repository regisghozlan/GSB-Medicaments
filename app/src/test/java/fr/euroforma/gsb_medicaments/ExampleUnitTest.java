package fr.euroforma.gsb_medicaments;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void checkMedicament() {
        // Context of the app under test.
        Medicament medicament = new Medicament();
        medicament.setCodeCIS(1234);
        medicament.setDenomination("denominationMedicament");
        medicament.setFormePharmaceutique("formePharmaceutiqueMedicament");
        medicament.setVoiesAdmin("voiesAdminMedicament");
        medicament.setTitulaires("titulairesMedicament");
        medicament.setStatutAdministratif("statutAdministratif");
        medicament.setNb_molecule("NbMolecule");
        assertEquals("denominationMedicament", medicament.getDenomination());

    }
}