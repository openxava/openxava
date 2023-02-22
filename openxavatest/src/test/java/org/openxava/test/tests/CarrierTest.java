package org.openxava.test.tests;

import org.openxava.model.meta.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

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
		assertEquals("javascript:openxava.executeAction('openxavatest', 'Carrier', 'Effacer l" 
			+ (char) 8216 
			+"entité courante: Etes-vous sûr(e) ?', false, 'CRUD.delete')", 
			deleteLink.getHrefAttribute());
		execute("CRUD.delete");
		execute("Mode.list");
		assertListRowCount(4);
		
		// Confirm row action with apostrophe and title in actions
		// To ensure the title and question have apostrophe
		String deleteRowLink = "<a class=\"ox-image-link\" "
				+ "title=\"Effacer l"  
				// (char) 145 // ANSI
				+ (char) 8216 // UNICODE
				+ "entité\" "
				+ "href=\"javascript:openxava.executeAction('openxavatest', 'Carrier', 'Effacer l" 
				// + (char) 145 // ANSI
				+ (char) 8216 // UNICODE
				+ "entité la ligne 1: êtes-vous sûr ?', false, 'CRUD.deleteRow', 'row=0')\">";
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
			"article coupé", showCutRowsButton.getValueAttribute());
		
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
	
	public void testCustomizeCollection() throws Exception {
		// Original status		
		assertListColumnCount(3);
		assertLabelInList(0, "Calculated");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Name");
		execute("List.viewDetail", "row=0");
				
		assertCollectionColumnCount("fellowCarriers", 4); 
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");		
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");
		
		// Customize the collection
		moveColumn("fellowCarriers", 2, 3); 
		assertNoErrors();
		
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");		
		
		// The main list not modified
		execute("Mode.list");
		assertListColumnCount(3);
		assertLabelInList(0, "Calculated");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Name");

		// The collection continues modified
		execute("List.viewDetail", "row=0");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");		
		
		// Add columns
		execute("List.addColumns", "collection=fellowCarriers");
		assertNoAction("AddColumns.showMoreColumns"); // Because has not more than second level properties
		assertCollectionRowCount("xavaPropertiesList", 6);
		assertValueInCollection("xavaPropertiesList",  0, 0, "Driving licence description"); 
		assertValueInCollection("xavaPropertiesList",  1, 0, "Driving licence level");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Driving licence type");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Warehouse name");
		assertValueInCollection("xavaPropertiesList",  4, 0, "Warehouse number");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Warehouse zone"); 
		checkRow("selectedProperties", "warehouse.name");		
 		execute("AddColumns.addColumns");

		assertCollectionColumnCount("fellowCarriers", 5);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");				
		assertLabelInCollection("fellowCarriers", 4, "Warehouse"); // This is "Name of Warehouse" with label optimized 
		 		
		// Other customizations
		moveColumn("fellowCarriers", 3, 4); 
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection("fellowCarriers", 4, "Remarks");
						
		removeColumn("fellowCarriers", 4); 
		assertCollectionColumnCount("fellowCarriers", 4); 
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		
		// Changing column name
		execute("List.changeColumnName", "property=name,collection=fellowCarriers");
		assertDialog();
		assertValue("name", "Name");
		setValue("name", "Carrier");
		execute("ChangeColumnName.change");
		assertLabelInCollection("fellowCarriers", 1, "Carrier");
		
		// Adding clicking in row
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.addColumn", "property=warehouse.number");
		assertCollectionColumnCount("fellowCarriers", 5);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection("fellowCarriers", 4, "Warehouse number");
		
		// Restoring		
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.restoreDefault");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");
		
		execute("List.changeColumnName", "property=name,collection=fellowCarriers");
		assertValue("name", "Carrier");
		setValue("name", "Name");
		execute("ChangeColumnName.change");
		assertLabelInCollection("fellowCarriers", 1, "Name");		
		
		// Cancel in AddColumns returns to detail (not list mode)
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.cancel");
		assertValue("name", "UNO"); // In detail mode 
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
		assertValue("description", "CAMIONES GRANDES");
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
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
		assertValue("Warehouse", "name", "NEW WAREHOUSE");
		assertNoAction("Mode.list"); 	// Inside a dialog mode actions are disable
		
		execute("NewCreation.cancel");		
		execute("WarehouseReference.createNewNoDialog");
		
		assertNoDialog(); 		
		assertNoErrors();
		assertAction("NewCreation.saveNew");
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
