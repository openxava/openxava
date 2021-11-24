package org.openxava.test.tests;

import java.text.*;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Jeromy Altuna
 */
public class ServiceExpenses2Test extends ModuleTestBase {
	
	public ServiceExpenses2Test(String testName) {
		super(testName, "ServiceExpenses2");		
	}
	
	public void testEnumsWithRequiedValueInElementCollection_defaultValueCalculatorWithNoFromInElementCollectionWhenSearchingReferenceWithDialog() throws Exception {		
		assertEnumsWithRequiredValueInElementCollection();
		assertDefaultValueCalculatorWithNoFromInElementCollectionWhenSearchingReferenceWithDialog(); 
	}

	private void assertDefaultValueCalculatorWithNoFromInElementCollectionWhenSearchingReferenceWithDialog() throws Exception { 
		execute("CRUD.new");
		assertNumberOfRowsShownInElementCollection(1);
		execute("Reference.search", "keyProperty=expenses.0.invoice.number");
		execute("ReferenceSearch.cancel");
		assertNumberOfRowsShownInElementCollection(1);
		assertValueInCollection("expenses", 0, "invoice.year", "");
		assertValueInCollection("expenses", 0, "invoice.number", "");
		assertValueInCollection("expenses", 0, "invoice.amount", "");
		assertValueInCollection("expenses", 0, "date", "");
		
		execute("Reference.search", "keyProperty=expenses.0.invoice.number");
		execute("ReferenceSearch.choose", "row=1");
		assertValueInCollection("expenses", 0, "invoice.year", "2007");
		assertValueInCollection("expenses", 0, "invoice.number", "2");
		assertValueInCollection("expenses", 0, "invoice.amount", "1,730.00");
		assertValueInCollection("expenses", 0, "date", getCurrentDate());
		assertNumberOfRowsShownInElementCollection(2);
		
		setValueInCollection("expenses", 1, "date", "1/1/2022");
		assertValueInCollection("expenses", 1, "date", "1/1/2022"); 
	}

	private void assertEnumsWithRequiredValueInElementCollection() throws Exception {
		execute("CRUD.new");
		assertNumberOfRowsShownInElementCollection(1);
		setValue("description", "JUNIT EXPENSE2");
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		setValueInCollection("expenses", 0, "invoice.number", "1");
		assertValueInCollection("expenses", 0, "invoice.amount", "790.00");
		assertValueInCollection("expenses", 0, "date", getCurrentDate()); 
		
		String [][] statusValidValues = {
			{ "", "" },
			{ "0", "Paid" },
			{ "1", "Pending" },
			{ "2", "Rejected" }
		};		
		assertValidValuesInCollection("expenses", 0, "status", statusValidValues);
		setValueInCollection("expenses", 0, "receptionist.oid", "3");
		assertNumberOfRowsShownInElementCollection(2);
		execute("CRUD.save");
		assertError("Value for Status in Service expense 2 is required");
		setValueInCollection("expenses", 0, "status", "1");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("description", "JUNIT EXPENSE2");
		assertNumberOfRowsShownInElementCollection(2);
		setValueInCollection("expenses", 1, "status", "2");
		assertNumberOfRowsShownInElementCollection(3);
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private void assertNumberOfRowsShownInElementCollection(int number) {
		String elementId = "ox_OpenXavaTest_ServiceExpenses2__expenses___" + number;
		HtmlElement tr = getHtmlPage().getHtmlElementById(elementId); 
		assertTrue(tr.getAttribute("style").contains("display: none"));		
	}
	
	private String getCurrentDate() { 
		DateFormat df = new SimpleDateFormat("M/d/yyyy"); 
		return df.format(new java.util.Date());
	}

}
