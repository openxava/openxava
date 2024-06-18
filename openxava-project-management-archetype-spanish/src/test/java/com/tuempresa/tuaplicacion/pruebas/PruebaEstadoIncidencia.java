package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class PruebaEstadoIncidencia extends ModuleTestBase {

	public PruebaEstadoIncidencia(String nameTest) {
		super(nameTest, "tuaplicacion", "EstadoIncidencia");
	}
	
	public void testUsarComoValorPorDefecto() throws Exception {
		login("admin", "admin");
		assertListRowCount(4);
		assertValueInList(0, 0, "Pendiente");
		assertValueInList(0, 2, "Usar como valor por defecto");
		assertValueInList(1, 0, "Hecho");
		assertValueInList(1, 2, "");
		assertValueInList(2, 0, "No reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(3, 0, "Rechazado");
		assertValueInList(3, 2, "");
		
		execute("List.viewDetail", "row=3");
		assertValue("nombre", "Rechazado");
		setValue("usarComoValorPorDefecto", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pendiente");
		assertValueInList(0, 2, "");
		assertValueInList(1, 0, "Hecho");
		assertValueInList(1, 2, "");
		assertValueInList(2, 0, "No reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(3, 0, "Rechazado");
		assertValueInList(3, 2, "Usar como valor por defecto");
		
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Pendiente");
		setValue("usarComoValorPorDefecto", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pendiente");
		assertValueInList(0, 2, "Usar como valor por defecto");
		assertValueInList(1, 0, "Hecho");
		assertValueInList(1, 2, "");
		assertValueInList(2, 0, "No reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(3, 0, "Rechazado");
		assertValueInList(3, 2, "");
		
	}
	
}
