package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;

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
	
	public void testAdding3RowsWithDescriptionsListAndTextFieldInElementCollection_calculationPropertyDependsOnSummationInElementCollection_sumUsedInCalculationAlwaysIncluded() throws Exception {  
		getWebClient().getOptions().setCssEnabled(true);
		execute("CRUD.new");
		assertComboOpens(0, 1); 
		setValueInCollection("expenses", 0, 0, "2016");
		assertComboOpens(1, 2); 
		setValueInCollection("expenses", 1, 0, "2016");
		assertComboOpens(2, 7); 
		
		assertCollectionTotals(); 		
		
		execute("ServiceExpenses.removeColumnSum");
		resetModule();
		assertCollectionTotals();
	}

	private void assertCollectionTotals() throws Exception {
		setValueInCollection("expenses", 0, "invoice.year", "2007");
		setValueInCollection("expenses", 0, "invoice.number", "1");
		assertValueInCollection("expenses", 0, "invoice.amount", "790.00");
		assertTotalInCollection("expenses", 0, "invoice.amount", "790.00");
		assertTotalInCollection("expenses", 1, "invoice.amount", "118.50");
	}
	
	private void assertComboOpens(int row, int uiId) throws Exception {
        HtmlElement editor = getHtmlPage().getHtmlElementById("ox_openxavatest_ServiceExpenses__reference_editor_expenses___" + row + "___receptionist");
        // Before opening, there should be no visible suggestions list
        java.util.List<DomElement> listsBefore = (java.util.List<DomElement>) (java.util.List<?>) getHtmlPage().getByXPath("//ul[contains(@class,'ui-menu')]");
        boolean anyVisibleBefore = false;
        for (DomElement ul : listsBefore) { if (ul.isDisplayed()) { anyVisibleBefore = true; break; } }
        assertFalse(anyVisibleBefore);

        // Click on the open icon for the descriptions list (mdi-menu-down)
        HtmlElement openIcon = null;
        for (DomElement i : editor.getElementsByTagName("i")) {
            String cls = i.getAttribute("class");
            if (cls != null && cls.contains("mdi-menu-down")) { openIcon = (HtmlElement) i; break; }
        }
        if (openIcon == null) { // Fallback to the first <i> if specific class not found
            openIcon = editor.getElementsByTagName("i").get(0);
        }
        openIcon.click();

        // Give time for the remote suggestions to load
        Thread.sleep(700);

        // After opening, there should be a visible list with items
        java.util.List<DomElement> listsAfter = (java.util.List<DomElement>) (java.util.List<?>) getHtmlPage().getByXPath("//ul[contains(@class,'ui-menu')]");
        DomElement visibleList = null;
        for (DomElement ul : listsAfter) { if (ul.isDisplayed()) { visibleList = ul; break; } }
        assertNotNull(visibleList);
        assertTrue(visibleList.getElementsByTagName("li").size() > 1); // Because it could be "ERROR" that count as one entry

        // Close the combo to leave the UI clean
        HtmlElement closeIcon = null;
        for (DomElement i : editor.getElementsByTagName("i")) {
            String cls = i.getAttribute("class");
            if (cls != null && cls.contains("mdi-menu-up")) { closeIcon = (HtmlElement) i; break; }
        }
        if (closeIcon != null) {
            closeIcon.click();
            Thread.sleep(300);
            // Assert that no suggestions list is visible after closing
            java.util.List<DomElement> listsClosed = (java.util.List<DomElement>) (java.util.List<?>) getHtmlPage().getByXPath("//ul[contains(@class,'ui-menu')]");
            boolean anyVisible = false;
            for (DomElement ul : listsClosed) { if (ul.isDisplayed()) { anyVisible = true; break; } }
            assertFalse(anyVisible);
        }
    }
			
}
