package org.openxava.test.tests.bymodule;

import java.math.*;
import java.rmi.*;
import java.text.*;
import java.util.*;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.html.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.calculators.*;
import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.web.*;



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
		
		execute("CRUD.delete");
		assertError("Cannot be deleted because it has a linked key with table DELIVERY");
		
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
		assertFalse(getHtml().contains("<span id=\"ox_openxavatest_Invoice__sc-container-InvoicePrint_detail\">"));
		
		execute("List.viewDetail", "row=0");
		assertTrue(getHtml().contains("<span id=\"ox_openxavatest_Invoice__sc-container-InvoicePrint_detail\">"));
		assertAction("InvoicePrint.printPdf");
		
		String linkXml = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice__sc-button-InvoicePrint_detail").asXml();
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
		
		// Adding one image		
		uploadFile("photos", "test-images/foto_javi.jpg"); 
		execute("Modification.update");
		assertDialogTitle("Edit - Invoice detail"); 
		assertNoErrors();
		execute("Collection.save");
		assertNoDialog();
		
		// Verifying the image has been added, and removing it  
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		execute("Reference.modify", "model=Product,keyProperty=product.number");
		assertFilesCount("photos", 1); 
		removeFile("photos", 0);		
		
		// Closing all the dialogs with X (we need to test this case)
		assertDialogTitle("Modify - Product");
		assertExists("unitPrice");
		closeDialog();
		assertDialogTitle("Edit - Invoice detail");
		assertExists("quantity");
		closeDialog();
		assertNoDialog();
		assertAction("CRUD.new");
		assertExists("paid");
		
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		execute("Reference.modify", "model=Product,keyProperty=product.number");
		assertFilesCount("photos", 0); 
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
			{ "customer.number", "Customer number" },
			{ "customer.name", "Customer name" },
			{ "customer.type", "Customer type" },
			{ "customer.photo", "Customer photo" },
			{ "customer.telephone", "Customer telephone" },
			{ "customer.email", "Customer email" },
			{ "customer.additionalEmails", "Customer additional emails" },
			{ "customer.website", "Customer web site" },
			{ "customer.remarks", "Customer remarks" },
			{ "customer.relationWithSeller", "Customer relation with seller" },
			{ "customer.passport", "Customer passport" },
			{ "customer.creditCard", "Customer credit card" },
			{ "customer.city", "Customer city" },
			{ "customer.local", "Customer lokal" },
			{ "details.serviceType", "Details service type" },
			{ "details.quantity", "Details quantity" },
			{ "details.unitPrice", "Details unit price" },
			{ "details.deliveryDate", "Details delivery date" },
			{ "details.remarks", "Details remarks" },
			{ "details.amount", "Details amount" },
			{ "details.free", "Details free" },
			{ "deliveries.number", "Deliveries number" },
			{ "deliveries.date", "Deliveries date" },
			{ "deliveries.description", "Deliveries description" },
			{ "deliveries.distance", "Deliveries distance" },
			{ "deliveries.vehicle", "Deliveries vehicle" },
			{ "deliveries.driverType", "Deliveries driver type" },
			{ "deliveries.employee", "Deliveries employee" },
			{ "deliveries.remarks", "Deliveries remarks" },
			{ "deliveries.incidents", "Deliveries incidents" },
			{ "deliveries.dateAsLabel", "Deliveries date as label" },
			{ "deliveries.transportMode", "Deliveries transport mode" },
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
			{ "__MORE__", "[SHOW MORE...]" }
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
			{ "customer.number", "Customer number" },
			{ "customer.name", "Customer name" },
			{ "customer.type", "Customer type" },
			{ "customer.photo", "Customer photo" },
			{ "customer.telephone", "Customer telephone" },
			{ "customer.email", "Customer email" },
			{ "customer.additionalEmails", "Customer additional emails" },
			{ "customer.website", "Customer web site" },
			{ "customer.remarks", "Customer remarks" },
			{ "customer.address.street", "Customer address street" },
			{ "customer.address.city", "Customer address city" },
			{ "customer.address.state.id", "Customer address state id" },
			{ "customer.address.state.name", "State" },
			{ "customer.address.state.fullNameWithFormula", "Customer address state full name with formula" },
			{ "customer.address.state.fullName", "Customer address state full name" },
			{ "customer.address.asString", "Customer address as string" },
			{ "customer.address.zipCode", "Customer address zip code" }, 
			{ "customer.seller.number", "Customer seller number" },
			{ "customer.seller.name", "Customer seller name" },
			{ "customer.seller.level.id", "Customer seller level id" },
			{ "customer.seller.level.description", "Customer seller level description" },
			{ "customer.seller.regions", "Customer seller regions" },
			{ "customer.relationWithSeller", "Customer relation with seller" },
			{ "customer.alternateSeller.number", "Customer alternate seller number" },
			{ "customer.alternateSeller.name", "Customer alternate seller name" },
			{ "customer.alternateSeller.level.id", "Customer alternate seller level id" },
			{ "customer.alternateSeller.level.description", "Customer alternate seller level description" },
			{ "customer.alternateSeller.regions", "Customer alternate seller regions" },
			{ "customer.group.name", "Customer group name" },
			{ "customer.deliveryPlaces.name", "Customer delivery places name" },
			{ "customer.deliveryPlaces.address", "Customer delivery places address" },
			{ "customer.deliveryPlaces.preferredWarehouse.zoneNumber", "Customer delivery places preferred warehouse zone" },
			{ "customer.deliveryPlaces.preferredWarehouse.number", "Warehouse number" },
			{ "customer.deliveryPlaces.preferredWarehouse.name", "Customer delivery places preferred warehouse name" },
			{ "customer.deliveryPlaces.remarks", "Customer delivery places remarks" },
			{ "customer.deliveryPlaces.receptionists.name", "Customer delivery places receptionists name" },
			{ "customer.passport", "Customer passport" },
			{ "customer.creditCard", "Customer credit card" },
			{ "customer.city", "Customer city" },
			{ "customer.local", "Customer lokal" },
			{ "details.serviceType", "Details service type" },
			{ "details.quantity", "Details quantity" },
			{ "details.unitPrice", "Details unit price" },
			{ "details.product.number", "Details product number" },
			{ "details.product.description", "Details product description" },
			{ "details.product.photos", "Details product photos" },
			{ "details.product.familyNumber", "Details product family" },
			{ "details.product.subfamilyNumber", "Details product subfamily" },
			{ "details.product.unitPrice", "Details product unit price" },
			{ "details.product.remarks", "Details product remarks" },
			{ "details.product.unitPriceInPesetas", "Details product unit price in pesetas" },
			{ "details.product.warehouseKey", "Details product warehouse" },
			{ "details.deliveryDate", "Details delivery date" },
			{ "details.soldBy.number", "Details sold by number" },
			{ "details.soldBy.name", "Details sold by name" },
			{ "details.soldBy.level.id", "Details sold by level id" },
			{ "details.soldBy.level.description", "Details sold by level description" },
			{ "details.soldBy.customers.number", "Details sold by customers number" },
			{ "details.soldBy.customers.name", "Details sold by customers name" },
			{ "details.soldBy.customers.type", "Details sold by customers type" },
			{ "details.soldBy.customers.photo", "Details sold by customers photo" },
			{ "details.soldBy.customers.telephone", "Details sold by customers telephone" },
			{ "details.soldBy.customers.email", "Details sold by customers email" },
			{ "details.soldBy.customers.additionalEmails", "Details sold by customers additional emails" },
			{ "details.soldBy.customers.website", "Details sold by customers web site" },
			{ "details.soldBy.customers.remarks", "Details sold by customers remarks" },
			{ "details.soldBy.customers.relationWithSeller", "Details sold by customers relation with seller" },
			{ "details.soldBy.customers.passport", "Details sold by customers passport" },
			{ "details.soldBy.customers.creditCard", "Details sold by customers credit card" },
			{ "details.soldBy.customers.city", "Details sold by customers city" },
			{ "details.soldBy.customers.local", "Details sold by customers lokal" },
			{ "details.soldBy.regions", "Details sold by regions" },
			{ "details.remarks", "Details remarks" },
			{ "details.amount", "Details amount" },
			{ "details.free", "Details free" },
			{ "deliveries.number", "Deliveries number" },
			{ "deliveries.type.number", "Deliveries type number" },
			{ "deliveries.type.description", "Deliveries type description" },
			{ "deliveries.date", "Deliveries date" },
			{ "deliveries.description", "Deliveries description" },
			{ "deliveries.distance", "Deliveries distance" },
			{ "deliveries.vehicle", "Deliveries vehicle" },
			{ "deliveries.driverType", "Deliveries driver type" },
			{ "deliveries.carrier.number", "Deliveries carrier number" },
			{ "deliveries.carrier.name", "Deliveries carrier name" },
			{ "deliveries.carrier.drivingLicence.type", "Deliveries carrier driving licence type" },
			{ "deliveries.carrier.drivingLicence.level", "Deliveries carrier driving licence level" },
			{ "deliveries.carrier.drivingLicence.description", "Deliveries carrier driving licence description" },
			{ "deliveries.carrier.warehouse.zoneNumber", "Deliveries carrier warehouse zone" },
			{ "deliveries.carrier.warehouse.number", "Warehouse number" },
			{ "deliveries.carrier.warehouse.name", "Deliveries carrier warehouse name" },
			{ "deliveries.carrier.remarks", "Deliveries carrier remarks" },
			{ "deliveries.carrier.calculated", "Deliveries carrier calculated" },
			{ "deliveries.employee", "Deliveries employee" },
			{ "deliveries.shipment.type", "Deliveries shipment type" },
			{ "deliveries.shipment.mode", "Deliveries shipment mode" },
			{ "deliveries.shipment.number", "Deliveries shipment number" },
			{ "deliveries.shipment.description", "Deliveries shipment description" },
			{ "deliveries.shipment.time", "Deliveries shipment time" },
			{ "deliveries.shipment.customerContactPerson.name", "Deliveries shipment contact person name" },
			{ "deliveries.remarks", "Deliveries remarks" },
			{ "deliveries.incidents", "Deliveries incidents" },
			{ "deliveries.details.number", "Deliveries details number" },
			{ "deliveries.details.description", "Deliveries details description" },
			{ "deliveries.dateAsLabel", "Deliveries date as label" },
			{ "deliveries.transportMode", "Deliveries transport mode" },
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
		assertValueInCollection("columns", 8, 0, "Customer seller name"); 
		execute("MyReport.editColumn", "row=8,viewObject=xava_view_columns");
		assertValue("name", "customer.seller.name");
		assertValue("label", "Customer seller name");
		assertValidValuesCount("name", defaultColumnNames.length + 1);
	}
	
	public void testMyReportConditionWithBoolanFromList_myReportEditDateWithYearComparator() throws Exception { 
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
		
		closeDialog();
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "");
		assertValueInCollection("columns", 2, 2, "");		
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		assertExists("dateValue");
		assertExists("comparator");
		assertNotExists("booleanValue"); 
		assertNotExists("value");
		assertExists("order");
		
		setValue("comparator", "year_comparator");
		assertNotExists("dateValue");
		assertExists("comparator");
		assertNotExists("booleanValue"); 
		assertExists("value");
		assertExists("order");
		
		setValue("value", "2021");		
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "year =");
		assertValueInCollection("columns", 2, 2, "2021");		
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		assertNotExists("dateValue");
		assertExists("comparator");
		assertNotExists("booleanValue"); 
		assertExists("value");
		assertExists("order");
		assertValue("comparator", "year_comparator");
		assertValue("value", "2021");
		
		setValue("comparator", "=");
		assertExists("dateValue");
		assertExists("comparator");
		assertNotExists("booleanValue"); 
		assertNotExists("value");
		assertExists("order");
		setValue("dateValue", "6/23/2021");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "=");
		assertValueInCollection("columns", 2, 2, "6/23/21"); // 6/23/2021 would be better, but it still a detail pending to improve 
		
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		assertExists("dateValue");
		assertExists("comparator");
		assertNotExists("booleanValue"); 
		assertNotExists("value");
		assertExists("order");
		assertValue("comparator", "eq_comparator");
		assertValue("dateValue", "6/23/2021");
		
		setValue("comparator", "year_comparator");
		assertNotExists("dateValue");
		assertExists("comparator");
		assertNotExists("booleanValue"); 
		assertExists("value");
		assertExists("order");
		setValue("value", "2021");		
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 0, "Date");
		assertValueInCollection("columns", 2, 1, "year =");
		assertValueInCollection("columns", 2, 2, "2021");
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
		
		String dateValue = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice__editor_dateValue").asXml();
		assertTrue(dateValue.contains("xava_date"));
		assertTrue(dateValue.contains("mdi-calendar"));
		
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
		reload(); 
		
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
	
	public void testSearchUsesSimpleView_isolateModuleSessionForEachBrowserTabWithSectionAndReloadingModuleWithInitTrue() throws Exception {  
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
		
		assertValue("year", "2002");						
		String moduleURL = getModuleURL() + "&init=true";
		getHtmlPage().executeJavaScript("window.open('" + moduleURL + "', '_blank')"); 
		waitAJAX();
		assertValue("year", "2002");
		execute("Sections.change", "activeSection=1");
		assertValue("year", "2002");		
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
		
		assertTotalInCollection("details", 0, 2, "");
		execute("List.sumColumn", "property=quantity,collection=details");
		assertTotalInCollection("details", 0, 2, "Sum of Quantity");
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
		assertGenerateTwoReportsAtOnce("InvoicePrint.print2Rtfs", "rtf"); 
	}
	
	public void testGenerateTwoReportsAtOnceWithDifferentParameters() throws Exception { 
		assertGenerateTwoReportsAtOnce("InvoicePrint.printInvoiceAndCustomer", "pdf"); 
	}
	
	public void testGenerateTwoReportsAtOnceWithDifferentParametersUsingAddParameters() throws Exception { 
		assertGenerateTwoReportsAtOnce("InvoicePrint.printInvoiceAndCustomer2", "pdf"); 
	}
	
	private void assertGenerateTwoReportsAtOnce(String action, String contentType) throws Exception {   
		execute("List.viewDetail", "row=0");
		execute(action);		
		assertNoErrors();		
		assertPopupCount(2); 
		assertContentTypeForPopup(0, "application/" + contentType);
		assertContentTypeForPopup(1, "application/" + contentType);		
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
		execute("Invoice.editDetail", "row=10,viewObject=xava_view_section1_details");
		closeDialog();
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
		assertValueInCollection("xavaPropertiesList",  0, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Considerable");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Customer additional emails");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Customer city");
		assertValueInCollection("xavaPropertiesList",  4, 0, "Customer credit card");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Customer email");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Customer lokal");
		assertValueInCollection("xavaPropertiesList",  8, 0, "Customer name");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Customer number");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Customer passport");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Customer photo");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Customer relation with seller");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Customer remarks");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Customer telephone");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Customer type");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList", 17, 0, "Customer web site");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Deliveries date");
		assertValueInCollection("xavaPropertiesList", 19, 0, "Deliveries date as label");
		
		execute("AddColumns.showMoreColumns");
		assertCollectionRowCount("xavaPropertiesList", 116);  
		assertValueInCollection("xavaPropertiesList",   0, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",   1, 0, "Considerable");
		assertValueInCollection("xavaPropertiesList",   2, 0, "Customer additional emails");
		assertValueInCollection("xavaPropertiesList",   3, 0, "Customer address as string");
		assertValueInCollection("xavaPropertiesList",   4, 0, "Customer address city");
		assertValueInCollection("xavaPropertiesList",   5, 0, "Customer address state");
		assertValueInCollection("xavaPropertiesList",   6, 0, "Customer address state full name");
		assertValueInCollection("xavaPropertiesList",   7, 0, "Customer address state full name with formula");
		assertValueInCollection("xavaPropertiesList",   8, 0, "Customer address state id");
		assertValueInCollection("xavaPropertiesList",   9, 0, "Customer address street");
		assertValueInCollection("xavaPropertiesList",  10, 0, "Customer address zip code");
		assertValueInCollection("xavaPropertiesList",  11, 0, "Customer alternate seller level description");
		assertValueInCollection("xavaPropertiesList",  12, 0, "Customer alternate seller level id");
		assertValueInCollection("xavaPropertiesList",  13, 0, "Customer alternate seller name");
		assertValueInCollection("xavaPropertiesList",  14, 0, "Customer alternate seller number");
		assertValueInCollection("xavaPropertiesList",  15, 0, "Customer alternate seller regions");
		assertValueInCollection("xavaPropertiesList",  16, 0, "Customer city");
		assertValueInCollection("xavaPropertiesList",  17, 0, "Customer credit card");
		assertValueInCollection("xavaPropertiesList",  18, 0, "Customer delivery places address");
		assertValueInCollection("xavaPropertiesList",  19, 0, "Customer delivery places name");
		assertValueInCollection("xavaPropertiesList",  20, 0, "Customer delivery places preferred warehouse name");
		assertValueInCollection("xavaPropertiesList",  21, 0, "Customer delivery places preferred warehouse number");
		assertValueInCollection("xavaPropertiesList",  22, 0, "Customer delivery places preferred warehouse zone");
		assertValueInCollection("xavaPropertiesList",  23, 0, "Customer delivery places receptionists name");
		assertValueInCollection("xavaPropertiesList",  24, 0, "Customer delivery places remarks");
		assertValueInCollection("xavaPropertiesList",  25, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  26, 0, "Customer email");
		assertValueInCollection("xavaPropertiesList",  27, 0, "Customer group name");
		assertValueInCollection("xavaPropertiesList",  28, 0, "Customer lokal");
		assertValueInCollection("xavaPropertiesList",  29, 0, "Customer name");
		assertValueInCollection("xavaPropertiesList",  30, 0, "Customer number");
		assertValueInCollection("xavaPropertiesList",  31, 0, "Customer passport");
		assertValueInCollection("xavaPropertiesList",  32, 0, "Customer photo");
		assertValueInCollection("xavaPropertiesList",  33, 0, "Customer relation with seller");
		assertValueInCollection("xavaPropertiesList",  34, 0, "Customer remarks");
		assertValueInCollection("xavaPropertiesList",  35, 0, "Customer seller level description");
		assertValueInCollection("xavaPropertiesList",  36, 0, "Customer seller level id");
		assertValueInCollection("xavaPropertiesList",  37, 0, "Customer seller name");
		assertValueInCollection("xavaPropertiesList",  38, 0, "Customer seller number");
		assertValueInCollection("xavaPropertiesList",  39, 0, "Customer seller regions");
		assertValueInCollection("xavaPropertiesList",  40, 0, "Customer telephone");
		assertValueInCollection("xavaPropertiesList",  41, 0, "Customer type");
		assertValueInCollection("xavaPropertiesList",  42, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList",  43, 0, "Customer web site");
		assertValueInCollection("xavaPropertiesList",  44, 0, "Deliveries carrier calculated");
		assertValueInCollection("xavaPropertiesList",  45, 0, "Deliveries carrier driving licence description");
		assertValueInCollection("xavaPropertiesList",  46, 0, "Deliveries carrier driving licence level");
		assertValueInCollection("xavaPropertiesList",  47, 0, "Deliveries carrier driving licence type");
		assertValueInCollection("xavaPropertiesList",  48, 0, "Deliveries carrier name");
		assertValueInCollection("xavaPropertiesList",  49, 0, "Deliveries carrier number");
		assertValueInCollection("xavaPropertiesList",  50, 0, "Deliveries carrier remarks");
		assertValueInCollection("xavaPropertiesList",  51, 0, "Deliveries carrier warehouse name");
		assertValueInCollection("xavaPropertiesList",  52, 0, "Deliveries carrier warehouse number");
		assertValueInCollection("xavaPropertiesList",  53, 0, "Deliveries carrier warehouse zone");
		assertValueInCollection("xavaPropertiesList",  54, 0, "Deliveries date");
		assertValueInCollection("xavaPropertiesList",  55, 0, "Deliveries date as label");
		assertValueInCollection("xavaPropertiesList",  56, 0, "Deliveries description");
		assertValueInCollection("xavaPropertiesList",  57, 0, "Deliveries details description");
		assertValueInCollection("xavaPropertiesList",  58, 0, "Deliveries details number");
		assertValueInCollection("xavaPropertiesList",  59, 0, "Deliveries distance");
		assertValueInCollection("xavaPropertiesList",  60, 0, "Deliveries driver type");
		assertValueInCollection("xavaPropertiesList",  61, 0, "Deliveries employee");
		assertValueInCollection("xavaPropertiesList",  62, 0, "Deliveries incidents");
		assertValueInCollection("xavaPropertiesList",  63, 0, "Deliveries number");
		assertValueInCollection("xavaPropertiesList",  64, 0, "Deliveries remarks");
		assertValueInCollection("xavaPropertiesList",  65, 0, "Deliveries shipment contact person name");
		assertValueInCollection("xavaPropertiesList",  66, 0, "Deliveries shipment description");
		assertValueInCollection("xavaPropertiesList",  67, 0, "Deliveries shipment mode");
		assertValueInCollection("xavaPropertiesList",  68, 0, "Deliveries shipment number");
		assertValueInCollection("xavaPropertiesList",  69, 0, "Deliveries shipment time");
		assertValueInCollection("xavaPropertiesList",  70, 0, "Deliveries shipment type");
		assertValueInCollection("xavaPropertiesList",  71, 0, "Deliveries transport mode");
		assertValueInCollection("xavaPropertiesList",  72, 0, "Deliveries type description");
		assertValueInCollection("xavaPropertiesList",  73, 0, "Deliveries type number");
		assertValueInCollection("xavaPropertiesList",  74, 0, "Deliveries vehicle");
		assertValueInCollection("xavaPropertiesList",  75, 0, "Delivery date");
		assertValueInCollection("xavaPropertiesList",  76, 0, "Details amount");
		assertValueInCollection("xavaPropertiesList",  77, 0, "Details delivery date");
		assertValueInCollection("xavaPropertiesList",  78, 0, "Details free");
		assertValueInCollection("xavaPropertiesList",  79, 0, "Details product description");
		assertValueInCollection("xavaPropertiesList",  80, 0, "Details product family");
		assertValueInCollection("xavaPropertiesList",  81, 0, "Details product number");
		assertValueInCollection("xavaPropertiesList",  82, 0, "Details product photos");
		assertValueInCollection("xavaPropertiesList",  83, 0, "Details product remarks");
		assertValueInCollection("xavaPropertiesList",  84, 0, "Details product subfamily");
		assertValueInCollection("xavaPropertiesList",  85, 0, "Details product unit price");
		assertValueInCollection("xavaPropertiesList",  86, 0, "Details product unit price in pesetas");
		assertValueInCollection("xavaPropertiesList",  87, 0, "Details product warehouse");
		assertValueInCollection("xavaPropertiesList",  88, 0, "Details quantity");
		assertValueInCollection("xavaPropertiesList",  89, 0, "Details remarks");
		assertValueInCollection("xavaPropertiesList",  90, 0, "Details service type");
		assertValueInCollection("xavaPropertiesList",  91, 0, "Details sold by customers additional emails");
		assertValueInCollection("xavaPropertiesList",  92, 0, "Details sold by customers city");
		assertValueInCollection("xavaPropertiesList",  93, 0, "Details sold by customers credit card");
		assertValueInCollection("xavaPropertiesList",  94, 0, "Details sold by customers email");
		assertValueInCollection("xavaPropertiesList",  95, 0, "Details sold by customers lokal");
		assertValueInCollection("xavaPropertiesList",  96, 0, "Details sold by customers name");
		assertValueInCollection("xavaPropertiesList",  97, 0, "Details sold by customers number");
		assertValueInCollection("xavaPropertiesList",  98, 0, "Details sold by customers passport");
		assertValueInCollection("xavaPropertiesList",  99, 0, "Details sold by customers photo");
		assertValueInCollection("xavaPropertiesList", 100, 0, "Details sold by customers relation with seller");
		assertValueInCollection("xavaPropertiesList", 101, 0, "Details sold by customers remarks");
		assertValueInCollection("xavaPropertiesList", 102, 0, "Details sold by customers telephone");
		assertValueInCollection("xavaPropertiesList", 103, 0, "Details sold by customers type");
		assertValueInCollection("xavaPropertiesList", 104, 0, "Details sold by customers web site");
		assertValueInCollection("xavaPropertiesList", 105, 0, "Details sold by level description");
		assertValueInCollection("xavaPropertiesList", 106, 0, "Details sold by level id");
		assertValueInCollection("xavaPropertiesList", 107, 0, "Details sold by name");
		assertValueInCollection("xavaPropertiesList", 108, 0, "Details sold by number");
		assertValueInCollection("xavaPropertiesList", 109, 0, "Details sold by regions");
		assertValueInCollection("xavaPropertiesList", 110, 0, "Details unit price");
		assertValueInCollection("xavaPropertiesList", 111, 0, "Product unit price sum");
		assertValueInCollection("xavaPropertiesList", 112, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 113, 0, "Total");
		assertValueInCollection("xavaPropertiesList", 114, 0, "VAT %");
		assertValueInCollection("xavaPropertiesList", 115, 0, "Year discount");
		
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
		assertLabelInList(8, "Customer address city");

		// Restoring, for next time that test execute
		removeColumn(8); 
		reload(); // Remove column does not work completely with HtmlUnit so a reload is needed()
		assertListColumnCount(8); 

		// Always starts with 20
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); 
	}
	
	public void testCustomizeListSearchColumns_customizeListPressEnterWithoutChoosingColumns() throws Exception {   
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Comment");  
		assertValueInCollection("xavaPropertiesList", 19, 0, "Deliveries date as label"); 
		assertAction("AddColumns.showMoreColumns");

		HtmlInput searchBox = getHtmlPage().getHtmlElementById("xava_search_columns_text");
		searchBox.type("DISCOUNT");
		assertEquals("DISCOUNT", searchBox.getValue()); 
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertCollectionRowCount("xavaPropertiesList", 4); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList", 2, 0, "Seller discount");
		assertValueInCollection("xavaPropertiesList", 3, 0, "Year discount");
		assertNoAction("AddColumns.showMoreColumns");		
		
		searchBox.type("\b\b\b\b\b\b\b\b");
		assertEquals("", searchBox.getValue()); 
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertCollectionRowCount("xavaPropertiesList", 20); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Comment"); 
		assertValueInCollection("xavaPropertiesList", 19, 0, "Deliveries date as label"); 
		assertAction("AddColumns.showMoreColumns");
		
		execute("AddColumns.showMoreColumns");		
		assertCollectionRowCount("xavaPropertiesList", 116); 
		searchBox = getHtmlPage().getHtmlElementById("xava_search_columns_text");
		searchBox.type("DISCOUNT");
		assertEquals("DISCOUNT", searchBox.getValue()); 
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
		
		assertCollectionRowCount("xavaPropertiesList", 20); // It only loads the first 20.
		assertValueInCollection("xavaPropertiesList",  0, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Considerable");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Customer additional emails");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Customer city");
		assertValueInCollection("xavaPropertiesList",  4, 0, "Customer credit card");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Customer email");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Customer lokal");
		assertValueInCollection("xavaPropertiesList",  8, 0, "Customer name");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Customer number");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Customer passport");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Customer photo");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Customer relation with seller");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Customer remarks");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Customer telephone");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Customer type");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList", 17, 0, "Customer web site");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Deliveries date");
		assertValueInCollection("xavaPropertiesList", 19, 0, "Deliveries date as label");
		checkRow("selectedProperties", "customer.name"); 
		checkRow("selectedProperties", "deliveries.date"); 
		 		
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
		assertLabelInList(9, "Deliveries date");
		
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 20); 
		assertValueInCollection("xavaPropertiesList",  0, 0, "Comment");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Considerable");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Customer additional emails");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Customer city");
		assertValueInCollection("xavaPropertiesList",  4, 0, "Customer credit card");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Customer discount");
		assertValueInCollection("xavaPropertiesList",  6, 0, "Customer email");
		assertValueInCollection("xavaPropertiesList",  7, 0, "Customer lokal");
		assertValueInCollection("xavaPropertiesList",  8, 0, "Customer number");
		assertValueInCollection("xavaPropertiesList",  9, 0, "Customer passport");
		assertValueInCollection("xavaPropertiesList", 10, 0, "Customer photo");
		assertValueInCollection("xavaPropertiesList", 11, 0, "Customer relation with seller");
		assertValueInCollection("xavaPropertiesList", 12, 0, "Customer remarks");
		assertValueInCollection("xavaPropertiesList", 13, 0, "Customer telephone");
		assertValueInCollection("xavaPropertiesList", 14, 0, "Customer type");
		assertValueInCollection("xavaPropertiesList", 15, 0, "Customer type discount");
		assertValueInCollection("xavaPropertiesList", 16, 0, "Customer web site");
		assertValueInCollection("xavaPropertiesList", 17, 0, "Deliveries date as label");
		assertValueInCollection("xavaPropertiesList", 18, 0, "Deliveries description");
		assertValueInCollection("xavaPropertiesList", 19, 0, "Deliveries distance");
		
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
		assertLabelInList(9, "Deliveries date");

		
		// To test that detail view is not broken because of the dialog
		execute("CRUD.new");
		assertExists("year");
		
		execute("Mode.list");
		assertLabelInList(9, "Deliveries date"); 
		execute("List.changeColumnName", "property=customer.name"); 
		assertDialog();
		assertValue("name", "Customer");
		setValue("name", "The Customer name ");
		execute("ChangeColumnName.change");
		assertLabelInList(8, "The Customer name");
	}
	
	private void doTestCustomizeList_storePreferences() throws Exception {
		// This test trusts that 'testCustomizeList_addColumns' is executed before
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count"); 
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "The Customer name"); 
		assertLabelInList(9, "Deliveries date");
	}	
	
	public void testGenerateExcel() throws Exception {
		execute("Print.generateExcel"); 
		assertContentTypeForPopup("text/x-csv");
		assertExcel(
			"Year;Number;Date;Amounts sum;V.A.T.;Details count;Paid;Importance",	
			"2002;1;\"1/1/2002\";\"2500.00\";\"400.00\";2;\"No\";\"Normal\""); // "2500.00" instead of "2,500.00"
		
		setLocale("es");
		execute("Print.generateExcel");		
		assertContentTypeForPopup("text/x-csv");
		assertExcel( 
			"Año;Número;Fecha;Suma importes;I.V.A.;Cantidad líneas;Pagada;Importancia",	
			"2002;1;\"01/01/2002\";\"2500,00\";\"400,00\";2;\"No\";\"Normal\""); // "2500,00" instead of "2.500,00"		
	}
	
	private void assertExcel(String expectedHeader, String expectedLine) throws Exception { 
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", expectedHeader, header);
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
		
		String [] condition2002 = { " ", " ", "2002", "true" }; // We suppose that there are invoices in 2002
		setConditionValues(condition2002);
		execute("List.filter");
		assertYearInList("02");

		String [] condition2004 = { " ", " ", "2004", "true" }; // We suppose that there are invoices in 2004
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
		assertValue("Customer", "type", "2");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		assertNoErrors();
		execute("ReferenceSearch.cancel");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");	
		assertValue("Customer", "type", "2");		
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
		
		setValue("date", "4/1/44"); // If current year is 2024 
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/2044"); 
		
		setValue("date", "040145"); // If current year is 2024 
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/1945"); 
		
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
		setValue("serviceType", "");
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
		setValue("serviceType", "0");
		setValue("quantity", "200");
		setValue("unitPrice", getProductUnitPrice());		
		assertValue("amount", getProductUnitPriceMultiplyBy("200"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		setValue("deliveryDate", "3/19/04"); // Testing multiple-mapping in aggregate
		setValue("soldBy.number", getProductNumber());		
		execute("Collection.saveAndStay");
		
		setValue("serviceType", "1");
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
		assertValue("deliveryDate", "3/19/2004");
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
		assertValue("deliveryDate", "4/23/2004");
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
		assertValue("deliveryDate", "4/23/2004");
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
		assertValue("deliveryDate", "4/23/2004");

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
		HtmlSpan field = (HtmlSpan) getHtmlPage().getElementById("ox_openxavatest_Invoice__editor_" + property);
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
			"InvoicePrint.print2Rtfs", 
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
			"Reference.clear",
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
			"InvoicePrint.print2Rtfs", 
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
			"Reference.clear",
			"Collection.save",
			"Collection.remove",
			"Collection.hideDetail",
			"Collection.next",
			"Collection.previous",
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
		assertValue("date", "2/22/2004");		
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
		
		execute("ListFormat.select", "editor=Charts");
		execute("Chart.selectType", "chartType=PIE");
		setValueInCollection("columns", 0, "name", "total");
		String html = getHtml(); 
		assertFalse(html.contains("Editor not available"));
		assertTrue(html.contains("ox_openxavatest_Invoice__xava_chart__columnCount")); // Anything from Chart editor
	}
	
	private void assertChartIcons() { 
		HtmlElement buttonBar = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice__button_bar");
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

	private void assertChartTypeSelected(String chartType) throws Exception { 
		assertEquals("xava_action ox-selected-chart-type", getChartTypeLink(chartType).getAttribute("class")); 
	}


	private void assertChartTypeLink(String chartType) throws Exception {
		try {
			getChartTypeLink(chartType); 
		}
		catch (ElementNotFoundException ex) {
			fail(chartType + " chart type link missing");  
		}
	}
	
	private HtmlElement getChartTypeLink(String chartType) throws Exception { 
		HtmlElement link = getHtmlPage().getBody().getOneHtmlElementByAttribute("a", "data-argv", "chartType=" + chartType);
		assertEquals("Action for select chart type should be Chart.selectType", "Chart.selectType", link.getAttribute("data-action"));
		return link;
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
		DateFormat df = new SimpleDateFormat("M/d/yyyy"); 
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
	
	private void createOneDetail() throws Exception {
		Calendar date = Calendar.getInstance();
		String todayDate = (date.get(Calendar.MONTH) + 1) + "/" +
			date.get(Calendar.DAY_OF_MONTH) + "/" +
			(date.get(Calendar.YEAR)); 
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
		assertError("Object of type Invoice does not exists with key Date:1/2/2004, Paid:No"); 		
		// with reference
		setValue("customer.number", "43");
		assertValue("customer.name", "Gonzalo Gonzalez");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Number:43, Customer discount:0.25, Paid:No");		
	}
	
	private boolean isVisibleConditionValueTo(int number) {
		return getHtmlPage().getHtmlElementById(Ids.decorate("openxavatest", "Invoice", "conditionValueTo___" + number)).isDisplayed(); 
	}
	
	private boolean isVisibleConditionValueToCalendar(int number) {
		HtmlElement parent = (HtmlElement) getHtmlPage().getHtmlElementById(Ids.decorate("openxavatest", "Invoice", "conditionValueTo___" + number)).getParentNode();
		List<HtmlElement> links = parent.getElementsByTagName("a");
		if (links.isEmpty()) return false;
		HtmlElement calendar = links.get(0);
		if (!calendar.isDisplayed()) return false;
		String html = parent.asXml();
		return html.contains("mdi-calendar") && html.contains("xava_date");
	}
	
	public void testBooleanComboHiddenAfterClearCondition() throws Exception{
		HtmlSelect select = getHtmlPage().getElementByName("ox_openxavatest_Invoice__conditionComparator___3"); 
		String s = select.getAttribute("style");
		assertFalse(s.contains("display: none") || s.contains("display:none"));
		// clear condition
		HtmlElement c = getHtmlPage().getHtmlElementById("ox_openxavatest_Invoice__xava_clear_condition"); 
		c.click();
		// 
		select = getHtmlPage().getElementByName("ox_openxavatest_Invoice__conditionComparator___3");
		s = select.getAttribute("style");
		assertFalse(s.contains("display: none") || s.contains("display:none"));
	}
	
}