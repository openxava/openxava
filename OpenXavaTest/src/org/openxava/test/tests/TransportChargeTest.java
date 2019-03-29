package org.openxava.test.tests;

import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class TransportChargeTest extends TransportChargeTestBase {
	
	public TransportChargeTest(String testName) {
		super(testName, "TransportCharge");		
	}
	
	public void testOrderAndFilterInCollectionOfReference() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=xava.TransportCharge.delivery.number");
		int row = getRowOf("2004", "2", "1", "666"); 
		assertValueInList(row, 0, "2004");
		assertValueInList(row, 1, "2");
		assertValueInList(row, 2, "1");		
		assertValueInList(row, 4, "666");
		execute("ReferenceSearch.choose", "row=" + row);
		execute("Sections.change", "activeSection=4,viewObject=xava_view_delivery");
		
		assertCollectionRowCount("delivery.details", 3); 
		execute("List.orderBy", "property=number,collection=delivery.details"); 
		assertValueInCollection("delivery.details", 0, 0, "11"); 
		assertValueInCollection("delivery.details", 1, 0, "12");
		assertValueInCollection("delivery.details", 2, 0, "13");
		
		execute("List.orderBy", "property=number,collection=delivery.details");
		assertCollectionRowCount("delivery.details", 3);
		assertValueInCollection("delivery.details", 0, 0, "13"); 
		assertValueInCollection("delivery.details", 1, 0, "12");
		assertValueInCollection("delivery.details", 2, 0, "11");

		setConditionValues("delivery.details", new String [] { "11" } );
		execute("List.filter", "collection=delivery.details");
		assertCollectionRowCount("delivery.details", 1);
		assertValueInCollection("delivery.details", 0, 0, "11");		
	}
	
	public void testViewForReferenceWithSections() throws Exception {
		execute("CRUD.new");
		assertExists("delivery.distance");
		assertNotExists("delivery.advice");
		execute("Sections.change", "activeSection=3,viewObject=xava_view_delivery");
		assertExists("delivery.advice");
		assertNotExists("delivery.distance");
	}
	
	public void testKeyNestedReferences() throws Exception {
		deleteAll();
		
		execute("CRUD.new");
		assertEditable("delivery.invoice.year");
		assertNoEditable("delivery.invoice.date");
		assertEditable("delivery.number");
		assertNoEditable("delivery.description");
		assertNoEditable("delivery.distance");
		execute("Reference.search", "keyProperty=xava.TransportCharge.delivery.number");
		String year = getValueInList(0, 0);
		String number = getValueInList(0, 1);
		assertTrue("It is required that year in invoice has value", !Is.emptyString(year));
		assertTrue("It is required that number in invoice has value", !Is.emptyString(number));
		
		execute("ReferenceSearch.choose", "row=0");
		assertNoErrors();
		assertValue("delivery.invoice.year", year); 
		assertValue("delivery.invoice.number", number);
		
		setValue("amount", "666");
		execute("CRUD.save");
		assertNoErrors();
				
		assertValue("delivery.invoice.year", "");
		assertValue("delivery.invoice.number", "");
		assertValue("amount", "");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0"); 
		assertNoErrors();
		assertValue("delivery.invoice.year", year);
		assertValue("delivery.invoice.number", number);
		assertValue("amount", "666.00");
		
		setValue("amount", "777");
		execute("CRUD.save");
		assertNoErrors();

		assertValue("delivery.invoice.year", "");
		assertValue("delivery.invoice.number", "");
		assertValue("amount", "");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("delivery.invoice.year", year);
		assertValue("delivery.invoice.number", number);
		assertValue("amount", "777.00");
						
		execute("CRUD.delete");										
		assertMessage("Transport charge deleted successfully");
	}
	
	public void testKeyNestedReferencesInList() throws Exception {
		deleteAll();
		createSome();
		resetModule(); 
		execute("List.orderBy", "property=amount");
		
		assertListRowCount(2);
		
		assertValueInList(0, 0, String.valueOf(getCharge1().getDelivery().getInvoice().getYear())); 
		assertValueInList(0, 1, String.valueOf(getCharge1().getDelivery().getInvoice().getNumber()));
		assertValueInList(0, 2, String.valueOf(getCharge1().getDelivery().getNumber()));
		assertValueInList(0, 3, "100.00");

		assertValueInList(1, 0, String.valueOf(getCharge2().getDelivery().getInvoice().getYear()));
		assertValueInList(1, 1, String.valueOf(getCharge2().getDelivery().getInvoice().getNumber()));
		assertValueInList(1, 2, String.valueOf(getCharge2().getDelivery().getNumber()));
		assertValueInList(1, 3, "200.00");

		String [] condition = {
				String.valueOf(getCharge2().getDelivery().getInvoice().getYear()),
				String.valueOf(getCharge2().getDelivery().getInvoice().getNumber()),
				String.valueOf(getCharge2().getDelivery().getNumber())
		};		
		setConditionValues(condition);
		execute("List.filter");
		
		assertListRowCount(1);
		
		assertValueInList(0, 0, String.valueOf(getCharge2().getDelivery().getInvoice().getYear()));
		assertValueInList(0, 1, String.valueOf(getCharge2().getDelivery().getInvoice().getNumber()));
		assertValueInList(0, 2, String.valueOf(getCharge2().getDelivery().getNumber()));
		assertValueInList(0, 3, "200.00");		
	}
	
	private int getRowOf(String v0, String v1, String v2, String v4) throws Exception {
		int rowCount = getListRowCount();
		for (int i=0; i<rowCount; i++) {
			if (
				getValueInList(i, 0).equals(v0) &&
				getValueInList(i, 1).equals(v1) &&
				getValueInList(i, 2).equals(v2) &&
				getValueInList(i, 4).equals(v4)
			) {
				return i;
			}
		}
		fail("Row with values " + v0 + ", " + v1 + ", " + v2 + ", " + v4 + " does not found in list");
		return -1; // never
	}
	

}
