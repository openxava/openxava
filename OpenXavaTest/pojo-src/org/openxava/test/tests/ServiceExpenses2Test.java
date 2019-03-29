package org.openxava.test.tests;

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
	
	public void testEnumsWithRequiedValueInElementCollection() throws Exception {		
		execute("CRUD.new");
		assertNumberOfRowsShownInElementCollection(1);
		setValue("description", "JUNIT EXPENSE2");
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		setValueInCollection("expenses", 0, "invoice.number", "1");
		assertValueInCollection("expenses", 0, "invoice.amount", "790.00");
		
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
}
