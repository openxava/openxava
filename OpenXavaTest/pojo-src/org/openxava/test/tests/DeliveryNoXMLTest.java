package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryNoXMLTest extends ModuleTestBase {
	
		
	public DeliveryNoXMLTest(String testName) {
		super(testName, "Delivery");		
	}
	
	public void testViewAddValidValueInSection() throws Exception { 
		execute("Delivery.addShortcutOptions"); 
		execute("CRUD.new");
		String [][] validValues = {
			{ "", "" },
			{ "a", "AA" },
			{ "b", "BB" }
		};
		assertValidValues("shortcut", validValues); 		
	}
	
}
