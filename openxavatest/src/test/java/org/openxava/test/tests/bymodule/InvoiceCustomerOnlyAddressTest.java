package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceCustomerOnlyAddressTest extends ModuleTestBase {
	
	public InvoiceCustomerOnlyAddressTest(String testName) {
		super(testName, "InvoiceCustomerOnlyAddress");		
	}
	
	public void testReferenceActionsPresentsEvenWhenNoPlainProperties_closeAllDialog() throws Exception { 
		assertValue("customer.address.street", "");
		execute("Reference.search", "keyProperty=customer.number");
		assertValueInList(0, 0, "Javi"); 
		execute("ReferenceSearch.choose", "row=0");
		assertValue("customer.address.street", "DOCTOR PESSET");
		
		execute("Reference.clear", "keyProperty=customer.number");
		execute("ShowCustomer.showRecommendedCustomer");
		execute("ShowSellerDialog.showSellerDialog");
		execute("CloseDialog.close");
		assertValue("customer.address.street", "");
	}
		
}
