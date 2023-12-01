package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * Create on 16/01/2012 (12:08:36)
 * @author Ana Andres
 */
public class VehicleTest extends ModuleTestBase {
	
	public VehicleTest(String testName) {
		super(testName, "Vehicle");		
	}
	
	/**
	 * DescriptionsList1 (key: state), DescriptionsList2 (key: state, city), Integer cityCode not in view.
	 * It not save the city value.
	 */
	public void testStateAndCity() throws Exception { 
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		setValue("state.id", "AZ"); 
		setValue("city.KEY", "[.1.AZ.]"); 
		execute("CRUD.save");
		assertMessage("Vehicle modified successfully"); // TMR ME QUEDÉ POR AQUÍ, PERO TAMBIÉN FALLA EL PROGRAMA REAL
		assertNoErrors();
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("state.id", "AZ"); 
		assertValue("city.KEY", "[.1.AZ.]"); 
		setValue("state.id", "");
		assertValue("city.KEY", "");
		execute("CRUD.save");
		assertMessage("Vehicle modified successfully");
		assertNoErrors();
	}
}
