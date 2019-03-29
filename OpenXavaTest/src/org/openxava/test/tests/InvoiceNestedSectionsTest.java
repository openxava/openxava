package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class InvoiceNestedSectionsTest extends ModuleTestBase {
	

	public InvoiceNestedSectionsTest(String testName) {
		super(testName, "InvoiceNestedSections");		
	}
	
	public void testPdfReportInNestedSections_subcontrollerImage() throws Exception { 
		execute("List.viewDetail", "row=0");

		String linkXml = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_InvoiceNestedSections__sc-a-InvoicePrint_detail").asXml();
		assertFalse(linkXml.contains("<i class="));
		assertTrue(linkXml.contains("images/report.gif"));

		execute("Sections.change", "activeSection=1");		
		assertCollectionNotEmpty("details"); 
		execute("Print.generatePdf", "viewObject=xava_view_section1_section0_details"); 
		assertNoErrors();
		assertContentTypeForPopup("application/pdf");		
	}
	
	public void testReadOnlyCheckBoxInSpanish() throws Exception { 
		setLocale("es");
		execute("List.viewDetail", "row=0");
		assertValue("year", "2002");
		assertValue("number", "1");
		assertValue("paid", "false"); 
		assertNoEditable("paid");
		execute("InvoiceNestedSections.showPaid");
		assertMessage("paid=false");
		
		execute("Navigation.next");
		assertValue("year", "2004");
		assertValue("number", "2");
		assertValue("paid", "true");
		assertNoEditable("paid");
		execute("InvoiceNestedSections.showPaid");
		assertMessage("paid=true");		
	}
	
	public void testCalculatedPropertiesDependingFromPropertiesInOtherSections() throws Exception {  
		execute("List.viewDetail", "row=0");
		execute("Sections.change", "activeSection=1");		
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1");
		assertValue("vatPercentage", "16.0"); // We rely on first invoice has this value 
		assertValue("vat", "400.00"); // We rely on first invoice has this value
		setValue("vatPercentage", "17");
		assertValue("vat", "425.00");
	}
		
	public void testNestedSections() throws Exception {
		// Level 0: Section 0
		execute("CRUD.new");
		assertExists("customer.type");
		assertNoAction("Collections.new");
				
		// Level 0: Section 1, Level 1: Section 0
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.type");
		assertAction("Collection.new");
		assertNotExists("vat");
		
		// Level 1: Section 1
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1");
		assertNotExists("customer.type");
		assertNoAction("Collection.new");
		assertExists("vat");
		assertNotExists("amountsSum");
		
		// Level 2: Section 1
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1_section1");
		assertNotExists("customer.type");
		assertNoAction("Collection.new");
		assertNotExists("vat");
		assertExists("amountsSum");
	}
	
	public void testReferenceActionsInNestedSections() throws Exception {
		execute("List.viewDetail", "row=0"); 
		execute("Sections.change", "activeSection=1");
		execute("Collection.new", "viewObject=xava_view_section1_section0_details");
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Reference.search", "keyProperty=product.number");
		assertNoErrors();
		execute("ReferenceSearch.cancel");
		execute("Reference.createNew", "model=Product,keyProperty=product.number");
		assertNoErrors();
		assertValue("Product", "number", "");		
	}	
									
}
