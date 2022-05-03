package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class NotAllActionsInDetailsInvoiceTest extends ModuleTestBase {
	
	public NotAllActionsInDetailsInvoiceTest(String testName) {
		super(testName, "NotAllActionsInDetailsInvoice");		
	}
	
	public void testEmptyCollectionActions() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertNoAction("Collection.new");
		assertNoAction("Collection.removeSelected");
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		assertNoErrors();
		assertNoAction("Collection.remove");
		assertAction("Collection.save");
		assertAction("Collection.hideDetail");
	}
								
}
