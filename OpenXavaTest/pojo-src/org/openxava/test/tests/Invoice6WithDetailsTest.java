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
	
	public void testEntityValidatorAddingToCollection_setterValidationAddingToCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValidationAddingToCollection("2002", "1", 2, "Detail not added: Invoice too old");
		
		execute("Navigation.next");
		assertValidationAddingToCollection("2004", "2", 1, "Detail not added: Invoice amount too low");
	}
	
	public void assertValidationAddingToCollection(String year, String number, int detailsCount, String expectedMessage) throws Exception {
		assertValue("year", year);
		assertValue("number", number);
	
		assertCollectionRowCount("details", detailsCount);
		execute("Collection.add", "viewObject=xava_view_details");
		setConditionValues("", "", "", "", "", "", "", "orphan");
		execute("List.filter");
		assertValueInList(0, 7, "This is an orphan detail");
		execute("AddToCollection.add", "row=0");
		assertError(expectedMessage); 
		
		assertDialog();
		assertAction("AddToCollection.add", "row=0"); // So we have the list
		execute("AddToCollection.cancel");
		assertCollectionRowCount("details", detailsCount);		
	}
			
}
