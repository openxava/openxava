package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceActiveYearTest extends ModuleTestBase {
	
	public InvoiceActiveYearTest(String testName) {
		super(testName, "InvoiceActiveYear");		
	}
	
		
	public void testApplicationScopeSessionObject() throws Exception {
		int count = getListRowCount();
		for (int i = 0; i < count; i++) {
			assertValueInList(i, "year", "2004");	
		}		
		
		changeModule("ChangeActiveYear");
		setValue("year", "2002");
		execute("ChangeActiveYear.changeActiveYear");
		assertMessage("Active year set to 2,002");
		
		changeModule("InvoiceActiveYear");		
		count = getListRowCount();
		for (int i = 0; i < count; i++) {
			assertValueInList(i, "year", "2002");	
		}				
	}
	
	public void testInjectInOnChangeAction() throws Exception {
		execute("CRUD.new");
		setValue("year", "2012"); 
		assertError("Active year is 2004, so 2012 is not allowed");
	}
	
}
