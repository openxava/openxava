package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerMinimumTest extends ModuleTestBase {
	
	public CustomerMinimumTest(String testName) {
		super(testName, "CustomerMinimum");		
	}
	
	public void testReadAnEmbeddedWithAllItsMembersNull() throws Exception { 
		execute("CRUD.new");
		setValue("number", "66");		
		setValue("name", "JUNIT TEST");
		execute("CRUD.save");
		assertNoErrors(); 
		
		changeModule("CustomerSimple");
		execute("CRUD.new");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("name", "Xunit Test");
		
		execute("CRUD.delete");
		assertNoErrors();
	}
			
}
