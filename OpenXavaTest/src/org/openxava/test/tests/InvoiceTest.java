package org.openxava.test.tests;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.XPersistence;
import org.openxava.test.calculators.YearInvoiceDiscountCalculator;
import org.openxava.test.model.*;
import org.openxava.util.Dates;
import org.openxava.util.Is;
import org.openxava.util.Strings;
import org.openxava.util.XavaPreferences;
import org.openxava.util.XavaResources;
import org.openxava.web.Ids;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;



/**
 * 
 * @author Javier Paniza
 */

public class InvoiceTest extends CustomizeListTestBase { 
	
	private Invoice invoice;
	private BigDecimal productUnitPriceDB;
	private String productUnitPricePlus10;
	private String productUnitPrice;
	private String productUnitPriceInPesetas;
	private String productDescription;
	private String productNumber;
	private Product product;	
	
	public InvoiceTest(String testName) {
		super(testName, "Invoice");		
	}
	
	public void testCutPasteCascadeCollection() throws Exception {
		assertValueInList(5, 0, "2004");
		assertValueInList(5, 1, "12");
		execute("List.viewDetail", "row=5");
		assertValue("year", "2004");
		assertValue("number", "12");
		
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, 1, "MULTAS DE TRAFICO");
		assertValueInCollection("details", 1, 1, "IBM ESERVER ISERIES 270");
		execute("CollectionCopyPaste.cut", "row=1,viewObject=xava_view_section1_details");
		assertMessage("1 row cut from Details");
		assertCollectionRowCount("details", 2);
		
		execute("Navigation.previous");
		assertValue("year", "2004");
		assertValue("number", "11");

		assertCollectionRowCount("details", 0);
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_section1_details");
		assertMessage("1 row pasted into Details");
		assertCollectionRowCount("details", 1);
		assertValueInCollection("details", 0, 1, "IBM ESERVER ISERIES 270");
		
		checkAllCollection("details");
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_section1_details");
		assertMessage("1 row cut from Details");
		assertCollectionRowCount("details", 1);
		
		execute("Navigation.next");
		assertValue("year", "2004");
		assertValue("number", "12");
		
		assertCollectionRowCount("details", 1);
		assertValueInCollection("details", 0, 1, "MULTAS DE TRAFICO");
		
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_section1_details");
		assertMessage("1 row pasted into Details");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, 1, "MULTAS DE TRAFICO");
		assertValueInCollection("details", 1, 1, "IBM ESERVER ISERIES 270");
		
	}
	
	public void testSubcontrollerWithoutActionsInMode_subcontrollerIcon() throws Exception {
		// subcontroller: InvoicePrint -> all actions are in mode detail
		assertNoAction("InvoicePrint.printPdf");
		assertFalse(getHtml().contains("<span id=\"ox_OpenXavaTest_Invoice__sc-container-InvoicePrint_detail\">"));
		
		execute("List.viewDetail", "row=0");
		assertTrue(getHtml().contains("<span id=\"ox_OpenXavaTest_Invoice__sc-container-InvoicePrint_detail\">"));
		assertAction("InvoicePrint.printPdf");
		
		String linkXml = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Invoice__sc-button-InvoicePrint_detail").asXml();
		assertTrue(linkXml.contains("<i class=\"mdi mdi-printer\""));
		assertFalse(linkXml.contains("images/"));
	}
		
	public void testHideShowSection() throws Exception { 
		execute("List.viewDetail", "row=0");
	
		assertNoAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertExists("customer.number");
		assertNotExists("details");
		assertValue("customer.number", "1");
		
		execute("Invoice.hideCustomer");
		assertNoAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertNoAction("Sections.change", "activeSection=3");
		assertNotExists("customer.number");
		assertExists("details");
		
		execute("Invoice.showCustomer"); 
		assertAction("Sections.change", "activeSection=0");
		assertNoAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertNotExists("customer.number");
		assertExists("details");
		assertNoErrors(); 
		
		execute("Sections.change", "activeSection=0");
		assertValue("customer.number", "1");
		
		execute("Sections.change", "activeSection=2"); 
		assertAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertNoAction("Sections.change", "activeSection=2");
		assertAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertNotExists("deliveries");
		assertExists("amountsSum");
		
		execute("Invoice.hideCustomer");		
		assertAction("Sections.change", "activeSection=0");
		assertNoAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertNoAction("Sections.change", "activeSection=3");
		assertNotExists("deliveries");
		assertExists("amountsSum");
		
		execute("Invoice.showCustomer");
		assertAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertNoAction("Sections.change", "activeSection=2");
		assertAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertNotExists("deliveries");
		assertExists("amountsSum");
		
		execute("Sections.change", "activeSection=3"); 
		assertAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertNoAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertExists("deliveries");
		assertNotExists("amountsSum");
		
		execute("Invoice.hideCustomer");		
		assertAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertNoAction("Sections.change", "activeSection=2");
		assertNoAction("Sections.change", "activeSection=3");
		assertExists("deliveries");
		assertNotExists("amountsSum");
		
		execute("Invoice.showCustomer");
		assertAction("Sections.change", "activeSection=0");
		assertAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertNoAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertExists("deliveries");
		assertNotExists("amountsSum");
		
		execute("Sections.change", "activeSection=1"); 
		assertAction("Sections.change", "activeSection=0");
		assertNoAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertExists("details");
		assertNotExists("customer");
		
		execute("Invoice.hideAmounts");		
		assertAction("Sections.change", "activeSection=0");
		assertNoAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertNoAction("Sections.change", "activeSection=3");
		assertExists("details");
		assertNotExists("customer");
		
		execute("Invoice.showAmounts");
		assertAction("Sections.change", "activeSection=0");
		assertNoAction("Sections.change", "activeSection=1");
		assertAction("Sections.change", "activeSection=2");
		assertAction("Sections.change", "activeSection=3");
		assertNoAction("Sections.change", "activeSection=4");		
		assertExists("details");
		assertNotExists("customer");
	}
	
	public void testImagesGalleryInDialog() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("Sections.change", "activeSection=1");
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		assertDialogTitle("Edit - Invoice detail"); 
		execute("Reference.modify", "model=Product,keyProperty=product.number");
		assertDialogTitle("Modify - Product");
		execute("Gallery.edit", "galleryProperty=photos");
		assertDialogTitle("Edit images gallery"); 
		assertMessage("No images"); 
		
		// Adding one image		
		execute("Gallery.addImage");
		assertNoErrors();
		String imageUrl = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newImage", imageUrl);
		execute("LoadImageIntoGallery.loadImage");
		assertDialogTitle("Edit images gallery");
		assertNoErrors();
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());
		
		execute("Gallery.close");		
		assertDialogTitle("Modify - Product");
		execute("Modification.update");
		assertDialogTitle("Edit - Invoice detail"); 
		assertNoErrors();
		execute("Collection.save");
		assertNoDialog();
		
		// Verifying the image has been added, and removing it  
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		execute("Reference.modify", "model=Product,keyProperty=product.number");
		execute("Gallery.edit", "galleryProperty=photos");
		assertNoMessage("No images");
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());
		String imageOid = getForm().getInputByName("xava.GALLERY.images").getValueAttribute();
		execute("Gallery.removeImage", "oid="+imageOid);
		assertNoErrors();
		assertEquals("Images count does not match", 0, getForm().getInputsByName("xava.GALLERY.images").size()); 
		
		// Closing all the dialogs with X (we need to test this case)
		assertDialogTitle("Edit images gallery");		
		closeDialog(); 
		assertDialogTitle("Modify - Product");
		assertExists("unitPrice");
		closeDialog();
		assertDialogTitle("Edit - Invoice detail");
		assertExists("quantity");
		closeDialog();
		assertNoDialog();
		assertAction("CRUD.new");
		assertExists("paid");
	}
	
	public void testMyReportAddColumnsOnlyFromTwoLevelQualifiedProperties() throws Exception { 
		execute("ExtendedPrint.myReports");
		execute("MyReport.newColumn", "viewObject=xava_view_columns");		
		String [][] defaultColumnNames = {
			{ "", "" },	
			{ "year", "Year" },
			{ "number", "Number" },
			{ "date", "Date" },	
			{ "vatPercentage", "VAT %" },	
			{ "comment", "Comment" },	
			{ "paid", "Paid" },	
			{ "customer.number", "Number of Customer" },	
			{ "customer.name", "Name of Customer" },
			{ "customer.type", "Type of Customer" },	
			{ "customer.photo", "Photo of Customer" },	
			{ "customer.telephone", "Telephone of Customer" },
			{ "customer.email", "Email of Customer" },
			{ "customer.additionalEmails", "Additional emails of Customer" }, 
			{ "customer.website", "Web site of Customer" },
			{ "customer.remarks", "Remarks of Customer" },
			{ "customer.relationWithSeller", "Relation with seller of Customer" },
			{ "customer.city", "City of Customer" },
			{ "customer.local", "Lokal of Customer" },			
			{ "amountsSum", "Amounts sum" },			
			{ "customerDiscount", "Customer discount" },
			{ "customerTypeDiscount", "Customer type discount" },
			{ "deliveryDate", "Delivery date" },
			{ "detailsCount", "Details count" },
			{ "importance", "Importance" },			
			{ "productUnitPriceSum", "Product unit price sum" },
			{ "sellerDiscount", "Seller discount" },
			{ "total", "Total" },
			{ "vat", "V.A.T." },
			{ "yearDiscount", "Year discount" },
			{ "considerable", "Considerable" },
			{ "__MORE__", "[SHOW MORE...]"}
		};		
		assertValidValues("name", defaultColumnNames); 
				
		setValue("name", "__MORE__");
		
		String [][] allColumnNames = {
			{ "", "" },	
			{ "year", "Year" },
			{ "number", "Number" },
			{ "date", "Date" },	
			{ "vatPercentage", "VAT %" },	
			{ "comment", "Comment" },	
			{ "paid", "Paid" },	
			{ "customer.number", "Number of Customer" },	
			{ "customer.name", "Name of Customer" },
			{ "customer.type", "Type of Customer" },	
			{ "customer.photo", "Photo of Customer" },	
			{ "customer.telephone", "Telephone of Customer" },
			{ "customer.email", "Email of Customer" },
			{ "customer.additionalEmails", "Additional emails of Customer" }, 
			{ "customer.website", "Web site of Customer" },
			{ "customer.remarks", "Remarks of Customer" },
			{ "customer.address.street", "Street of Address of Customer" },
			{ "customer.address.zipCode", "Zip code of Address of Customer" },
			{ "customer.address.city", "City of Address of Customer" },
			{ "customer.address.state.id", "Id of State of Address of Customer" },
			{ "customer.address.state.name", "State" },
			{ "customer.address.state.fullNameWithFormula", "Full name with formula of State of Address of Customer" },
			{ "customer.address.state.fullName", "Full name of State of Address of Customer" },
			{ "customer.address.asString", "As string of Address of Customer" },
			{ "customer.seller.number", "Number of Seller of Customer" },
			{ "customer.seller.name", "Name of Seller of Customer" },
			{ "customer.seller.level.id", "Id of Level of Seller of Customer" },
			{ "customer.seller.level.description", "Description of Level of Seller of Customer" },
			{ "customer.seller.regions", "Regions of Seller of Customer" },			
			{ "customer.relationWithSeller", "Relation with seller of Customer" },
			{ "customer.alternateSeller.number", "Number of Alternate seller of Customer" },
			{ "customer.alternateSeller.name", "Name of Alternate seller of Customer" },
			{ "customer.alternateSeller.level.id", "Id of Level of Alternate seller of Customer" },
			{ "customer.alternateSeller.level.description", "Description of Level of Alternate seller of Customer" },
			{ "customer.alternateSeller.regions", "Regions of Alternate seller of Customer" },
			{ "customer.group.name", "Name of Group of Customer" }, 
			{ "customer.city", "City of Customer" },			
			{ "customer.local", "Lokal of Customer" },			
			{ "amountsSum", "Amounts sum" },
			{ "customerDiscount", "Customer discount" },
			{ "customerTypeDiscount", "Customer type discount" },
			{ "deliveryDate", "Delivery date" },
			{ "detailsCount", "Details count" },
			{ "importance", "Importance" },
			{ "productUnitPriceSum", "Product unit price sum" },			
			{ "sellerDiscount", "Seller discount" },
			{ "total", "Total" },
			{ "vat", "V.A.T." },			
			{ "yearDiscount", "Year discount" },
			{ "considerable", "Considerable" }
		};		
		assertValidValues("name", allColumnNames); 
		
		closeDialog();
		
		execute("MyReport.newColumn", "viewObject=xava_view_columns");
		assertValidValues("name", defaultColumnNames);
		
		setValue("name", "__MORE__");
		setValue("name", "customer.seller.name");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 8, 0, "Name of Seller of Customer"); 
		execute("MyReport.editColumn", "row=8,viewObject=xava_view_columns");
		assertValue("name", "customer.seller.name");
		assertValue("label", "Name of Seller of Customer");
		assertValidValuesCount("name", defaultColumnNames.length + 1);
	}
	
	public void testMyReportConditionWithBoolanFromList() throws Exception { 
		setConditionComparators("", "", "", "=");
		execute("List.filter");
		assertListRowCount(1);		
		assertValueInList(0, 0, "2004");
		assertValueInList(0, 1, "2");
		
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 6, 0, "Paid");
		assertValueInCollection("columns", 6, 1, "=");
		assertValueInCollection("columns", 6, 2, "Yes");

		execute("MyReport.editColumn", "row=6,viewObject=xava_view_columns");
		assertExists("booleanValue"); 
		assertNotExists("dateValue");
		assertNotExists("comparator");
		assertNotExists("value");
		assertExists("order");
		assertValue("booleanValue", "true"); 
	}
	
	public void testMyReportFilteringByDateAndBooleanWithConverter() throws Exception {  
		// Date
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 2, 0, "Date");
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		
		assertExists("comparator"); 
		assertNotExists("value"); 
		assertNotExists("descriptionsListValue"); 
		assertNotExists("booleanValue");
		assertNotExists("validValuesValue");
		
		assertTrue(getHtml().contains("showCalendar('ox_OpenXavaTest_Invoice__dateValue'"));
		
		setValue("dateValue", "5/28/07");
		
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "5/28/07");
		
		execute("MyReport.generatePdf");				
		assertPopupPDFLinesCount(5);  
		assertTrue(getPopupPDFLine(3).startsWith("2007 14"));
		
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 2, 0, "Date"); 
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "5/28/07"); 		
		execute("MyReport.remove", "xava.keyProperty=name");

		assertValueInCollection("columns", 2, 0, "Date");
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		String [][] dateComparators = {
			{ "eq_comparator", "=" },
			{ "ne_comparator", "<>" },
			{ "ge_comparator", ">=" },
			{ "le_comparator", "<=" }, 
			{ "gt_comparator", ">" },
			{ "lt_comparator", "<" },
			{ "empty_comparator", "is empty" },
			{ "not_empty_comparator", "is not empty" },			
			{ "year_comparator", "year =" },
			{ "month_comparator", "month =" },			
			{ "year_month_comparator", "year/month =" },
			{ "in_comparator", "in group" }, 
			{ "not_in_comparator", "not in group" } 						
		};
		assertValidValues("comparator", dateComparators);  		
		setValue("comparator", "year_comparator");		
		setValue("value", "2004");
		assertNotExists("dateValue"); 
		assertNotExists("descriptionsListValue"); 
		assertNotExists("booleanValue");
		assertNotExists("validValuesValue");
		assertValidValues("comparator", dateComparators); 
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "year =");
		assertValueInCollection("columns", 2, 2, "2004");
		
		execute("MyReport.generatePdf");				
		assertPopupPDFLinesCount(7); 
		assertTrue(getPopupPDFLine(3).startsWith("2004 2"));
		assertTrue(getPopupPDFLine(4).startsWith("2004 9"));
		assertTrue(getPopupPDFLine(5).startsWith("2004 10"));

		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "year =");
		assertValueInCollection("columns", 2, 2, "2004");		
		execute("MyReport.remove", "xava.keyProperty=name");
		
		// Boolean
		assertValueInCollection("columns", 6, 0, "Paid");
		assertValueInCollection("columns", 6, 1, "");
		assertValueInCollection("columns", 6, 2, "");
		execute("MyReport.editColumn", "row=6,viewObject=xava_view_columns");
		String [][] booleanValues = {
			{ "", "" },	
			{ "true", "Yes" },
			{ "false", "No" }
		};
		assertValidValues("booleanValue", booleanValues);
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 6, 0, "Paid");
		assertValueInCollection("columns", 6, 1, "");
		assertValueInCollection("columns", 6, 2, ""); // BTW, this tests BooleanFormatter for supporting nulls 
		
		execute("MyReport.editColumn", "row=6,viewObject=xava_view_columns");
		assertExists("booleanValue");
		assertNotExists("dateValue"); 
		assertNotExists("comparator");
		assertNotExists("value");
		assertExists("order");
		setValue("name", "year");
		assertNotExists("booleanValue");
		assertNotExists("dateValue"); 
		assertExists("comparator");
		assertExists("value");
		assertExists("order");
		setValue("name", "paid");
		assertExists("booleanValue");
		assertNotExists("dateValue"); 
		assertNotExists("comparator");
		assertNotExists("value");
		assertExists("order");
		setValue("booleanValue", "true");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 6, 0, "Paid");
		assertValueInCollection("columns", 6, 1, "=");
		assertValueInCollection("columns", 6, 2, "Yes");
		
		execute("MyReport.generatePdf");		
		assertPopupPDFLinesCount(5);  
		assertTrue(getPopupPDFLine(3).startsWith("2004 2"));
		
		execute("ExtendedPrint.myReports");
		execute("MyReport.generatePdf");		
		assertPopupPDFLinesCount(5);  
		assertTrue(getPopupPDFLine(3).startsWith("2004 2"));		
		
		execute("ExtendedPrint.myReports"); 
		execute("MyReport.remove", "xava.keyProperty=name"); 
	}
	
	public void testFilterByRange() throws Exception{ 
		getWebClient().getOptions().setCssEnabled(true); 
		
		assertListRowCount(9);  
		assertLabelInList(0, "Year");
		assertLabelInList(2, "Date");
		assertLabelInList(6, "Paid");		
		// int
		setConditionValues("2000");
		setConditionComparators("range_comparator"); 
		assertListRowCount(9); 
		assertTrue(isVisibleConditionValueTo(0));
		assertFalse(isVisibleConditionValueToCalendar(0));
		assertFalse(isVisibleConditionValueTo(2));		
		assertFalse(isVisibleConditionValueToCalendar(2));
		setConditionValuesTo("2004");
		execute("List.filter");
		assertListRowCount(6);
		// int & Date
		setConditionComparators("range_comparator", "eq", "range_comparator");
		setConditionValues("2000", "", "01/01/2002");
		setConditionValuesTo("2004", "", "05/01/2004");
		assertTrue(isVisibleConditionValueTo(2)); 
		assertTrue(isVisibleConditionValueToCalendar(2)); 
		execute("List.filter");
		assertListRowCount(3); 
		// int & Date & boolean
		setConditionComparators("range_comparator", "eq", "range_comparator", "eq");
		execute("List.filter");
		assertListRowCount(1);
		
		assertTrue(isVisibleConditionValueTo(0));
		assertFalse(isVisibleConditionValueToCalendar(0));
		assertTrue(isVisibleConditionValueTo(2));		
		assertTrue(isVisibleConditionValueToCalendar(2));		
		setConditionComparators("eq", "eq", "eq", "eq");
		assertFalse(isVisibleConditionValueTo(0));
		assertFalse(isVisibleConditionValueToCalendar(0));
		assertFalse(isVisibleConditionValueTo(2));		
		assertFalse(isVisibleConditionValueToCalendar(2));
		
		clearCondition();
		assertListRowCount(9);
		setConditionValues("2002");
		setConditionComparators(">");
		assertListRowCount(8);
	}
	
	public void testSearchUsesSimpleView() throws Exception { 
		execute("CRUD.new");		
		assertValue("comment", "");
		assertNoDialog();
		execute("SearchForCRUD.search"); 
		assertDialog();
		assertNotExists("paid");
		assertNotExists("customerDiscount");
		assertNotExists("customer.number");
		assertValue("year", "");
		assertValue("number", "");
		setValue("year", "2002");
		setValue("number", "1");
		execute("Search.search");
		assertNoDialog();
		assertNoErrors();
		assertValue("comment", "INVOICE WITH SPACES");
	}
	
	public void testCollectionSelectionIsCleared() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Sections.change", "activeSection=1");
		assertCollectionNotEmpty("details");
		checkRowCollection("details", 0);
		execute("Sections.change", "activeSection=3");
		assertCollectionNotEmpty("deliveries");
		checkRowCollection("deliveries", 0);
		execute("Navigation.next");
		assertCollectionNotEmpty("deliveries");
		assertRowCollectionUnchecked("deliveries", 0);
		execute("Sections.change", "activeSection=1");
		assertRowCollectionUnchecked("details", 0);
	}
	
	public void testGenerateCustomPdfAndPrepareNewAfter() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("InvoicePrint.printPdfNewAfter"); 
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/pdf");
		assertEditable("year");
	}
	
	public void testGenerateCustomPdfExcelRtfOdt() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("InvoicePrint.printPdf"); 
		assertNoErrors(); 
		assertMessage("The print was successful"); 
		assertContentTypeForPopup("application/pdf");
		
		execute("InvoicePrint.printExcel"); 
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/vnd.ms-excel"); 		
		
		execute("InvoicePrint.printRtf"); 
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/rtf");
		
		execute("InvoicePrint.printOdt"); 
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/vnd.oasis.opendocument.text");				
	}
	
	public void testGenerateTwoReportsAtOnce() throws Exception { 
		assertGenerateTwoReportsAtOnce("InvoicePrint.print2Pdfs"); 
	}
	
	public void testGenerateTwoReportsAtOnceWithDifferentParameters() throws Exception { 
		assertGenerateTwoReportsAtOnce("InvoicePrint.printInvoiceAndCustomer"); 
	}
	
	public void testGenerateTwoReportsAtOnceWithDifferentParametersUsingAddParameters() throws Exception { 
		assertGenerateTwoReportsAtOnce("InvoicePrint.printInvoiceAndCustomer2"); 
	}

	
	
	private void assertGenerateTwoReportsAtOnce(String action) throws Exception {  
		execute("List.viewDetail", "row=0");
		execute(action);		
		assertNoErrors();		
		assertPopupCount(2); 
		assertContentTypeForPopup(0, "application/pdf");
		assertContentTypeForPopup(1, "application/pdf");
	}
		
	// Only behaves thus when mapFacadeAutocommit=false (the default)
	public void testFailOnSaveFirstCollectionElementNotSaveMainEntity() throws Exception {
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) return; 
		execute("CRUD.new");
		setValue("year", "2008");
		setValue("number", "66");
		setValue("comment", "JUNIT INVOICE");
		setValue("customer.number", "1");
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "16");
		execute("Sections.change", "activeSection=1");
		execute("Collection.new", "viewObject=xava_view_section1_details");
		execute("Collection.save");
		assertNoMessage("Invoice created successfully");
		closeDialog(); 
		execute("CRUD.new");
		assertValue("comment", "");
		setValue("year", "2008");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Number:66, Year:2008");
		assertValue("comment", "");
	}
	
	public void testPaginationInCollections() throws Exception {
		// The invoice 2007/14 has 14 detail lines
		execute("CRUD.new");
		setValue("year", "2007");
		setValue("number", "14");
		execute("CRUD.refresh");
		assertNoErrors();		
		assertValue("comment", "MORE THAN 10 LINES");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 10);
		execute("List.goNextPage", "collection=details");
		assertCollectionRowCount("details", 4);
		execute("List.goPreviousPage", "collection=details");
		assertCollectionRowCount("details", 10);
		execute("List.goPage", "page=2,collection=details");
		assertCollectionRowCount("details", 4);
		execute("List.goPage", "page=1,collection=details");
		assertCollectionRowCount("details", 10);
		
		execute("List.goPage", "page=2,collection=details");
		assertCollectionRowCount("details", 4);
		execute("Navigation.first");
		assertValue("year", "2002");
		assertValue("number", "1");
		assertCollectionRowCount("details", 2);
	}
	
	public void testGeneratePdfAggregateCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Sections.change", "activeSection=1");
		execute("Print.generatePdf", "viewObject=xava_view_section1_details"); 
		assertContentTypeForPopup("application/pdf");
	}
	
	public void testSearchByPropertyWithConverterInDetailMode() throws Exception {
		execute("CRUD.new");
		setValue("year", ""); 
		setValue("date", "");
		setValue("paid", "true");
		execute("CRUD.refresh");
		assertNoErrors();
	}
	
	public void testI18nOfLabelOfAConcreteView_alwaysEnabledActions() throws Exception {
		execute("CRUD.new");
		assertLabel("customer.number", "Little code");
		assertAction("Customer.changeNameLabel");
		assertAction("Customer.prefixStreet");
	}
	
	public void testTestingCheckBox() throws Exception { 
		// Demo for make tests with checkbox
		
		// Create
		execute("CRUD.new");
		
		String year = getValue("year");		
		setValue("number", "66");
		
		setValue("customer.number", "1");
		
		// First vat percentage for no validation error on save first detail
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "23");
				
		createOneDetail(); // Because at least one detail is required 
		setValue("paid", "true"); // assign true to checkbox 
		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("paid", "false"); // assert if checkbox is false
		
		// Consult
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("paid", "true");
		
		// Changing the boolean value
		setValue("paid", "false"); // assign false to checkbox
		execute("CRUD.save");
		assertNoErrors();
		
		// Consult again
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();		
		assertValue("paid", "false"); // assert if checkbox is false
		
		// Delete
		execute("CRUD.delete");		
		assertMessage("Invoice deleted successfully");			
	}	
	
	public void testCustomizeListShowMoreColumns() throws Exception {  
		assertListColumnCount(8); 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); // The limit is 20
		assertValueInCollection("xavaPropertiesList",  0, 0, "Additional emails of Customer"); 
		assertValueInCollection("xavaPropertiesList",  1, 0, "City of Customer"); 
		assertValueInCollection("xavaPropertiesList",  2, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Considerable");		
		assertValueInCollection("xavaPropertiesList",  4, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Delivery date");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Email of Customer"); 
		assertValueInCollection("xavaPropertiesList",  8, 0, "Lokal of Customer");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Name of Customer");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Number of Customer");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Photo of Customer");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Product unit price sum");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Relation with seller of Customer");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Remarks of Customer");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Telephone of Customer");
		assertValueInCollection("xavaPropertiesList", 17, 0, "Total");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Type of Customer");
		assertValueInCollection("xavaPropertiesList", 19, 0, "VAT %");
		
		execute("AddColumns.showMoreColumns");		
		assertCollectionRowCount("xavaPropertiesList", 41); 
		assertValueInCollection("xavaPropertiesList",  0, 0, "Additional emails of Customer");
		assertValueInCollection("xavaPropertiesList",  1, 0, "As string of Address of Customer"); 
		assertValueInCollection("xavaPropertiesList",  2, 0, "City of Address of Customer");
		assertValueInCollection("xavaPropertiesList",  3, 0, "City of Customer");
		assertValueInCollection("xavaPropertiesList",  4, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Considerable");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList",  8, 0, "Delivery date");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Description of Level of Alternate seller of Customer");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Description of Level of Seller of Customer");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Email of Customer");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Full name of State of Address of Customer");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Full name with formula of State of Address of Customer");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Id of Level of Alternate seller of Customer");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Id of Level of Seller of Customer");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Id of State of Address of Customer");
		assertValueInCollection("xavaPropertiesList", 17, 0, "Lokal of Customer");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Name of Alternate seller of Customer");
		assertValueInCollection("xavaPropertiesList", 19, 0, "Name of Customer");
		assertValueInCollection("xavaPropertiesList", 20, 0, "Name of Group of Customer");
		assertValueInCollection("xavaPropertiesList", 21, 0, "Name of Seller of Customer");
		assertValueInCollection("xavaPropertiesList", 22, 0, "Number of Alternate seller of Customer");
		assertValueInCollection("xavaPropertiesList", 23, 0, "Number of Customer");
		assertValueInCollection("xavaPropertiesList", 24, 0, "Number of Seller of Customer");
		assertValueInCollection("xavaPropertiesList", 25, 0, "Photo of Customer");
		assertValueInCollection("xavaPropertiesList", 26, 0, "Product unit price sum");
		assertValueInCollection("xavaPropertiesList", 27, 0, "Regions of Alternate seller of Customer"); 
		assertValueInCollection("xavaPropertiesList", 28, 0, "Regions of Seller of Customer");
		assertValueInCollection("xavaPropertiesList", 29, 0, "Relation with seller of Customer");
		assertValueInCollection("xavaPropertiesList", 30, 0, "Remarks of Customer");
		assertValueInCollection("xavaPropertiesList", 31, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 32, 0, "State of Address of Customer");
		assertValueInCollection("xavaPropertiesList", 33, 0, "Street of Address of Customer");
		assertValueInCollection("xavaPropertiesList", 34, 0, "Telephone of Customer");
		assertValueInCollection("xavaPropertiesList", 35, 0, "Total");
		assertValueInCollection("xavaPropertiesList", 36, 0, "Type of Customer");
		assertValueInCollection("xavaPropertiesList", 37, 0, "VAT %");
		assertValueInCollection("xavaPropertiesList", 38, 0, "Web site of Customer");
		assertValueInCollection("xavaPropertiesList", 39, 0, "Year discount");
		assertValueInCollection("xavaPropertiesList", 40, 0, "Zip code of Address of Customer");		
		
		assertNoAction("AddColumns.showMoreColumns");
		
		checkRow("selectedProperties", "customer.address.city");
		execute("AddColumns.addColumns");		
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "City of Address of Customer");

		// Restoring, for next time that test execute
		removeColumn(8); 
		assertListColumnCount(8); 

		// Always starts with 20
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); 
	}
	
	public void testCustomizeListSearchColumns_customizeListPressEnterWithoutChoosingColumns() throws Exception {   
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Additional emails of Customer");   
		assertValueInCollection("xavaPropertiesList", 19, 0, "VAT %"); 
		assertAction("AddColumns.showMoreColumns");

		HtmlElement searchBox = getHtmlPage().getHtmlElementById("xava_search_columns_text");
		searchBox.type("DISCOUNT");
		assertEquals("DISCOUNT", searchBox.getAttribute("value"));		
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertCollectionRowCount("xavaPropertiesList", 4); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList", 2, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 3, 0, "Year discount");
		assertNoAction("AddColumns.showMoreColumns");		
		
		searchBox.type("\b\b\b\b\b\b\b\b");
		assertEquals("", searchBox.getAttribute("value"));
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertCollectionRowCount("xavaPropertiesList", 20); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Additional emails of Customer"); 
		assertValueInCollection("xavaPropertiesList", 19, 0, "VAT %"); 
		assertAction("AddColumns.showMoreColumns");
		
		execute("AddColumns.showMoreColumns");		
		assertCollectionRowCount("xavaPropertiesList", 41); 
		searchBox = getHtmlPage().getHtmlElementById("xava_search_columns_text");
		searchBox.type("DISCOUNT");
		assertEquals("DISCOUNT", searchBox.getAttribute("value"));		
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertCollectionRowCount("xavaPropertiesList", 4); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList", 2, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 3, 0, "Year discount");
		assertNoAction("AddColumns.showMoreColumns");

		// Press Enter without choosing column
		executeDefaultAction();
		assertError("Please, choose some columns before pressing Add button or Enter");
		assertErrorsCount(1);
		assertDialog();
	}
	
	public void testRemoveSeveralColumns() throws Exception { 
		assertListColumnCount(8); 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");

		removeColumn(2);
		assertListColumnCount(7);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "V.A.T.");
		assertLabelInList(4, "Details count");
		assertLabelInList(5, "Paid");
		assertLabelInList(6, "Importance");
		
		removeColumn(4); // VAT, the original index is used until an ajax reload is done
		assertListColumnCount(6);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "Details count");
		assertLabelInList(4, "Paid");
		assertLabelInList(5, "Importance");
		
		execute("List.filter");
		assertListColumnCount(6);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "Details count");
		assertLabelInList(4, "Paid");
		assertLabelInList(5, "Importance");

		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");		
	}
	
	public void testCustomizeList() throws Exception {
		doTestCustomizeList_addColumns(); 
		resetModule(); 
		doTestCustomizeList_storePreferences(); 
	}
	
	private void doTestCustomizeList_addColumns() throws Exception {
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		
		execute("List.addColumns");
		
		assertCollectionRowCount("xavaPropertiesList", 20); // There are 22, but it only loads the first 20.		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Additional emails of Customer");
		assertValueInCollection("xavaPropertiesList",  1, 0, "City of Customer");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Considerable");		
		assertValueInCollection("xavaPropertiesList",  4, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Delivery date");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Email of Customer"); 
		assertValueInCollection("xavaPropertiesList",  8, 0, "Lokal of Customer");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Name of Customer");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Number of Customer");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Photo of Customer");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Product unit price sum");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Relation with seller of Customer");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Remarks of Customer");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Telephone of Customer");
		assertValueInCollection("xavaPropertiesList", 17, 0, "Total");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Type of Customer");
		assertValueInCollection("xavaPropertiesList", 19, 0, "VAT %");
		checkRow("selectedProperties", "customer.name"); 
		checkRow("selectedProperties", "sellerDiscount"); 
		 		
		execute("AddColumns.addColumns");
				
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "Customer"); 
		assertLabelInList(9, "Seller discount"); 
		
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); 
		assertValueInCollection("xavaPropertiesList",  0, 0, "Additional emails of Customer");
		assertValueInCollection("xavaPropertiesList",  1, 0, "City of Customer");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Considerable");		
		assertValueInCollection("xavaPropertiesList",  4, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Delivery date");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Email of Customer");
		assertValueInCollection("xavaPropertiesList",  8, 0, "Lokal of Customer");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Number of Customer");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Photo of Customer");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Product unit price sum");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Relation with seller of Customer");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Remarks of Customer");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Telephone of Customer");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Total");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Type of Customer");
		assertValueInCollection("xavaPropertiesList", 17, 0, "VAT %");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Web site of Customer");
		assertValueInCollection("xavaPropertiesList", 19, 0, "Year discount");
		
		execute("AddColumns.cancel");
		
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "Customer"); 
		assertLabelInList(9, "Seller discount"); 
		
		// To test that detail view is not broken because of the dialog
		execute("CRUD.new");
		assertExists("year");
		
		execute("Mode.list");
		assertLabelInList(5, "Details count");
		execute("List.changeColumnName", "property=detailsCount");
		assertDialog();
		assertValue("name", "Details count");
		setValue("name", "Number of lines");
		execute("ChangeColumnName.change");
		assertLabelInList(5, "Number of lines");
	}
	
	private void doTestCustomizeList_storePreferences() throws Exception {
		// This test trusts that 'testCustomizeList_addColumns' is executed before
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Number of lines"); 
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "Customer"); 
		assertLabelInList(9, "Seller discount"); 
				
		
		// Restoring, for next time that test execute
		removeColumn(9); 
		removeColumn(8);
		
		execute("List.changeColumnName", "property=detailsCount");
		assertValue("name", "Number of lines");
		setValue("name", "Details count");
		execute("ChangeColumnName.change");
		
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");		
	}	
	
	public void testGenerateExcel() throws Exception {
		String year = getValueInList(0, 0);
		String number = getValueInList(0, 1);		
		String date = getValueInList(0, 2);
		String amountsSum = formatBigDecimal(getValueInList(0, 3));		
		String vat = formatBigDecimal(getValueInList(0, 4));
		String detailsCount = getValueInList(0, 5);
		String paid = getValueInList(0, 6);
		paid = paid.equals("")?"No":"Yes"; 
		String importance = Strings.firstUpper(getValueInList(0, 7).toLowerCase());
		String expectedLine = year + ";" + number + ";\"" + 
			date + "\";\"" + amountsSum + "\";\"" + 
			vat + "\";" + detailsCount + ";\"" +
			paid + "\";\"" + importance + "\"";
		
		execute("Print.generateExcel"); 
		assertContentTypeForPopup("text/x-csv");
		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", "Year;Number;Date;Amounts sum;V.A.T.;Details count;Paid;Importance", header); 		
		String line1 = excel.nextToken();
		assertEquals("line1", expectedLine, line1);		
	}
	
	public void testGenerateExcelForOnlyCheckedRows() throws Exception { 
		checkRow(0);
		checkRow(2); // We assume that there are at least 3 invoices		
		execute("Print.generateExcel"); 
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		assertEquals("Must be exactly 3 (1 header + 2 detail) lines in the exported file", 
			3, excel.countTokens());	
	}
	
	public void testFilterByDate() throws Exception {
		String date = getValueInList(0, "date");		
		String [] conditionValues = { " ", " ", date, "true" };
		setConditionValues(conditionValues); 
		execute("List.filter");
		assertDateInList(date);
		
		String [] yearComparators = { "=", "=", "year_comparator", ""};
		setConditionComparators(yearComparators);
		
		String [] condition2002 = { " ", " ", "2002", "true" }; // We supussed that there are invoices in 2002
		setConditionValues(condition2002);
		execute("List.filter");
		assertYearInList("02");

		String [] condition2004 = { " ", " ", "2004", "true" }; // We supussed that there are invoices in 2004
		setConditionValues(condition2004);
		execute("List.filter");
		assertYearInList("04");
		
		String [] monthComparators = { "=", "=", "month_comparator", ""};
		setConditionComparators(monthComparators);		
		String [] conditionMonth1 = { " ", " ", "1", "true" }; 
		setConditionValues(conditionMonth1);
		execute("List.filter");
		assertListRowCount(3); // We suppose that there are 3 invoices of month 1
		
		String [] yearMonthComparators = { "=", "=", "year_month_comparator", ""};
		setConditionComparators(yearMonthComparators);		
		String [] conditionYear2004Month1 = { " ", " ", "2004/1", "true" }; 
		setConditionValues(conditionYear2004Month1);
		execute("List.filter");
		assertListRowCount(2); // We suppose that there are 2 invoices of month 1 of year 2004				
	}
	
	public void testFilterByBoolean() throws Exception { 
		int total = Invoice.findAll().size();		
		int paidOnes = Invoice.findPaidOnes().size();		
		int notPaidOnes = Invoice.findNotPaidOnes().size();
		
		assertTrue("It has to have invoices for run this test", total > 0);
		assertTrue("It has to have paid invoices for run this test", paidOnes > 0);		
		assertTrue("It has to have not paid invoices for run this test", notPaidOnes > 0);
		assertTrue("The sum of paid and not paid invoices has to match with the total count", total == (paidOnes + notPaidOnes));
		assertTrue("It has to have less than 10 invoices to run this test", total < 10); 
		assertListRowCount(total);
		
		String [] paidComparators = { "=", "=", "=", "="};
		String [] paidConditions = { "", "", "", "true"	};
		setConditionComparators(paidComparators);
		setConditionValues(paidConditions); 
		// execute("List.filter"); // Not needed because filterOnChange=true 
		assertListRowCount(paidOnes); 
		assertValueInList(0, "paid", "Paid"); 
		
		String [] notPaidComparatos = { "=", "=", "=", "<>"};
		String [] notPaidConditions = { " ", " ", " ", "true" }; // For dark reasons it is necessary to leave a blank space so it runs.
		setConditionComparators(notPaidComparatos);
		setConditionValues(notPaidConditions);		
		// execute("List.filter"); // Not needed because filterOnChange=true
		assertNoErrors();
		assertListRowCount(notPaidOnes);
		assertValueInList(0, "paid", ""); 
		
		String [] totalComparators = { "=", "=", "=", ""};
		String [] totalCondition = { " ", " ", " ", "true" }; // For dark reasons it is necessary to leave a blank space so it runs.
		setConditionComparators(totalComparators);
		setConditionValues(totalCondition);		
		// execute("List.filter"); // Not needed because filterOnChange=true
		assertNoErrors();
		assertListRowCount(total);				
	}
	
	public void testCreateFromReference() throws Exception {
		execute("CRUD.new");		
		execute("Reference.createNew", "model=Customer,keyProperty=xava.Invoice.customer.number"); 
		assertNoErrors();
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");	
		assertValue("Customer", "type", usesAnnotatedPOJO()?"2":"3");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		assertNoErrors();
		execute("ReferenceSearch.cancel");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");	
		assertValue("Customer", "type", usesAnnotatedPOJO()?"2":"3");		
		execute("NewCreation.cancel");
		assertExists("year");
		assertExists("number");
	}
	
	public void testChangeTab() throws Exception {		
		assertListColumnCount(8); 
		execute("Invoice.changeTab");
		assertNoErrors();
		assertListColumnCount(4);  
	}	
	
	public void testDateFormatter() throws Exception { 
		// In order to this test works inside Liferay you have to put
		// locale.default.request=true in portal-ext.properties
		setLocale("es");	
		execute("CRUD.new");
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		
		execute("CRUD.refresh");
		assertNoErrors();
		String originalDate = getValue("date"); // For restore at end
		
		setValue("date", "1/1/2004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "01/01/2004"); 
		
		setValue("date", "02012004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));		
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "02/01/2004");
		
		setValue("date", "3.1.2004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "03/01/2004");
		
		setValue("date", "4-1-2004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/2004");
		
		setValue("date", "4/1/39"); // If current year is 2019 
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/2039"); 
		
		setValue("date", "040140"); // If current year is 2019 
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/1940"); 
		
		setValue("date", "30/2/2008");
		execute("CRUD.save");
		assertError("Fecha en Factura no es un dato del tipo esperado"); 		
		
		// Restore original date
		setValue("date", originalDate);
		execute("CRUD.save");
		assertNoErrors();		 				
	}

	public void testValidateExistsRequiredReference() throws Exception { 
		execute("CRUD.new");		
		setValue("number", "66");
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "24");		
		execute("CRUD.save");
		assertError("Value for Customer in Invoice is required");				
	}
	
	public void testNotEditableCustomerData() throws Exception { 
		execute("CRUD.new");		
		assertEditable("customer.number");
		assertNoEditable("customer.name");
		assertNoEditable("customer.address.street");
	}
	
	public void testSearchReferenceWithListInsideSection() throws Exception {		
		execute("CRUD.new");		
				
		execute("Reference.search", "keyProperty=xava.Invoice.customer.number");
		String customerName = getValueInList(0, 0);		
		checkRow(0);		
		execute("ReferenceSearch.choose");
		assertValue("customer.name", customerName);				
	}
	
	public void testSections_aggregateCollection_orderedCollectionsInModel_posdeleteCollectionElement() throws Exception {       		
		// Create
		execute("CRUD.new");					
		assertExists("customer.number");
		assertNotExists("vatPercentage");
		
		String year = getValue("year");		
		setValue("number", "66");
		
		setValue("customer.number", "1");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.number");
		assertNotExists("vatPercentage");
		
		assertCollectionRowCount("details", 0); 

		assertNoDialog();
		execute("Collection.new", "viewObject=xava_view_section1_details");		
		assertNoDialog();
		assertError("Value for VAT % in Invoice is required");
		execute("Sections.change", "activeSection=2");
		assertNotExists("customer.number");
		assertExists("vatPercentage");
		assertValue("amountsSum", "");
		setValue("vatPercentage", "23");		
		
		execute("Sections.change", "activeSection=1");
		assertNoDialog();
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertNoErrors();
		assertDialog();
		setValue("serviceType", usesAnnotatedPOJO()?"":"0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPrice());		
		assertValue("amount", getProductUnitPriceMultiplyBy("20")); 
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		assertValue("deliveryDate", getCurrentDate()); 
		setValue("deliveryDate", "03/18/04"); // Testing multiple-mapping in aggregate
		setValue("soldBy.number", getProductNumber());
		execute("Collection.save");		
		assertMessage("Invoice detail created successfully"); 
		assertMessage("Invoice created successfully"); 
		assertNoErrors();		
		// assertExists("serviceType"); // In OX3 it does not hide detail on save, 
		assertNoDialog();			// but since OX4m2 a dialog is used and it's close
		assertCollectionRowCount("details", 1);

		// Next line tests IModelCalculator in an aggregate collection (only apply to XML components)
		assertValueInCollection("details", 0, "free", "0".equals(getProductUnitPrice())?"Free":"");  

		assertNoEditable("year"); // Testing header is saved
		assertNoEditable("number");

		// Testing if recalculate dependent properties
		execute("Sections.change", "activeSection=2");
		assertValue("amountsSum", getProductUnitPriceMultiplyBy("20")); 
		setValue("vatPercentage", "23");		
		execute("Sections.change", "activeSection=1");
		// end of recalculate testing
		
		execute("Collection.new", "viewObject=xava_view_section1_details"); 
		setValue("serviceType", usesAnnotatedPOJO()?"0":"1");
		setValue("quantity", "200");
		setValue("unitPrice", getProductUnitPrice());		
		assertValue("amount", getProductUnitPriceMultiplyBy("200"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		setValue("deliveryDate", "3/19/04"); // Testing multiple-mapping in aggregate
		setValue("soldBy.number", getProductNumber());		
		execute("Collection.saveAndStay");
		
		setValue("serviceType", usesAnnotatedPOJO()?"1":"2");
		setValue("quantity", "2");
		setValue("unitPrice", getProductUnitPrice());
		assertValue("amount", getProductUnitPriceMultiplyBy("2"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		assertValue("deliveryDate", getCurrentDate()); 
		setValue("deliveryDate", "3/20/04"); // Testing multiple-mapping in aggregate
		execute("Collection.save");
		assertCollectionRowCount("details", 3);
				
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "Special");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "200");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("200"));

		assertValueInCollection("details", 2, 0, ""); 
		assertValueInCollection("details", 2, 1, getProductDescription());
		assertValueInCollection("details", 2, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 2, 3, "20");
		assertValueInCollection("details", 2, 4, getProductUnitPrice());
		assertValueInCollection("details", 2, 5, getProductUnitPriceMultiplyBy("20"));

		execute("CRUD.save");
		assertNoErrors();
		assertValue("number", "");
		execute("Sections.change", "activeSection=0");
		assertValue("customer.number", "");
		assertValue("customer.name", "");
		execute("Sections.change", "activeSection=1");				
		assertCollectionRowCount("details", 0);
		execute("Sections.change", "activeSection=2");
		assertValue("vatPercentage", "");
	
		// Consulting	
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("year", year);
		assertValue("number", "66");
		execute("Sections.change", "activeSection=0");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 3);
		
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "Special");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "200");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("200"));

		assertValueInCollection("details", 2, 0, "");
		assertValueInCollection("details", 2, 1, getProductDescription());
		assertValueInCollection("details", 2, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 2, 3, "20");
		assertValueInCollection("details", 2, 4, getProductUnitPrice());
		assertValueInCollection("details", 2, 5, getProductUnitPriceMultiplyBy("20"));
				
		execute("Sections.change", "activeSection=2");		
		assertValue("vatPercentage", "23.0"); 
		
		// Edit line
		execute("Sections.change", "activeSection=1");		
		assertNotExists("product.description");
		assertNotExists("quantity");
		assertNotExists("deliveryDate");
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "200");
		assertValue("deliveryDate", "3/19/04");
		setValue("quantity", "234");
		setValue("deliveryDate", "4/23/04");
		execute("Collection.save");
		assertNoErrors();
		assertMessage("Invoice detail modified successfully");
		assertValueInCollection("details", 1, 3, "234");		
		assertNotExists("product.description"); 
		assertNotExists("quantity");
		assertNotExists("deliveryDate");
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "234");
		assertValue("deliveryDate", "4/23/04");
		closeDialog();
		
		// Return to save and consult for see if the line is edited
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();		
		assertValueInCollection("details", 1, 0, "Special");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "234");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("234"));
		assertNotExists("product.description");
		assertNotExists("quantity");
		assertNotExists("deliveryDate");
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "234");
		assertValue("deliveryDate", "4/23/04");
		closeDialog();
		
		// Verifying that it do not delete member in collection that not are in list
		execute("CRUD.new");
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", year);
		setValue("number", "66");		
		execute("CRUD.refresh");
		assertNoErrors();
		execute("Sections.change", "activeSection=1");
		
		assertCollectionRowCount("details", 3); 
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "234");
		assertValue("deliveryDate", "4/23/04");

		// Remove a row from collection
		execute("Collection.remove");
		assertMessage("Invoice detail deleted from database");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "20");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("20"));
		
		
		//ejecutar("CRUD.save"); // It is not necessary delete for record the deleted of a row 		
		execute("CRUD.new");
		execute("Sections.change", "activeSection=1");
		assertNoErrors();
		assertCollectionRowCount("details", 0); 
		
		// Verifying that line is deleted
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		
		assertCollectionRowCount("details", 2); 
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "20");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("20"));
		
		assertValue("comment", "DETAIL DELETED"); // verifying postdelete-calculator in collection
		
		// Testing if recalculate dependent properties on remove using chechbox in collection
		execute("Sections.change", "activeSection=2");		
		assertValue("amountsSum", getSumOf2ProductsUnitPriceMultiplyBy("2", "20"));		
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 2);
		checkRowCollection("details", 0);
		execute("Collection.removeSelected", "viewObject=xava_view_section1_details");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		assertRowCollectionUnchecked("details", 0); 
		execute("Sections.change", "activeSection=2");		 		
		assertValue("amountsSum", getProductUnitPriceMultiplyBy("20"));
		// end of recalculate testing		

		// Delete		
		execute("CRUD.delete");
		assertMessage("Invoice deleted successfully");
	}
	
	public void testAggregateValidatorUsingReferencesToContainer() throws Exception {  		
		// Create
		execute("CRUD.new");				
						
		setValue("number", "66");
		setValue("paid", "true");
		setValue("customer.number", "1");
		
		// First, vat percentage for not validate errors on save first detail  
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "23");
				
		execute("Sections.change", "activeSection=1");
		
		execute("Collection.new", "viewObject=xava_view_section1_details");
		setValue("serviceType", "0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPrice());
		assertValue("amount", getProductUnitPriceMultiplyBy("20")); 
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());				
		setValue("deliveryDate", "03/18/04");
		setValue("soldBy.number", getProductNumber());
		execute("Collection.save");		
		assertError("It is not possible to add details, the invoice is paid"); 
		
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) {
			execute("CRUD.delete");
			assertMessage("Invoice deleted successfully");
		}		
	}
	
	
	public void testValidationOnSaveAggregateAndModelValidatorReceivesReferenceAndCalculatedProperty() throws Exception {   		
		// Create
		execute("CRUD.new");						
		assertExists("customer.number");
		assertNotExists("vatPercentage");
				
		setValue("number", "66");
		
		setValue("customer.number", "1");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		
		// First, vat percentage for not validation errors on save first detail
		execute("Sections.change", "activeSection=2");
		assertNotExists("customer.number");
		assertExists("vatPercentage");						
		setValue("vatPercentage", "23");
				
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.number");
		assertNotExists("vatPercentage");
		
		assertCollectionRowCount("details", 0); 
		
		assertNoDialog(); 
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertDialog();  
		setValue("serviceType", "0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPricePlus10());
		assertValue("amount", "600.00"); 
		assertValue("product.number", "");
		assertValue("product.description", ""); 
		setValue("deliveryDate", "03/18/04");
		setValue("soldBy.number", getProductNumber());
		execute("Collection.save"); 		
		assertError("It is needed specify a product for a valid invoice detail");
		
		setValue("product.number", getProductNumber()); 
		assertValue("product.description", getProductDescription());
		execute("Collection.save");
		assertError("Invoice price of a product can not be greater to official price of the product");
		assertDialog(); 
		
		setValue("unitPrice", getProductUnitPrice());
		execute("Collection.save");		 
		assertNoErrors();
		assertNoDialog(); 
		
		// Delete
		execute("CRUD.delete");		
		assertMessage("Invoice deleted successfully"); 
	}
	
	
	public void testDefaultValueCalculation_requiredIcons() throws Exception { 		
		execute("CRUD.new");
		assertValue("year", getCurrentYear()); 		
		assertValue("date", getCurrentDate());
		assertValue("yearDiscount", getYearDiscount(getCurrentYear()));
		setValue("year", "2002");
		assertValue("yearDiscount", getYearDiscount("2002"));	
		
		assertRequiredStyle(); 
	}
	
	private void assertRequiredStyle() { 
		assertTrue("year has no required style", getEditorCSSClass("year").contains("ox-required-editor"));
		assertTrue("number has no required style", getEditorCSSClass("number").contains("ox-required-editor"));
		assertTrue("date has no required style", getEditorCSSClass("date").contains("ox-required-editor"));
		assertFalse("comment has required style", getEditorCSSClass("comment").contains("ox-required-editor"));
	}
	
	private String getEditorCSSClass(String property) { 
		HtmlSpan field = (HtmlSpan) getHtmlPage().getElementById("ox_OpenXavaTest_Invoice__editor_" + property);
		return field.getAttribute("class");
	}
		
	public void testCalculatedValuesFromSubviewToUpperView() throws Exception {
		execute("CRUD.new");		
		assertValue("customerDiscount", "");
		assertValue("customerTypeDiscount", "");
		assertValue("customer.number", "");
		assertValue("customer.name", "");
		setValue("customer.number", "1");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		assertValue("customerDiscount", "11.50");
		//assertValue("customerTypeDiscount", "30"); // Still not supported: customer type 
					// changes at same time that number, and to throw the change  
					// of 2 properties at same time still is not supported
		setValue("customer.number", "2");
		assertValue("customer.number", "2");		
		assertValue("customerDiscount", "22.75");
		setValue("customer.number", "3");
		assertValue("customer.number", "3");		
		assertValue("customerDiscount", "0.25");				
	}
	
	public void testCalculatedValueOnChangeBoolean() throws Exception {
		execute("CRUD.new");		
		assertValue("customerDiscount", "");
		setValue("paid", "true");
		assertValue("customerDiscount", "77.00");				
	}
		
	public void testEditableCollectionActions_i18nforMemberOfCollections() throws Exception { 
		execute("CRUD.new");
		String [] initialActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.refresh",
			"SearchForCRUD.search", 
			"InvoicePrint.printPdf",
			"InvoicePrint.print2Pdfs",
			"InvoicePrint.printInvoiceAndCustomer",
			"InvoicePrint.printInvoiceAndCustomer2",
			"InvoicePrint.printExcel",
			"InvoicePrint.printRtf",
			"InvoicePrint.printOdt",
			"Invoice.removeViewDeliveryInInvoice",
			"Invoice.addViewDeliveryInInvoice",			
			"Invoice.viewCustomer",			
			"Sections.change",
			"Customer.changeNameLabel",
			"Customer.prefixStreet",
			"Reference.search",
			"Reference.createNew",			
			"Reference.modify",
			"Mode.list",
			"InvoicePrint.printPdfNewAfter", 
			"Invoice.hideCustomer",
			"Invoice.showCustomer",
			"Invoice.hideAmounts",
			"Invoice.showAmounts"  			
		};		
		assertActions(initialActions); 
				
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		
		execute("Sections.change", "activeSection=1");

		String [] aggregateListActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.refresh",
			"SearchForCRUD.search", 
			"InvoicePrint.printPdf",
			"InvoicePrint.print2Pdfs",
			"InvoicePrint.printInvoiceAndCustomer",
			"InvoicePrint.printInvoiceAndCustomer2",
			"InvoicePrint.printExcel",
			"InvoicePrint.printRtf",
			"InvoicePrint.printOdt",
			"Invoice.removeViewDeliveryInInvoice",
			"Invoice.addViewDeliveryInInvoice",									
			"Invoice.viewCustomer",
			"Mode.list",
			"Sections.change",
			"Invoice.editDetail", // because it is overwrite, otherwise 'Collection.edit'
			"Collection.new",
			"Collection.removeSelected",
			"CollectionCopyPaste.cut", 
			"Print.generatePdf", // In collection
			"Print.generateExcel", // In collection
			"List.filter", 
			"List.orderBy", 
			"List.sumColumn",
			"List.changeColumnName", 
			"InvoicePrint.printPdfNewAfter", 
			"Invoice.hideCustomer",
			"Invoice.showCustomer",
			"Invoice.hideAmounts",
			"Invoice.showAmounts"  			
		};		
		assertActions(aggregateListActions); 
		
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		
		String [] aggregateDetailActions = { 
			"Reference.createNew",
			"Reference.search",
			"Reference.modify",
			"Gallery.edit",
			"Collection.save",
			"Collection.remove",
			"Collection.hideDetail",
			"Invoice.viewProduct"
		};				
		assertActions(aggregateDetailActions);
		
		assertEditable("serviceType");
		
		closeDialog(); 
		// i18n for member of collections
		// In resource file we have: Invoice.details.product.description=Product
		assertLabelInCollection("details", 1, "Product");
	}
	
	public void testDetailActionInCollection_overwriteEditAction_goAndReturnToAnotherXavaView() throws Exception { 
		assertNoListTitle();
		execute("CRUD.new");							
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();		
		execute("Sections.change", "activeSection=1");		
		assertNoDialog(); 
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details"); 
		assertDialog(); 
		assertNoErrors();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		assertValue("remarks", "Edit at " + df.format(new java.util.Date())); 
		
		String productNumber = getValue("product.number");
		assertTrue("Detail must have product number", !Is.emptyString(productNumber));
		String productDescription = getValue("product.description");
		assertTrue("Detail must have product description", !Is.emptyString(productDescription));
				
		execute("Invoice.viewProduct"); 
		assertNoErrors();
		assertNoAction("CRUD.new");
		assertAction("ProductFromInvoice.return");
		assertValue("Product", "number", productNumber);
		assertValue("Product", "description", productDescription);
		
		execute("ProductFromInvoice.return");
		assertNoErrors();
		assertAction("CRUD.new");
		assertNoAction("ProductFromInvoice.return");
		assertValue("year", String.valueOf(getInvoice().getYear()));
		assertValue("number", String.valueOf(getInvoice().getNumber()));									
	}
	
	public void testShowNewViewAndReturn() throws Exception {  		
		execute("CRUD.new");							
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();		
		
		String customerNumber = getValue("customer.number");
		assertTrue("Invoice must to have customer number", !Is.emptyString(customerNumber));
		String customerName = getValue("customer.name");
		assertTrue("Detail must to have customer name", !Is.emptyString(customerName));
				
		execute("Invoice.viewCustomer"); 
		assertNoErrors();
		assertNoAction("CRUD.new");
		assertAction("Return.return");
		assertValue("Customer", "number", customerNumber);
		assertValue("Customer", "name", customerName);
		
		execute("Return.return");
		assertNoErrors();
		assertAction("CRUD.new");
		assertNoAction("Return.return");
		assertValue("year", String.valueOf(getInvoice().getYear()));
		assertValue("number", String.valueOf(getInvoice().getNumber()));									
	}
	
	
	
	public void testViewCollectionElementWithKeyWithReference() throws Exception { 
		deleteInvoiceDeliveries();
		createDelivery();  
		
		execute("CRUD.new");
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		
		execute("CRUD.refresh");
		assertNoErrors();
		
		execute("Sections.change", "activeSection=3");
		assertCollectionRowCount("deliveries", 1);
		
		assertNoDialog(); 
		execute("Collection.view", "row=0,viewObject=xava_view_section3_deliveries");
		assertDialog(); 
		assertValue("number", "666");		
		assertValue("date", "2/22/04");		
		assertValue("description", "DELIVERY JUNIT 666");
		assertNoEditable("number"); 
		assertNoEditable("date"); 		
		assertNoEditable("description"); 		
	}
	
	public void testDefaultValueInDetailCollection() throws Exception {
		execute("List.viewDetail", "row=0"); 
		execute("Sections.change", "activeSection=1");		
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertValue("deliveryDate", getCurrentDate()); 
	}
				
	public void testCalculatedPropertiesInSection_notValidateMainEntityOnNewAggregateIfTheEntityAlreadyExists() throws Exception { 
		execute("List.viewDetail", "row=0");  
		execute("Sections.change", "activeSection=2");		
		String samountsSum = getValue("amountsSum");		
		BigDecimal amountsSum = stringToBigDecimal(samountsSum);
		assertTrue("Amounts sum not must be zero", amountsSum.compareTo(new BigDecimal("0")) != 0);
		String svatPercentage = getValue("vatPercentage"); 		
		BigDecimal vatPercentage = stringToBigDecimal(svatPercentage);
		BigDecimal newVatPercentage = vatPercentage.add(new BigDecimal("1")).setScale(0);
		setValue("vatPercentage", newVatPercentage.toString());		
		BigDecimal vat = amountsSum.multiply(newVatPercentage).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		String svat = nf.format(vat);
		assertValue("vat", svat);
		
		setValue("vatPercentage", "");
		execute("Sections.change", "activeSection=1");
		assertNoDialog();
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertNoErrors();
		assertDialog();
	}		
	
	public void testCharts() throws Exception {
		assertChartIcons(); 
		assertListNotEmpty();
		execute("Invoice.testChartTab");
		assertMessage("xava_chartTab does not exist");
		assertAction("CRUD.deleteSelected"); 
		execute("ListFormat.select", "editor=Charts");
		assertNoAction("CRUD.deleteSelected"); 
		assertNoDialog(); 
		assertChartTypeLink("BAR");
		assertChartTypeLink("LINE");
		assertChartTypeLink("PIE");
		assertValue("xColumn", "year");
		assertValidValueExists("xColumn", OnChangeChartColumnNameAction.SHOW_MORE, "[SHOW MORE...]"); 
		assertChartDisplayed(); 

		reload(); 
		assertChartDisplayed();
		assertSaveRestoreCharts(); 
		assertPieChart(); 
		
		execute("CRUD.new");
		execute("Mode.list");
		assertChartDisplayed(); 
		
		execute("Invoice.testChartTab");
		assertMessage("xava_chartTab exists");		
		assertNoAction("CRUD.deleteSelected"); 
		execute("ListFormat.select", "editor=List");
		assertAction("CRUD.deleteSelected"); 
		execute("Invoice.testChartTab");
		assertMessage("xava_chartTab does not exist");		
	}
	
	private void assertChartIcons() { 
		HtmlElement buttonBar = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Invoice__button_bar");
		HtmlElement icon = buttonBar.getOneHtmlElementByAttribute("i", "class", "mdi mdi-chart-line"); 
		assertEquals("Charts", icon.getAttribute("title")); 
	}

	private void assertSaveRestoreCharts() throws Exception { 
		assertChartTypeSelected("BAR");
		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 1, 0, "Amounts sum");
		assertValueInCollection("columns", 2, 0, "V.A.T.");
		assertValueInCollection("columns", 3, 0, "Details count");
		assertValue("xColumn", "year");
		
		execute("Chart.removeColumn", "row=3,viewObject=xava_view_chartType_columns"); 
		execute("Chart.selectType", "chartType=LINE");
		setValue("xColumn", "number");
		assertChartTypeSelected("LINE");
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 1, 0, "Amounts sum");
		assertValueInCollection("columns", 2, 0, "V.A.T.");
		assertValue("xColumn", "number");
		
		resetModule();
		assertChartTypeSelected("LINE");
		assertCollectionRowCount("columns", 3);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 1, 0, "Amounts sum");
		assertValueInCollection("columns", 2, 0, "V.A.T.");
		assertValue("xColumn", "number");

		setValueInCollection("columns", 3, "name", "detailsCount"); 
		execute("Chart.selectType", "chartType=BAR");
		setValue("xColumn", "year");
		assertChartTypeSelected("BAR");
		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 1, 0, "Amounts sum");
		assertValueInCollection("columns", 2, 0, "V.A.T.");
		assertValueInCollection("columns", 3, 0, "Details count");		
		assertValue("xColumn", "year");
		
		resetModule();
		assertChartTypeSelected("BAR");
		assertCollectionRowCount("columns", 4);
		assertValueInCollection("columns", 0, 0, "Number");
		assertValueInCollection("columns", 1, 0, "Amounts sum");
		assertValueInCollection("columns", 2, 0, "V.A.T.");
		assertValueInCollection("columns", 3, 0, "Details count");
		assertValue("xColumn", "year");
	}

	private void assertPieChart() throws Exception { 
		assertAction("Chart.removeColumn");
		execute("Chart.selectType", "chartType=PIE");
		assertNoAction("Chart.removeColumn");
		assertCollectionRowCount("columns", 1);
		assertValueInCollection("columns", 0, 0, "Number");
	}

	private void assertChartTypeSelected(String chartType) { 
		assertEquals("ox-selected-chart-type", getChartTypeLink(chartType).getAttribute("class"));
	}


	private void assertChartTypeLink(String chartType) { 
		try {
			getChartTypeLink(chartType); 
		}
		catch (ElementNotFoundException ex) {
			fail(chartType + " chart type link missing");  
		}
	}
	
	private HtmlElement getChartTypeLink(String chartType) { 
		return getHtmlPage().getAnchorByHref("javascript:openxava.executeAction('OpenXavaTest', 'Invoice', '', false, 'Chart.selectType', 'chartType=" + chartType + "')"); 
	}
	
	private void assertChartDisplayed() throws Exception {
		DomElement container = getHtmlPage().getElementById(decorateId("xava_chart__container"));
		if (container == null || (!container.hasChildNodes() && container.getChildNodes().size() < 10)) {	
			fail(XavaResources.getString("my_chart_not_displayed"));
		}
	}

	private BigDecimal stringToBigDecimal(String s) throws ParseException {
		NumberFormat nf = NumberFormat.getInstance();
		Number n = nf.parse(s);
		return new BigDecimal(n.toString());
	}
		
	private void deleteInvoiceDeliveries() throws Exception {
		// Also delete transport charge, because they can reference to some delivery
		XPersistence.getManager().createQuery("delete from TransportCharge").executeUpdate();
		java.util.Iterator it = getInvoice().getDeliveries().iterator(); 
		while (it.hasNext()) {
			Delivery delivery = (Delivery) it.next(); 
			XPersistence.getManager().remove(delivery);	
		}		
	}

	private void createDelivery() throws Exception {
		Delivery delivery = new Delivery();
		delivery.setInvoice(getInvoice());
		DeliveryType type = XPersistence.getManager().find(DeliveryType.class, 1);
		delivery.setType(type); 		
		delivery.setNumber(666);
		delivery.setDate(Dates.create(22,2,2004));
		delivery.setDescription("Delivery JUNIT 666");
		delivery.setRemarks("FOUR\nLINES\nCUATRO\nLINEAS"); // It's used in DeliveriesRemarks2002Test
		DeliveryTypeTest.setDeliveryAdvice(delivery, "JUNIT ADVICE");
				
		XPersistence.getManager().persist(delivery);
		XPersistence.commit();
		
	}
	
	
	private String getProductNumber() throws Exception {
		if (productNumber == null) {
			productNumber = String.valueOf(getProduct().getNumber());
		}
		return productNumber;
	}

	private String getProductDescription() throws Exception {
		if (productDescription == null) {
			productDescription = getProduct().getDescription();
		}
		return productDescription;
	}
		
	private String getProductUnitPriceInPesetas() throws Exception {
		if (productUnitPriceInPesetas == null) {			
			productUnitPriceInPesetas = DecimalFormat.getInstance().format(getProduct().getUnitPriceInPesetas());
		}
		return productUnitPriceInPesetas;
		
	}
	
	private String getProductUnitPrice() throws Exception {
		if (productUnitPrice == null) {			
			productUnitPrice = getMoneyFormat().format(getProductUnitPriceDB());
		}
		return productUnitPrice;		
	}
	
	private BigDecimal getProductUnitPriceDB() throws RemoteException, Exception {
		if (productUnitPriceDB == null) {
			productUnitPriceDB = getProduct().getUnitPrice();
		}
		return productUnitPriceDB;
	}
	
	private String getProductUnitPricePlus10() throws Exception {
		if (productUnitPricePlus10 == null) {			
			productUnitPricePlus10 = getMoneyFormat().format(getProductUnitPriceDB().add(new BigDecimal("10")));
		}
		return productUnitPricePlus10;		
	}
	
	private String getProductUnitPriceMultiplyBy(String quantity) throws Exception {
		return getMoneyFormat().format(getProductUnitPriceDB().multiply(new BigDecimal(quantity)));
	}

	private NumberFormat getMoneyFormat() {
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMinimumFractionDigits(2);
		f.setMaximumFractionDigits(2);
		return f;
	}
	
	
	private String getSumOf2ProductsUnitPriceMultiplyBy(String quantity1, String quantity2) throws Exception { 
		BigDecimal sum = getProductUnitPriceDB().multiply(new BigDecimal(quantity1)).add(getProductUnitPriceDB().multiply(new BigDecimal(quantity2)));		
		return getMoneyFormat().format(sum);
	}
	
	
	private Product getProduct() throws Exception {
		if (product == null) {
			product = (Product) XPersistence.getManager().find(Product.class, new Long(2));
		}
		return product;
	}
		
	private String getCurrentDate() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		return df.format(new java.util.Date());
	}
	
	private String getCurrentYear() {
		DateFormat df = new SimpleDateFormat("yyyy");
		return df.format(new java.util.Date());
	}
	
	private String getYearDiscount(String syear) throws Exception {
		int year = Integer.parseInt(syear);
		YearInvoiceDiscountCalculator calculator = new YearInvoiceDiscountCalculator();
		calculator.setYear(year);
		BigDecimal bd = (BigDecimal) calculator.calculate();		
		return getMoneyFormat().format(bd);
	}

	private Invoice getInvoice() throws Exception {
		if (invoice == null) {	
 			Collection invoices = XPersistence.getManager().createQuery("from Invoice").getResultList(); 
 			java.util.Iterator it = invoices.iterator(); 
			while (it.hasNext()) {			
				Invoice inv = (Invoice) it.next();
				if (inv.getDetailsCount() > 0) {
					invoice = inv;
					break;
				}			
			}
			if (invoice == null) {
				fail("It must to exists at least one invoice with details for run this test");
			}
		}
		return invoice;
	}
	
	private void assertDateInList(String date) throws Exception {
		int c = getListRowCount();
		for (int i=0; i<c; i++) {
			assertValueInList(i, "date", date);
		}
	}
	
	private void assertYearInList(String year) throws Exception {
		int c = getListRowCount();
		for (int i=0; i<c; i++) {
			String date = getValueInList(i, "date");
			assertTrue(date + " is not of " + year, date.endsWith(year));
		}
	}
	
	private String formatBigDecimal(String value) throws Exception {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
		nf.setMinimumFractionDigits(2);
		Number b = nf.parse(value);
		return nf.format(b);
	}
	
	private void createOneDetail() throws Exception {
		Calendar date = Calendar.getInstance();
		String todayDate = (date.get(Calendar.MONTH) + 1) + "/" +
				date.get(Calendar.DAY_OF_MONTH) + "/" +
				(date.get(Calendar.YEAR) - 2000);
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.number");
		assertNotExists("vatPercentage");

		assertCollectionRowCount("details", 0);

		execute("Collection.new", "viewObject=xava_view_section1_details");
		setValue("serviceType", "0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPrice());
		assertValue("amount", getProductUnitPriceMultiplyBy("20"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		setValue("deliveryDate", "09/05/2007");
		setValue("soldBy.number", getProductNumber());
		execute("Collection.saveAndStay");
		assertMessage("Invoice detail created successfully");
		// validate if fields are cleared
		assertValue("quantity", "");
		// validate that default values were ran
		assertValue("deliveryDate", todayDate);
		assertAction("Collection.saveAndStay");
		execute("Collection.hideDetail");
		assertNoErrors();
	}

	public void testInvoiceNotFound() throws Exception { 
		execute("CRUD.new");
		// with key
		String year = getValue("year");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Year:" + year);
		// without key
		assertTrue(Is.empty(getValue("year")));
		setValue("date", "1/2/2004");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Date:1/2/04, Paid:No");		
		// with reference
		setValue("customer.number", "43");
		assertValue("customer.name", "Gonzalo Gonzalez");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Number:43, Customer discount:0.25, Paid:No");		
	}
	
	private boolean isVisibleConditionValueTo(int number) {
		return getHtmlPage().getHtmlElementById(Ids.decorate("OpenXavaTest", "Invoice", "conditionValueTo___" + number)).isDisplayed(); 
	}
	
	private boolean isVisibleConditionValueToCalendar(int number) { 
		DomNode node = getHtmlPage().getHtmlElementById(Ids.decorate("OpenXavaTest", "Invoice", "conditionValueTo___" + number)).getNextSibling(); 
		if (!node.isDisplayed()) return false;
		return node.toString().contains("javascript:showCalendar");
	}
	
	public void testBooleanComboHiddenAfterClearCondition() throws Exception{
		HtmlSelect select = getHtmlPage().getElementByName("ox_OpenXavaTest_Invoice__conditionComparator___3"); 
		String s = select.getAttribute("style");
		assertFalse(s.contains("display: none") || s.contains("display:none"));
		// clear condition
		HtmlElement c = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Invoice__xava_clear_condition"); 
		c.click();
		// 
		select = getHtmlPage().getElementByName("ox_OpenXavaTest_Invoice__conditionComparator___3");
		s = select.getAttribute("style");
		assertFalse(s.contains("display: none") || s.contains("display:none"));
	}
	
}