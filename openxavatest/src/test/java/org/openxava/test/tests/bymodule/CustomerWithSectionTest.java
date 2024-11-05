package org.openxava.test.tests.bymodule;

import org.htmlunit.*;
import org.htmlunit.html.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 *  
 * @author Javier Paniza
 */

public class CustomerWithSectionTest extends CustomerTest { 
	
	private class MessageConfirmHandler implements ConfirmHandler { 
		
		private boolean confirm = true;
		
		private String message;
		
		public MessageConfirmHandler() {			
		}
		
		public MessageConfirmHandler(boolean confirm) {
			this.confirm = confirm;
		}
		
		public boolean handleConfirm(Page page, String message) {
			this.message = message; 
			return confirm;
		}
		
		public void assertNoMessage() {
			assertEquals(null, message);
			message = null;
		}
		
		public void assertMessage() {
			assertEquals("You will lose all changes made since your last save. Do you want to continue?", message);
			message = null;
		}

	}

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
		assertValueInCollection("columns", 3, 0, "Address city"); 
		assertValueInCollection("columns", 4, 0, "Seller level");
		assertValueInCollection("columns", 5, 0, "Address state");
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
		assertValueInCollection("columns", 3, 0, "Address city");
		assertValueInCollection("columns", 4, 0, "My seller level");
		assertValueInCollection("columns", 5, 0, "Address state");
		assertValueInCollection("columns", 6, 0, "Web site");
		
		execute("MyReport.generatePdf");						 
		assertPopupPDFLine(3, "My name Type Seller Address city My seller level Address state Web site"); 
		assertLabelInList(0, "Name"); // The list labels are not affected
		
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 0, 0, "My name");
		assertValueInCollection("columns", 1, 0, "Type");
		assertValueInCollection("columns", 2, 0, "Seller");
		assertValueInCollection("columns", 3, 0, "Address city");
		assertValueInCollection("columns", 4, 0, "My seller level");
		assertValueInCollection("columns", 5, 0, "Address state");
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
		assertPopupPDFLinesCount(6); 
		assertTrue(getPopupPDFLine(4).startsWith("Javi Steady"));
		
		execute("ExtendedPrint.myReports");
		execute("MyReport.generatePdf");
		assertPopupPDFLinesCount(6);
		assertTrue(getPopupPDFLine(4).startsWith("Javi Steady"));
				
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
		assertTrue(getHtml().indexOf("This domain is for use in illustrative examples in documents") >= 0);  
		
	}
	
	public void testForwardToJavaScript() throws Exception {  
		execute("CRUD.new");
		HtmlElement console = getHtmlPage().getHtmlElementById("xava_console"); 
		assertTrue(!console.asNormalizedText().contains("[CustomerWithSection.testForwardToJavaScript()] javascript: works"));
		setValue("website", "javascript:openxava.log('[CustomerWithSection.testForwardToJavaScript()] javascript: works');"); 
		execute("CustomWebURL.go", "property=website,viewObject=xava_view_section0"); 
		assertTrue(console.asNormalizedText().contains("[CustomerWithSection.testForwardToJavaScript()] javascript: works"));
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
		
		assertValue("seller.number", "");
		assertValue("seller.name", "");
		assertValue("alternateSeller.number", "");
		assertValue("alternateSeller.name", ""); 
		setValue("name", "Pedro");
		assertValue("seller.number", "2");
		assertValue("seller.name", "JUANVI LLAVADOR");
		assertValue("alternateSeller.number", "1");
		assertValue("alternateSeller.name", "DON MANUEL CHAVARRI");		
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
		
		Customer customer = Customer.findByNumber(2);
		customer.setWebsite("www.rae.es"); // With no http:// prefix
		XPersistence.commit();
		execute("Mode.list");
		assertTrue("website column must have a clickable link", getHtml().contains("<a href=\"http://www.rae.es\">"));
		customer = Customer.findByNumber(2);
		customer.setWebsite(""); 
		XPersistence.commit();		
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
		String actionsXml = getHtmlPage().getHtmlElementById("ox_openxavatest_CustomerWithSection__property_actions_seller___number").asXml();
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
		changeModule("StateJPA"); 
		assertValueInList(0, 0, "AK"); 
		assertValueInList(4, 0, "CA");		
	}

	private void assertAddingStates() throws Exception {
		execute("Collection.add", "viewObject=xava_view_section1_states");
		assertValueInList(0, 0, "AK");
		assertValueInList(4, 0, "CA");
		checkRow(0);
		checkRow(4);			
		execute("AddToCollection.add");
		assertCollectionRowCount("states", 2); 
		assertValueInCollection("states", 0, 0, "AK");
		assertValueInCollection("states", 0, 1, "ALASKA");		
		assertValueInCollection("states", 1, 0, "CA");
		assertValueInCollection("states", 1, 1, "CALIFORNIA");
	}
	
	public void testTabSetConditionValueForReference_changeReferenceLabel() throws Exception { 
		assertListRowCount(5);
		execute("Customer.filterBySellerOne");
		assertListRowCount(2); 
		assertValueInList(0, "name", "Javi");
		assertValueInList(0, "seller.name", "MANUEL CHAVARRI");
		assertValueInList(1, "name", "Juanillo");
		assertValueInList(1, "seller.name", "MANUEL CHAVARRI");
		HtmlSelect comboSeller = getHtmlPage().getElementByName("ox_openxavatest_CustomerWithSection__conditionValue___2");
		String selectedValue = comboSeller.getSelectedOptions().get(0).getValueAttribute();
		assertEquals("MANUEL CHAVARRI", selectedValue); 

		
		execute("CRUD.new");
		assertLabel("alternateSeller", "Alternate seller");
		execute("Customer.changeAlternateSellerLabel");
		assertLabel("alternateSeller", "Secondary seller");
	}
		
	private boolean hasElementById(String elementId) { // Copied from ModuleTestBase because we don't want to promote the direct HTML manipulation  
		try {
			getElementById(elementId);
			return true;
		}
		catch (org.htmlunit.ElementNotFoundException ex) {			
			return false;
		}		
	}
	
	private HtmlElement getElementById(String id) { // Copied from ModuleTestBase because we don't want to promote the direct HTML manipulation
		return getHtmlPage().getHtmlElementById(decorateId(id));
	}
		
	public void testRowStyle() throws Exception {
		int c = getListRowCount();
		boolean found = false;
		boolean found_red = false;
		
		for (int i=0; i<c; i++) {
			String type = getValueInList(i, "type");
			if ("Steady".equals(type)) {				
				assertRowStyleInList(i, "row-highlight italic-label"); 				
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
	
	public void testLeaveEntityWithoutSavingLosesChangedDataMessage() throws Exception {  
		MessageConfirmHandler confirmHandler = new MessageConfirmHandler();
		getWebClient().setConfirmHandler(confirmHandler);
		confirmHandler.assertNoMessage();
		
		// No changes
		execute("List.viewDetail", "row=0");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		assertValue("name", "");
		confirmHandler.assertNoMessage();
		
		execute("Navigation.first");
		assertValue("name", "Javi");
		assertValue("extendedCity", "");
		execute("CustomerWithSection.setExtendedCity");
		assertValue("extendedCity", "46540 EL PUIG (NY)"); // The value does not matter
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		assertValue("name", "");
		confirmHandler.assertNoMessage();
		
		assertChangeTransientReferenceByCodeNoMessage(confirmHandler, 
			"CustomerWithSection.setTransientSellerRefreshing", 
			"3", "ELISEO FERNANDEZ");
		
		assertChangeTransientReferenceByCodeNoMessage(confirmHandler, 
			"CustomerWithSection.setTransientSellerUsingMap", 
			"6", "THE SIX");

		assertChangeTransientReferenceByCodeNoMessage(confirmHandler, 
			"CustomerWithSection.setTransientSellerRefreshingInSubview", 
			"2", "JUANVI LLAVADOR");
		
		assertChangeTransientReferenceByCodeNoMessage(confirmHandler, 
			"CustomerWithSection.setTransientSellerUsingMapInSubview", 
			"7", "THE SEVEN");		
		
		// Property with event
		execute("Navigation.first");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();		
		setValue("name", "Javix");
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		confirmHandler.assertMessage();
		assertValue("name", "");

		// Property without event
		execute("Navigation.first");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();
		setValue("email", "jose@mycompany.com");
		getHtmlPage().executeJavaScript("$('#ox_openxavatest_CustomerWithSection__name').change();");
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		confirmHandler.assertMessage();
		assertValue("name", "");
		
		// Reference
		execute("Navigation.first");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();
		execute("Reference.search", "keyProperty=alternateSeller.number");
		confirmHandler.assertNoMessage();
		execute("ReferenceSearch.choose", "row=0");
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		confirmHandler.assertMessage();
		assertValue("name", "");
		
		// Reference to null
		execute("Navigation.first");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();
		assertValue("alternateSeller.number", "2");
		assertValue("alternateSeller.name", "JUANVI LLAVADOR");
		setValue("alternateSeller.number", "");
		assertValue("alternateSeller.name", "");
		execute("CRUD.new");
		confirmHandler.assertMessage();
		assertValue("name", "");		
		
		// DescriptionsList
		execute("Navigation.first");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();
		setValue("address.state.id", "CA");
		getHtmlPage().executeJavaScript("$('#ox_openxavatest_CustomerWithSection__reference_editor_address___state .ox-descriptions-list').change();");
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		confirmHandler.assertMessage();
		assertValue("name", "");
		
		// Saving
		execute("Navigation.first");
		assertValue("name", "Javi");
		confirmHandler.assertNoMessage();		
		setValue("name", "Javix");
		confirmHandler.assertNoMessage();
		execute("Customer.save");
		confirmHandler.assertNoMessage();
		assertValue("name", "Javix"); // This save version does not clear the view
		execute("CRUD.new");
		confirmHandler.assertNoMessage(); 
		assertValue("name", "");
		
		// Canceling
		MessageConfirmHandler cancelHandler = new MessageConfirmHandler(false);
		getWebClient().setConfirmHandler(cancelHandler);
		execute("Navigation.first");
		assertValue("name", "Javix");
		cancelHandler.assertNoMessage();		
		setValue("name", "Javi");
		cancelHandler.assertNoMessage();
		execute("CRUD.new");
		cancelHandler.assertMessage();
		assertValue("name", "Javi");
		execute("Customer.save");
		assertValue("name", "Javi");
		cancelHandler.assertNoMessage();
		execute("CRUD.new");
		cancelHandler.assertNoMessage();
		assertValue("name", "");
		
		// Adding in @OneToMany collection does not require confirm
		getWebClient().setConfirmHandler(confirmHandler);
		execute("Navigation.first");
		assertValue("name", "Javi");
		assertCollectionRowCount("deliveryPlaces", 0);
		execute("Collection.new", "viewObject=xava_view_section0_deliveryPlaces");
		setValue("name", "JUNIT DEVIVERY PLACE");
		setValue("address", "JUNIT STREET");
		execute("Collection.save");
		assertCollectionRowCount("deliveryPlaces", 1);
		assertValueInCollection("deliveryPlaces", 0, 0, "JUNIT DEVIVERY PLACE");
		execute("Mode.list");
		confirmHandler.assertNoMessage();
		execute("List.viewDetail", "row=0");
		assertValue("name", "Javi");
		assertCollectionRowCount("deliveryPlaces", 1);
		assertValueInCollection("deliveryPlaces", 0, 0, "JUNIT DEVIVERY PLACE");
		execute("Collection.deleteSelected", "row=0,viewObject=xava_view_section0_deliveryPlaces");
		assertCollectionRowCount("deliveryPlaces", 0); 
	}
	
	private void assertChangeTransientReferenceByCodeNoMessage(MessageConfirmHandler confirmHandler, String action,	String expectedNumber, String expectedName) throws Exception {
		execute("Navigation.first");
		assertValue("name", "Javi");
		assertValue("transientSeller.number", "");
		assertValue("transientSeller.name", "");
		execute(action);
		assertValue("transientSeller.number", expectedNumber);
		assertValue("transientSeller.name", expectedName); 
		confirmHandler.assertNoMessage();
		execute("CRUD.new");
		assertValue("name", "");
		confirmHandler.assertNoMessage();
	}
	
	
			
}
