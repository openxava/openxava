package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.html.*;

import static org.openxava.util.Strings.multiline;

/**
 * 
 * @author Javier Paniza
 */

public class ProductTest extends ModuleTestBase { 
	
	private String [] newActions = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",
		"CRUD.new",
		"CRUD.save",
		"CRUD.refresh",
		"EditableOnOff.setOn",
		"EditableOnOff.setOff",
		"Mode.list",
		"Product.setLimitZoneTo1",
		"Product.setLimitZoneTo0",
		"Product.changeProductPrice",
		"Gallery.edit"
	};
	
	private String [] editActions = { 
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",
		"CRUD.new",
		"CRUD.save",
		"CRUD.delete",
		"CRUD.refresh",
		"EditableOnOff.setOn",
		"EditableOnOff.setOff",
		"Mode.list",
		"Product.setLimitZoneTo1",
		"Product.setLimitZoneTo0",
		"Product.changeProductPrice",
		"Gallery.edit"
	};
	
	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"ExtendedPrint.myReports",
		"ImportData.importData", 
		"CRUD.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow",
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn",
		"List.changeConfiguration",
		"List.changeColumnName", 
		"ListFormat.select", 
	};
		
	public ProductTest(String testName) {
		super(testName, "Product");		
	}
	
	public ProductTest(String testName, String module) {
		super(testName, module);		
	}
		
	/*
	 * There is no point here, the autonumeric,js truncate 
	 * the value to the number of decimals.
	public void testMoneyScaleValidator() throws Exception {		
		execute("List.viewDetail", "row=0");
		setValue("unitPrice", "11.123");
		execute("CRUD.save");
		assertError("Unit price in Product has too much decimals. Only 2 are allowed");
	}
	*/
	
	public void testSumInMyReport() throws Exception { 
		execute("ExtendedPrint.myReports");
		
		assertValueInCollection("columns", 0, 0, "Number");		
		execute("MyReport.editColumn", "row=0,viewObject=xava_view_columns");
		assertExists("sum");
		closeDialog();
		
		assertValueInCollection("columns", 1, 0, "Description");		
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		assertNotExists("sum");
		closeDialog();
				
		assertValueInCollection("columns", 3, 0, "Unit price in pesetas");		
		execute("MyReport.editColumn", "row=3,viewObject=xava_view_columns");
		assertNotExists("sum");
		closeDialog();			
		
		assertValueInCollection("columns", 2, 0, "Unit price");
		assertValueInCollection("columns", 2, 4, ""); 
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		setValue("sum", "true");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 4, "Sum"); 
		execute("MyReport.generatePdf");
		assertPopupPDFLinesCount(12); // There are 7 products 
		assertPopupPDFLine(10, "629.00"); // The sum of the 7 product, if the price of some product has been changed you have to change this value

		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 2, 4, "Sum"); 
		execute("MyReport.editColumn", "row=2,viewObject=xava_view_columns");
		setValue("sum", "false");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 2, 4, ""); 
		execute("MyReport.generatePdf");
		
		assertPopupPDFLinesCount(11); // There are 7 products, but now without the summation
	}
	
	public void testCards() throws Exception { 
		execute("ListFormat.select", "editor=Cards");
		assertListRowCount(7);
		assertValueInList(2, multiline("XAVA", "3", "Unit price: 0.00, Unit price in pesetas: 0"));
		
		assertFalse(getHtml().contains("There are no records"));
		assertTrue(getHtmlPage().getElementById("xava_loading_more_elements") == null);
		
		// To test if the click works, specially that the javascript is well formed including the correct row an so,
		// for a regular test using execute("List.viewDetail", "row=2") is the way to go
		HtmlElement card = assertCard3Title("XAVA");
		assertNoAction("CRUD.save");
		String onClick = card.getOnClickAttribute();
		assertTrue(onClick.startsWith("if (!getSelection().toString()) ")); // getSelection() does not work in HtmlUnit
		onClick = onClick.replace("if (!getSelection().toString()) ", "");	// so we remove it				 
		getHtmlPage().executeJavaScript(onClick);
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertAction("CRUD.save");
		assertValue("number", "3");
		assertValue("description", "XAVA");
		setValue("description", "XAVAPRO"); 
		execute("CRUD.save");
		
		execute("Mode.list");
		assertCard3Title("XAVAPRO");
		execute("ListFormat.select", "editor=List");
		assertListRowCount(7);
		setConditionValues("66");
		execute("List.filter");
		assertListRowCount(0);
		execute("ListFormat.select", "editor=Cards");
		assertListRowCount(0);
		assertTrue(getHtml().contains("There are no records"));
		
		execute("ListFormat.select", "editor=List");
		assertListRowCount(0);
		setConditionValues("1");
		execute("List.filter");
		assertListRowCount(1);
		execute("ListFormat.select", "editor=Cards");
		assertListRowCount(1); // This was 2 because a bug, it shown "Loading..."
		
		execute("ListFormat.select", "editor=List");
		
		execute("CRUD.new");
		setValue("number", "3");
		execute("CRUD.refresh");
		assertValue("description", "XAVAPRO");
		setValue("description", "XAVA"); 
		execute("CRUD.save");
		assertNoErrors();
	}

	private HtmlElement assertCard3Title(String expectedTitle) { 
		HtmlElement body = (HtmlElement) getHtmlPage().getElementsByTagName("body").get(0);
		HtmlElement card = body.getElementsByAttribute("div", "class", "ox-card").get(2);
		assertEquals(multiline(expectedTitle, "3", "Unit price: 0.00, Unit price in pesetas: 0"), card.asText());
		return card;
	}
		
	public void testFiltersInDescriptionsEditor() throws Exception { 
		execute("CRUD.new");
		execute("Product.setLimitZoneTo1"); 
		Warehouse key1 = new Warehouse();
		key1.setZoneNumber(1);
		key1.setNumber(1);
		Warehouse key2 = new Warehouse();
		key2.setZoneNumber(1);
		key2.setNumber(2);
		Warehouse key3 = new Warehouse();
		key3.setZoneNumber(1);
		key3.setNumber(3);		
		
		String [][] zone1WarehouseValues = new String [][] {
			{ "", "" },
			{ toKeyString(key1), "CENTRAL VALENCIA" },
			{ toKeyString(key3), "VALENCIA NORTE" },
			{ toKeyString(key2), "VALENCIA SURETE" }
		};
		
		assertValidValues("warehouseKey", zone1WarehouseValues); 
	}

	public void testDepedentsStereotypesAndDescriptionsEditor_someDescriptions_and_formatters() throws Exception { 
		assertActions(listActions);
		
		execute("CRUD.new");
		assertActions(newActions);

		// Verifying initial status		
		String [][] familyValues = {
			{ "", "" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" }	
		};
		
		assertValue("familyNumber", "");		
		assertValidValues("familyNumber", familyValues); 
		
		String [][] emptyValues = {
			{ "", "" }
		};
		
		assertValue("subfamilyNumber", "");		
		assertValidValues("subfamilyNumber", emptyValues);
		
		// Change value
		setValue("familyNumber", "2");
		String [][] hardwareValues = {
			{ "", ""},
			{ "11", "011 SERVIDORES"},								
			{ "12", "012 PC"},
			{ "13", "013 PERIFERICOS"}							
		};
		assertValue("subfamilyNumber", "");
		assertValidValues("subfamilyNumber", hardwareValues);
		
		// We change the value again
		setValue("familyNumber", "1");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "01 DESARROLLO"},
			{ "2", "02 GESTION"},						  
			{ "3", "03 SISTEMA"}						
		};
		assertValue("subfamilyNumber", "");
		assertValidValues("subfamilyNumber", softwareValues);										
	}
	
	public void testDescriptionsFormatterWhenReadOnly() throws Exception {
		execute("List.viewDetail", "row=0");
		String subfamily = getDescriptionValue("subfamilyNumber");
		assertTrue("Subfamily must not to be empty", !Is.emptyString(subfamily));
		execute("EditableOnOff.setOff");
		assertDescriptionValue("subfamilyNumber", subfamily);		
	}
	
	public void testNavigationWithDepedentsStereotypes() throws Exception { 
		assertActions(listActions);
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");
		assertValue("familyNumber", "1");
		assertValue("subfamilyNumber", "2");		
		execute("Navigation.next");		
		assertValue("number", "2"); 
		assertValue("familyNumber", "2");
		assertValue("subfamilyNumber", "11");		
		execute("Navigation.next");
		assertValue("number", "3");
		assertValue("familyNumber", "1");
		assertValue("subfamilyNumber", "1");						
	}
	
	public void testCreateWithDescriptionsEditorsAndFormatters() throws Exception {
		assertActions(listActions);
		
		// Create
		execute("CRUD.new");
		assertActions(newActions);
		setValue("number", "66");
		setValue("description", "TEST PRODUCT");
		setValue("familyNumber", "1");
		setValue("subfamilyNumber", "1");
		setValue("warehouseKey", "[.1.1.]");
		setValue("unitPrice", "125.66");
		assertNoEditable("unitPriceInPesetas");
		execute("CRUD.save");				
		assertNoErrors();
				
		// Searching for verify
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "66");
		assertValue("description", "TEST PRODUCT");
		assertValue("familyNumber", "1");
		assertValue("subfamilyNumber", "1");
		assertValue("warehouseKey", "[.1.1.]");
		assertValue("unitPrice", "125.66");
				
		// Go to page for delete
		execute("CRUD.delete");
		assertMessage("Product deleted successfully");		
	}
	
	public void testValueCalculatedDependent() throws Exception {
		assertActions(listActions);
		
		execute("CRUD.new");
		assertActions(newActions);
		
		// Change value
		setValue("unitPrice", "100");
		assertValue("unitPriceInPesetas", "16,639");		
	}
	
	public void testCalculatedDefaultValueDependent() throws Exception { 
		assertActions(listActions);				
		execute("CRUD.new");
		assertActions(newActions);	
		assertValue("familyNumber", "");
		assertValue("unitPrice", "");
		assertValue("unitPriceInPesetas", "");	
		
		setValue("familyNumber", "2");
		assertValue("unitPrice", "20.00");
		assertValue("unitPriceInPesetas", "3,328");
		
		setValue("familyNumber", "1");
		assertValue("unitPrice", "10.00"); 
		assertValue("unitPriceInPesetas", "1,664");		
	}
	
	public void testConsultWithCalculatedValuesAndByDefault() throws Exception { 
		assertActions(listActions);
		
		execute("CRUD.new");
		assertActions(newActions);
		setValue("number", "1");
		execute("CRUD.refresh");
						
		assertValue("familyNumber", "1"); 
		assertValue("unitPrice", "11.00"); 
		assertValue("unitPriceInPesetas", "1,830");		
	}
	
	public void testPropertyValidator_SomesAndCustomized() throws Exception {
		assertActions(listActions);		
		execute("CRUD.new");
		assertActions(newActions);
		
		setValue("number", "66");
		setValue("description", "UNA MOTO RAPIDA");
		setValue("familyNumber", "1");
		setValue("subfamilyNumber", "1");
		setValue("warehouseKey", "[.1.1.]");
		setValue("unitPrice", "100");
		assertNoEditable("unitPriceInPesetas");
		execute("CRUD.save");				
		assertError("Product can not contains MOTO in Description");
		
		setValue("description", "UN COCHE COMODO");
		execute("CRUD.save");
		assertError("Product can not contains COCHE in Description");				
	}
	
	public void testEntityValidator() throws Exception {
		assertActions(listActions);		
		execute("CRUD.new");
		assertActions(newActions);
		
		setValue("number", "66");
		setValue("description", "UN PRODUCTO CARO");
		setValue("familyNumber", "1");
		setValue("subfamilyNumber", "1");
		setValue("warehouseKey", "[.1.1.]");
		setValue("unitPrice", "100");
		assertNoEditable("unitPriceInPesetas");
		execute("CRUD.save");				
		assertError("The products EXPENSIVE must to have price greater than 1,000");
		
		setValue("description", "UN PRODUCTO BARATO");
		setValue("unitPrice", "1000");
		execute("CRUD.save");
		assertError("The products CHEAP can not be of price greater than 100");				
	}
	
	public void testEntityValidatorOnlyOnCreate() throws Exception {		
		assertActions(listActions);		
		execute("CRUD.new");
		assertActions(newActions);
		
		setValue("number", "66");
		setValue("description", "CUATRE CON PRECIO PROHIBIDO"); // CUATRE is forbidden
		setValue("familyNumber", "1");
		setValue("subfamilyNumber", "1");
		setValue("warehouseKey", "[.1.1.]");
		setValue("unitPrice", "555"); // 555 is a forbidden price but only on create
		execute("CRUD.save");
		assertError("Product can not contains CUATRE in Description");
		assertError("555 is a forbidden price");
				
		execute("CRUD.new");
		setValue("number", "4");
		execute("CRUD.refresh");
		assertValue("number", "4");
		assertValue("description", "CUATRE");
		setValue("unitPrice", "555"); // 555 is a forbidden price but only on create
		execute("CRUD.save");
		assertNoErrors(); // because the previous validations are only on create		
	}
	

	public void testCalculatedInListMode() throws Exception { 	 	
		assertActions(listActions);
		assertValueInList(1, "number", "2"); 
		assertValueInList(1, "unitPrice", "20.00"); 
		assertValueInList(1, "unitPriceInPesetas", "3,328");			
	}
	
	public void testValidationWithValidatorsChanged() throws Exception {
		assertActions(listActions);				
		execute("CRUD.new");
		assertActions(newActions);
		execute("CRUD.save");		
		// Since validator for FAMILY and SUBFAMILY has set to NOT_NEGATIVE
		// it does not fail validation although required is true
		assertNoError("Value for Family in Product is required");
		assertNoError("Value for Subfamily in Product is required");
	}

	public void testGoFromListToDetailAlwaysSetDefaultController_editableWellOnSearch() throws Exception {
		String [] changeProductPriceActions = {
			"Mode.list",
			"ChangeProductsPrice.save",
			"ChangeProductsPrice.editDescription",
			"Gallery.edit"
		};
		
		assertActions(listActions);
		execute("List.viewDetail", "row=0");
		assertNoEditable("number");
		assertEditable("description");		
		assertActions(editActions); 
		execute("Product.changeProductPrice");		
		assertActions(changeProductPriceActions);
		assertNoEditable("unitPrice"); 		
		execute("Mode.list");
		assertActions(listActions);
		execute("List.viewDetail", "row=0");
		assertActions(editActions); 
		assertNoEditable("number");
		assertEditable("unitPrice");		
	}
								
	public void testOnChangeDependentsOfPropertyWithDefaultValue() throws Exception { 
		execute("CRUD.new");
		assertValue("unitPrice","");
		assertValue("remarks", "");		
		setValue("familyNumber", "1");		
		assertValue("unitPrice","10.00"); 
		assertValue("remarks", "The price is 10");				
	}
	
}
