package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*; 

/**
 *
 * @author Javier Paniza
 */
public class DeliveryFullInvoiceTest extends ModuleTestBase { 
	
	public DeliveryFullInvoiceTest(String testName) {
		super(testName, "DeliveryFullInvoice");		
	}
	
	public void testGeneratePDFFromCollectionOfReferenceInsideSection() throws Exception { 
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		execute("Sections.change", "activeSection=1,viewObject=xava_view_invoice");
		assertCollectionRowCount("invoice.details", 2);
		execute("Print.generatePdf", "viewObject=xava_view_invoice_section1_details"); 
		assertContentTypeForPopup("application/pdf");
		
		assertCollectionRowCount("invoice.details", 2);
		HtmlElement filterText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_DeliveryFullInvoice__xava_collectionTab_invoice_details_conditionValue___1");
		filterText.type("IBM");
		filterText.type('\r');
		waitAJAX();
		assertNoErrors();
		assertCollectionRowCount("invoice.details", 1); 
		
		execute("List.addColumns", "collection=invoice.details");
		assertNoErrors();		
	}
	
	protected BrowserVersion getBrowserVersion() throws Exception { 
		return BrowserVersion.INTERNET_EXPLORER; 
	}
	
}
