package org.openxava.test.tests.bymodule;


/**
 * 
 * @author Javier Paniza
 */

public class InvoicePropertiesFromCollectionInListTest extends CustomizeListTestBase { 
	
	public InvoicePropertiesFromCollectionInListTest(String testName) {
		super(testName, "InvoicePropertiesFromCollectionInList");		
	}
	
	public void testPropertiesFromCollectionInList() throws Exception { 
		assertListRowCount(9);
		//correccion
		//assertLabelInList(9, "Unit price of product of details");
		assertLabelInList(9, "Details product unit price");
		assertLabelInList(10, "Deliveries");
		assertValueInList(0, 9, "...");
		assertValueInList(0, 10, "...");
		assertTrue(getHtml().contains("There are 9 records in list")); // Count works when no filter
		assertNoAction("List.orderBy", "property=details.product.unitPrice");
		assertNoAction("List.sumColumn", "property=details.product.unitPrice");
		
		setConditionValues("", "", "", "", "", "20");
		execute("List.filter");
		assertListRowCount(5); 
		assertValueInList(0, 9, "Matching details: 1");
		assertValueInList(1, 9, "Matching details: 1");
		assertValueInList(2, 9, "Matching details: 1");
		assertValueInList(3, 9, "Matching details: 1");
		assertValueInList(4, 0, "2007");
		assertValueInList(4, 1, "14");
		assertValueInList(4, 9, "Matching details: 5");
		assertValueInList(0, 10, "...");
		assertFalse(getHtml().contains("records in list")); // Count does not work when filter yet
		
		// Ordering and that properties of references are compatible
		execute("List.orderBy", "property=customer.name");
		assertListRowCount(5);
		assertValueInList(0, 3, "Cuatrero");
		
		
		setConditionValues("", "", "", "", "", "20", "DELIVERY JUNIT");
		execute("List.orderBy", "property=customer.name");
		assertListRowCount(4);		
		assertValueInList(0,  9, "Matching details: 1");
		assertValueInList(1,  9, "Matching details: 1");
		assertValueInList(2,  9, "Matching details: 1");
		assertValueInList(3,  9, "Matching details: 1");
		assertValueInList(0, 10, "Matching deliveries: 1");
		assertValueInList(1, 10, "Matching deliveries: 1");
		assertValueInList(2, 10, "Matching deliveries: 1");
		assertValueInList(3, 10, "Matching deliveries: 1");		
	}
	
	
}