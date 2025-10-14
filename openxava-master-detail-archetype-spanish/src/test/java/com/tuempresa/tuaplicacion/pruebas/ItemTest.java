package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class ItemTest extends ModuleTestBase {
	
	public ItemTest(String testName) {
		super(testName, "Item");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		login("admin", "admin");
		// Crear
		execute("CRUD.new");
		setValue("codigo", "99999");
		setValue("descripcion", "Item de Prueba");
		setValue("precioUnitario", "125,50");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Item creado/a satisfactoriamente");
		
		// Leer - buscar el item creado
		execute("CRUD.new");
		setValue("codigo", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("codigo", "99999");
		assertValue("descripcion", "Item de Prueba");
		assertValue("precioUnitario", "125,50");
		
		// Actualizar
		setValue("descripcion", "Item de Prueba Modificado");
		setValue("precioUnitario", "200,00");
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Item modificado/a satisfactoriamente");
		
		// Verificar la modificación usando la lista
		execute("Mode.list");
		execute("List.orderBy", "property=codigo");
		execute("List.orderBy", "property=codigo");
		execute("List.viewDetail", "row=0");
		assertValue("codigo", "99999");
		assertValue("descripcion", "Item de Prueba Modificado");
		assertValue("precioUnitario", "200,00");
		
		// Borrar
		execute("CRUD.delete");
		assertMessage("Item borrado satisfactoriamente");
	}	
	
}
