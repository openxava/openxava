package org.openxava.test.tests;

import javax.persistence.NoResultException;

import org.openxava.model.meta.MetaModel;
import org.openxava.test.model.Customer;
import org.openxava.test.model.Warehouse;
import org.openxava.util.Is;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerTest extends CustomizeListTestBase { 
	
	private String section;
	private String moduleName = "Customer"; 

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
		"List.hideRows",
		"List.changeConfiguration", 
		"List.changeColumnName", 
		"ListFormat.select", 
		"Customer.hideSellerInList",
		"Customer.showSellerInList"
	};
	
	
	public CustomerTest(String testName) {
		super(testName, "Customer");
		section = "";				
	}
	
	public CustomerTest(String testName, String moduleName, boolean section) {
		super(testName, moduleName);		
		this.section = section?"_section0":"";
		this.moduleName = moduleName; 
	}
	
	public void testDescriptionsListInListForSecondLevelReferences_clearConditionExecutesFilter() throws Exception { 
		assertListRowCount(5); 
		assertLabelInList(4, "Seller level"); 
		setConditionValues("", "", "", "", "A");
		execute("List.filter");
		assertListRowCount(3);
		
		HtmlElement c = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_" + moduleName + "__xava_clear_condition");  
		c.click();
		waitAJAX();
		assertListRowCount(5);
	}
	
	public void testReloadModuleInsideHtml_iconsInViewAction() throws Exception { // NaviOX with a special group combination, address with a group for city 		
		execute("CRUD.new");
		assertAction("EditableOnOff.setOn"); 
		reload();
		assertAction("EditableOnOff.setOn");
	}
	
	public void testPdfReportInNestedCollection() throws Exception { 
		execute("CRUD.new");
		setValue("number", "4");
		execute("CRUD.refresh");
		assertValue("name", "Cuatrero"); 
		assertCollectionRowCount("deliveryPlaces", 1); // Cuatrero has 1 delivery place
		execute("Collection.edit", "row=0,viewObject=xava_view" + getSection() + "_deliveryPlaces");
		assertCollectionRowCount("receptionists", 2); // The delivery place has 2 receptionist
		execute("Print.generatePdf", "viewObject=xava_view_receptionists");		
		assertContentTypeForPopup("application/pdf");		
	}
	
	public void testListActionInNestedCollection() throws Exception { 
		execute("CRUD.new");
		setValue("number", "4");
		execute("CRUD.refresh");
		assertValue("name", "Cuatrero"); 
		assertCollectionRowCount("deliveryPlaces", 1); // Cuatrero has 1 delivery place
		execute("Collection.edit", "row=0,viewObject=xava_view" + getSection() + "_deliveryPlaces");
		assertCollectionRowCount("receptionists", 2); // The delivery place has 2 receptionist
		execute("Collection.new", "viewObject=xava_view_receptionists");
		setValue("name", "JUNIT");
		execute("Collection.save");
		assertCollectionRowCount("receptionists", 3);
		checkRowCollection("receptionists", 2);
		execute("Collection.removeSelected", "viewObject=xava_view_receptionists");
		assertCollectionRowCount("receptionists", 2);
	}
				
	public void testObtainAggregateValues() throws Exception { 
		String city = getValueInList(0, "address.city");
		assertTrue("Value for city in first customer is required for run this test", !Is.emptyString(city));
		execute("List.viewDetail", "row=0");
		assertValue("address.city", city);
		assertNoLabel("addres.city");
	}
	
	public void testCalculatedPropertyDependsOnPropertyOfAggregate() throws Exception {
		execute("CRUD.new");
		assertValue("city", "");
		setValue("address.zipCode", "46540");
		assertValue("city", "46540 ");		
		setValue("address.city", "EL PUIG");
		assertValue("city", "46540 EL PUIG");
	}
	
	public void testChangeLabelProgrammatic() throws Exception {
		execute("CRUD.new");
		assertLabel("name", "Name"); 
		execute("Customer.changeNameLabel");
		assertLabel("name", "Malnom");
		
		assertLabel("address.state", "State");
		execute("Customer.changeStateLabel");
		assertLabel("address.state", "Province");
	}
		
	public void testFilterByMemberOfAggregate() throws Exception {  
		assertListRowCount(5); 
		String [] totalCondition = { "", "", "", "V" };		
		setConditionValues(totalCondition);		
		execute("List.filter");
		assertNoErrors();		
		assertListRowCount(3); // We rely in that there are 3 customer of Valencia
		
		// To sure that it works after customizing list
		removeColumn(5); 

		setConditionValues(totalCondition);		
		execute("List.filter");
		assertNoErrors(); 
		assertListRowCount(3); // We rely in that there are 3 customer of Valencia

		// Restoring the list
		execute("List.addColumns");
		execute("AddColumns.restoreDefault"); 
		assertNoErrors();
	}
	
	public void testChangeView() throws Exception { 
		execute("CRUD.new");
		assertExists("number");
		assertExists("remarks");
		execute("Customer.changeToSimpleView");
		assertExists("number");		
		assertNotExists("remarks");
	}
	
	public void testOnChangePropertyOfReferenceWithMultipleKeyAsListDescriptionInAggregateOfCollection() throws Exception {
		execute("List.viewDetail", "row=0"); 
		execute("Collection.new", "viewObject=xava_view" + getSection() + "_deliveryPlaces");		
		assertValue("remarks", "");
		Warehouse warehouse = new Warehouse();
		warehouse.setNumber(1);
		warehouse.setZoneNumber(1);
		String warehouseKey = MetaModel.getForPOJO(warehouse).toString(warehouse);
		setValue("preferredWarehouse.KEY", warehouseKey);
		assertValue("remarks", "PREFERRED WAREHOUSE IS 1");
	}
		
	public void testViewGetValueInGroup() throws Exception {
		execute("CRUD.new");
		assertValue("remarks", ""); 
		setValue("relationWithSeller", "RELATION WITH SELLER JUNIT");
		execute("Customer.moveRelationWithSellerToRemarks");
		assertNoErrors();
		assertValue("remarks", "RELATION WITH SELLER JUNIT");
	}
	
	public void testFilterByValidValues() throws Exception { 
		int total = Customer.findAll().size();
		int normalOnes = Customer.findNormalOnes().size();
		int steadyOnes = Customer.findSteadyOnes().size();
		assertTrue("It is required customers for run this test", total > 0);
		assertTrue("It is required normal customers for run this test", normalOnes > 0);
		assertTrue("It is required steady customers for run this test", steadyOnes > 0);
		assertTrue("It is required at least 10 customers to run this test", total < 10);
		assertListRowCount(total);
						
		if (usesAnnotatedPOJO()) {
			// In OX3 (using EJB3 entities) we use Enum with null for no value, and 0 for first value
			String [] normalCondition = { " ", "0", "", "" };		
			setConditionValues(normalCondition);
		}
		else {
			// In OX2 (using XML component) we use int with 0 for no value, and 1 for first value
			String [] normalCondition = { " ", "1", "", "" };		
			setConditionValues(normalCondition);			
		}
		// execute("List.filter"); // Not needed because filterOnChange=true
		assertNoErrors();
		
		assertListRowCount(normalOnes);
				
		if (usesAnnotatedPOJO()) {
			// In OX3 (using EJB3 entities) we use Enum with null for no value, and 0 for first value
			String [] steadyCondition = { " ", "1", "", "" }; 
			setConditionValues(steadyCondition);
		}
		else {
			// In OX2 (using XML component) we use int with 0 for no value, and 1 for first value
			String [] steadyCondition = { " ", "2", "", "" }; 
			setConditionValues(steadyCondition);			
		}
		// execute("List.filter"); // Not needed because filterOnChange=true
		assertNoErrors();
		assertListRowCount(steadyOnes);		
		
		String [] totalCondition = { "", "", "", "" };		
		setConditionValues(totalCondition);		
		// execute("List.filter"); // Not needed because filterOnChange=true
		assertNoErrors();
		assertListRowCount(total);		
	}
		
	public void testHideShowGroup() throws Exception {		
		execute("CRUD.new");
		assertExists("seller.number");
		assertExists("seller.name");
		assertExists("relationWithSeller");
		
		execute("Customer.hideSeller");
		assertNotExists("seller.number");
		assertNotExists("seller.name");
		assertNotExists("relationWithSeller");
		
		execute("Customer.showSeller");
		assertExists("seller.number");
		assertExists("seller.name");
		assertExists("relationWithSeller");
	}
	

	public void testSearchReferenceWithAListInAGroup() throws Exception {
		execute("CRUD.new");		
		assertValue("seller.name", "");		
		execute("MyReference.search", "keyProperty=seller.number");
		String sellerName = getValueInList(0, 1);
		execute("ReferenceSearch.choose", "row=0");
		assertTrue("The name of first seller can't be empty string", sellerName.trim().length() > 0);		
		assertValue("seller.name", sellerName);		
	}

	public void testValidValues() throws Exception {   				
		execute("CRUD.new");
		// OX3 uses Java 5 enums, and enums have base 0. OX2 valid-value has base 1  
		boolean base0 = usesAnnotatedPOJO();
		String [][] validValues = { 
			{ base0?"0":"1", "Normal" }, 
			{ base0?"1":"2", "Steady" },
			{ base0?"2":"3", "Special" }	
		};
		
		assertValue("type", base0?"2":"3");		
		assertValidValues("type", validValues);
	}
	
	public void testOnChangeAction() throws Exception {
		execute("CRUD.new");		
		assertValue("type", usesAnnotatedPOJO()?"2":"3");		
		setValue("name", "PEPE");
		assertValue("type", usesAnnotatedPOJO()?"2":"3");
		setValue("name", "JAVI");
		assertValue("type", usesAnnotatedPOJO()?"1":"2"); 		
	}	
		
	
	// references to entities and aggregates
	public void testCreateModifyAndReadWithReferencesAndOverwriteSaveAction() throws Exception { 			
		// Create one new
		execute("CRUD.new");
		assertNoErrors();
		setValue("number", "66");
		setValue("type", usesAnnotatedPOJO()?"1":"2");
		setValue("name", "JUNIT CUSTOMER");
		setValue("address.street", "Junit Street");
		setValue("address.zipCode", "66666");
		setValue("address.city", "POBLE JUNIT PER A J");
		setValue("address.state.id", "NY");
		setValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI"); 
		setValue("relationWithSeller", "A RELATION");
		setValue("alternateSeller.number", "2");		
		execute("Customer.save");  		
		assertNoErrors(); 

		assertNoEditable("number");
		assertEditable("type");		
		assertValue("number", "66");
		assertValue("type", usesAnnotatedPOJO()?"1":"2");
		assertValue("name", "Junit Customer"); // Testing formatter with sets
		assertValue("address.street", "JUNIT STREET"); // Testing overwrite default formatter for applicacion. Use UpperCaseFormatter
		assertValue("address.zipCode", "66666");
		assertValue("address.city", "POBLE JUNIT PER A J");
		assertValue("address.state.id", "NY");
		assertValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI"); 
		assertValue("relationWithSeller", "A RELATION");
		assertValue("alternateSeller.number", "2");						

		execute("CRUD.new"); 
		assertValue("number", "");
		assertValue("type", usesAnnotatedPOJO()?"2":"3");
		assertValue("name", "");
		assertValue("address.street", "");
		assertValue("address.zipCode", "");
		assertValue("address.city", "");
		assertValue("address.state.id", "");		
		assertValue("seller.number", "");
		assertValue("seller.name", "");
		assertValue("relationWithSeller", "GOOD"); // default-value-calculator in a group
		assertValue("alternateSeller.number", "");
		assertValue("alternateSeller.name", "");		
		
		// Search just created
		setValue("number", "66");
		execute("CRUD.refresh");		
		assertValue("number", "66");
		assertValue("type", usesAnnotatedPOJO()?"1":"2"); 
		assertValue("name", "Junit Customer"); // Testing formatter with sets
		assertValue("address.street", "JUNIT STREET"); // Testing overwrite default formatter for applicacion. Use UpperCaseFormatter
		assertValue("address.zipCode", "66666");
		assertValue("address.city", "POBLE JUNIT PER A J");
		assertValue("address.state.id", "NY");		
		assertValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI");
		assertValue("relationWithSeller", "A RELATION");
		assertValue("alternateSeller.number", "2");
		assertValue("alternateSeller.name", "JUANVI LLAVADOR"); 
		
		// Modify
		setValue("seller.number", "2");
		execute("Customer.save");
		assertNoErrors();
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("type", usesAnnotatedPOJO()?"2":"3");
		assertValue("name", "");
		
		// Verifying modified
		execute("CRUD.new");
		setValue("number", "66");
		execute("CRUD.refresh");		
		assertValue("number", "66");
		assertValue("type", usesAnnotatedPOJO()?"1":"2"); 
		assertValue("name", "Junit Customer");
		assertValue("seller.number", "2");
		assertValue("seller.name", "JUANVI LLAVADOR");
										
		// Delete it
		execute("CRUD.delete");													
		assertNoErrors();
		assertMessage("Customer deleted successfully");
	}
		
	public void testSearchReferenceOnChangeCodeAndOnChangeActionInSubview_requiredErrorStyleInAggregates() throws Exception { 
		execute("CRUD.new");
		setValue("seller.number", "1");
		assertValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI");
		assertNoErrors();
		setValue("seller.number", "2");
		assertValue("seller.number", "2");
		assertValue("seller.name", "JUANVI LLAVADOR");
		assertNoErrors();		
		setValue("seller.number", "");
		assertValue("seller.number", "");
		assertValue("seller.name", "");
		assertNoErrors();				
		setValue("alternateSeller.number", "2");
		assertValue("alternateSeller.number", "2");
		assertValue("alternateSeller.name", "DON JUANVI LLAVADOR");
		assertNoErrors();
		assertMessage("OnChangeVoidAction executed");
		assertMessagesCount(1);					
		
		execute("Customer.save");
		assertError("Value for Street in Address is required");
		assertError("Value for State in Address is required");
		HtmlSpan street = (HtmlSpan) getHtmlPage().getElementById("ox_OpenXavaTest_" + moduleName + "__editor_address___street");
		assertTrue("street has no error style", street.getAttribute("class").contains("ox-error-editor"));
		HtmlSpan state = (HtmlSpan) getHtmlPage().getElementById("ox_OpenXavaTest_" + moduleName + "__reference_editor_address___state");
		assertTrue("state has no error style", state.getAttribute("class").contains("ox-error-editor"));
	}
	
	public void testSearchReferenceWithListAndOnChangeActionInSubview() throws Exception {
		execute("CRUD.new");		
		assertValue("alternateSeller.number", "");
		assertValue("alternateSeller.name", "");
		// Choose using check boxes
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		checkRow(0);
		execute("ReferenceSearch.choose");
		assertValue("alternateSeller.number", "1");
		assertValue("alternateSeller.name", "DON MANUEL CHAVARRI");
		assertMessage("OnChangeVoidAction executed");
		assertMessagesCount(1);								
				
		// Choose using link		
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		execute("ReferenceSearch.choose", "row=1");
		assertValue("alternateSeller.number", "2");
		assertValue("alternateSeller.name", "DON JUANVI LLAVADOR");
		assertMessage("OnChangeVoidAction executed");
		assertMessagesCount(1);								
		
		// Canceling
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		// It is checked the 0 of other time
		execute("ReferenceSearch.cancel");
		assertValue("alternateSeller.number", "2");
		assertValue("alternateSeller.name", "DON JUANVI LLAVADOR");								
	}
	
	public void testCustomSearchReferenceAction_searchDialogWhenNotFound() throws Exception {
		execute("CRUD.new");
		String html = getHtml();		
		assertTrue("Search of 'seller' should be 'MyReference.search'", html.indexOf("'MyReference.search', 'keyProperty=seller.number'") > 0);
		assertTrue("Search 'alternateSeller' should be 'Reference.search'", html.indexOf("'Reference.search', 'keyProperty=alternateSeller.number'") > 0);
		execute("MyReference.search", "keyProperty=seller.number");
		assertListRowCount(2); // Because custome searh action filter
		execute("ReferenceSearch.cancel");
		
		// Typing an inexistent code shows the search dialog
		setValue("seller.number", "66");
		assertListRowCount(2); // The search list is shown using the custom search action
		execute("ReferenceSearch.cancel");		
		
		// Testing that the main tab of module is not affected
		execute("Mode.list");		
		assertNoAction("Mode.list");
		assertAction("List.changeConfiguration"); // To test we are in list mode 
	}
	
	public void testReferencesIfBlankKey() throws Exception {
		execute("CRUD.new");
		setValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI");
		setValue("seller.number", "");
		assertValue("seller.name", "");
		setValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI");
	}
	
	public void testLeftJoinInListModeForReference() throws Exception {  
		assertActions(listActions);
		int initialRows = getListRowCount();
		assertTrue("This test only run with less than 10 rows", initialRows < 10);
		
		// Create
		execute("CRUD.new");
		setValue("number", "66");
		setValue("type", "1");
		setValue("name", "JUNIT CUSTOMER");
		setValue("address.street", "JUNIT STREET");
		setValue("address.zipCode", "66666");
		setValue("address.city", "POBLE JUNIT PER A J");
		setValue("address.state.id", "NY");
		execute("Customer.save");		
		assertNoErrors(); 
		
		// Verifying that it is in the list althought it has not seller
		execute("Mode.list");
		assertActions(listActions);
		assertListRowCount(initialRows + 1);		
		
		// Search just created
		execute("CRUD.new");
		setValue("number", "66");
		execute("CRUD.refresh");		
		assertValue("number", "66");
		assertValue("type", "1"); 
		assertValue("name", "Junit Customer");
		assertValue("address.street", "JUNIT STREET");
		assertValue("address.zipCode", "66666");
		assertValue("address.city", "POBLE JUNIT PER A J");
		assertValue("address.state.id", "NY");		
		assertValue("seller.number", "");
		assertValue("seller.name", "");
		assertValue("relationWithSeller", "GOOD"); // default-value-calculator in a group
		assertValue("alternateSeller.number", "");
		assertValue("alternateSeller.name", "");
		
		// Delete it
		execute("CRUD.delete");											
		assertNoErrors();
		assertMessage("Customer deleted successfully");
	}
	
	public void testIfKeyNotExistsInReferenceNotExecuteAction() throws Exception {
		execute("CRUD.new");				
		setValue("relationWithSeller", "HOLA");		
		setValueNotNotify("seller.number", "53"); // We suppose that not exists		
		assertNoDialog(); 
		execute("CRUD.new");		
		assertDialog(); 
		assertAction("ReferenceSearch.choose"); 
		closeDialog();
		assertValue("relationWithSeller", "HOLA"); // That implies that 'new' not was executed		
	}
		
	public void testPropertiesOfEntityReferenceAndAggregateInList() throws Exception {  
		setConditionValues(new String [] { "JAVI", "" });
		execute("List.filter");
		assertListRowCount(1);
		
		assertValueInList(0, 0, "Javi");
		assertValueInList(0, 2, "MANUEL CHAVARRI"); // property of reference to entity
		assertValueInList(0, 3, "EL PUIG"); // property of reference to aggregate
		assertValueInList(0, 4, "MANAGER"); // reference with 2 levels of indirection
		assertValueInList(0, 5, "NEW YORK"); // property of a reference inside an aggregate
	}
	
	public void testNestedAggregateCollections() throws Exception { 
		// Creating
		execute("CRUD.new");
		setValue("number", "66");
		setValue("type", usesAnnotatedPOJO()?"0":"1");
		setValue("name", "JUNIT CUSTOMER");
		setValue("address.street", "JUNIT STREET");
		setValue("address.zipCode", "66666");
		setValue("address.city", "POBLE JUNIT PER A J");
		setValue("address.state.id", "NY");
				
		assertNoDialog();
		execute("Collection.new", "viewObject=xava_view"  + getSection() + "_deliveryPlaces");
		assertDialog();
		setValue("name", "DELIVERY JUNIT 1");
		setValue("address", "STREET JUNIT 1");		
		execute("Collection.new", "viewObject=xava_view_receptionists"); 
		setValue("name", "RECEPTIONISTS JUNIT 1 - 1");		
		execute("Collection.save");
		assertNoErrors(); 

		assertCollectionRowCount("receptionists", 1);
		assertValueInCollection("receptionists", 0, 0, "RECEPTIONISTS JUNIT 1 - 1");
		
		closeDialog(); 
		assertNoEditable("number"); // Header is saved hence it is not editable
		
		assertCollectionRowCount("deliveryPlaces", 1);
		assertValueInCollection("deliveryPlaces", 0, 0, "DELIVERY JUNIT 1");
		assertValueInCollection("deliveryPlaces", 0, 1, "STREET JUNIT 1");
				
		execute("Collection.edit", "row=0,viewObject=xava_view" + getSection() + "_deliveryPlaces"); 
		execute("Collection.new", "viewObject=xava_view_receptionists"); 
		setValue("name", "RECEPTIONISTS JUNIT 1 - 2");		
		execute("Collection.save");
		
		assertCollectionRowCount("receptionists", 2);
		assertValueInCollection("receptionists", 0, 0, "RECEPTIONISTS JUNIT 1 - 1");
		assertValueInCollection("receptionists", 1, 0, "RECEPTIONISTS JUNIT 1 - 2");		
			
		execute("Collection.save");
		assertCollectionRowCount("deliveryPlaces", 1); 
				
		execute("Collection.new", "viewObject=xava_view"  + getSection() + "_deliveryPlaces"); 
		setValue("name", "DELIVERY JUNIT 2");
		setValue("address", "STREET JUNIT 2");		
		execute("Collection.new", "viewObject=xava_view_receptionists");
		setValue("name", "RECEPTIONISTS JUNIT 2 - 1");				
		execute("Collection.save");
		
		execute("Collection.new", "viewObject=xava_view_receptionists"); 
		setValue("name", "RECEPTIONISTS JUNIT 2 - 2");				
		execute("Collection.save");
		
		assertCollectionRowCount("receptionists", 2);
		assertValueInCollection("receptionists", 0, 0, "RECEPTIONISTS JUNIT 2 - 1");
		assertValueInCollection("receptionists", 1, 0, "RECEPTIONISTS JUNIT 2 - 2");
		
		closeDialog();
		assertCollectionRowCount("deliveryPlaces", 2);
		assertValueInCollection("deliveryPlaces", 0, 0, "DELIVERY JUNIT 1");
		assertValueInCollection("deliveryPlaces", 0, 1, "STREET JUNIT 1");
		assertValueInCollection("deliveryPlaces", 1, 0, "DELIVERY JUNIT 2");
		assertValueInCollection("deliveryPlaces", 1, 1, "STREET JUNIT 2");		
		
		execute("Collection.edit", "row=0,viewObject=xava_view" + getSection() + "_deliveryPlaces");
		
		assertValueInCollection("receptionists", 0, 0, "RECEPTIONISTS JUNIT 1 - 1");
		assertValueInCollection("receptionists", 1, 0, "RECEPTIONISTS JUNIT 1 - 2");
		closeDialog(); 
				
		// Search
		execute("CRUD.new"); 
		assertCollectionRowCount("deliveryPlaces", 0); 
		setValue("number", "66");
		execute("CRUD.refresh");
		
		assertCollectionRowCount("deliveryPlaces", 2);
		assertValueInCollection("deliveryPlaces", 0, 0, "DELIVERY JUNIT 1");
		assertValueInCollection("deliveryPlaces", 0, 1, "STREET JUNIT 1");
		assertValueInCollection("deliveryPlaces", 1, 0, "DELIVERY JUNIT 2");
		assertValueInCollection("deliveryPlaces", 1, 1, "STREET JUNIT 2");

		execute("Collection.edit", "row=0,viewObject=xava_view" + getSection() + "_deliveryPlaces");
		assertValueInCollection("receptionists", 0, "name", "RECEPTIONISTS JUNIT 1 - 1");
		assertValueInCollection("receptionists", 1, "name", "RECEPTIONISTS JUNIT 1 - 2");
		closeDialog(); 
		
		execute("Collection.edit", "row=1,viewObject=xava_view" + getSection() + "_deliveryPlaces");
		assertValueInCollection("receptionists", 0, "name", "RECEPTIONISTS JUNIT 2 - 1"); 
		assertValueInCollection("receptionists", 1, "name", "RECEPTIONISTS JUNIT 2 - 2");
		closeDialog(); 
		assertNoDialog();
		
		// Delete
		execute("CRUD.delete");												
		assertNoErrors();
		assertMessage("Customer deleted successfully");
	}

	public void testSetEditableOfReferences_notOnChangeActionsOfReferences() throws Exception {  
		execute("List.viewDetail", "row=0");
		assertNoMessage("OnChangeVoidAction executed"); 
		assertEditable("address.street");
		assertEditable("seller.number");
		assertNoEditable("seller.name");
		assertAction("Reference.search");
		assertAction("Reference.createNew");
		
		execute("EditableOnOff.setOff");
		assertNoEditable("address.street");
		assertNoEditable("seller.number");
		assertNoEditable("seller.name");
		assertNoAction("Reference.search"); 
		assertNoAction("Reference.createNew");
		
		execute("EditableOnOff.setOn");
		assertEditable("address.street");
		assertEditable("seller.number");
		assertNoEditable("seller.name");
		assertAction("Reference.search");					
		assertAction("Reference.createNew");
	}
	
	public void testFocus() throws Exception {
		// Focus in first active
		execute("List.viewDetail", "row=0");
		assertFocusOn("type");
		execute("CRUD.new");
		assertFocusOn("number");
		
		// Focus not move when we execute actions
		execute("Customer.hideSeller");
		assertFocusOn("number");
		execute("Customer.showSeller");
		assertFocusOn("number");
		
		// Property changed that produce submit do advance the focus
		setValue("seller.number", "1");
		assertFocusOn("relationWithSeller");
	}
	
	
	private String getSection() {
		return section;		
	}
	
	public void testFilterToDescriptionsListWithBaseConditionAndFilter() throws Exception {   
		try{
			// warehouse has a filter zoneNumber <= 999
			Warehouse.findByZoneNumberNumber(1000, 1);	
		}
		catch(NoResultException ex){
			fail("It needs a warehouse with zone number 1000");
		}
		
		setConditionValues(new String[] {"cuatrero"} );
		execute("List.filter");
		execute("List.viewDetail", "row=0");
		
		assertLabelInCollection("deliveryPlaces", 3, "Preferred warehouse"); 
		assertValueInCollection("deliveryPlaces", 0, 3, "CENTRAL VALENCIA"); 
		setConditionValues("deliveryPlaces", new String[] { "", "", "", "[.1.2.]" } );
		// execute("List.filter", "collection=deliveryPlaces"); // Not needed because filterOnChange=true
		assertCollectionRowCount("deliveryPlaces", 0);		
		setConditionValues("deliveryPlaces", new String[] { "", "", "", "[.1.1.]" } );
		// execute("List.filter", "collection=deliveryPlaces"); // Not needed because filterOnChange=true
		assertCollectionNotEmpty("deliveryPlaces");
		
		try{
			setConditionValues("deliveryPlaces", new String[] { "", "", "", "[.1.1000.]"} );	
		}
		catch(IllegalArgumentException ex){
			assertTrue(ex.getMessage().equals("No option found with value: [.1.1000.]"));
		}
		
	}
	
	public void testDescriptionValidValuesEditor() throws Exception { 
		execute("CRUD.new");
		assertValue("type", usesAnnotatedPOJO()?"2":"3");
		setValue("type", usesAnnotatedPOJO()?"1":"2");
		assertValue("type", usesAnnotatedPOJO()?"1":"2");
		setValue("type", usesAnnotatedPOJO()?"2":"3");
		assertValue("type", usesAnnotatedPOJO()?"2":"3");		
	}
}
