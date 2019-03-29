package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderIssueTest extends ModuleTestBase {
	
	public OrderIssueTest(String testName) {
		super(testName, "OrderIssue");		
	}
	
	public void testLastSearchKeyWithReadOnlyShowsReferenceActions() throws Exception {
		execute("CRUD.new");		
		execute("Reference.createNew", "model=Order,keyProperty=order.number"); 
		assertDialog();
	}
	
	public void testSearchKeyReferenceMustBeEditable() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertAction("Reference.search");
		assertEditable("order.year"); 
		assertEditable("order.number");				
		assertNoEditable("order.date");
	} 
	
}
