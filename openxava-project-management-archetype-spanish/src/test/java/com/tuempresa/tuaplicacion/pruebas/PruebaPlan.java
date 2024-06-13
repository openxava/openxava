package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class PruebaPlan extends ModuleTestBase {

	public PruebaPlan(String nameTest) {
		super(nameTest, "tuaplicacion", "Plan");
	}
	
	public void testCreateNewPlan() throws Exception {
		login("admin", "admin");
		assertListRowCount(2);
		execute("CRUD.new");
		
		String [][] trabajadores = {
			{ "", "" },
			{ "2c94f081900875e80190088fd8f60004", "Javi" },
			{ "2c94f081900875e8019008901a180005", "Pedro" }
		};
		assertValidValues("trabajador.id", trabajadores);
		setValue("trabajador.id", "2c94f081900875e8019008901a180005"); // Pedro 
		
		String [][] periodos = {
			{ "", "" },
			{ "2c94f081900875e80190089394730006", "2024.10" },
			{ "2c94f081900875e801900893a5e90007", "2024.11" }
		};
		assertValidValues("periodo.id", periodos);
		setValue("periodo.id", "2c94f081900875e801900893a5e90007"); // 2024.11 
		
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("titulo", "El primer paso de mi gran plan");
		setValue("tipo.id", "2c94f081900875e801900896f25b0008"); // Fallo 
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("incidencias", 1);
		
		execute("Mode.list");
		assertListRowCount(3);
		assertValueInList(1, 0, "Pedro");
		assertValueInList(1, 1, "2024.11");
		
		execute("List.viewDetail", "row=1"); 
		assertValue("trabajador.id", "2c94f081900875e8019008901a180005"); // Pedro
		assertValue("periodo.id", "2c94f081900875e801900893a5e90007"); // 2024.11		
		assertCollectionRowCount("incidencias", 1);
		assertValueInCollection("incidencias", 0, 0, "El primer paso de mi gran plan");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_issues");
		execute("CRUD.delete");
		assertNoErrors();
		
		changeModule("Incidencia");
		assertListRowCount(1);
		assertValueInList(0, 0, "El primer paso de mi gran plan");
		assertValueInList(0, 1, "Fallo");
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
	}

}
