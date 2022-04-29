package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class InventTest extends ModuleTestBase {
	
	public InventTest(String testName) {
		super(testName, "Invent");		
	}
	
	public void testTabDefaultValuesNotAffectCollections() throws Exception { 
		assertListRowCount(1); 
		execute("List.viewDetail", "row=0");
		assertValue("description", "INVENT 1");
		assertCollectionRowCount("details", 2);		
	}
	
}
