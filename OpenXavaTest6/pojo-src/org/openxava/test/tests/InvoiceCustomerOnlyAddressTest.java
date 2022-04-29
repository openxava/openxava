package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceCustomerOnlyAddressTest extends ModuleTestBase {
	
	public InvoiceCustomerOnlyAddressTest(String testName) {
		super(testName, "InvoiceCustomerOnlyAddress");		
	}
	
	public void testReferenceActionsPresentsEvenWhenNoPlainProperties() throws Exception { 
		assertValue("customer.address.street", "");
		execute("Reference.search", "keyProperty=customer.number");
		assertValueInList(0, 0, "Javi"); 
		execute("ReferenceSearch.choose", "row=0");
		assertValue("customer.address.street", "DOCTOR PESSET");
	}
		
}
