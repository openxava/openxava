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
		assertListRowCount(9);
		assertLabelInList(9, "Product of details");
		assertLabelInList(10, "Deliveries");
		assertValueInList(0, 9, "...");
		assertValueInList(0, 10, "...");
		assertTrue(getHtml().contains("There are 9 records in list")); // Count works when no filter
		setConditionValues("", "", "", "", "", "IBM");
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
		
		
		setConditionValues("", "", "", "", "", "IBM", "666");
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
		
		// TMP ME QUEDÉ POR AQUÍ: EL TEST YA FUNCIONA, LO SIGUIENTE SERÍA:
		// TMP - Restaurar el count de registros
		// TMP - Falta probar ordenar por columna de colección: ¿Permitirlo?
	}
	
	
}