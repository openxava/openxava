package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * tmr
 * @author Javier Paniza
 */

public class OrderProductInDetailAsDescriptionsListTest extends ModuleTestBase {
	
	public OrderProductInDetailAsDescriptionsListTest(String testName) {
		super(testName, "OrderProductInDetailAsDescriptionsList");		
	}
			
	public void testCalculatedPropertyDependsOnDescriptionsListInCollection() throws Exception { // tmr
		execute("List.viewDetail", "row=0");
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		assertValue("product.number", "7");
		assertValue("quantity", "10");
		assertValue("amount", "200.00");
		setValue("product.number", "1");
		assertValue("amount", "110.00");
	}
					
}
