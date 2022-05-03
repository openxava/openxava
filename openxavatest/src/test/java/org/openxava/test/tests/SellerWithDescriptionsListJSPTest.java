package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class SellerWithDescriptionsListJSPTest extends ModuleTestBase {
	
	public SellerWithDescriptionsListJSPTest(String testName) {
		super(testName, "SellerWithDescriptionsListJSP");		
	}
	
	public void testDescriptionsListJSPTag() throws Exception {
		execute("List.viewDetail", "row=0");
		String [][] levelValues = {
			{ "", "" },
			{ "C", "JUNIOR" },
			{ "A", "MANAGER" },
			{ "B", "SENIOR" }	
		};			
		assertValue("level.id", "A");
		assertValidValues("level.id", levelValues);
	}	
			
}
