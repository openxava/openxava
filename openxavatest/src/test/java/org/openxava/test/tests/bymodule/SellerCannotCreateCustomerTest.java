package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class SellerCannotCreateCustomerTest extends ModuleTestBase {
	

	public SellerCannotCreateCustomerTest(String testName) {
		super(testName, "SellerCannotCreateCustomer");		
	}

	public void testNotCreateNewReferenceFromCollection_readOnlyMembersInCollectionElementDialog() throws Exception {		
		execute("List.viewDetail", "row=0");
		assertNoAction("Collection.add");  

		execute("Collection.edit", "row=0,viewObject=xava_view_section0_customers");
		assertNoEditable("name");
	}
	
	
}
