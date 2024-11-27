package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class OrderDetailNoIdTest extends ModuleTestBase {
	
	public OrderDetailNoIdTest(String testName) {
		super(testName, "OrderDetailNoId");		
	}
	
	public void testEntityInAPackageNotNamedModel() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details", 0, "product.code", "1313"); // if error, cant be saved
		assertValueInCollection("details2", 0, "product.code", "1414");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details", 0, "product.code", "1313"); 
		assertValueInCollection("details2", 0, "product.code", "1414"); //if error, can be saved but value is wrong when read
	}
	
}
