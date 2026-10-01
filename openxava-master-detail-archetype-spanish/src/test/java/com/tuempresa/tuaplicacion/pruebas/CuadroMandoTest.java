package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class CuadroMandoTest extends ModuleTestBase {

    public CuadroMandoTest(String testName) {
        super(testName, "CuadroMando");
    }

    public void testCuadroMandoSeVisualiza() throws Exception {
        login("admin", "admin");
        assertPositivo("numeroMaestros");
        assertPositivo("numeroPersonas");
        assertCollectionRowCount("personasDestacadas", 5);
        assertCollectionRowCount("mejoresAnyos", 5);
    }

    private void assertPositivo(String propiedad) throws Exception {
        String valor = getValue(propiedad).replaceAll("[^0-9]", "");
        assertFalse(propiedad + " no tiene valor", valor.isEmpty());
        assertTrue(propiedad + " debe ser positivo", Long.parseLong(valor) > 0);
    }

}
