package org.openxava.test.tests;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailsWithTotalsTest extends CustomizeListTestBase { 
	
	public InvoiceDetailsWithTotalsTest(String testName) {
		super(testName, "InvoiceDetailsWithTotals");		
	}
	
	public void testTotalsInCollection() throws Exception { 	
		execute("List.viewDetail", "row=0");		
		
		assertTotalsInCollection("details");   		
		assertTotalsInCollection("calculatedDetails");		
		
		assertNoAction("List.removeColumnSum");
		assertNoAction("Collection.removeColumnSum"); 
		
		execute("Navigation.next");

		assertValueInCollection("details", 0, "deliveryDate", "7/26/2004");
		assertTotalInCollection("details", "deliveryDate", "7/26/2004"); 		

		assertValueInCollection("details", 0, "product.unitPrice", "11.00");
		assertTotalInCollection("details", "product.unitPrice", "11.00");		
		
		assertValueInCollection("details", 0, "amount", "11.00");
		assertTotalInCollection("details",    "amount", "11.00");
		assertTotalInCollection("details", 1, "amount",  "4.62");
		assertTotalInCollection("details", 2, "amount", "15.62");
		
		execute("Navigation.previous");
		
		removeColumn("details", 2);  
		
		assertTotalInCollection("details", 1, 3,   "400.00"); 
		assertTotalInCollection("details", 2, 3, "2,900.00");		
		
		execute("Print.generatePdf", "viewObject=xava_view_details"); 
		assertContentTypeForPopup("application/pdf");
		
		removeColumn("details", 3);
		assertTotalInCollection("details", "deliveryDate", "12/15/2010");			
		
		execute("List.addColumns", "collection=details");
		execute("AddColumns.restoreDefault");
				
		assertTotalUpdatedWhenDependsPropertyChanged(); 
	}
	
	private void assertTotalUpdatedWhenDependsPropertyChanged() throws Exception {  
		assertTotalInCollection("details", 1, "amount",   "400.00"); 
		assertTotalInCollection("details", 2, "amount", "2,900.00");
		assertTotalInCollection("calculatedDetails", 1, "amount",   "400.00"); 
		assertTotalInCollection("calculatedDetails", 2, "amount", "2,900.00");
		
		setValue("vatPercentage", "17");
		
		assertTotalInCollection("details", 1, "amount",   "425.00"); 
		assertTotalInCollection("details", 2, "amount", "2,925.00");
		assertTotalInCollection("calculatedDetails", 1, "amount",   "425.00"); 
		assertTotalInCollection("calculatedDetails", 2, "amount", "2,925.00");
		
		assertTotalsInFrameOfCollection("details", "(2)    Delivery date: 12/15/2010    Product unit price sum: 20.00    Amounts sum: 2,500.00    V.A.T.: 425.00    Total: 2,925.00", false);      
		assertTotalsInFrameOfCollection("calculatedDetails", "(2)    Delivery date: 12/15/2010    Product unit price sum: 20.00    Amounts sum: 2,500.00    V.A.T.: 425.00    Total: 2,925.00", true); 
	}

	public void testTotalsAndAddActionInCollectionFrame() throws Exception {
		getWebClient().getOptions().setCssEnabled(true);
		reload();

		execute("List.viewDetail", "row=0");
		execute("List.sumColumn", "property=quantity,collection=details");
		assertTotalsInFrameOfCollection("details", "(2)    Delivery date: 12/15/2010    Product unit price sum: 20.00    Amounts sum: 2,500.00    V.A.T.: 400.00    Total: 2,900.00    Sum of Quantity: 150", false);          
		execute("List.removeColumnSum", "property=quantity,collection=details");
		assertTotalsInFrameOfCollection("calculatedDetails", "(2)    Delivery date: 12/15/2010    Product unit price sum: 20.00    Amounts sum: 2,500.00    V.A.T.: 400.00    Total: 2,900.00", true);  
		
		// Calling to a collection action does not show the header
		HtmlElement header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_detailsheader"); 
		assertEquals("", header.asNormalizedText());
		execute("List.filter", "collection=details");
		assertEquals("", header.asNormalizedText());
	}
		
	public void testFrameTotalsUpdated() throws Exception {  
		execute("CRUD.new");
		setValue("year", "2004");
		setValue("number", "10");
		execute("CRUD.refresh"); 
		assertTotalInCollection("details", 2, "amount", "1,403.02"); 
		hideCollection("details");
		HtmlElement header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_detailsheader"); 
		assertTrue(header.asNormalizedText().endsWith("1,403.02"));  
		showCollection("details");
		execute("List.orderBy", "property=quantity,collection=details"); 
		execute("Collection.edit", "row=2,viewObject=xava_view_details"); 
		setValue("quantity", "65");
		execute("Collection.save");		
		assertTotalInCollection("details", 2, "amount", "1,390.04"); 
		hideCollection("details");
		header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_detailsheader"); 
		assertTrue("Unexpected end: " + header.asNormalizedText(),  header.asNormalizedText().endsWith("1,390.04"));
		showCollection("details");
		execute("Collection.edit", "row=2,viewObject=xava_view_details"); 
		setValue("quantity", "66");
		execute("Collection.save");
		assertTotalInCollection("details", 2, "amount", "1,403.02");
		hideCollection("details");
		header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_detailsheader"); 
		assertTrue("Unexpected end: " + header.asNormalizedText(),  header.asNormalizedText().endsWith("1,403.02"));
		execute("Navigation.previous");
		header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_detailsheader"); 
		assertTrue("Unexpected end: " + header.asNormalizedText(), !header.asNormalizedText().endsWith("1,403.02"));
		showCollection("details");
	}	
	
	private void assertTotalsInFrameOfCollection(String collection, String totals, boolean addAction) throws Exception {
		HtmlElement header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_" + collection + "header"); 
		assertEquals("", header.asNormalizedText());
		hideCollection(collection); 
		assertEquals(totals, header.asNormalizedText());		
		reload(); 
		header = getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_" + collection + "header"); 
		assertEquals(totals, header.asNormalizedText());
		if (addAction) assertTrue(header.asXml().contains("executeAction('openxavatest', 'InvoiceDetailsWithTotals', '', false, 'Collection.new', 'viewObject=xava_view_" + collection));
		else assertTrue(!header.asXml().contains("executeAction('openxavatest', 'InvoiceDetailsWithTotals', '', false, 'Collection.new', 'viewObject=xava_view_" + collection));
		showCollection(collection);
	}
	
	private void showCollection(String collection) throws Exception { 
		clickOn(collection, "show");
	}
	
	private void hideCollection(String collection) throws Exception { 
		clickOn(collection, "hide");
	}
	
	private void clickOn(String collection, String button) throws Exception { 
		HtmlElement link = (HtmlElement) getHtmlPage().getHtmlElementById("ox_openxavatest_InvoiceDetailsWithTotals__frame_" + collection + button).getChildElements().iterator().next(); 
		link.click();				
		waitAJAX(); 
	}
	
	private void assertTotalsInCollection(String collection) throws Exception {
		assertCollectionRowCount(collection, 2);
		
		assertValueInCollection(collection, 0, "deliveryDate", "11/11/2011");
		assertValueInCollection(collection, 1, "deliveryDate", "12/15/2010");
		assertTotalInCollection(collection, "deliveryDate", "12/15/2010"); 		

		assertValueInCollection(collection, 0, "product.unitPrice", "20.00");
		assertValueInCollection(collection, 1, "product.unitPrice", "0.00");
		assertTotalInCollection(collection, "product.unitPrice", "20.00");		
		
		assertValueInCollection(collection, 0, "amount", "2,000.00");
		assertValueInCollection(collection, 1, "amount",   "500.00");
		assertTotalInCollection(collection,    "amount", "2,500.00");
		assertTotalInCollection(collection, 1, "amount",   "400.00"); 
		assertTotalInCollection(collection, 2, "amount", "2,900.00");
		
		// Labels
		assertTotalInCollection(collection, "product.description", "Product unit price sum");
		assertTotalInCollection(collection, "quantity", ""); 
		assertTotalInCollection(collection, 1, "quantity", "V.A.T.");
		assertTotalInCollection(collection, 2, "quantity", "Total");
	}
	
}
