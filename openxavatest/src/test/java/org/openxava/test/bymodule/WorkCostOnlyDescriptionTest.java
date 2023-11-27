package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WorkCostOnlyDescriptionTest extends ModuleTestBase {
	
	public WorkCostOnlyDescriptionTest(String testName) {
		super(testName, "WorkCostOnlyDescription");		
	}
	
	public void testAssertTrueWithMessageIncludingPropertyWithNullValue() throws Exception { 
		execute("CRUD.new");
		assertNotExists("profit"); // In order profit value will be null
		setValue("description", "JUNIT WORK COST");
		execute("CRUD.save");
		assertError("Incorrect value for profit: null");
		
	}
		
}
