package org.openxava.test.tests;

import java.util.*;

import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * Create on 07/04/2008 (12:16:03)
 * 
 * @author Ana Andrés
 * @author Javier Paniza
 */
public class CarrierWithSectionsTest extends ModuleTestBase {
	
	public CarrierWithSectionsTest(String testName) {
		super(testName, "CarrierWithSections");
	}
	
	public void testInComparator() throws Exception {
		assertListRowCount(5);
		
		setConditionComparators("in_comparator");
		setConditionValues("1,3");
		execute("List.filter");
		assertListRowCount(2); 
		assertValueInList(0, 1, "1");
		assertValueInList(1, 1, "3");		
		
		setConditionComparators("", "in_comparator");
		setConditionValues("", "dos, cuatro"); // With space and lowercase
		execute("List.filter");
		assertListRowCount(2);
		assertValueInList(0, 1, "2");
		assertValueInList(0, 2, "DOS");
		assertValueInList(1, 1, "4");
		assertValueInList(1, 2, "CUATRO");
		
		setConditionComparators("not_in_comparator", "");
		setConditionValues("1, 3", "");
		execute("List.filter");
		assertListRowCount(3);
		assertValueInList(0, 1, "2");
		assertValueInList(1, 1, "4");
		assertValueInList(2, 1, "5");
	}
	
	public void testMyReports() throws Exception { 
		execute("ExtendedPrint.myReports");
		assertDialogTitle("My reports"); 
		assertValue("name", "Carrier report"); 
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");

		execute("MyReport.columnUp", "row=2,viewObject=xava_view_columns"); 
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Name");
		assertValueInCollection("columns", 2, 0, "Number");
		 
		reload();
		assertDialogTitle("My reports");  
		
		execute("MyReport.columnDown", "row=0,viewObject=xava_view_columns");
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 1, 0, "Calculated");
		assertValueInCollection("columns", 2, 0, "Number");
		
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		assertDialogTitle("Edit - Report column"); 
		assertValue("name", "calculated");
		assertValue("label", "Calculated"); 
		assertNotExists("comparator");
		assertNotExists("value");
		assertNotExists("order");
		assertNotExists("descriptionsListValue");
		assertNotExists("dateValue"); 
		assertNotExists("booleanValue"); 
		assertNotExists("validValuesValue"); 
		
		setValue("name", "number");
		assertValue("label", "Number"); 
		assertExists("comparator");
		assertExists("value");
		assertExists("order");
		assertNotExists("descriptionsListValue");
		assertNotExists("dateValue"); 
		assertNotExists("booleanValue"); 
		assertNotExists("validValuesValue"); 
		
		setValue("name", "calculated");
		assertValue("label", "Calculated"); 
		assertNotExists("comparator");
		assertNotExists("value");
		assertNotExists("order");
		assertNotExists("descriptionsListValue");
		assertNotExists("dateValue"); 
		assertNotExists("booleanValue"); 
		assertNotExists("validValuesValue"); 
		
		execute("Collection.hideDetail");
		
		execute("MyReport.removeColumn", "row=1,viewObject=xava_view_columns");
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 1, 0, "Number");
		
		execute("MyReport.newColumn", "viewObject=xava_view_columns");
		assertNoErrors(); // Needed 
		assertDialogTitle("Add - Report column");
		execute("MyReport.saveColumn");		
		assertError("Value for Name in Report column is required");
		String [][] validColumnNames = {
			{ "", "" },	
			{ "number", "Number" },
			{ "name", "Name" },
			{ "drivingLicence.type", "Type of Driving licence" },	
			{ "drivingLicence.level", "Level of Driving licence" },	
			{ "drivingLicence.description", "Description of Driving licence" },	
			{ "warehouse.zoneNumber", "Zone of Warehouse" },	
			{ "warehouse.number", "Warehouse number" },	
			{ "warehouse.name", "Name of Warehouse" },	
			{ "remarks", "Remarks" },	
			{ "calculated", "Calculated" }
		};		
		assertValidValues("name", validColumnNames);
		assertValue("name", "");		
		String [][] emptyComparators = {
		};
		assertValidValues("comparator", emptyComparators);
		setValue("name", "warehouse.zoneNumber");
		assertValue("label", "Zone of Warehouse");
		assertValue("comparator", "eq_comparator"); 
		String [][] numberComparators = {
			{ "eq_comparator", "=" },
			{ "ne_comparator", "<>" },  
			{ "ge_comparator", ">=" }, 
			{ "le_comparator", "<=" }, 
			{ "gt_comparator", ">" }, 
			{ "lt_comparator", "<" }, 
			{ "in_comparator", "in group" }, 
			{ "not_in_comparator", "not in group" } 
		};
		assertValidValues("comparator", numberComparators);
		
		setValue("name", "name");
		assertValue("comparator", "contains_comparator"); 
		String [][] stringComparators = {
			{ "contains_comparator", "contains" },	
			{ "starts_comparator", "starts with" },
			{ "ends_comparator", "ends with" },			
			{ "not_contains_comparator", "not contains" },
			{ "empty_comparator", "is empty" },
			{ "not_empty_comparator", "is not empty" },			
			{ "eq_comparator", "=" },
			{ "ne_comparator", "<>" },  
			{ "ge_comparator", ">=" }, 
			{ "le_comparator", "<=" }, 
			{ "gt_comparator", ">" }, 
			{ "lt_comparator", "<" },
			{ "in_comparator", "in group" }, 
			{ "not_in_comparator", "not in group" } 			
		};
		assertValidValues("comparator", stringComparators); 

		setValue("name", "warehouse.zoneNumber"); 
		assertValue("label", "Zone of Warehouse"); 
		assertValue("comparator", "eq_comparator"); 
		assertValidValues("comparator", numberComparators); 
		
		setValue("value", "1");
		execute("MyReport.saveColumn");
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 0, 1, "");
		assertValueInCollection("columns", 0, 2, "");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 1, 1, "");
		assertValueInCollection("columns", 1, 2, "");		
		assertValueInCollection("columns", 2, 0, "Zone of Warehouse");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "1");
		
		execute("MyReport.editColumn", "row=0,viewObject=xava_view_columns");
		assertValue("name", "name");
		setValue("comparator", "starts_comparator"); 
		setValue("value", "c"); // In lowercase to verify that it's case insensitive
		execute("MyReport.saveColumn");
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 0, 1, "starts with"); 
		assertValueInCollection("columns", 0, 2, "c");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 1, 1, "");
		assertValueInCollection("columns", 1, 2, "");		
		assertValueInCollection("columns", 2, 0, "Zone of Warehouse");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "1");
		
						
		setValue("name", "jUnit Carrier report");
		execute("MyReport.generatePdf");		
		
		assertPopupPDFLinesCount(5); // Instead of 9, because of warehouse.zoneNumber = 1 and name like 'c%' condition 
		assertPopupPDFLine(1, "jUnit Carrier report");
		assertPopupPDFLine(2, "Name Number Zone of Warehouse");  
		assertPopupPDFLine(3, "CUATRO 4 1");
		
		execute("ExtendedPrint.myReports");
		execute("MyReport.remove", "xava.keyProperty=name");
	}
	
	public void testMyReportConditionAndSortFromList() throws Exception {  
		assertListRowCount(5);
		setConditionComparators(">");
		setConditionValues("1");
		execute("List.filter");
		assertListRowCount(4);
		execute("List.orderBy", "property=name");
		assertValueInList(0, 1, "4");
		assertValueInList(1, 1, "5");
		assertValueInList(2, 1, "2");
		assertValueInList(3, 1, "3");
		
		execute("ExtendedPrint.myReports");
		assertDialogTitle("My reports"); 
		assertValue("name", "Carrier report"); 
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 1, 1, ">");
		assertValueInCollection("columns", 1, 2, "1");
		assertValueInCollection("columns", 1, 3, "");
		assertValueInCollection("columns", 2, 0, "Name");
		assertValueInCollection("columns", 2, 1, "");
		assertValueInCollection("columns", 2, 2, "");		
		assertValueInCollection("columns", 2, 3, "Ascending");		
	}
	
	
	public void testMyReportWithHiddenProperties() throws Exception { 
		execute("ExtendedPrint.myReports");
		setValue("name", "Carriers of zone 2");
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");
		
		execute("MyReport.newColumn", "viewObject=xava_view_columns");
		setValue("name", "warehouse.zoneNumber");
		setValue("value", "2");
		setValue("hidden", "true");
		execute("MyReport.saveColumn");

		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");
		assertValueInCollection("columns", 3, 0, "Zone of Warehouse");
		assertValueInCollection("columns", 3, 1, "="); 
		assertValueInCollection("columns", 3, 2, "2");
		assertValueInCollection("columns", 3, 5, "Hidden"); 
		
		execute("MyReport.columnUp", "row=3,viewObject=xava_view_columns");
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Zone of Warehouse");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "2");
		assertValueInCollection("columns", 2, 5, "Hidden"); 
		assertValueInCollection("columns", 3, 0, "Name");

		execute("MyReport.generatePdf");

		assertPopupPDFLinesCount(5); // Instead of 9, because of warehouse.zoneNumber = 1 and name like 'c%' condition 
		assertPopupPDFLine(1, "Carriers of zone 2");
		assertPopupPDFLine(2, "Calculated Number Name");  
		assertPopupPDFLine(3, "TR 5 Cinco");
		
		execute("ExtendedPrint.myReports");
		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Zone of Warehouse");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "2");
		assertValueInCollection("columns", 2, 5, "Hidden"); 
		assertValueInCollection("columns", 3, 0, "Name");
		
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		assertValue("hidden", "true"); 
		closeDialog();
		
		execute("MyReport.generateExcel");
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", "Calculated;Number;Name", header);		
		String line1 = excel.nextToken();
		assertEquals("line1", "\"TR\";5;\"Cinco\"", line1);
		assertTrue("Only one line must be generated", !excel.hasMoreTokens());		

		execute("ExtendedPrint.myReports");
		execute("MyReport.remove", "xava.keyProperty=name");		
	}
	
	public void testMyReportWithDuplicateProperties() throws Exception { 
		execute("ExtendedPrint.myReports");
		setValue("name", "Carriers between 2 and 4");
		assertCollectionRowCount("columns", 3); 
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");
		
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		assertValue("name", "number");
		setValue("comparator", "ge_comparator"); 
		setValue("value", "2");		
		execute("MyReport.saveColumn");		
		
		execute("MyReport.newColumn", "viewObject=xava_view_columns");
		setValue("name", "number");
		setValue("comparator", "le_comparator");
		setValue("value", "4");		
		execute("MyReport.saveColumn");

		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 1, 1, ">=");
		assertValueInCollection("columns", 1, 2, "2");
		assertValueInCollection("columns", 1, 5, "");  
		assertValueInCollection("columns", 2, 0, "Name");
		assertValueInCollection("columns", 3, 0, "Number");
		assertValueInCollection("columns", 3, 1, "<=");
		assertValueInCollection("columns", 3, 2, "4");
		assertValueInCollection("columns", 3, 5, "Hidden"); // Given it is duplicate it is automatically marked as hidden 
		
		execute("MyReport.editColumn", "row=3,viewObject=xava_view_columns");
		assertValue("name", "number");
		assertValue("value", "4"); 
		setValue("hidden", "false");
		execute("MyReport.saveColumn");		

		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 1, 1, ">=");
		assertValueInCollection("columns", 1, 2, "2");
		assertValueInCollection("columns", 1, 5, ""); 
		assertValueInCollection("columns", 2, 0, "Name");
		assertValueInCollection("columns", 3, 0, "Number");
		assertValueInCollection("columns", 3, 1, "<=");
		assertValueInCollection("columns", 3, 2, "4");
		assertValueInCollection("columns", 3, 5, "Hidden"); // Even on modifying you cannot have more than one not hidden column for the same property 
		
		execute("MyReport.generatePdf");		
		
		assertPopupPDFLinesCount(7); // Instead of 9, because of number is between 2 and 4 
		assertPopupPDFLine(1, "Carriers between 2 and 4");
		assertPopupPDFLine(2, "Calculated Number Name");  
		assertPopupPDFLine(3, "TR 2 DOS");
		assertPopupPDFLine(4, "TR 3 TRES");
		assertPopupPDFLine(5, "TR 4 CUATRO");
		
		execute("ExtendedPrint.myReports");
		execute("MyReport.remove", "xava.keyProperty=name");		
	}

	
	public void testMyReportFilteringByExactStringAndOrdering() throws Exception {
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 2, 0, "Name"); 
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		setValue("comparator", "eq_comparator"); 
		setValue("value", "UNO");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Name");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "UNO");
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		assertValue("comparator", "eq_comparator"); 
		assertValue("value", "UNO");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Name"); 
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "UNO");		
		execute("MyReport.generatePdf");		
		
		assertPopupPDFLinesCount(5); // Instead of 9, because of name = 'UNO' 
		assertPopupPDFLine(1, "Carrier report");
		assertPopupPDFLine(2, "Calculated Number Name");
		assertPopupPDFLine(3, "TR 1 UNO");
		
		checkRow(3); // To test that checked rows are ignored in custom reports
		execute("ExtendedPrint.myReports");
		execute("MyReport.remove", "xava.keyProperty=name"); 		
		assertValueInCollection("columns", 1, 0, "Number");
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		setValue("order", "1"); // DESCENDING
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 1, 0, "Number"); 
		assertValueInCollection("columns", 1, 1, ""); 
		assertValueInCollection("columns", 1, 2, "");
		assertValueInCollection("columns", 1, 3, "Descending");
		execute("MyReport.generatePdf");		
		
		assertPopupPDFLinesCount(9);  
		assertPopupPDFLine(1, "Carrier report");
		assertPopupPDFLine(2, "Calculated Number Name");
		assertPopupPDFLine(3, "TR 5 Cinco");
		assertPopupPDFLine(7, "TR 1 UNO");
		
		execute("ExtendedPrint.myReports");
		execute("MyReport.generateExcel");
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", "Calculated;Number;Name", header);		
		String line1 = excel.nextToken();
		assertEquals("line1", "\"TR\";5;\"Cinco\"", line1);
		excel.nextToken(); excel.nextToken(); excel.nextToken();// Lines 2, 3, 4
		String line5 = excel.nextToken();
		assertEquals("line5", "\"TR\";1;\"UNO\"", line5);
		assertTrue("Only five lines must be generated", !excel.hasMoreTokens());		
		
		execute("ExtendedPrint.myReports"); 
		execute("MyReport.remove", "xava.keyProperty=name"); 				
	}
			
	public void testStoringMyReports() throws Exception { 
		execute("ExtendedPrint.myReports");
		assertValue("name", "Carrier report"); 
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");
		assertNoAction("MyReport.createNew");
		assertNoAction("MyReport.remove");
		setValue("name", "Carrier report NUMBER first");
		execute("MyReport.columnUp", "row=1,viewObject=xava_view_columns");
		assertValueInCollection("columns", 0, 0, "Number");
		execute("MyReport.generatePdf");
		
		execute("ExtendedPrint.myReports");
		assertValue("name", "Carrier report NUMBER first");
		String [][] customReports1 = {
			{ "Carrier report NUMBER first", "Carrier report NUMBER first" }			
		};
		assertValidValues("name", customReports1);
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 1, 0, "Calculated");
		assertValueInCollection("columns", 2, 0, "Name");
		assertAction("MyReport.createNew");
		assertAction("MyReport.remove");
		execute("MyReport.createNew", "xava.keyProperty=name");
		setValue("name", "Carrier report NAME first");
		execute("MyReport.columnUp", "row=2,viewObject=xava_view_columns");
		execute("MyReport.columnUp", "row=1,viewObject=xava_view_columns");
		assertValueInCollection("columns", 0, 0, "Name");
		execute("MyReport.generatePdf");
		
		execute("ExtendedPrint.myReports");
		assertValue("name", "Carrier report NAME first");
		String [][] customReports2 = {
			{ "Carrier report NAME first", "Carrier report NAME first" },	
			{ "Carrier report NUMBER first", "Carrier report NUMBER first" }			
		};
		assertValidValues("name", customReports2);
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 1, 0, "Calculated");
		assertValueInCollection("columns", 2, 0, "Number");
		assertAction("MyReport.createNew");
		assertAction("MyReport.remove");
		execute("MyReport.createNew", "xava.keyProperty=name");
		setValue("name", "Carrier report With no CALCULATED");
		execute("MyReport.removeColumn", "row=0,viewObject=xava_view_columns");
		execute("MyReport.editColumn", "row=0,viewObject=xava_view_columns");
		setValue("comparator", "lt_comparator"); 
		setValue("value", "5");		
		execute("MyReport.saveColumn");
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		setValue("order", "1"); // DESCENDING
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 0, 1, "<");
		assertValueInCollection("columns", 0, 2, "5");		
		assertValueInCollection("columns", 0, 3, "");
		assertValueInCollection("columns", 1, 0, "Name");
		assertValueInCollection("columns", 1, 1, "");
		assertValueInCollection("columns", 1, 2, "");
		assertValueInCollection("columns", 1, 3, "Descending");
		execute("MyReport.generatePdf");		
		assertPopupPDFLinesCount(8);  
		assertPopupPDFLine(1, "Carrier report With no CALCULATED");		
		assertPopupPDFLine(2, "Number Name");
		assertPopupPDFLine(3, "1 UNO");
		assertPopupPDFLine(4, "3 TRES");
		assertPopupPDFLine(5, "2 DOS");
		assertPopupPDFLine(6, "4 CUATRO");				
		
		execute("ExtendedPrint.myReports");
		assertValue("name", "Carrier report With no CALCULATED"); 
		execute("MyReport.generatePdf");
		assertPopupPDFLinesCount(8);  
		assertPopupPDFLine(1, "Carrier report With no CALCULATED");		
		assertPopupPDFLine(2, "Number Name");
		assertPopupPDFLine(3, "1 UNO");
		assertPopupPDFLine(4, "3 TRES");
		assertPopupPDFLine(5, "2 DOS");
		assertPopupPDFLine(6, "4 CUATRO");				
		execute("ExtendedPrint.myReports"); 
		assertValue("name", "Carrier report With no CALCULATED"); 
		String [][] customReports3 = {
			{ "Carrier report NAME first", "Carrier report NAME first" },	
			{ "Carrier report NUMBER first", "Carrier report NUMBER first" },
			{ "Carrier report With no CALCULATED", "Carrier report With no CALCULATED" }
		};
		assertValidValues("name", customReports3);
		assertCollectionRowCount("columns", 2);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 0, 1, "<");
		assertValueInCollection("columns", 0, 2, "5");
		assertValueInCollection("columns", 0, 3, "");
		assertValueInCollection("columns", 1, 0, "Name");
		assertValueInCollection("columns", 1, 1, "");
		assertValueInCollection("columns", 1, 2, "");
		assertValueInCollection("columns", 1, 3, "Descending");
		
		setValue("name", "Carrier report NAME first");		
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 1, 0, "Calculated");
		assertValueInCollection("columns", 2, 0, "Number");

		setValue("name", "Carrier report With no CALCULATED");
		assertValue("name", "Carrier report With no CALCULATED");
		assertCollectionRowCount("columns", 2);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 0, 1, "<");
		assertValueInCollection("columns", 0, 2, "5");		
		assertValueInCollection("columns", 0, 3, "");
		assertValueInCollection("columns", 1, 0, "Name");
		assertValueInCollection("columns", 1, 1, "");
		assertValueInCollection("columns", 1, 2, "");
		assertValueInCollection("columns", 1, 3, "Descending");
		
		execute("MyReport.remove", "xava.keyProperty=name"); 
		assertMessage("Report 'Carrier report With no CALCULATED' removed");		
		assertValidValuesCount("name", 2);
		assertValidValues("name", customReports2);
		assertValue("name", "Carrier report NAME first");
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 1, 0, "Calculated");
		assertValueInCollection("columns", 2, 0, "Number");
		
		execute("MyReport.remove", "xava.keyProperty=name");
		assertValidValuesCount("name", 1);
		assertValidValues("name", customReports1);
		assertValue("name", "Carrier report NUMBER first");
		assertValueInCollection("columns", 0, 0, "Number");
		assertAction("MyReport.createNew");
		assertAction("MyReport.remove");
				
		execute("MyReport.remove", "xava.keyProperty=name");		
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");
		assertNoAction("MyReport.createNew");
		assertNoAction("MyReport.remove");
	}
	
	public void testRemoveReportInMyReport() throws Exception { 
		execute("ExtendedPrint.myReports");
		setValue("name", "Carrier report 1");
		execute("MyReport.generatePdf");		
		
		execute("ExtendedPrint.myReports"); 
		String [][] customReports1 = {
			{ "Carrier report 1", "Carrier report 1" },	
		};
		assertValidValues("name", customReports1); 
		
		assertValue("name", "Carrier report 1");
		execute("MyReport.createNew", "xava.keyProperty=name");
		setValue("name", "Carrier report 7");
		execute("MyReport.generatePdf");
		execute("ExtendedPrint.myReports");
		String [][] customReports2 = {
			{ "Carrier report 1", "Carrier report 1" },	
			{ "Carrier report 7", "Carrier report 7" }
		};
		assertValidValues("name", customReports2);
		assertValue("name", "Carrier report 7"); 
		execute("MyReport.remove", "xava.keyProperty=name");
		assertValidValues("name", customReports1);
		assertValue("name", "Carrier report 1");
		
		setValue("name", "Carrier report 1");
		execute("MyReport.generatePdf");
		assertNoErrors();
		
		execute("ExtendedPrint.myReports");
		assertValue("name", "Carrier report 1");
		assertValueInCollection("columns", 0, 0, "Calculated");
		execute("MyReport.columnUp", "row=1,viewObject=xava_view_columns");
		assertValueInCollection("columns", 0, 0, "Number");
		execute("MyReport.remove", "xava.keyProperty=name");
		assertMessage("Report 'Carrier report 1' removed"); 		
	}
	
	public void testRemoveColumnsInMyReport() throws Exception  { 
		execute("ExtendedPrint.myReports"); 
		assertCollectionRowCount("columns", 3); 
		assertValueInCollection("columns", 0, 0, "Calculated");  
		assertValueInCollection("columns", 1, 0, "Number");
		assertValueInCollection("columns", 2, 0, "Name");
		checkRowCollection("columns", 1);
		execute("MyReport.removeColumn", "viewObject=xava_view_columns");
		assertNoErrors();
		assertCollectionRowCount("columns", 2);
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Name");
		
		execute("MyReport.generatePdf");
		execute("ExtendedPrint.myReports");
		assertCollectionRowCount("columns", 2); 
		assertValueInCollection("columns", 0, 0, "Calculated");
		assertValueInCollection("columns", 1, 0, "Name");		
				
		execute("MyReport.removeColumn", "row=0,viewObject=xava_view_columns");
		assertNoErrors();
		assertCollectionRowCount("columns", 1);
		assertValueInCollection("columns", 0, 0, "Name");
		
		execute("MyReport.generatePdf");
		execute("ExtendedPrint.myReports"); 
		assertCollectionRowCount("columns", 1);
		assertValueInCollection("columns", 0, 0, "Name");
		execute("MyReport.remove", "xava.keyProperty=name");
	}

		
	public void testCarrierSelected() throws Exception{ 
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		execute("CarrierWithSections.fellowCarriersSelected");
		assertTrue(Is.empty(getValue("fellowCarriersSelected")));
		execute("Sections.change", "activeSection=1");
		checkRowCollection("fellowCarriersCalculated", 0);
		checkRowCollection("fellowCarriersCalculated", 1);
		execute("Sections.change", "activeSection=0");
		execute("CarrierWithSections.fellowCarriersSelected");
		assertTrue(getValue("fellowCarriersSelected").equalsIgnoreCase("DOS TRES")); 
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		execute("CarrierWithSections.fellowCarriersSelected");
		assertTrue(Is.empty(getValue("fellowCarriersSelected")));
	}
	
	public void testSetControllers() throws Exception { 
		String [] defaultActions = {
			"List.hideRows",
			"List.filter",
			"List.orderBy",
			"List.viewDetail",
			"List.sumColumn",
			"List.changeConfiguration",
			"List.changeColumnName", 
			"ListFormat.select", 
			"Print.generatePdf",
			"Print.generateExcel",
			"ExtendedPrint.myReports",
			"ImportData.importData", 
			"CRUD.new",
			"CRUD.deleteSelected",
			"CRUD.deleteRow", 
			"Carrier.translateAll",
			"Carrier.deleteAll",
			"CarrierWithSections.setTypicalController",
			"CarrierWithSections.setPrintController",
			"CarrierWithSections.setDefaultControllers",
			"CarrierWithSections.returnToPreviousControllers"
		};
		String [] printActions = {
			"List.hideRows",
			"List.filter",
			"List.orderBy",
			"List.viewDetail",
			"List.sumColumn",
			"List.changeConfiguration",
			"List.changeColumnName", 
			"ListFormat.select", 
			"Print.generatePdf",
			"Print.generateExcel",			
			"CarrierWithSections.setTypicalController",
			"CarrierWithSections.setPrintController",
			"CarrierWithSections.setDefaultControllers",
			"CarrierWithSections.returnToPreviousControllers"			
		};		
		String [] typicalActions = {
			"List.hideRows",
			"List.filter",
			"List.orderBy",
			"List.viewDetail",
			"List.sumColumn", 
			"List.changeConfiguration",
			"List.changeColumnName", 
			"ListFormat.select", 
			"Print.generatePdf",
			"Print.generateExcel",
			"ExtendedPrint.myReports",
			"ImportData.importData", 
			"CRUD.new",
			"CRUD.deleteSelected",
			"CRUD.deleteRow", 
			"CarrierWithSections.setTypicalController",
			"CarrierWithSections.setPrintController",
			"CarrierWithSections.setDefaultControllers",
			"CarrierWithSections.returnToPreviousControllers"			
		};		
		
		// Returning with returnToPreviousController
		assertActions(defaultActions); 
		execute("CarrierWithSections.setTypicalController");
		assertActions(typicalActions); 
		execute("CarrierWithSections.setPrintController");
		assertActions(printActions);
		execute("CarrierWithSections.returnToPreviousControllers");
		assertActions(typicalActions); 
		execute("CarrierWithSections.returnToPreviousControllers");
		assertActions(defaultActions);
		
		// Returning with setDefaultControllers()
		assertActions(defaultActions);
		execute("CarrierWithSections.setTypicalController");
		assertActions(typicalActions);
		execute("CarrierWithSections.setPrintController");
		assertActions(printActions);
		execute("CarrierWithSections.setDefaultControllers");
		assertActions(defaultActions);
		execute("CarrierWithSections.returnToPreviousControllers");
		assertActions(defaultActions); // Verifies that setDefaultControllers empties the stacks
		
	}
	
}
