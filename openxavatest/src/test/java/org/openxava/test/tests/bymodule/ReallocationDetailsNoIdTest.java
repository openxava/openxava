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
	
	public void testSaveElementCollectionWithoutVisibleOrHiddenId() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details", 0, "product.code", "1"); //visible
		assertValueInCollection("details", 1, "product.code", "4");
		assertValueInCollection("details", 2, "product.code", "3");
		assertValueInCollection("details2", 0, "product.code", "1"); //hidden id
	}
	
}
