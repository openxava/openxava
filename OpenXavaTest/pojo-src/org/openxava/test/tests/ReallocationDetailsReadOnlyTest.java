package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;


/**
 * 
 * @author Javier Paniza
 */

public class ReallocationDetailsReadOnlyTest extends ModuleTestBase {
	
	public ReallocationDetailsReadOnlyTest(String testName) {
		super(testName, "ReallocationDetailsReadOnly");		
	}	
	
	public void testReadOnlyElementCollections() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("description", "THE BIG REALLOCATION");
		assertNoErrors();
		
		assertCollectionRowCount("details", 3);

		assertValueInCollection("details", 0, "place", "VALENCIA OFFICE");
		assertValueInCollection("details", 0, "product.number", "1");
		assertValueInCollection("details", 0, "product.description", "MULTAS DE TRAFICO");
		assertValueInCollection("details", 0, "product.unitPrice", "11.00");
		
		assertValueInCollection("details", 2, "place", "CASA EN MICHIGAN");
		assertValueInCollection("details", 2, "product.number", "3");
		assertValueInCollection("details", 2, "product.description", "XAVA");
		assertValueInCollection("details", 2, "product.unitPrice", "0.00");
		
		assertNoEditableInCollection("details", 0, "place");
		assertNoEditableInCollection("details", 0, "product.number");
		assertNoEditableInCollection("details", 0, "product.description");
		assertNoEditableInCollection("details", 0, "product.unitPrice");
		 
		assertNoEditableInCollection("details", 2, "place");
		assertNoEditableInCollection("details", 2, "product.number");
		assertNoEditableInCollection("details", 2, "product.description");
		assertNoEditableInCollection("details", 2, "product.unitPrice");
		
		HtmlTable table = (HtmlTable) getHtmlPage().getElementById("ox_OpenXavaTest_ReallocationDetailsReadOnly__details");
		assertEquals(5, table.getRowCount()); 
		assertEquals(5, table.getRow(4).getCells().size());  
		String html = getHtml(); 
		assertFalse(html.contains("elementCollectionEditor.removeRow("));
		assertFalse(html.contains("Reference.search"));
		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("description", "THE BIG REALLOCATION");
		assertCollectionRowCount("details", 3);
		assertValueInCollection("details", 0, "place", "VALENCIA OFFICE");
		assertValueInCollection("details", 2, "place", "CASA EN MICHIGAN");		
	}
	
	
	
}
