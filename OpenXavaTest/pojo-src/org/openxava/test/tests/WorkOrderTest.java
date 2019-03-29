package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WorkOrderTest extends ModuleTestBase {
	
	public WorkOrderTest(String testName) {
		super(testName, "WorkOrder");		
	}
	
	public void testCompositeKeyEntityWithNestedCollections() throws Exception { 
		execute("CRUD.new");
		setValue("year", "2011");
		setValue("number", "66");
		execute("Collection.new", "viewObject=xava_view_requisitions");
		setValue("description", "JUNIT REQUISITION");
		execute("Collection.new", "viewObject=xava_view_details");
		setValue("description", "JUNIT DETAIL");
		execute("Collection.save");
		assertNoErrors();
		assertValue("description", "JUNIT REQUISITION");
		assertCollectionRowCount("details", 1);
		assertValueInCollection("details", 0, 0, "JUNIT DETAIL");
		closeDialog();
		assertValue("year", "2011"); 
		assertValue("number", "66");
		assertCollectionRowCount("requisitions", 1);
		assertValueInCollection("requisitions", 0, 0, "JUNIT REQUISITION");
		execute("CRUD.delete");
		assertNoErrors();
	}
		
}
