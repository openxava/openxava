package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Invoice6WithDetailsTest extends ModuleTestBase {
	
	public Invoice6WithDetailsTest(String testName) {
		super(testName, "Invoice6WithDetails");		
	}
	
	
	public void testEntityValidatorAddingToCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("year", "2002");
		assertValue("number", "1");
		assertCollectionRowCount("details", 2);
		execute("Collection.add", "viewObject=xava_view_details");
		setConditionValues("", "", "", "", "", "", "", "orphan");
		execute("List.filter");
		assertValueInList(0, 7, "This is an orphan detail");
		execute("AddToCollection.add", "row=0");
		assertError("Detail not added: Invoice too old"); 
		
		assertDialog();
		assertAction("AddToCollection.add", "row=0"); // So we have the list
		execute("AddToCollection.cancel");
		assertCollectionRowCount("details", 2);
	}
			
}
