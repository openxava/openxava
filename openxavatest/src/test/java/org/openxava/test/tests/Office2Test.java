package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Office2Test extends ModuleTestBase {
			

	public Office2Test(String testName) {
		super(testName, "Office2");		
	}
	
	public void testOverlappedKeyReference() throws Exception { 
		// Creating
		execute("CRUD.new");
		setValue("number", "1");
		setValue("zoneNumber", "1");
		setValue("name", "JUNIT OFFICE");
		setValue("mainWarehouse.number", "1");
		assertValue("mainWarehouse.name", "CENTRAL VALENCIA"); 
		setValue("officeManager.number", "1");
		assertValue("officeManager.name", "PEPE");		
		execute("CRUD.save");
		assertNoErrors();
		
		// Searching
		assertValue("name", ""); 
		setValue("number", "1");
		setValue("zoneNumber", "1");		
		setValue("mainWarehouse.number", "1");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT OFFICE");
		
		// Deleting
		execute("CRUD.delete");
		assertMessage("Office 2 deleted successfully");
	}
	
}
