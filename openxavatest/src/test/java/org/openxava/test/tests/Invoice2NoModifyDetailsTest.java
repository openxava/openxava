package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Invoice2NoModifyDetailsTest extends ModuleTestBase {
	

	public Invoice2NoModifyDetailsTest(String testName) {
		super(testName, "Invoice2NoModifyDetails");		
	}

	public void testNoModifyInCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionNotEmpty("details");
		execute("Collection.view", "row=0,viewObject=xava_view_details");
		assertNoEditable("quantity");
		assertNoAction("Collection.save");
	}
	
}
