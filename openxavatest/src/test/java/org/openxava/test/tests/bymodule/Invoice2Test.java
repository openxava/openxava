package org.openxava.test.tests.bymodule;

import org.htmlunit.*;
import org.openxava.tab.*;
import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class Invoice2Test extends ModuleTestBase {
	
	public Invoice2Test(String testName) {
		super(testName, "Invoice2");		
	}
	
		
	public void testListConfigurationsAndGroupByInAllListFormats() throws Exception { 
		// Preserve the order of filtering, selecting configuration, resetModules for List and Cards format, it's in that way for a bug 
		assertListSelectedConfiguration("All");
		assertListAllConfigurations("All");
		assertListRowCount(9); 
		
		setConditionValues("2002"); 
		execute("List.filter");
		assertListSelectedConfiguration("Year = 2002");
		assertListAllConfigurations("Year = 2002", "All"); 
		assertListRowCount(1);
		execute("List.saveConfiguration");
		execute("SaveListConfiguration.save");
			
		selectListConfiguration("All"); 
		assertListAllConfigurations("All", "Year = 2002"); 
		assertListRowCount(9);
		
		resetModule();
		
		assertListSelectedConfiguration("All"); 
		assertListAllConfigurations("All", "Year = 2002");
		assertListRowCount(9);

		selectListConfiguration("Year = 2002"); 
		assertListAllConfigurations("Year = 2002", "All");
		assertListRowCount(1);
		
		execute("ListFormat.select", "editor=Cards");
		assertListSelectedConfiguration("Year = 2002"); 
		assertListAllConfigurations("Year = 2002", "All");
		assertListRowCount(1);
		assertValueInList(0, "1\n2002\nDate: 1/1/2002, VAT %: 16.0, Amounts sum: 2,500.00, Number: 1, Name: Javi, Discount: -250.00, Total: 2,650.00"); 
		
		
		selectListConfiguration("All"); 
		assertListAllConfigurations("All", "Year = 2002");
		assertListRowCount(9);	

		selectListConfiguration("Year = 2002"); 
		assertListAllConfigurations("Year = 2002", "All");
		assertListRowCount(1);
		
		selectListConfiguration("All");
		selectGroupBy("Group by year");
		
		assertListRowCount(5); 
		
		assertValueInList(0, "2002\n2,500.00\nRecord count: 1"); 
		assertValueInList(1, "2004\n5,706.00\nRecord count: 5");
		assertValueInList(2, "2007\n6,059.00\nRecord count: 1");
		assertValueInList(3, "2009\n0.00\nRecord count: 1");
		assertValueInList(4, "2011\n18,207.00\nRecord count: 1");
		
		execute("ListFormat.select", "editor=Charts");
		assertCombosForGroupByInCharts(); 		
		
		selectGroupBy("No grouping");
		String [][] chartColumnValues = {
			{"year", "Year"},
			{"number", "Number"},
			{"vatPercentage", "VAT %"},
			{"amountsSum", "Amounts sum"},
			{"customer.number", "Customer number"},
			{"discount", "Discount"},
			{"total", "Total"},
			{"__MORE__", "[SHOW MORE...]"}
		};
		assertValidValuesInCollection("columns", 0, "name", chartColumnValues); 
		
		selectListConfiguration("Year = 2002"); 
		assertListAllConfigurations("Year = 2002", "All");
		assertNoErrors();
	}
	
	public void testChangingModuleInChartsFormat() throws Exception {  
		// Only fails in this order from starting module. Don't merge with other test.
		execute("ListFormat.select", "editor=Charts");
		assertChartEditor();
		changeModule("ServiceInvoice");
		execute("ListFormat.select", "editor=Charts");
		changeModule("Invoice2");
		assertChartEditor();
	}

	
	public void testRecordCountInChart_groupByInChartAfterChangingChartType() throws Exception {  
		int originalColumnCount = getListColumnCount();
		execute("ListFormat.select", "editor=Charts");
		execute("Chart.selectType", "chartType=PIE");
		selectGroupBy("Group by year");
		assertChartEditor(); 
		setValueInCollection("columns", 0, "name", Tab.GROUP_COUNT_PROPERTY); 
		assertChartEditor();
		execute("ListFormat.select", "editor=List");
		assertAction("List.filter"); 
		resetModule();
		assertListColumnCount(originalColumnCount);
		execute("ListFormat.select", "editor=Charts");
		assertChartEditor();		
		
		execute("Chart.selectType", "chartType=PIE");
		execute("Chart.selectType", "chartType=BAR");
		selectGroupBy("Group by year");
		assertChartEditor();
	}
	
    private void assertChartEditor() throws Exception { 
        try {
            getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice2__xava_chart__container");
        }
        catch (ElementNotFoundException ex) {
            fail("Editor for charts not displayed");
        }
    }
	
	public void testGroupBy() throws Exception { 

		assertAllGroupBys( 
			"No grouping",
			"Group by year",
			"Group by number",
			"Group by date",
			"Group by month of date",
			"Group by year of date",
			"Group by vat %",
			"Group by amounts sum",
			"Group by customer number",
			"Group by customer"
		);
		
		assertAction("CRUD.deleteSelected");
		assertAction("List.viewDetail");
		assertAction("CRUD.deleteRow");		
		assertTrue(getHtml().contains("There are 9 records in list")); 
		assertListRowCount(9);
		assertValuesInList(0, "2002",  "1",  "1/1/2002", "16.0",  "2,500.00", "1", "Javi"); 
		assertValuesInList(1, "2004",  "2",  "1/4/2004", "42.0",     "11.00", "2", "Juanillo");
		assertValuesInList(2, "2004",  "9",  "1/4/2004", "71.0",  "4,396.00", "1", "Javi");
		assertValuesInList(3, "2004", "10", "12/4/2004", "18.0",  "1,189.00", "2", "Juanillo");
		assertValuesInList(4, "2004", "11", "11/4/2006", "22.0",      "0.00", "3", "Carmelo");
		assertValuesInList(5, "2004", "12", "11/5/2006", "13.0",    "110.00", "4", "Cuatrero");
		assertValuesInList(6, "2007", "14", "5/28/2007", "16.0",  "6,059.00", "1", "Javi");
		assertValuesInList(7, "2009",  "1", "6/23/2009", "16.0",      "0.00", "1", "Javi");
		assertValuesInList(8, "2011",  "1", "3/14/2011", "18.0", "18,207.00", "1", "Javi");
		
		selectGroupBy("Group by date");
		assertNoAction("CRUD.deleteSelected");
		assertNoAction("List.viewDetail");
		assertNoAction("CRUD.deleteRow");
		assertFalse(getHtml().contains("There are 9 records in list"));
		assertListRowCount(8); 
		assertListColumnCount(3);
		assertValuesInList(0,  "1/1/2002",  "2,500.00", "1");
		assertValuesInList(1,  "1/4/2004",  "4,407.00", "2");
		assertValuesInList(2, "12/4/2004",  "1,189.00", "1");
		assertValuesInList(3, "11/4/2006",      "0.00", "1");
		assertValuesInList(4, "11/5/2006",    "110.00", "1");
		assertValuesInList(5, "5/28/2007",  "6,059.00", "1");
		assertValuesInList(6, "6/23/2009",      "0.00", "1");
		assertValuesInList(7, "3/14/2011", "18,207.00", "1");
		
		selectGroupBy("Group by year");
		assertNoAction("CRUD.deleteSelected");
		assertNoAction("List.viewDetail");
		assertNoAction("CRUD.deleteRow");		
		assertListRowCount(5);
		assertListColumnCount(3);
		assertValuesInList(0, "2002",  "2,500.00", "1");
		assertValuesInList(1, "2004",  "5,706.00", "5");
		assertValuesInList(2, "2007",  "6,059.00", "1");
		assertValuesInList(3, "2009",      "0.00", "1");
		assertValuesInList(4, "2011", "18,207.00", "1");
		
		selectGroupBy("Group by customer");
		assertNoAction("CRUD.deleteSelected");
		assertNoAction("List.viewDetail");
		assertNoAction("CRUD.deleteRow");		
		assertListRowCount(4);
		assertListColumnCount(3);
		assertValuesInList(0, "31,162.00", "Javi",     "5");
		assertValuesInList(1,  "1,200.00", "Juanillo", "2");
		assertValuesInList(2,      "0.00", "Carmelo",  "1");
		assertValuesInList(3,    "110.00", "Cuatrero", "1");
		
		selectGroupBy("Group by month of date");
		assertNoAction("CRUD.deleteSelected");
		assertNoAction("List.viewDetail");
		assertNoAction("CRUD.deleteRow");
		assertListRowCount(7);
		assertListColumnCount(3);
		assertValuesInList(0, "2002/1",  "2,500.00", "1");
		assertValuesInList(1, "2004/1",  "4,407.00", "2");
		assertValuesInList(2, "2004/12", "1,189.00", "1");
		assertValuesInList(3, "2006/11",   "110.00", "2");
		assertValuesInList(4, "2007/5",  "6,059.00", "1");
		assertValuesInList(5, "2009/6",      "0.00", "1"); 
		assertValuesInList(6, "2011/3", "18,207.00", "1");
		
		execute("List.orderBy", "property=date");
		assertNoErrors();
		execute("List.orderBy", "property=date");
		assertListRowCount(7);
		assertListColumnCount(3);
		assertValuesInList(0, "2011/3", "18,207.00", "1");
		assertValuesInList(1, "2009/6",      "0.00", "1");
		assertValuesInList(2, "2007/5",  "6,059.00", "1");
		assertValuesInList(3, "2006/11",   "110.00", "2");
		assertValuesInList(4, "2004/12", "1,189.00", "1");
		assertValuesInList(5, "2004/1",  "4,407.00", "2");
		assertValuesInList(6, "2002/1",  "2,500.00", "1");
		
		selectGroupBy("Group by year of date");
		assertNoAction("CRUD.deleteSelected");
		assertNoAction("List.viewDetail");
		assertNoAction("CRUD.deleteRow");
		assertListRowCount(6);
		assertListColumnCount(3);
		assertValuesInList(0, "2002",  "2,500.00", "1");
		assertValuesInList(1, "2004",  "5,596.00", "3");
		assertValuesInList(2, "2006",    "110.00", "2");
		assertValuesInList(3, "2007",  "6,059.00", "1");
		assertValuesInList(4, "2009",      "0.00", "1");
		assertValuesInList(5, "2011", "18,207.00", "1");
		
		execute("List.orderBy", "property=date");
		assertNoErrors();
		execute("List.orderBy", "property=date");
		assertListRowCount(6);
		assertListColumnCount(3);
		assertValuesInList(0, "2011", "18,207.00", "1");
		assertValuesInList(1, "2009",      "0.00", "1");
		assertValuesInList(2, "2007",  "6,059.00", "1");
		assertValuesInList(3, "2006",    "110.00", "2");
		assertValuesInList(4, "2004",  "5,596.00", "3");
		assertValuesInList(5, "2002",  "2,500.00", "1");
		
		selectGroupBy("No grouping");
		assertAction("CRUD.deleteSelected");
		assertAction("List.viewDetail");
		assertAction("CRUD.deleteRow");
		assertTrue(getHtml().contains("There are 9 records in list"));
		assertListRowCount(9);
		assertValuesInList(0, "2002",  "1",  "1/1/2002", "16.0",  "2,500.00", "1", "Javi");
		assertValuesInList(1, "2004",  "2",  "1/4/2004", "42.0",     "11.00", "2", "Juanillo");
		assertValuesInList(2, "2004",  "9",  "1/4/2004", "71.0",  "4,396.00", "1", "Javi");
		assertValuesInList(3, "2004", "10", "12/4/2004", "18.0",  "1,189.00", "2", "Juanillo");
		assertValuesInList(4, "2004", "11", "11/4/2006", "22.0",      "0.00", "3", "Carmelo");
		assertValuesInList(5, "2004", "12", "11/5/2006", "13.0",    "110.00", "4", "Cuatrero");
		assertValuesInList(6, "2007", "14", "5/28/2007", "16.0",  "6,059.00", "1", "Javi");
		assertValuesInList(7, "2009",  "1", "6/23/2009", "16.0",      "0.00", "1", "Javi");
		assertValuesInList(8, "2011",  "1", "3/14/2011", "18.0", "18,207.00", "1", "Javi");
		
		selectGroupBy("Group by year");
		assertListRowCount(5);
		assertListColumnCount(3);
		execute("List.orderBy", "property=year");
		execute("List.orderBy", "property=year");
		assertListRowCount(5);
		assertListColumnCount(3);
		assertValuesInList(0, "2011", "18,207.00", "1"); 
		assertValuesInList(1, "2009",      "0.00", "1");
		assertValuesInList(2, "2007",  "6,059.00", "1");
		assertValuesInList(3, "2004",  "5,706.00", "5");
		assertValuesInList(4, "2002",  "2,500.00", "1");
		
		execute("List.orderBy", "property=amountsSum");
		assertListRowCount(5);
		assertListColumnCount(3);
		assertValuesInList(0, "2009",      "0.00", "1");
		assertValuesInList(1, "2002",  "2,500.00", "1");
		assertValuesInList(2, "2004",  "5,706.00", "5");
		assertValuesInList(3, "2007",  "6,059.00", "1");
		assertValuesInList(4, "2011", "18,207.00", "1");
		assertNoErrors();
		
		execute("List.orderBy", "property=" + Tab.GROUP_COUNT_PROPERTY);
		assertListRowCount(5);
		assertListColumnCount(3);
		assertValuesInList(0, "2009",      "0.00", "1");
		assertValuesInList(1, "2002",  "2,500.00", "1");
		assertValuesInList(2, "2007",  "6,059.00", "1");
		assertValuesInList(3, "2011", "18,207.00", "1");
		assertValuesInList(4, "2004",  "5,706.00", "5");
		
		assertListSelectedConfiguration("All");
		execute("List.filter");
		assertListSelectedConfiguration("All");

		setConditionValues("", "", "5");
		execute("List.filter");
		assertListRowCount(1); 
		assertListColumnCount(3);
		assertValuesInList(0, "2004",  "5,706.00", "5");
		setConditionComparators("=", "=", "<");
		execute("List.filter");
		assertListRowCount(4); 
		assertListColumnCount(3);		

		resetModule();
		assertListSelectedConfiguration("All");
		assertListRowCount(9);
		assertListColumnCount(9); 

		selectGroupBy("Group by year");
		execute("ListFormat.select", "editor=Charts");
		assertCombosForGroupByInCharts(); 
		assertCollectionRowCount("columns", 2);
		assertValueInCollection("columns", 0, 0, "Amounts sum");
		assertValueInCollection("columns", 1, 0, "Record count");
		execute("ListFormat.select", "editor=List");
		
		selectGroupBy("No grouping");
		assertListRowCount(9);
		setConditionValues("", "", "", "", "", "", "Javi");
		execute("List.filter");
		assertListRowCount(5); 
		selectGroupBy("Group by year");
		assertListRowCount(5);
		assertListColumnCount(3);
		assertValuesInList(0, "2002",  "2,500.00", "1");
		assertValuesInList(1, "2004",  "4,396.00", "1");
		assertValuesInList(2, "2007",  "6,059.00", "1");
		assertValuesInList(3, "2009",      "0.00", "1");
		assertValuesInList(4, "2011", "18,207.00", "1");
		
		setConditionValues("2004");
		execute("List.filter");
		assertListRowCount(1); 
		assertListColumnCount(3);
		assertValuesInList(0, "2004",  "4,396.00", "1");
				
		selectGroupBy("No grouping");
		assertListRowCount(5); // The 'Javis' 
		assertListColumnCount(9);
	}

	private void assertCombosForGroupByInCharts() throws Exception {
		String [][] chartColumnValues = {
			{"year", "Year"},
			{"amountsSum", "Amounts sum"},
			{"__GROUP_COUNT__", "Record count"}, 
			{"__MORE__", "[SHOW MORE...]"}
		};
		assertValidValuesInCollection("columns", 0, "name", chartColumnValues);
		
		String [][] newChartColumnValues = {
			{"", " "},
			{"year", "Year"},
			{"amountsSum", "Amounts sum"},
			{"__GROUP_COUNT__", "Record count"}, 
			{"__MORE__", "[SHOW MORE...]"}
		};
		assertValidValuesInCollection("columns", getCollectionRowCount("columns"), "name", newChartColumnValues);
	}
	
	public void testDependentEditorsForHiddenPropertiesInCollectionElement() throws Exception {
		execute("List.viewDetail", "row=0"); 
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		assertNotExists("familyList");
		assertNotExists("productList");
		execute("InvoiceDetail2.showProductList");
		assertExists("familyList"); 
		assertExists("productList");
		assertValidValuesCount("productList", 1);
		setValue("familyList", "1");
		assertValidValuesCount("productList", 7); 
		
	}
	
	public void testTouchContainerFromCallback_calculatedPropertiesInNestedGroupsInCollectionDetail() throws Exception { 
		execute("CRUD.new");
		setValue("number", "66");
		setValue("vatPercentage", "16");
		setValue("customer.number", "1");
		assertCollectionRowCount("details", 0);
		
		// Creating a new detail
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		assertNotExists("details.invoice.year"); 
		setValue("quantity", "7");
		setValue("unitPrice", "8");
		
		assertValue("amount", "56.00");  
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		execute("CRUD.refresh");
		assertValue("amountsSum", "56.00");
		
		// Creating another one
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		setValue("quantity", "10");
		setValue("unitPrice", "10");
		assertValue("amount", "100.00");
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 2);
		execute("CRUD.refresh");
		assertValue("amountsSum", "156.00");
		
		// Modifiying
		execute("Collection.edit", "row=1,viewObject=xava_view_details");
		setValue("quantity", "20");
		setValue("unitPrice", "10");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 2);
		execute("CRUD.refresh");
		assertValue("amountsSum", "256.00");
		
		// Removing
		execute("Collection.edit", "row=1,viewObject=xava_view_details");
		setValue("quantity", "20");
		setValue("unitPrice", "10");
		execute("Collection.remove");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		execute("CRUD.refresh");
		assertValue("amountsSum", "56.00");
		
		execute("CRUD.delete");
		assertNoErrors();		
	}
	
	public void testInjectPropertiesOfContainerInOnCreateCalculatorOfAggregate() throws Exception {   
		execute("CRUD.new");
		setValue("number", "66");
		setValue("vatPercentage", "16");
		setValue("customer.number", "1");
		assertCollectionRowCount("details", 0);
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		setValue("quantity", "7");
		setValue("unitPrice", "8");
		assertValue("amount", "56.00"); 
		setValue("product.number", "1"); 
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Collection.save");
		assertNoErrors(); 
		assertCollectionRowCount("details", 1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testCollectionOrderedByAPropertyOfAReference_valueOfNestedRerenceInsideAnEmbeddedCollection() throws Exception {
		execute("CRUD.new");
		setValue("year", "2002"); 
		setValue("number", "1");
		execute("CRUD.refresh");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, "product.description", "XAVA");
		assertValueInCollection("details", 1, "product.description", "IBM ESERVER ISERIES 270");
		
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		assertValue("product.description", "XAVA");
		assertValue("product.family.description", "SOFTWARE"); 
		closeDialog();
		
		execute("Collection.edit", "row=1,viewObject=xava_view_details");
		assertValue("product.description", "IBM ESERVER ISERIES 270"); 
		assertValue("product.family.description", "HARDWARE");		
	}
	
	public void testMinSizeForCollections() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("vatPercentage", "18");
		setValue("customer.number", "1");
		execute("CRUD.save");
		assertError("It's required at least 1 element in Details of Invoice 2"); 
	} 							
}
