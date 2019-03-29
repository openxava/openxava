package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *  
 * @author Javier Paniza
 */

public class ServiceExpensesTest extends ModuleTestBase {
	
	public ServiceExpensesTest(String testName) {
		super(testName, "ServiceExpenses");		
	}
		
	public void testSeachKeysEnumsAndDescriptionsListInElementCollection() throws Exception { 
		execute("CRUD.new");
		assertLabelInCollection("expenses", 4, "Receptionist");
		setValue("description", "JUNIT EXPENSES");
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		assertValueInCollection("expenses", 0, "invoice.year", "2007"); 
		assertValueInCollection("expenses", 0, "invoice.amount", "");
		setValueInCollection("expenses", 0, "invoice.number", "2");
		assertValueInCollection("expenses", 0, "invoice.amount", "1,730.00");
		
		String [][] statusValidValues = {
			{ "", "" },
			{ "0", "Paid" },
			{ "1", "Pending" },
			{ "2", "Rejected" }
		};		
		assertValidValuesInCollection("expenses", 0, "status", statusValidValues);
		setValueInCollection("expenses", 0, "status", "1");
		
		String [][] receptionistValidValues = {
			{ "", "" },
			{ "2", "ANTONIOA" },
			{ "3", "JUAN" },
			{ "1", "PEPE" },
			{ "4", "PEPE" }
		};
		assertValidValuesInCollection("expenses", 0, "receptionist.oid", receptionistValidValues); 
		setValueInCollection("expenses", 0, "receptionist.oid", "3");		
		
		assertValueInCollection("expenses", 1, "invoice.year", "");
		assertValueInCollection("expenses", 1, "invoice.number", "");
		assertValueInCollection("expenses", 1, "invoice.amount", "");
		execute("Reference.search", "keyProperty=expenses.1.invoice.number");
		execute("ReferenceSearch.choose", "row=0");
		assertValueInCollection("expenses", 1, "invoice.year", "2007"); 
		assertValueInCollection("expenses", 1, "invoice.number", "1");
		assertValueInCollection("expenses", 1, "invoice.amount", "790.00");

		setValueInCollection("expenses", 1, "status", "2");
		setValueInCollection("expenses", 1, "receptionist.oid", "4");		
		
		execute("CRUD.save");
		
		assertValue("description", "");
		assertValueInCollection("expenses", 0, "invoice.year", "");
		assertValueInCollection("expenses", 0, "invoice.number", "");
		assertValueInCollection("expenses", 0, "invoice.amount", "");
		assertValueInCollection("expenses", 0, "status", "");
		assertValueInCollection("expenses", 0, "receptionist.oid", "");
		assertValueInCollection("expenses", 1, "invoice.year", "");
		assertValueInCollection("expenses", 1, "invoice.number", "");
		assertValueInCollection("expenses", 1, "invoice.amount", "");
		assertValueInCollection("expenses", 1, "status", "");
		assertValueInCollection("expenses", 1, "receptionist.oid", "");		
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");

		assertValue("description", "JUNIT EXPENSES");
		assertValueInCollection("expenses", 0, "invoice.year", "2007");
		assertValueInCollection("expenses", 0, "invoice.number", "2");
		assertValueInCollection("expenses", 0, "invoice.amount", "1,730.00");
		assertValueInCollection("expenses", 0, "status", "1");
		assertValueInCollection("expenses", 0, "receptionist.oid", "3");
		assertValueInCollection("expenses", 1, "invoice.year", "2007");
		assertValueInCollection("expenses", 1, "invoice.number", "1");
		assertValueInCollection("expenses", 1, "invoice.amount", "790.00");
		assertValueInCollection("expenses", 1, "status", "2");
		assertValueInCollection("expenses", 1, "receptionist.oid", "4");		
		
		execute("CRUD.delete");
		assertNoErrors();
		
		execute("CRUD.new");
		assertNoErrors();
		setValueInCollection("expenses", 0, "invoice.year", "2015");
		assertNoErrors();
		setValueInCollection("expenses", 0, "invoice.number", "15"); // It does not exist
		assertDialog();
		assertAction("ReferenceSearch.choose");
	}
	
	public void testAdding3RowsWithDescriptionsListAndTextFieldInElementCollection_calculationPropertyDependsOnSummationInElementCollection() throws Exception {  
		getWebClient().getOptions().setCssEnabled(true);
		execute("CRUD.new");
		assertComboOpens(0, 1);
		setValueInCollection("expenses", 0, 0, "2016");
		assertComboOpens(1, 2); 
		setValueInCollection("expenses", 1, 0, "2016");
		assertComboOpens(2, 7); 
	
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		setValueInCollection("expenses", 0, "invoice.number", "1");
		assertValueInCollection("expenses", 0, "invoice.amount", "790.00");
		assertTotalInCollection("expenses", 0, "invoice.amount", "790.00");
		assertTotalInCollection("expenses", 1, "invoice.amount", "118.50");
	}
	
	private void assertComboOpens(int row, int uiId) throws Exception {
		HtmlElement editor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ServiceExpenses__reference_editor_expenses___" + row + "___receptionist");
		HtmlElement handler = editor.getElementsByTagName("i").get(0);
		assertTrue(!getHtmlPage().getHtmlElementById("ui-id-" + uiId).isDisplayed());
		handler.click();
		assertTrue(getHtmlPage().getHtmlElementById("ui-id-" + uiId).isDisplayed());		
	}
			
}
