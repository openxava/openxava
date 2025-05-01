package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class PruebaEstadoIncidencia extends ModuleTestBase {

	public PruebaEstadoIncidencia(String nameTest) {
		super(nameTest, "tuaplicacion", "EstadoIncidencia");
	}
	
	public void testUsarComoValorPorDefecto() throws Exception {
		login("admin", "admin");
		assertListRowCount(5);
		assertValueInList(0, 0, "Pendiente");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "Usar como valor por defecto");
		assertValueInList(1, 0, "Hecho");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "No reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Rechazado");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Planificado");
		assertValueInList(4, 2, "Usar como valor por defecto para Mi calendario");
		assertValueInList(4, 3, "");
		
		execute("List.viewDetail", "row=3");
		assertValue("nombre", "Rechazado");
		setValue("usarComoValorPorDefecto", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pendiente");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "");
		assertValueInList(1, 0, "Hecho");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "No reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Rechazado");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "Usar como valor por defecto");
		assertValueInList(4, 0, "Planificado");
		assertValueInList(4, 2, "Usar como valor por defecto para Mi calendario");
		assertValueInList(4, 3, "");
		
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Pendiente");
		setValue("usarComoValorPorDefecto", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		assertValueInList(0, 0, "Pendiente");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "Usar como valor por defecto");
		assertValueInList(1, 0, "Hecho");
		assertValueInList(1, 2, "");
		assertValueInList(1, 3, "");
		assertValueInList(2, 0, "No reproducible");
		assertValueInList(2, 2, "");
		assertValueInList(2, 3, "");
		assertValueInList(3, 0, "Rechazado");
		assertValueInList(3, 2, "");
		assertValueInList(3, 3, "");
		assertValueInList(4, 0, "Planificado");
		assertValueInList(4, 2, "Usar como valor por defecto para Mi calendario");
		assertValueInList(4, 3, "");
	}
	
	public void testUsarComoValorPorDefectoParaMiCalendario() throws Exception {
		login("admin", "admin");
		assertListRowCount(5);
		
		// Verificar que "Planificado" es el valor por defecto para MiCalendario
		execute("List.viewDetail", "row=4");
		assertValue("nombre", "Planificado");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "true");
		
		// Cambiar el valor por defecto para MiCalendario a "Pendiente"
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Pendiente");
		setValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verificar que "Pendiente" es ahora el valor por defecto para MiCalendario
		// y "Planificado" ya no lo es
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Pendiente");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=4");
		assertValue("nombre", "Planificado");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "false");
		
		// Restaurar "Planificado" como valor por defecto para MiCalendario
		setValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verificar que "Planificado" es nuevamente el valor por defecto para MiCalendario
		// y "Pendiente" ya no lo es
		execute("List.viewDetail", "row=4");
		assertValue("nombre", "Planificado");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Pendiente");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "false");
		
		// Restaurar el estado original
		execute("CRUD.save");
	}
}
