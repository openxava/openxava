package org.openxava.test.tests;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * This test when there is an explicit @View.
 * 
 * @author Javier Paniza
 */

public class WorkCostOrderedInvoicesTest extends WorkCostTestBase {

	public WorkCostOrderedInvoicesTest(String testName) {
		super(testName, "WorkCostOrderedInvoices", "invoices");
	}		
	
	public void testCutPasteOrderColumnCollection_printPdfNotInModelCollections() throws Exception { 
		assertListRowCount(1); 
		execute("List.viewDetail", "row=0");
		assertValue("description", "CAR SERVICE");
		assertNoAction("Print.generatePdf"); 
		
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_invoices");
		assertWarning("No rows selected to cut");
		
		assertCollectionRowCount("invoices", 0); 
		execute("Collection.add", "viewObject=xava_view_invoices");
		checkAll();
		execute("AddToCollection.add");
		assertCollectionRowCount("invoices", 2);
		assertValueInCollection("invoices", 0, 0, "1");
		assertValueInCollection("invoices", 1, 0, "2");

		assertNoAction("CollectionCopyPaste.paste");
		assertNoCutRowStyle(0);
		execute("CollectionCopyPaste.cut", "row=0,viewObject=xava_view_invoices");
		assertCutRowStyle(0);
		assertNoAction("CollectionCopyPaste.paste");
		assertMessage("1 row cut from Invoices");
		assertCollectionRowCount("invoices", 2);
		execute("CollectionCopyPaste.cut", "row=0,viewObject=xava_view_invoices");
		assertMessage("1 row cut from Invoices"); // Does not accumulate rows
		
		execute("CRUD.new");
		setValue("description", "MOTO SERVICE");
		assertCollectionRowCount("invoices", 0);
		assertTotals("0.00", "", "0.00", "0.00");
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_invoices");
		assertNoAction("CollectionCopyPaste.paste");
		assertMessage("1 row pasted into Invoices");
		assertCollectionRowCount("invoices", 1);
		assertValueInCollection("invoices", 0, 0, "1");
		assertTotals("52.20", "", "0.00", "52.20");
		
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_invoices");
		assertCollectionRowCount("invoices", 0);
		execute("Navigation.first");
		assertValue("description", "CAR SERVICE");
		assertNoAction("CollectionCopyPaste.paste");
		assertCollectionRowCount("invoices", 1);
		assertTotals("78.88", "10", "7.89", "86.77");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_invoices");
		assertCollectionRowCount("invoices", 0);
		execute("Mode.list");		
		assertListRowCount(2);
		execute("List.orderBy", "property=description");
		assertValueInList(1, 0, "MOTO SERVICE");
		execute("CRUD.deleteRow", "row=1");
		assertListRowCount(1);
	}
	
	private void assertCutRowStyle(int row) { 
		assertTrue(hasCutRowStyle(row));
	}
	
	private void assertNoCutRowStyle(int row) { 
		assertFalse(hasCutRowStyle(row));
	}
	
	private boolean hasCutRowStyle(int row) { 
		HtmlElement tr = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_WorkCostOrderedInvoices__invoices___" + row);
		return tr.getAttribute("class").contains("ox-cut-row");
	}
	
}
