package org.openxava.test.tests;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceCalculatedDetailsTest extends CustomizeListTestBase { 
	
	public InvoiceCalculatedDetailsTest(String testName) {
		super(testName, "InvoiceCalculatedDetails");		
	}
	
	public void testSumInCalculatedCollection() throws Exception { 	
		execute("CRUD.new");
		setValue("year", "2004");
		setValue("number", "9");
		execute("CRUD.refresh");
		
		assertTotals("", "", "4,396.00"); 
		
		assertNoAction("CollectionTotals.sumColumn", "property=amount,collection=calculatedDetails");
		execute("CollectionTotals.removeColumnSum", "property=amount,collection=calculatedDetails");
		assertAction("CollectionTotals.sumColumn", "property=amount,collection=calculatedDetails");
		assertNoAction("CollectionTotals.removeColumnSum", "property=amount,collection=calculatedDetails");
		assertTotals("", "", "");
		
		assertNoAction("CollectionTotals.removeColumnSum", "property=quantity,collection=calculatedDetails");
		execute("CollectionTotals.sumColumn", "property=quantity,collection=calculatedDetails");
		assertAction("CollectionTotals.removeColumnSum", "property=quantity,collection=calculatedDetails");
		assertNoAction("CollectionTotals.sumColumn", "property=quantity,collection=calculatedDetails");
		assertTotals("", "8", "");
		
		assertNoAction("CollectionTotals.removeColumnSum", "property=product.unitPrice,collection=calculatedDetails");
		execute("CollectionTotals.sumColumn", "property=product.unitPrice,collection=calculatedDetails");
		assertAction("CollectionTotals.removeColumnSum", "property=product.unitPrice,collection=calculatedDetails");
		assertNoAction("CollectionTotals.sumColumn", "property=product.unitPrice,collection=calculatedDetails");
		assertTotals("31.00", "8", "");
		
		resetModule();
		execute("CRUD.new");
		setValue("year", "2004");
		setValue("number", "9");
		execute("CRUD.refresh");
		
		assertTotals("31.00", "8", "");
		
		assertAction("CollectionTotals.sumColumn", "property=amount,collection=calculatedDetails");
		assertNoAction("CollectionTotals.removeColumnSum", "property=amount,collection=calculatedDetails");
		
		assertAction("CollectionTotals.removeColumnSum", "property=quantity,collection=calculatedDetails");
		assertNoAction("CollectionTotals.sumColumn", "property=quantity,collection=calculatedDetails");

		assertAction("CollectionTotals.removeColumnSum", "property=product.unitPrice,collection=calculatedDetails");
		assertNoAction("CollectionTotals.sumColumn", "property=product.unitPrice,collection=calculatedDetails");
		
		assertTotalInCollection("calculatedDetails", 1, ""); 
		hideCollection();
		HtmlElement header = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_InvoiceCalculatedDetails__frame_calculatedDetailsheader");
		assertEquals("(2)    Sum of Unit price: 31.00    Sum of Quantity: 8", header.asText());
	}

	private void assertTotals(String unitPriceSum, String quantitySum, String amountSum) throws Exception {
		assertValueInCollection("calculatedDetails", 0, "product.unitPrice", "11.00");
		assertValueInCollection("calculatedDetails", 1, "product.unitPrice", "20.00");
		assertTotalInCollection("calculatedDetails", "product.unitPrice", unitPriceSum);
		
		assertValueInCollection("calculatedDetails", 0, "quantity", "6");
		assertValueInCollection("calculatedDetails", 1, "quantity", "2");
		assertTotalInCollection("calculatedDetails", "quantity", quantitySum);
		
		assertValueInCollection("calculatedDetails", 0, "amount",   "396.00");
		assertValueInCollection("calculatedDetails", 1, "amount", "4,000.00");
		assertTotalInCollection("calculatedDetails",    "amount", amountSum);
	}		
	
	private void hideCollection() throws Exception {  
		HtmlElement link = (HtmlElement) getHtmlPage().getHtmlElementById("ox_OpenXavaTest_InvoiceCalculatedDetails__frame_calculatedDetailshide").getChildElements().iterator().next(); 
		link.click();
	}
		
}
