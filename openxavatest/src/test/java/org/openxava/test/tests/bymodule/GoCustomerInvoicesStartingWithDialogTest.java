package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoCustomerInvoicesStartingWithDialogTest extends ModuleTestBase {
	
	public GoCustomerInvoicesStartingWithDialogTest(String testName) {
		super(testName, "GoCustomerInvoicesStartingWithDialog");		
	}
	
	public void testDialogOnInitAction() throws Exception {
		assertDialog();
		assertDialogTitle("Show dialog - Customer");
	}		
	
}
