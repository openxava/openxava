package org.openxava.test.tests;

/**
 * This tests without @View. 
 *  
 * @author Javier Paniza
 */

public class WorkCostTest extends WorkCostTestBase {

	
	public WorkCostTest(String testName) {
		super(testName, "WorkCost", "invoices");		
	}
	
	public void testCalculationInCollection() throws Exception {  
		execute("CRUD.new"); 
		setValue("description", "SOMETHING"); 
		assertEquals(1, getHtmlPage().getElementsByName("ox_OpenXavaTest_WorkCost__profitPercentage").size()); 
		execute("Collection.new", "viewObject=xava_view_invoices");
		
		assertValue("vatPercentage", "16");
		setValue("hours", "250");
		assertValue("total", "0.00");
		
		setValue("worker.nickName", "john");
		assertValue("worker.hourPrice", "20.00");
		assertValue("total", "5800.00");
		
		setValue("tripCost", "299.9");
		assertValue("total", "6147.88");
		
		setValue("discount", "1200");
		assertValue("total", "4755.88");
		
		setValue("vatPercentage", "21");
		assertValue("total", "4960.88");
	}
	
	public void testCalculationInCollectionInSpanish() throws Exception { 
		setLocale("es");
	
		execute("List.viewDetail", "row=0");
		assertTotals("0,00", "10", "0,00", "0,00");
		
		execute("Collection.add", "viewObject=xava_view_invoices");
		checkAll();
		execute("AddToCollection.add");
		
		assertValueInCollection("invoices", 0, "total",  "52,20");
		assertValueInCollection("invoices", 1, "total",  "78,88");
		assertTotals("131,08", "10", "13,11", "144,19");
				
		setValue("profitPercentage", "15");
		assertTotals("131,08", "15", "19,66", "150,74");
		
		// Restoring
		setValue("profitPercentage", "10");
		checkAllCollection("invoices");
		execute("Collection.removeSelected", "viewObject=xava_view_invoices");
		assertCollectionRowCount("invoices", 0); 		
	}
	
	public void testCalculationAndEditableTotalsInCollections() throws Exception { 
		super.testCalculationAndEditableTotalsInCollections();
		execute("Navigation.first");
		execute("CRUD.save"); 
	}
	
}
