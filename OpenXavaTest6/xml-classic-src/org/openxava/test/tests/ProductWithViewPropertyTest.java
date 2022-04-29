package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ProductWithViewPropertyTest extends ModuleTestBase {
	
	public ProductWithViewPropertyTest(String testName) {
		super(testName, "ProductWithViewProperty");				
	}

	public void testViewPropertyAndValidatorWithFromInTheSameComponent() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		setValue("familyNumber", "1");
		setValue("subfamilyNumber", "1");
		setValue("warehouseKey", "[.1.1.]");
		execute("CRUD.save");
		assertNoErrors();
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		execute("CRUD.delete");
		assertMessage("Product deleted successfully");	
	}	
			
}
