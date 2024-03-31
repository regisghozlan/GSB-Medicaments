package fr.euroforma.gsb_medicaments;

import static org.junit.Assert.*;
import org.junit.Test;



public class MedicamentTest  {
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