package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;


/**
 * 
 * @author Chungyen Tsai
 */

public class ReallocationDetailsNoIdTest extends ModuleTestBase {
	
	public ReallocationDetailsNoIdTest(String testName) {
		super(testName, "ReallocationDetailsNoId");		
	}	
	
	public void testElementCollectionSaveWithOutIdInListProperties() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details", 0, "product.code", "1");
	}
	
}
