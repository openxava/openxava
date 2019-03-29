package org.openxava.test.tests;

/**
 * @author Javier Paniza
 */

public class Invoice2002Test extends CustomizeListTestBase {
	
	
	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"ImportData.importData", 
		"CRUD.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow",
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn",
		"List.changeConfiguration",
		"List.changeColumnName", 
		"ListFormat.select", 
		"Invoice2002.changeListTitle"
	};

	public Invoice2002Test(String testName) {
		super(testName, "Invoice2002");		
	}
	
	public void testGeneratePdfWithFilter() throws Exception {
		execute("Print.generatePdf"); 		
		assertContentTypeForPopup("application/pdf"); 		
	}
	
	public void testGenerateExcelWithFilter() throws Exception {
		execute("Print.generateExcel");		
		assertContentTypeForPopup("text/x-csv");		
	}	
	
	public void testChangeTabTitle() throws Exception {
		assertListTitle("Invoices report of year 2,002"); 
		execute("Invoice2002.changeListTitle");
		assertListTitle("The little invoices of 2002");
	}
	
	public void testCustomizeListWithFilterAndBaseCondition() throws Exception {
		assertValueInList(0, 0, "2002"); 
		moveColumn(0, 1);
		assertValueInList(0, 1, "2002"); 	
		execute("List.filter");
		assertValueInList(0, 1, "2002");
		// Restoring
		execute("List.addColumns");
		execute("AddColumns.restoreDefault"); 
	}

	public void testFilterWithConverterAndFilter() throws Exception {
		assertNoErrors();
		String [] comparators = { "=", "=", "=", "=", "="};
		String [] condition = { "", "", "", "", "true"	};
		setConditionComparators(comparators);
		setConditionValues(condition); 
		execute("List.filter"); 
		assertNoErrors();
	}	
	
	public void testOnInitAction_IRequestFilter_BaseContextFilter() throws Exception {
		assertActions(listActions);	
		assertListTitle("Invoices report of year 2,002");
		int count = getListRowCount();
		for (int i = 0; i < count; i++) {
			assertValueInList(i, "year", "2002"); 	
		}		
	}
	
	public void testCalculatedPropertiesInListMode() throws Exception {
		assertActions(listActions); 
		int rowCount = getListRowCount();
		for (int i = 0; i < rowCount; i++) {
			String number = getValueInList(i, "number");
			if ("1".equals(number)) {
				assertValueInList(i, "amountsSum", "2,500.00");
				assertValueInList(i, "vat", "400.00");
				assertValueInList(i, "detailsCount", "2");
				assertValueInList(i, "importance", "NORMAL"); // UpperCaseFormatter is used
				return;
			}			
		}		
		fail("It must to exists invoice 2002/1 for run this test"); 
	}	
							
}
