package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class ItemTest extends ModuleTestBase {

    public ItemTest(String testName) {
        super(testName, "Item");
    }

    public void testListAndNew() throws Exception {
        // TMR ME QUEDÉ POR AQUÍ: ME SUENA QUE EL TEST ES DEMASIADO SUAVE
        login("admin", "admin");
        execute("Mode.list");
        execute("CRUD.new");
        assertNoErrors();
    }
}
