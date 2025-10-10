package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class PersonaTest extends ModuleTestBase {

    public PersonaTest(String testName) {
        super(testName, "Persona");
    }

    public void testListAndNew() throws Exception {
        login("admin", "admin");
        execute("Mode.list");
        execute("CRUD.new");
        assertNoErrors();
    }
}
