package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product3ChangeActionsOnSearchTest extends ModuleTestBase {
	
	public Product3ChangeActionsOnSearchTest(String testName) {
		super(testName, "Product3ChangeActionsOnSearch");		
	}
	
	public void testChangeActionWhenSearch() throws Exception { 
		execute("CRUD.new");
		execute("Product3.showDescription"); // description is hide in a init action for test purpose
		setValue("number", "77");
		execute("CRUD.refresh");
		assertValue("description", "ANATHEMA"); 
		assertNoEditable("description"); // well: on-change for make this not editable is thrown
	}
							
}
