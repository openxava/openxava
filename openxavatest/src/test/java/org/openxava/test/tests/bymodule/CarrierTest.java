package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.model.meta.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CarrierTest extends CarrierTestBase {
	
	
	public CarrierTest(String testName) {
		super(testName, "Carrier");
	}
	
	public void testPermalink() throws Exception {
		execute("List.viewDetail", "row=0");
		assertPageURI("/Carrier?detail=1");
		execute("Navigation.next");
		assertPageURI("/Carrier?detail=2");
		execute("Mode.list");
		assertPageURI("/Carrier");
		execute("List.filter");
		assertPageURI("/Carrier");
		execute("CRUD.new");
		assertPageURI("/Carrier?action=CRUD.new");
	}
	
	private void assertPageURI(String expectedURI) { 
		HtmlUnitUtils.assertPageURI(getHtmlPage(), expectedURI);
	}
		
	public void testCutIOnlyKeysInCollections_cutPasteOnlyWhenEditable() throws Exception {  
		execute("List.viewDetail", "row=0");
		execute("CollectionCopyPaste.cut", "row=0,viewObject=xava_view_fellowCarriersCalculated");
		execute("Carrier.showCutRows"); 
		assertMessage("Cut rows: [{number=2}]");
		checkRowCollection("fellowCarriersCalculated", 1);
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_fellowCarriersCalculated");
		execute("Carrier.showCutRows");
		assertMessage("Cut rows: [{number=3}]");
		
		changeModule("CarrierWithReadOnlyCalculatedFellows");
		execute("List.viewDetail", "row=0");
		assertNoAction("CollectionCopyPaste.cut");
		assertNoAction("CollectionCopyPaste.paste");
	}
	
	public void testApostrophes_switzerlandLocale() throws Exception { 
		setLocale("fr");

		// Confirm action with apostrophe
		assertListRowCount(5);
		execute("List.viewDetail", "row=0");
		HtmlAnchor deleteLink = getHtmlPage().getHtmlElementById("ox_openxavatest_Carrier__CRUD___delete");
		// To ensure the question has an apostrophe
		assertEquals("Effacer l" + (char) 8216 + "entité courante: Etes-vous sûr(e) ?", 
			deleteLink.getAttribute("data-confirm-message"));
		execute("CRUD.delete");
		execute("Mode.list");
		assertListRowCount(4);
		
		// Confirm row action with apostrophe and title in actions
		// To ensure the title and question have apostrophe
		String deleteRowLink = "<a title=\"Effacer l" 
			// (char) 145 // ANSI
			+ (char) 8216 // UNICODE
			+ "entité\" class=\"xava_action ox-image-link\" data-application=\"openxavatest\" data-module=\"Carrier\" data-confirm-message=\"Effacer l"
			// (char) 145 // ANSI
			+ (char) 8216 // UNICODE
			+ "entité la ligne 1: êtes-vous sûr ?\"";
		assertTrue(getHtml().contains(deleteRowLink));
		execute("CRUD.deleteRow", "row=0");
		assertListRowCount(3);
		
		// Apostrophe in dialog title
		execute("List.viewDetail", "row=0");
		execute("Collection.new", "viewObject=xava_view_fellowCarriersCalculated");
		assertDialogTitle("Préparation d'une nouvelle entité - Transporteur");
		closeDialog();
		
		// Apostrophe in bottom button
		HtmlInput showCutRowsButton = getHtmlPage().getHtmlElementById("ox_openxavatest_Carrier__Carrier___showCutRows");
		assertEquals("Montrer l" + 
			// (char) 145 + // ANSI  
			(char) 8216 + // UNICODE
			"article coupé", showCutRowsButton.getValue());
		
		// Switzerland locale
		execute("Mode.list"); 
		setLocale("it-CH");
		assertAction("CRUD.new");
	}
		
	public void testRowActions() throws Exception {		
		execute("List.orderBy", "property=number"); 		
		assertListRowCount(5);
		execute("CRUD.deleteRow", "row=2");
		assertListRowCount(4);
		execute("List.viewDetail", "row=0");
		
		assertCollectionRowCount("fellowCarriers", 2);
		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "CUATRO");
		assertCollectionRowCount("fellowCarriersCalculated", 2);
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "DOS");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "CUATRO");
				
		execute("Carrier.translateName", "row=0,viewObject=xava_view_fellowCarriers");
		assertValueInCollection("fellowCarriers", 0, "name", "TWO");
		assertValueInCollection("fellowCarriers", 1, "name", "CUATRO");
		
		execute("Carrier.translateName", "row=1,viewObject=xava_view_fellowCarriersCalculated");
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "TWO");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "FOUR");	
	}
	
	public void testHideShowRows() throws Exception {		
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");
		
		execute("List.hideRows");		
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");
		
		execute("List.filter");
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");

		resetModule();
		assertListRowCount(0); 
		assertNoAction("List.hideRows");
		assertAction("List.showRows");

		execute("List.showRows");
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");
		
		resetModule();
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");	
		
		execute("List.hideRows");
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");
		
		customizeList();
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");

		execute("List.showRows");
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");
	}
	
	private void customizeList() throws Exception { 
		execute("List.addColumns");
		checkRow("selectedProperties", "drivingLicence.type");
		execute("AddColumns.addColumns");
		
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");		
	}

	public void testJDBCAction() throws Exception {
		assertListRowCount(5); 		
		execute("Carrier.deleteAll");
		assertNoErrors();
		assertListRowCount(0);
	}
	
	public void testResetSelectedOnReturnToList() throws Exception {
		checkRow(3);
		assertRowChecked(3);
		execute("CRUD.new");
		execute("Mode.list");
		assertRowUnchecked(3);
	}
	
	public void testActionOfCalculatedPropertyAlwaysPresent_referenceKeyEditableWhenInGroup_iconsImagesInViewAction_newFromChartsWithCalculatedCollection_viewSetValueNullForReference() throws Exception {  
		execute("ListFormat.select", "editor=Charts"); 
		execute("CRUD.new");		
		assertAction("Carrier.translateName");
		assertExists("calculated");
		assertNoEditable("calculated");

		assertEditable("warehouse.zoneNumber");
		assertEditable("warehouse.number");
		assertNoEditable("warehouse.name");
		
		assertIconsImagesInViewAction(); 
		execute("Mode.list");
		execute("ListFormat.select", "editor=List");
		
		execute("List.viewDetail", "row=0");
		assertValue("warehouse.zoneNumber", "1");
		assertValue("warehouse.number", "1");
		assertValue("warehouse.name", "CENTRAL VALENCIA");
		execute("Carrier.removeWarehouse");
		assertNoErrors();
		assertValue("warehouse.zoneNumber", "");
		assertValue("warehouse.number", "");
		assertValue("warehouse.name", "");		
	}
		
	public void testFilterIgnoringCase() throws Exception {
		assertListRowCount(5);
		String [] condition = { "", "cinco" };
		setConditionValues(condition);		
		execute("List.filter");		
		assertListRowCount(1);
		assertValueInList(0, "number", "5");
		assertValueInList(0, "name", "Cinco");
	}
	
	public void testPropertyDependsDescriptionsListReference_multipleKeyWithSpaces_descriptionsListLabels_modifyDialog_jdbcCalculatorWithFromProperties() throws Exception {
		execute("CRUD.new");
		assertLabel("drivingLicence", "Driving licence"); 
		assertValue("remarks","");
		DrivingLicence licence = new DrivingLicence();
		licence.setType("C ");			
		licence.setLevel(2); 
		String key = MetaModel.getForPOJO(licence).toString(licence);
		setValue("drivingLicence.KEY", key);		
		assertNoErrors();
		assertValue("drivingLicence.KEY", key);
		assertValue("remarks", "He can drive trucks: 5"); 
		
		assertNoDialog();
		execute("Reference.modify", "model=DrivingLicence,keyProperty=drivingLicence__KEY__"); 
		assertNoErrors();
		assertDialog();
		assertValue("description", "CAMIONES GRANDES \\ DE CARGA");
	}
	
	
	public void testOwnControllerForCreatingAndModifyingFromReference() throws Exception { 
		execute("List.viewDetail", "row=0");		
		// Modifying		
		execute("Reference.modify", "model=Warehouse,keyProperty=warehouse.number");		
		assertNoErrors();		
		assertDialog();
		assertAction("Modification.update");
		assertAction("Modification.cancel");
		assertValue("Warehouse", "name", "MODIFIED WAREHOUSE");		
		execute("Modification.cancel");
		assertNoDialog();
		
		// Creating
		execute("Reference.createNew", "model=Warehouse,keyProperty=warehouse.number");
		assertDialog(); 		
		assertNoErrors();
		assertAction("WarehouseCreation.saveNew");
		assertAction("NewCreation.cancel");
		assertValue("Warehouse", "name", "NEW WAREHOUSE");
		assertNoAction("Mode.list"); 	// Inside a dialog mode actions are disable
		
		execute("NewCreation.cancel");		
		execute("WarehouseReference.createNewNoDialog");
		
		assertNoDialog(); 		
		assertNoErrors();
		assertAction("WarehouseCreation.saveNew");
		assertAction("NewCreation.cancel");
		assertValue("Warehouse", "name", "NEW WAREHOUSE");
		assertNoAction("Mode.list"); 	// When navigate to another view actions are disable		
	}
	
	public void testDeleteUsingBeforeReferenceSearch_dialogLabel_noGroupingInReferenceSearchList() throws Exception {  
		assertListNotEmpty();
		String groupingLabel = "No grouping"; 
		assertTrue(getHtml().contains(groupingLabel)); 
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=xava.Carrier.warehouse.number");
		assertFalse(getHtml().contains(groupingLabel)); 
		assertDialog();
		assertDialogTitle("Choose a new value for Warehouse");
		execute("ReferenceSearch.cancel");
		assertNoDialog();
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Carrier deleted successfully");		
	}
	
	public void testGoListModeWithoutRecords() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("number", "1");
		assertValue("name", "UNO");
		
		deleteCarriers();
		
		execute("Mode.list");				
		execute("CRUD.new"); 
		assertNoErrors();
		assertValue("number", "");
		assertValue("name", "");
	}

	
	public void testDeleteWithoutSelected() throws Exception {
		assertCarriersCount(5);
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=2");		
		assertValue("number", "3");
		assertValue("name", "TRES");
		execute("CRUD.delete");
		assertMessage("Carrier deleted successfully");
		assertNoEditable("number");
		assertEditable("name");				
		assertValue("number", "4");
		assertValue("name", "CUATRO");
		assertCarriersCount(4);
		execute("Navigation.previous");
		assertValue("number", "2");
		assertValue("name", "DOS");
		assertNoErrors();		
		execute("Navigation.previous");
		assertValue("number", "1");
		assertValue("name", "UNO");
		assertNoErrors();		
		execute("CRUD.delete");
		assertMessage("Carrier deleted successfully");
		assertValue("number", "2");
		assertValue("name", "DOS");
		execute("Navigation.next");
		assertValue("number", "4");
		assertValue("name", "CUATRO");
		assertNoErrors();
		execute("Navigation.next");
		assertValue("number", "5");
		assertValue("name", "Cinco");
		assertNoErrors();				
		execute("CRUD.delete");
		assertMessage("Carrier deleted successfully");
		assertValue("number", "4");
		assertValue("name", "CUATRO");
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertValue("number", "2");
		assertValue("name", "DOS");
		assertCarriersCount(1);
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertNoErrors(); // If removal is done, any additional error message may be confused		
		assertValue("number", "");
		assertValue("name", "");
		// The last ramain without edit
		assertNoEditable("number");
		assertNoEditable("name");						
		assertCarriersCount(0);
		execute("CRUD.new");
		assertEditable("number");
		assertEditable("name");
	}
	
	public void testDeleteWithSelected() throws Exception {
		assertCarriersCount(5);
		checkRow(1); // 2, DOS
		checkRow(2); // 3, TRES
		checkRow(4); // 5, CINCO		
		execute("List.viewDetail", "row=1");
		assertValue("number", "2"); 
		assertValue("name", "DOS");
		execute("Navigation.next");
		assertValue("number", "3"); 
		assertValue("name", "TRES");
		assertNoErrors();		
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertCarriersCount(4);
		assertValue("number", "5");
		assertValue("name", "Cinco");
		assertNoErrors();
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertCarriersCount(3);
		assertValue("number", "2");
		assertValue("name", "DOS");
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertValue("number", "");
		assertValue("name", "");
		assertCarriersCount(2);
	}
		
	public void testFilterWithCalculatedValues() throws Exception {
		setConditionValues(new String [] { "3" });
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "number", "3");
		assertValueInList(0, "name", "TRES");
		setConditionValues(new String [] { "4", "CUA" }); // With 2 arguments
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "number", "4");
		assertValueInList(0, "name", "CUATRO");		
	}
	
	public void testCollectionWithCondition_clearConditionInCollectionExecutesFilter() throws Exception { 
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertNoErrors(); 
		assertValue("name", "UNO");
		assertCollectionRowCount("fellowCarriers", 3);
		assertValueInCollection("fellowCarriers", 0, "number", "2");
		assertValueInCollection("fellowCarriers", 1, "number", "3");
		assertValueInCollection("fellowCarriers", 2, "number", "4"); 
		setConditionValues("fellowCarriers", new String [] { "3"});
		execute("List.filter", "collection=fellowCarriers");
		assertCollectionRowCount("fellowCarriers", 1); 
		assertValueInCollection("fellowCarriers", 0, "number", "3");	
		
		HtmlElement c = getHtmlPage().getHtmlElementById("ox_openxavatest_Carrier__xava_collectionTab_fellowCarriers_xava_clear_condition");  
		c.click();
		waitAJAX();
		assertCollectionRowCount("fellowCarriers", 3);
	}
	
	public void testCalculatedCollection() throws Exception { 
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertNoErrors(); 
		assertValue("name", "UNO");
		assertCollectionRowCount("fellowCarriersCalculated", 3);
		assertValueInCollection("fellowCarriersCalculated", 0, "number", "2");
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "DOS");
		assertValueInCollection("fellowCarriersCalculated", 1, "number", "3");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "TRES");
		assertValueInCollection("fellowCarriersCalculated", 2, "number", "4");
		assertValueInCollection("fellowCarriersCalculated", 2, "name", "CUATRO");
		
		checkRowCollection("fellowCarriersCalculated", 1);
		checkRowCollection("fellowCarriersCalculated", 2);
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriersCalculated");
		assertNoErrors();
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "DOS");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "THREE"); 
		assertValueInCollection("fellowCarriersCalculated", 2, "name", "FOUR");		
	}
	
	public void testListActionInCollection() throws Exception {		
		execute("CRUD.new");		
		setValue("number", "1");		
		execute("CRUD.refresh");
		assertNoErrors(); 		

		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "TRES");
		assertValueInCollection("fellowCarriers", 2, "name", "CUATRO");
		
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriers");
		assertNoErrors();
		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "TRES");
		assertValueInCollection("fellowCarriers", 2, "name", "CUATRO");
				
		checkRowCollection("fellowCarriers", 1);
		checkRowCollection("fellowCarriers", 2);
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriers");
		assertNoErrors();
		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "THREE");
		assertValueInCollection("fellowCarriers", 2, "name", "FOUR");
		
		// Testing add/remove list actions programatically
		assertAction("Carrier.allToEnglish");
		assertNoAction("Carrier.todosAEspanol");		
		execute("Carrier.allToEnglish", "viewObject=xava_view_fellowCarriers");
		assertNoAction("Carrier.allToEnglish");
		assertAction("Carrier.todosAEspanol");
		
		// After ordering
		assertValueInCollection("fellowCarriers", 0, "name", "TWO");
		execute("List.orderBy", "property=number,collection=fellowCarriers"); // Ascending
		execute("List.orderBy", "property=number,collection=fellowCarriers"); // Descending
		assertValueInCollection("fellowCarriers", 0, "name", "FOUR"); 
		checkRowCollection("fellowCarriers", 0);
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriers");
		assertValueInCollection("fellowCarriers", 0, "name", "CUATRO");		
	}
	
	public void testAvailabeActionInListAndCollections() throws Exception {
		assertAvailableActionInList();
		assertAvailableActionInCalculatedCollection();
	}
	
	/**
	 * Test to verify the availability of the toUpperCase action.
	 * The first 4 rows should not have the action available,
	 * but the fifth row should have it.
	 */
	public void assertAvailableActionInList() throws Exception {
		// Check the number of isAvailable calls
		execute("Carrier.resetIsAvailableCallsCounter");
		execute("Carrier.showIsAvailableCallsCounter");
		assertMessage("isAvailable() has been called 5 times");
		
		execute("List.orderBy", "property=number");
		
		// Verify that the action is not available for the first 4 rows
		for (int i = 0; i < 4; i++) {
			assertNoAction("Carrier.toUpperCase", "row=" + i);
		}
		
		// Verify that the action is available for the fifth row (index 4)
		assertAction("Carrier.toUpperCase", "row=4");
		
		
		// Execute the action on the fifth row
		execute("Carrier.toUpperCase", "row=4");
		
		// Verify that the action is no longer available for the fifth row
		assertNoAction("Carrier.toUpperCase", "row=4");		
	}
	
	/**
	 * Test to verify the availability and functionality of the removeRemarks action.
	 * The action should be available only for carriers that have remarks.
	 * After executing the action, the remarks should be removed and the action should no longer be available.
	 */
	public void assertAvailableActionInCalculatedCollection() throws Exception {
		// Select the first carrier to access its detail view
		execute("List.viewDetail", "row=0");
		
		// To verify call number at end of the test
		execute("Carrier.resetIsAvailableCallsCounter");		
				
		// Verify that the removeRemarks action is available for the carriers with remarks (rows 0 and 1)
		// In the collection, carrier 2 is at index 0 and carrier 3 is at index 1
		assertAction("Carrier.removeRemarks", "row=0,viewObject=xava_view_fellowCarriersCalculated"); // Carrier 2 has remarks
		assertAction("Carrier.removeRemarks", "row=1,viewObject=xava_view_fellowCarriersCalculated"); // Carrier 3 has remarks
		
		// Verify that the action is not available for carrier 4 (index 2) which doesn't have remarks
		assertNoAction("Carrier.removeRemarks", "row=2,viewObject=xava_view_fellowCarriersCalculated");

		// Execute the action on carrier 3 (index 1)
		execute("Carrier.removeRemarks", "row=1,viewObject=xava_view_fellowCarriersCalculated");		
		
		// Verify that the action is no longer available for carrier 3 (index 1) after removing its remarks
		assertNoAction("Carrier.removeRemarks", "row=1,viewObject=xava_view_fellowCarriersCalculated");
		
		// The action should still be available for carrier 2 (index 0)
		assertAction("Carrier.removeRemarks", "row=0,viewObject=xava_view_fellowCarriersCalculated");
		
		// Check the number of isAvailable calls
		
		execute("Carrier.showIsAvailableCallsCounter");
		assertMessage("isAvailable() has been called 3 times");	
	}
		
	private void assertIconsImagesInViewAction() { 
		HtmlElement frameHeader = (HtmlElement) getHtmlPage().getHtmlElementById("ox_openxavatest_Carrier__label_warehouse").getParentNode().getParentNode();
		assertEquals("ox-frame-title", frameHeader.getAttribute("class"));
		String actionsXml = frameHeader.asXml();
		assertTrue(actionsXml.contains("<i class=\"mdi mdi-magnify"));
		assertTrue(actionsXml.contains("<i class=\"mdi mdi-library-plus"));
		assertTrue(actionsXml.contains("images/create_new.gif"));
	}
	
	private void assertCarriersCount(int c) throws Exception {
		int carrierCount = Carrier.findAll().size(); 
		assertEquals("Carriers count",c,carrierCount);
	}
		
}
