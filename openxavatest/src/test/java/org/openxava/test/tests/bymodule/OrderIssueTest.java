package org.openxava.test.tests.bymodule;

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
	
	public void testSearchKeyReferenceMustBeEditable_nullFirstsNonRequiredFields() throws Exception { 
		assertListRowCount(1); 
		execute("List.viewDetail", "row=0");
		assertAction("Reference.search");
		assertEditable("order.year"); 
		assertEditable("order.number");				
		assertNoEditable("order.date");
		
		execute("CRUD.new");
		setValue("description", "JUnit order issue");
		execute("CRUD.save");
		assertNoErrors();
		
		execute("Mode.list");
		execute("List.orderBy", "property=description"); 
		assertListRowCount(2); 
		assertValueInList(0, "description", "JUnit order issue"); 
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
		assertListRowCount(1);
	} 
	
}
