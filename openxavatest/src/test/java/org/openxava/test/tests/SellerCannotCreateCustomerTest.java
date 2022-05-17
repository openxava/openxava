package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class SellerCannotCreateCustomerTest extends ModuleTestBase {
	

	public SellerCannotCreateCustomerTest(String testName) {
		super(testName, "SellerCannotCreateCustomer");		
	}

	public void testNotCreateNewReferenceFromCollection() throws Exception {		
		execute("CRUD.new");
		assertNoAction("Collection.add");  
	}
	
	
}
