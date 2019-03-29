package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */

abstract public class InvoiceDetailsWithVatPercentageTestBase extends CustomizeListTestBase {
	
	private String collection;
	
	public InvoiceDetailsWithVatPercentageTestBase(String testName, String module, String collection) {
		super(testName, module);
		this.collection = collection;
	}
	
	public void testEditablePropertiesInCollectionTotals() throws Exception { 
		execute("List.viewDetail", "row=0");		
		
		assertValueInCollection(collection, 0, "amount", "2,000.00");
		assertValueInCollection(collection, 1, "amount",   "500.00");
		assertTotalInCollection(collection,    "amount", "2,500.00");
		assertTotalInCollection(collection, 1, "amount",    "16.0"); 
		assertTotalInCollection(collection, 2, "amount",   "400.00");
		assertTotalInCollection(collection, 3, "amount", "2,900.00");
		
		setValue("vatPercentage", "17");
		assertTotalInCollection(collection, 2, "amount",   "425.00");
		assertTotalInCollection(collection, 3, "amount", "2,925.00");

		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertTotalInCollection(collection, 1, "amount",    "17.0");
		assertTotalInCollection(collection, 2, "amount",   "425.00");
		assertTotalInCollection(collection, 3, "amount", "2,925.00");
		
		setValue("vatPercentage", "16");
		execute("CRUD.save");
	}
	
}
