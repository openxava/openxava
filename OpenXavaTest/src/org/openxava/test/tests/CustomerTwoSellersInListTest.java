package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerTwoSellersInListTest extends ModuleTestBase {

	
	public CustomerTwoSellersInListTest(String testName) {
		super(testName, "CustomerTwoSellersInList");				
	}
	
	public void test2ReferenceToSameModelInList() throws Exception { 
		assertListRowCount(5); 
		assertValueInList(0, "name", "Javi");
		assertValueInList(0, "seller.name", "MANUEL CHAVARRI");
		assertValueInList(0, "seller.level.description", "MANAGER");
		assertValueInList(0, "alternateSeller.name", "JUANVI LLAVADOR");		
	}	
			
}
