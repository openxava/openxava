package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoCustomerInvoicesTest extends ModuleTestBase {
	
	public GoCustomerInvoicesTest(String testName) {
		super(testName, "GoCustomerInvoices");		
	}
	
	public void testChangeModuleFromDialog() throws Exception { 
		execute("GoCustomerInvoices.showDialog");
		assertDialog();
		assertExists("number");
		assertNoAction("List.filter");
		assertNoAction("CustomerInvoices.return");
		execute("GoCustomerInvoices.goCustomer");
		assertNoDialog();
		assertNotExists("number");
		assertAction("List.filter");
		execute("CustomerInvoices.return");
		assertDialog();
		assertExists("number");
		assertNoAction("List.filter");
		assertNoAction("CustomerInvoices.return");
		closeDialog();
		assertNoDialog();
		assertExists("number");
	}		
	
}
