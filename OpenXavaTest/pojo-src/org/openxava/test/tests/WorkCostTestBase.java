package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

abstract public class WorkCostTestBase extends ModuleTestBase {

	private String collection;
	private String prefix = "";
	
	public WorkCostTestBase(String testName, String module, String collection, String prefix) {
		this(testName, module, collection);
		this.prefix = prefix;
	}
	
	public WorkCostTestBase(String testName, String module, String collection) {
		super(testName, module);
		this.collection = collection;
	}
		
	public void testCalculationAndEditableTotalsInCollections() throws Exception { 
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("description", "CAR SERVICE");
		
		assertNoAction("CollectionTotals.removeColumnSum", "property=total,collection=" + collection);
		assertCollectionRowCount(collection, 0);
		assertTotals("0.00", "10", "0.00", "0.00");
		
		execute("Collection.add", "viewObject=xava_view_" + collection);
		checkAll();
		execute("AddToCollection.add");
		
		assertEditable(prefix + "profitPercentage");
		assertNoEditable(prefix + "profit");
		assertNoEditable(prefix + "total");
		
		assertValueInCollection(collection, 0, "total",  "52.20");
		assertValueInCollection(collection, 1, "total",  "78.88");
		assertTotals("131.08", "10", "13.11", "144.19");
				
		setValue(prefix + "profitPercentage", "15");
		assertTotals("131.08", "15", "19.66", "150.74");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue(prefix + "profitPercentage", "10"); 
		assertTotals("131.08", "10", "13.11", "144.19");
		
		setValue(prefix + "profitPercentage", "15");
		assertTotals("131.08", "15", "19.66", "150.74");
		
		execute("Collection.edit", "row=1,viewObject=xava_view_" + collection);
		assertValue("hours", "3");
		assertValue("total", "78.88");
		setValue("hours", "4");
		assertValue("total", "103.24");
		execute("Collection.save");
		
		assertValueInCollection(collection, 0, "total",  "52.20");
		assertValueInCollection(collection, 1, "total", "103.24");
		assertTotals("155.44", "15", "23.32", "178.76");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertTotals("155.44", "15", "23.32", "178.76");
		
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_" + collection);
		assertCollectionRowCount(collection, 1);
		assertValueInCollection(collection, 0, "total",  "103.24");
		assertTotals("103.24", "15", "15.49", "118.73");		
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertTotals("103.24", "15", "15.49", "118.73");
		
		// Restoring
		execute("Collection.edit", "row=0,viewObject=xava_view_" + collection);
		setValue("hours", "3");
		execute("Collection.save");
		assertNoErrors();
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_" + collection);
		assertCollectionRowCount(collection, 0);		
		setValue(prefix + "profitPercentage", "10");
		execute("CRUD.save");
		assertNoErrors(); // This tests a case
	}
	
	protected void assertTotals(String totalSum, String profitPercentage, String profit, String total) throws Exception {
		assertTotalInCollection(collection, 0, "total", totalSum);
		assertTotalInCollection(collection, 1, "total", profitPercentage);
		assertTotalInCollection(collection, 2, "total", profit);
		assertTotalInCollection(collection, 3, "total", total);
	}

}
