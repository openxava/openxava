package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class PersonaTest extends ModuleTestBase {
	
	public PersonaTest(String testName) {
		super(testName, "Persona");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		login("admin", "admin");
		// Crear
		execute("CRUD.new");
		setValue("numero", "99999");
		setValue("nombre", "Persona de Prueba");
		setValue("direccion", "Calle de Prueba 123");
		setValue("ciudad", "Ciudad de Prueba");
		setValue("pais", "País de Prueba");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Persona creado/a satisfactoriamente");
		
		// Leer - buscar la persona creada
		execute("CRUD.new");
		setValue("numero", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("numero", "99999");
		assertValue("nombre", "Persona de Prueba");
		assertValue("direccion", "Calle de Prueba 123");
		assertValue("ciudad", "Ciudad de Prueba");
		assertValue("pais", "País de Prueba");
		
		// Actualizar
		setValue("nombre", "Persona de Prueba Modificada");
		setValue("direccion", "Calle Modificada 456");
		setValue("ciudad", "Ciudad Modificada");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Persona modificado/a satisfactoriamente");
		
		// Verificar la modificación usando la lista
		execute("Mode.list");
		execute("List.orderBy", "property=numero");
		execute("List.orderBy", "property=numero");
		execute("List.viewDetail", "row=0");
		assertValue("numero", "99999");
		assertValue("nombre", "Persona de Prueba Modificada");
		assertValue("direccion", "Calle Modificada 456");
		assertValue("ciudad", "Ciudad Modificada");
		assertValue("pais", "País de Prueba");
		
		// Borrar
		execute("CRUD.delete");
		assertMessage("Persona borrado satisfactoriamente");
	}	
	
}
