package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class PruebaTipoIncidencia extends ModuleTestBase {

	public PruebaTipoIncidencia(String nameTest) {
		super(nameTest, "tuaplicacion", "TipoIncidencia");
	}
	
	public void testUsarComoValorPorDefectoParaMiCalendario() throws Exception {
		login("admin", "admin");
		assertListRowCount(3);
		
		// Verificar que "Tarea" es el valor por defecto para MiCalendario
		execute("List.viewDetail", "row=2");
		assertValue("nombre", "Tarea");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "true");
		
		// Cambiar el valor por defecto para MiCalendario a "Fallo"
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Fallo");
		setValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verificar que "Fallo" es ahora el valor por defecto para MiCalendario
		// y "Tarea" ya no lo es
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Fallo");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=2");
		assertValue("nombre", "Tarea");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "false");
		
		// Restaurar "Tarea" como valor por defecto para MiCalendario
		setValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("CRUD.save");
		execute("Mode.list");
		
		// Verificar que "Tarea" es nuevamente el valor por defecto para MiCalendario
		// y "Fallo" ya no lo es
		execute("List.viewDetail", "row=2");
		assertValue("nombre", "Tarea");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "true");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("nombre", "Fallo");
		assertValue("usarComoValorPorDefectoParaMiCalendario", "false");
		
		// Restaurar el estado original
		execute("CRUD.save");
	}
}
