package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class ReallocationTest extends ModuleTestBase {
	
	public ReallocationTest(String testName) {
		super(testName, "Reallocation");		
	}	

	public void testDefaultValueCalculatorForReferencesInElementCollections_booleansInElementCollections() throws Exception {
		assertDefaultValueCalculatorForReferencesInElementCollections(); 
		assertBooleansInElementCollections();		
	}

	private void assertDefaultValueCalculatorForReferencesInElementCollections() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("details", 3);
		assertValueInCollection("details", 0, "place", "Valencia Office"); // To verify @Editor
		setValueInCollection("details", 3, "place", "4MATIC");
		assertValueInCollection("details", 3, "product.number", "1");
		assertValueInCollection("details", 3, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 3, "product.unitPrice", "11.00");

		execute("CRUD.new");
		assertNoErrors();
		assertCollectionRowCount("details", 0);
		setValueInCollection("details", 0, "place", "THE PLACE");
		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "product.unitPrice", "11.00");		
		
		setValueInCollection("details", 1, "place", "THE SECOND PLACE");
		assertValueInCollection("details", 1, "product.number", "1");
		assertValueInCollection("details", 1, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 1, "product.unitPrice", "11.00");		
		setValueInCollection("details", 1, "product.number", "2");
		assertValueInCollection("details", 1, "product.description", "IBM ESERVER ISERIES 270");
		assertValueInCollection("details", 1, "product.unitPrice", "20.00");
		
		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "product.unitPrice", "11.00");
		
		setValueInCollection("details", 2, "place", "THE THIRD PLACE");
		assertValueInCollection("details", 2, "product.number", "1");
		assertValueInCollection("details", 2, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 2, "product.unitPrice", "11.00");
	}

	private void assertBooleansInElementCollections() throws Exception {
		execute("Navigation.first");
		assertCollectionRowCount("details", 3);
		assertValueInCollection("details", 0, "done", "true");
		assertValueInCollection("details", 1, "done", "false");
		assertValueInCollection("details", 2, "done", "false");
		
		setValueInCollection("details", 0, "done", "false");
		setValueInCollection("details", 2, "done", "true");
		execute("CRUD.save");
		
		execute("Navigation.first");
		assertValueInCollection("details", 0, "done", "false");
		assertValueInCollection("details", 1, "done", "false");
		assertValueInCollection("details", 2, "done", "true");		
		
		setValueInCollection("details", 0, "done", "true");
		setValueInCollection("details", 2, "done", "false");
		execute("CRUD.save");
	}
	
	public void testElementCollectionAddRowsProperly() throws Exception {
		execute("CRUD.new");
		setValueInCollection("details", 0, "product.number", "1");
		setValueInCollection("details", 1, "product.number", "2");
		assertCollectionRowCount("details", 2);
		execute("Reference.search", "keyProperty=details.2.product.number");
		execute("ReferenceSearch.choose", "row=3");
		assertCollectionRowCount("details", 3);
		setValueInCollection("details", 3, "product.number", "3");
		execute("Reference.search", "keyProperty=details.4.product.number");
		execute("ReferenceSearch.choose", "row=4");
		assertCollectionRowCount("details", 5);
	}
	
	public void testBooleanInElementCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("details", 3);
		String place = getValueInCollection("details", 2, "place");
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_openxavatest_Reallocation__details___2"); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("i").get(0);
		removeIcon.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("details", 2);
		setValueInCollection("details", 2, "product.number", "3");
		setValueInCollection("details", 2, "place", place);
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("details", 3);
	}
	
}
