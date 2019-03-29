package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Subfamily2Test extends ModuleTestBase {
	
	public Subfamily2Test(String testName) {
		super(testName, "Subfamily2");		
	}
	
	public void testDescriptionsListWithMultipleDescription() throws Exception {
		execute("CRUD.new");
		String [][] familyValues = {
				{ "", "" },
				{ "1", "1 SOFTWARE" },
				{ "2", "2 HARDWARE" },
				{ "3", "3 SERVICIOS" }	
		};
		assertValidValues("family.number", familyValues);
		
	}
	
}
