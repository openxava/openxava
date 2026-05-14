package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.util.*;

/**
 * This tests without @View. 
 *  
 * @author Javier Paniza
 */

public class WorkCostTest extends WorkCostTestBase {

	
	public WorkCostTest(String testName) {
		super(testName, "WorkCost", "invoices");		
	}
	
	public void testCalculationInCollection() throws Exception {  
		execute("CRUD.new"); 
		setValue("description", "SOMETHING"); 
		assertEquals(1, getHtmlPage().getElementsByName("ox_openxavatest_WorkCost__profitPercentage").size()); 
		execute("Collection.new", "viewObject=xava_view_invoices");
		
		assertValue("vatPercentage", "16");
		setValue("hours", "250");
		assertValue("total", "0.00");
		
		setValue("worker.nickName", "john");
		assertValue("worker.hourPrice", "20.00");
		assertValue("total", "5,800.00"); 
		
		setValue("tripCost", "299.9");
		assertValue("total", "6,147.88");
		
		setValue("discount", "1200");
		assertValue("total", "4,755.88");
		
		setValue("vatPercentage", "21");
		assertValue("total", "4,960.88");
	}
	
	public void testCalculationInCollectionInSpanish() throws Exception { 
		setLocale("es");
	
		execute("List.viewDetail", "row=0");
		assertTotals("0,00", "10", "0,00", "0,00"); 
		
		execute("Collection.add", "viewObject=xava_view_invoices");
		checkAll();
		execute("AddToCollection.add");
		
		assertValueInCollection("invoices", 0, "total",  "52,20");
		assertValueInCollection("invoices", 1, "total",  "78,88");
		assertTotals("131,08", "10", "13,11", "144,19");
				
		setValue("profitPercentage", "15");
		assertTotals("131,08", "15", "19,66", "150,74");
		
		// Restoring
		setValue("profitPercentage", "10");
		checkAllCollection("invoices");
		execute("Collection.removeSelected", "viewObject=xava_view_invoices");
		assertCollectionRowCount("invoices", 0);
	}
	
	public void testCalculationAndEditableTotalsInCollections_notLoseChangesMessageWhenCalculationProperties_sumInCorrectColumnForCollectionFromModelWithTwoReferences() throws Exception {
		// Execute the base test
		super.testCalculationAndEditableTotalsInCollections();

		execute("Navigation.first");
		execute("CRUD.save");

		// Setup confirm handler to verify no confirmation dialog appears
		MessageConfirmHandler confirmHandler = new MessageConfirmHandler();
		getWebClient().setConfirmHandler(confirmHandler);

		// Verify that no confirmation dialog appears when creating a new record
		execute("CRUD.new");
		confirmHandler.assertNoMessage();

		// Sum for total in total column
		assertTextInVisibleCell("ox_openxavatest_WorkCost__invoices", 0, 9, "Total");
		assertTextInVisibleCell("ox_openxavatest_WorkCost__invoices", 1, 9, "0.00");
	}

	private void assertTextInVisibleCell(String collection, int row, int column, String text) throws Exception {
		HtmlTable table = getHtmlPage().getHtmlElementById(collection);
		HtmlTableCell cell = getVisibleCell(table, row, column);
		assertEquals(text, getTextIgnoringNotVisible(cell));
	}
	
	private HtmlTableCell getVisibleCell(HtmlTable table, int row, int column) {
		int visibleColumn = 0;
		for (HtmlTableCell cell: table.getRow(row).getCells()) {
			if (!isDisplayed(cell)) continue;
			if (visibleColumn++ == column) return cell;
		}
		fail("Visible column not found: " + column);
		return null;
	}
	
	private String getTextIgnoringNotVisible(DomNode node) {
		if (node instanceof DomElement && !isDisplayed((DomElement) node)) return "";
		if (!node.hasChildNodes()) return node.getTextContent();
		StringBuffer result = new StringBuffer();
		for (DomNode child: node.getChildren()) {
			String childText = getTextIgnoringNotVisible(child);
			if (Is.emptyString(childText)) continue;
			if (result.length() > 0) result.append(' ');
			result.append(childText);
		}
		return result.toString().replace('\u00a0', ' ').replaceAll("\\s+", " ").trim();
	}
	
	private boolean isDisplayed(DomElement element) {
		return element.isDisplayed();
	}
	
}
