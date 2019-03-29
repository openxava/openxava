package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerTwoSellersNumberInListTest extends ModuleTestBase {

	
	public CustomerTwoSellersNumberInListTest(String testName) {
		super(testName, "CustomerTwoSellersNumberInList");				
	}
	
	public void testTwoReferencesToSameComponentButOnlyShowingKeyOfEach() throws Exception {		
		int customerCount = Customer.findAll().size();
		assertListRowCount(customerCount);
	}
		
}
