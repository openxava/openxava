package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *  
 * @author Javier Paniza
 */

public class CustomerWithSectionTest extends CustomerTest {

	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"ImportData.importData", 
		"ExtendedPrint.myReports",
		"CRUD.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow", 
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.customize", // It does not exist since 5.2, we put here to verify that ModuleTestBase ignore it 
		"List.hideRows",
		"List.changeConfiguration",
		"List.changeColumnName", 
		"ListFormat.select",
		"Customer.hideSellerInList",
		"Customer.showSellerInList"
	};

	public CustomerWithSectionTest(String testName) { 
		super(testName, "CustomerWithSection", true);		
	}
	
	public void testMyReportColumnLabels() throws Exception { 
		assertLabelInList(0, "Name");
		
		execute("ExtendedPrint.myReports");
		assertLabelInCollection("columns", 0, "Column");
		assertValueInCollection("columns", 0, 0, "Name");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 2, 0, "Seller");
		assertValueInCollection("columns", 3, 0, "City of Address");
		assertValueInCollection("columns", 4, 0, "Seller level");
		assertValueInCollection("columns", 5, 0, "State of Address");
		assertValueInCollection("columns", 6, 0, "Web site");
		
		execute("MyReport.editColumn", "row=0,viewObject=xava_view_columns");
		setValue("label", "My name");
		execute("MyReport.saveColumn");
		
		execute("MyReport.editColumn", "row=4,viewObject=xava_view_columns");
		setValue("label", "My seller level");
		execute("MyReport.saveColumn");
		
		assertValueInCollection("columns", 0, 0, "My name");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 2, 0, "Seller");
		assertValueInCollection("columns", 3, 0, "City of Address");
		assertValueInCollection("columns", 4, 0, "My seller level");
		assertValueInCollection("columns", 5, 0, "State of Address");
		assertValueInCollection("columns", 6, 0, "Web site");
		
		execute("MyReport.generatePdf");						 
		assertPopupPDFLine(2, "My name Type Seller City of Address My seller level State of Address Web site"); 
		assertLabelInList(0, "Name"); // The list labels are not affected
		
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 0, 0, "My name");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 2, 0, "Seller");
		assertValueInCollection("columns", 3, 0, "City of Address");
		assertValueInCollection("columns", 4, 0, "My seller level");
		assertValueInCollection("columns", 5, 0, "State of Address");
		assertValueInCollection("columns", 6, 0, "Web site");
		execute("MyReport.editColumn", "row=0,viewObject=xava_view_columns");
		assertValue("label", "My name");
		setValue("name", "number");
		assertValue("label", "Number");
		setValue("name", "name");
		assertValue("label", "My name");
		closeDialog();
		execute("MyReport.remove", "xava.keyProperty=name");		
	}
	
	public void testMyReportFilteringByValidValues() throws Exception { 
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 1, 0, "Type"); 
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		String [][] validValuesValues = {
			{ "0", "" },	
			{ "1", "Normal" },
			{ "2", "Steady" },
			{ "3", "Special" }
		};
		assertValidValues("validValuesValue", validValuesValues);
		assertExists("validValuesValue");
		assertNotExists("dateValue");
		assertNotExists("booleanValue");
		assertNotExists("comparator");
		assertNotExists("value");
		setValue("name", "number");
		setValue("comparator", "eq"); // It's important to fill it, in order to see if afterwards is ignored
		setValue("value", "2");	// It's important to fill it, in order to see if afterwards is ignored			
		assertNotExists("validValuesValue");
		assertNotExists("dateValue");
		assertNotExists("booleanValue");
		assertExists("comparator");
		assertExists("value");
		assertExists("order");
		setValue("name", "type");
		assertExists("validValuesValue");
		assertNotExists("dateValue");
		assertNotExists("booleanValue");
		assertNotExists("comparator");
		assertNotExists("value");
		assertExists("order");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 1, 1, "");
		assertValueInCollection("columns", 1, 2, "");  
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");		
		setValue("validValuesValue", "3");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 1, 1, "=");
		assertValueInCollection("columns", 1, 2, "Special");
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		assertValue("validValuesValue", "3"); 
		setValue("validValuesValue", "2");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 1, 1, "=");
		assertValueInCollection("columns", 1, 2, "Steady");
		
		execute("MyReport.generatePdf");
		assertPopupPDFLinesCount(5); 
		assertTrue(getPopupPDFLine(3).startsWith("Javi Steady"));
		
		execute("ExtendedPrint.myReports");
		execute("MyReport.generatePdf");
		assertPopupPDFLinesCount(5);
		assertTrue(getPopupPDFLine(3).startsWith("Javi Steady"));
				
		execute("ExtendedPrint.myReports");  		
		execute("MyReport.remove", "xava.keyProperty=name");
	}
	
	public void testDialogsInNestedCollections() throws Exception {
		execute("List.viewDetail", "row=0"); 
		assertDialogsInNestedCollections(false);
		assertDialogsInNestedCollections(true);
	}
	
	public void assertDialogsInNestedCollections(boolean closeDialog) throws Exception { 
		assertNoDialog();		
		assertAction("CRUD.new");
		assertNoAction("Collection.hideDetail");
		assertExists("deliveryPlaces");
		assertNotExists("receptionists");
		
		execute("Collection.new", "viewObject=xava_view_section0_deliveryPlaces");
		assertDialog();		
		assertNoAction("CRUD.new");
		assertAction("Collection.save");
		assertAction("Collection.hideDetail");
		assertNotExists("deliveryPlaces");
		assertExists("receptionists");

		setValue("name", "SOMEONE");
		setValue("address", "SOMEWHERE");
		execute("Collection.new", "viewObject=xava_view_receptionists");
		assertDialog();		
		assertActions(new String [] { "Collection.save", "Collection.saveAndStay", "Collection.hideDetail" });
		assertNotExists("deliveryPlaces");
		assertNotExists("receptionists");
		
		if (closeDialog) closeDialog();
		else execute("Collection.hideDetail");
		assertDialog();		
		assertNoAction("CRUD.new");
		assertAction("Collection.save");
		assertAction("Collection.hideDetail");
		assertAction("Collection.new");
		assertNotExists("deliveryPlaces");
		assertExists("receptionists");
	
		if (closeDialog) closeDialog();
		else execute("Collection.hideDetail");
		assertNoDialog();		
		assertAction("CRUD.new");
		assertNoAction("Collection.hideDetail");
		assertAction("Collection.new");
		assertExists("deliveryPlaces");
		assertNotExists("receptionists");		
	}
	
	public void testForwardToAbsoluteURL() throws Exception { 
		execute("CRUD.new");
		assertValue("website", "");
		execute("CustomWebURL.go", "property=website,viewObject=xava_view_section0"); 
		assertError("Empty URL, so you cannot go to it");
		setValue("website", "http://www.example.org/"); 
		execute("CustomWebURL.go", "property=website,viewObject=xava_view_section0");
		assertTrue(getHtml().indexOf("This domain is established to be used for illustrative examples") >= 0);  
		
	}
	
	public void testForwardToJavaScript() throws Exception {  
		execute("CRUD.new");
		HtmlElement console = getHtmlPage().getHtmlElementById("xava_console"); 
		assertTrue(!console.asText().contains("[CustomerWithSection.testForwardToJavaScript()] javascript: works"));
		setValue("website", "javascript:openxava.log('[CustomerWithSection.testForwardToJavaScript()] javascript: works');"); 
		execute("CustomWebURL.go", "property=website,viewObject=xava_view_section0"); 
		assertTrue(console.asText().contains("[CustomerWithSection.testForwardToJavaScript()] javascript: works"));
	}
	
	// To fix a concrete bug
	public void testNavigateToSearchReferenceAndCreateReference() throws Exception { 
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=alternateSeller.number");
		execute("ReferenceSearch.choose", "row=0");
		execute("Reference.createNew", "model=Seller,keyProperty=alternateSeller.number");
		execute("NewCreation.cancel");
		assertExists("alternateSeller.number");
	}

	
	public void testDialogChangesPreviousView() throws Exception {		
		execute("CRUD.new");
		assertValue("address.street", "");
		assertValue("address.zipCode", "");
		assertValue("address.city", "");
		assertValue("address.state.id", "");
		assertNoDialog();
		execute("Address.addFullAddress");
		assertActions(new String[] {
			"AddFullAddress.add", "Dialog.cancel"	
		});
		assertDialog();
		assertDialogTitle("Entry the full address");		
		setValue("fullAddress", "AV. BARON DE CARCER, 48 - 12E 46001 VALENCIA CA");		
		execute("AddFullAddress.add");
		assertNoErrors();
		assertNoDialog();
		assertValue("address.street", "AV. BARON DE CARCER, 48 - 12E");
		assertValue("address.zipCode", "46001");
		assertValue("address.city", "VALENCIA");
		assertValue("address.state.id", "CA");		
		
		execute("Address.addFullAddress");
		assertDialog();
		execute("Dialog.cancel");
		assertNoDialog();
	}
	
	public void testTELEPHONE_EMAIL_EMAIL_LIST_WEBURLstereotypes() throws Exception { 
		assertTrue("website column must have a clickable link", getHtml().contains("<a href=\"http://www.openxava.org\">"));
		execute("List.viewDetail", "row=0");
		setValue("telephone", "asf");
		setValue("email", "pepe");
		setValue("website", "openxava");
		execute("Customer.save");		
		assertError("Telephone must be a valid number"); 
		assertError("Email must be a valid email address"); 
		assertError("Web site must be a valid url");
		setValue("telephone", "123");
		setValue("email", "pepe@mycompany");
		setValue("additionalEmails", "pepe@myproject.org, pepe@yahoo"); 
		setValue("website", "www.openxava.org");
		execute("Customer.save");
		assertError("Telephone must be at least 8 Digits long");
		assertError("Email must be a valid email address");
		assertError("Web site must be a valid url");
		assertError("Additional emails should contain valid emails separated by commas"); 
		assertValue("email", "pepe@mycompany"); // not converted to uppercase
		assertValue("additionalEmails", "pepe@myproject.org, pepe@yahoo"); // not converted to uppercase 
		assertValue("website", "www.openxava.org"); // not converted to uppercase
		setValue("telephone", "961112233");
		setValue("email", "pepe@mycompany.com");
		setValue("additionalEmails", "pepe@myproject.org, pepe@yahoo.com"); 
		setValue("website", "http://www.openxava.org");
		execute("Customer.save");
		assertNoErrors();
	}
	
	public void testOrderAndFilterInNestedCollection() throws Exception { 
		execute("CRUD.new");
		setValue("number", "4");
		execute("CRUD.refresh");		
		assertValue("name", "Cuatrero"); 
		
		assertCollectionRowCount("deliveryPlaces", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_section0_deliveryPlaces");
		
		assertCollectionRowCount("receptionists", 2);
		assertValueInCollection("receptionists", 0, 0, "JUAN");
		assertValueInCollection("receptionists", 1, 0, "PEPE");
		
		execute("List.orderBy", "property=name,collection=receptionists"); 
		execute("List.orderBy", "property=name,collection=receptionists");
		assertValueInCollection("receptionists", 0, 0, "PEPE"); 
		assertValueInCollection("receptionists", 1, 0, "JUAN");
		
		setConditionValues("receptionists", new String[] { "J"} ); 
		execute("List.filter", "collection=receptionists"); 
		assertCollectionRowCount("receptionists", 1); 
		assertValueInCollection("receptionists", 0, 0, "JUAN");				
	}
	
	public void testModifyFromReference() throws Exception {
		execute("CRUD.new");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number"); 
		assertError("Impossible to modify an empty reference");
		setValue("number", "1");
		execute("CRUD.refresh");		
		assertValue("name", "Javi");
		assertValue("seller.name", "MANUEL CHAVARRI");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");		
		assertValue("Seller", "number", "1");
		assertValue("Seller", "name", "MANUEL CHAVARRI");
		execute("Modification.cancel");
		assertValue("seller.name", "MANUEL CHAVARRI");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");
		assertValue("Seller", "number", "1");
		assertValue("Seller", "name", "MANUEL CHAVARRI");
		setValue("Seller", "name", "MANOLO");
		execute("Modification.update");
		assertValue("seller.name", "MANOLO");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");
		setValue("Seller", "name", "MANUEL CHAVARRI");
		execute("Modification.update");
		assertValue("seller.name", "MANUEL CHAVARRI");
	}
	
	public void testChooseInReferenceWithoutSelecting() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");		
		execute("ReferenceSearch.choose");
		assertNoErrors();		
		assertAction("ReferenceSearch.choose"); // Because no row is selected we keep in searching list
	}
	
	public void testCustomizeReferenceListDoesNotReturnToListModeOfModule() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		assertListColumnCount(3); 
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(3); // To test that it's still is the tab of sellers, not the customer's one
	}
	
	public void testDefaultValidator() throws Exception {
		execute("CRUD.new");
		setValue("name", "x");
		execute("Customer.save");
		assertNoError("Person name MAKARIO is not allowed for Name in Customer");
		setValue("name", "MAKARIO");
		execute("Customer.save");
		assertError("Person name MAKARIO is not allowed for Name in Customer");
	}
	
	public void testCreatedFromReferenceIsChosenAndThrowsOnChange() throws Exception {
		execute("CRUD.new");
		execute("Reference.createNew", "model=Seller,keyProperty=xava.Customer.alternateSeller.number");
		setValue("Seller", "number", "66");
		setValue("Seller", "name", "SELLER JUNIT 66");
		execute("NewCreation.saveNew");
		assertNoErrors(); 
		assertValue("alternateSeller.number", "66");
		assertValue("alternateSeller.name", "DON SELLER JUNIT 66"); // The 'DON' is added by an on-change action
		deleteSeller(66);
	}	
	
	private void deleteSeller(int number) throws Exception {
		XPersistence.getManager().remove(XPersistence.getManager().find(Seller.class, number));				
	}

	public void testPropertyAction() throws Exception { 
		execute("CRUD.new");
		setValue("address.street", "DOCTOR PESSET");
		assertValue("address.street", "DOCTOR PESSET");		
		execute("Customer.prefixStreet", "xava.keyProperty=address.street"); 
		assertValue("address.street", "C/ DOCTOR PESSET");
		assertIconsInViewAction(); 
	}
	
	private void assertIconsInViewAction() { 
		String actionsXml = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_CustomerWithSection__property_actions_seller___number").asXml();
		assertTrue(actionsXml.contains("<i class=\"mdi mdi-magnify"));
		assertTrue(actionsXml.contains("<i class=\"mdi mdi-library-plus"));
		assertFalse(actionsXml.contains("images/"));		
	}
	
	public void testAddingToManyToManyCollectionFromANewObject() throws Exception { 
		execute("CRUD.new");
		
		// The minimum data to save a customer
		setValue("number", "66");
		setValue("name", "JUNIT Customer");
		setValue("address.street", "JUNIT Street");
		setValue("address.zipCode", "46540");
		setValue("address.city", "EL PUIG");
		setValue("address.state.id", "CA");
		
		// Trying to add a state
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("states", 0);

		assertAddingStates();		
		assertNoErrors();
		assertNoEditable("number");
		
		execute("CRUD.delete");		
		assertNoErrors();		
	}

	
	public void testManyToManyCollection() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("states", 0);
		
		assertAddingStates();
		
		// Using Edit + Remove
		/* To remove editing collections of entities not available since OX4m2
		execute("Collection.edit", "row=0,viewObject=xava_view_section1_states");
		execute("Collection.remove", "viewObject=xava_view_section1_states");
		assertCollectionRowCount("states", 1);
		*/
		// Using Check row + Remove selected
		checkRowCollection("states", 0);		
		execute("Collection.removeSelected", "viewObject=xava_view_section1_states");
		assertNoErrors();
		assertCollectionRowCount("states", 1); 
		
		checkRowCollection("states", 0);		
		execute("Collection.removeSelected", "viewObject=xava_view_section1_states");
		assertNoErrors();
		assertCollectionRowCount("states", 0);
		
		
		// Verifying if that other part is not removed
		changeModule("StateHibernate");
		assertValueInList(0, 0, "AK");
		assertValueInList(4, 0, "CA");		
	}

	private void assertAddingStates() throws Exception {
		if (usesAnnotatedPOJO()) {
			// In OX3 ManyToMany is supported, then we have a collection of entities
			execute("Collection.add", "viewObject=xava_view_section1_states");
			assertValueInList(0, 0, "AK");
			assertValueInList(4, 0, "CA");
			checkRow(0);
			checkRow(4);			
			execute("AddToCollection.add");
		}
		else {
			// In OX2 many to many is not supported, we simulate it using a collection of aggregates,
			// therefore the User Interface it's not the same (because it's a collection of aggragates)
			execute("Collection.new", "viewObject=xava_view_section1_states");
			setValue("state.id", "AK");
			assertValue("state.name", "ALASKA");
			execute("Collection.save");
			execute("Collection.new", "viewObject=xava_view_section1_states");
			setValue("state.id", "CA");
			assertValue("state.name", "CALIFORNIA");
			execute("Collection.save");			
		}		
		assertCollectionRowCount("states", 2); 
		assertValueInCollection("states", 0, 0, "AK");
		assertValueInCollection("states", 0, 1, "ALASKA");		
		assertValueInCollection("states", 1, 0, "CA");
		assertValueInCollection("states", 1, 1, "CALIFORNIA");
	}
	
	public void testChangeReferenceLabel() throws Exception {
		execute("CRUD.new");
		assertLabel("alternateSeller", "Alternate seller");
		execute("Customer.changeAlternateSellerLabel");
		assertLabel("alternateSeller", "Secondary seller");
	}
	
	public void testShowHideFilterInList() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true); 
		assertFalse(getElementById("show_filter_list").isDisplayed());
		assertTrue(getElementById("hide_filter_list").isDisplayed()); 
		assertTrue(getElementById("list_filter_list").isDisplayed());
		getElementById("hide_filter_list").click();
		Thread.sleep(500); 
		assertTrue(getElementById("show_filter_list").isDisplayed());
		assertFalse(getElementById("hide_filter_list").isDisplayed());
		assertFalse(getElementById("list_filter_list").isDisplayed());
		getElementById("show_filter_list").click();
		Thread.sleep(500); 
		assertFalse(getElementById("show_filter_list").isDisplayed()); 
		assertTrue(getElementById("hide_filter_list").isDisplayed());
		assertTrue(getElementById("list_filter_list").isDisplayed());
	}
	
	private boolean hasElementById(String elementId) { // Copied from ModuleTestBase because we don't want to promote the direct HTML manipulation  
		try {
			getElementById(elementId);
			return true;
		}
		catch (com.gargoylesoftware.htmlunit.ElementNotFoundException ex) {			
			return false;
		}		
	}
	
	private HtmlElement getElementById(String id) { // Copied from ModuleTestBase because we don't want to promote the direct HTML manipulation
		return getHtmlPage().getHtmlElementById(decorateId(id));
	}
	
	public void testCustomizeList() throws Exception { 
		doTestCustomizeList_moveAndRemove(); 
		resetModule(); 
		doTestCustomizeList_generatePDF();
		resetModule(); 
		doTestRestoreColumns_addRemoveTabColumnsDynamically();
	}
	
	private void doTestCustomizeList_moveAndRemove() throws Exception {
		assertActions(listActions);

		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address");
		assertLabelInList(6, "Web site");
		assertTrue("It is needed customers for execute this test", getListRowCount() > 1);
		String name = getValueInList(0, 0);
		String type = getValueInList(0, 1);
		String seller = getValueInList(0, 2);
		String city = getValueInList(0, 3);
		String sellerLevel = getValueInList(0, 4);
		String state = getValueInList(0, 5);
		String site = getValueInList(0, 6);
		
		// move 0 to 2
		moveColumn(0, 2);
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");
		assertLabelInList(2, "Name");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address");
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, type);
		assertValueInList(0, 1, seller);
		assertValueInList(0, 2, name);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);						
		assertValueInList(0, 5, state);
		assertValueInList(0, 6, site);		
		
		// move 2 to 4
		moveColumn(2, 4);
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");		
		assertLabelInList(2, "City of Address");
		assertLabelInList(3, "Seller level");
		assertLabelInList(4, "Name");
		assertLabelInList(5, "State of Address");
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, type);
		assertValueInList(0, 1, seller);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, sellerLevel);
		assertValueInList(0, 4, name);
		assertValueInList(0, 5, state);		
		assertValueInList(0, 6, site);		
		
		// remove column 3
		removeColumn(3); 
		assertNoErrors();
		assertListColumnCount(6);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");		
		assertLabelInList(2, "City of Address");		
		assertLabelInList(3, "Name");
		assertLabelInList(4, "State of Address");
		assertLabelInList(5, "Web site");
		assertValueInList(0, 0, type);
		assertValueInList(0, 1, seller);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, name);
		assertValueInList(0, 4, state);
		assertValueInList(0, 5, site);		
						
		assertActions(listActions);
	}
	
	private void doTestCustomizeList_generatePDF() throws Exception {
		// Trusts in that testCustomizeList_moveAndRemove is executed before
		assertListColumnCount(6);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");		
		assertLabelInList(2, "City of Address");		
		assertLabelInList(3, "Name");
		assertLabelInList(4, "State of Address");
		assertLabelInList(5, "Web site");
		removeColumn(3); 
		assertNoErrors();
		assertListColumnCount(5);		
		execute("Print.generatePdf"); 
		assertContentTypeForPopup("application/pdf");
		
	}
		
	private void doTestRestoreColumns_addRemoveTabColumnsDynamically() throws Exception { 
		// Restoring initial tab setup
		execute("List.addColumns");							
		execute("AddColumns.restoreDefault");		
		// End restoring
		
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertTrue("Must to have customers for run this test", getListRowCount() > 1);
		String name = getValueInList(0, 0);
		String type = getValueInList(0, 1);
		String seller = getValueInList(0, 2);
		String city = getValueInList(0, 3);
		String sellerLevel = getValueInList(0, 4);
		String state = getValueInList(0, 5); 
		String site = getValueInList(0, 6);
		
		execute("Customer.hideSellerInList");
		assertNoErrors();
		assertListColumnCount(6);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "City of Address");
		assertLabelInList(3, "Seller level");
		assertLabelInList(4, "State of Address"); 
		assertLabelInList(5, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, sellerLevel);
		assertValueInList(0, 4, state); 
		assertValueInList(0, 5, site);
		
		execute("Customer.showSellerInList");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");		
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, seller);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state); 
		assertValueInList(0, 6, site);
	}
	
	public void testCustomizeList_addAndResetModule() throws Exception {   
		assertListColumnCount(7); 
		String value = getValueInList(0, 0);
		execute("List.addColumns");		
		checkRow("selectedProperties", "number"); 		
		execute("AddColumns.addColumns");
		assertListColumnCount(8);
		assertValueInList(0, 0, value);
				
		resetModule();
		assertListColumnCount(8); 
		assertValueInList(0, 0, value);
		
		removeColumn(7); 
		assertListColumnCount(7); 
	}	
	
	public void testRowStyle() throws Exception {
		// When testing again a portal styleClass in xava.properties must match with
		// the tested portal in order that this test works fine
		int c = getListRowCount();
		boolean found = false;
		boolean found_red = false;
		
		for (int i=0; i<c; i++) {
			String type = getValueInList(i, "type");
			if ("Steady".equals(type)) {				
				assertRowStyleInList(i, "row-highlight"); 				
				found = true;
			}
			else if ("Special".equals(type)) {
				assertRowStyleInList(i, "row-red");				
				found_red = true;
			}
			else { 
				assertNoRowStyleInList(i);	
			}						
		}
		if (!found) {
			fail("It is required at least one Customer of 'Steady' type for run this test");
		}
		if (!found_red) {
			fail("It is required at least one Customer of 'Special' type for run this test");
		}
	}
		
}
