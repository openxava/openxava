package org.openxava.test.tests;

import com.gargoylesoftware.htmlunit.html.*;


/**
 * 
 * @author Javier Paniza
 */

public class InvoicePropertiesFromCollectionInListTest extends CustomizeListTestBase { 
	
	public InvoicePropertiesFromCollectionInListTest(String testName) {
		super(testName, "InvoicePropertiesFromCollectionInList");		
	}
	
	public void testPropertiesFromCollectionInList() throws Exception { // tmp ¿Mezclar?
		// TMP ME QUEDÉ POR AQUÍ: INTENTANDO QUE BUSQUE POR NUMERO, TEST YA HECHO
		assertListRowCount(9);
		assertLabelInList(9, "Unit price of product of details");
		assertLabelInList(10, "Deliveries");
		assertValueInList(0, 9, "...");
		assertValueInList(0, 10, "...");
		assertTrue(getHtml().contains("There are 9 records in list")); // Count works when no filter
		assertNoAction("List.orderBy", "property=details.product.unitPrice");
		assertNoAction("List.sumColumn", "property=details.product.unitPrice");
		
		setConditionValues("", "", "", "", "", "20");
		execute("List.filter");
		assertListRowCount(5);
		assertValueInList(0, 9, "MATCHING DETAILS: 1");
		assertValueInList(1, 9, "MATCHING DETAILS: 1");
		assertValueInList(2, 9, "MATCHING DETAILS: 1");
		assertValueInList(3, 9, "MATCHING DETAILS: 1");
		assertValueInList(4, 0, "2007");
		assertValueInList(4, 1, "14");
		assertValueInList(4, 9, "MATCHING DETAILS: 3");
		assertValueInList(0, 10, "...");
		assertFalse(getHtml().contains("records in list")); // Count does not work when filter yet
		
		// Ordering and that properties of references are compatible
		execute("List.orderBy", "property=customer.name");
		assertListRowCount(5);
		assertValueInList(0, 3, "Cuatrero");
		
		
		setConditionValues("", "", "", "", "", "20", "DELIVERY JUNIT");
		execute("List.orderBy", "property=customer.name");
		assertListRowCount(4);		
		assertValueInList(0,  9, "MATCHING DETAILS: 1");
		assertValueInList(1,  9, "MATCHING DETAILS: 1");
		assertValueInList(2,  9, "MATCHING DETAILS: 1");
		assertValueInList(3,  9, "MATCHING DETAILS: 1");
		assertValueInList(0, 10, "MATCHING DELIVERIES: 1");
		assertValueInList(1, 10, "MATCHING DELIVERIES: 1");
		assertValueInList(2, 10, "MATCHING DELIVERIES: 1");
		assertValueInList(3, 10, "MATCHING DELIVERIES: 1");		
	}
	
	
}