package org.openxava.test.tests.bymodule;

import java.util.*;

import org.apache.commons.lang3.*;
import org.htmlunit.html.*;
import org.openxava.tests.*;
import org.openxava.web.*;

/**
 * For testing that only the needed parts are reload by AJAX. <p>
 * 
 * Because this is very technology dependent we put all this test here
 * and not scattered for the module tests, in order to remove when 
 * we'll go with other presentation technology.<br> 
 * 
 * @author Javier Paniza
 */

public class AJAXTest extends ModuleTestBase { 
	
	public AJAXTest(String nameTest) {
		super(nameTest, null);
	}
	
	public void testListFormats_filterInCharts() throws Exception { 
		changeModule("Carrier");
		setConditionValues("1");
		execute("List.filter");
		execute("ListFormat.select", "editor=Charts");
		assertLoadedParts("bottom_buttons, button_bar, view, errors, messages");
		selectListConfiguration("All");
		assertLoadedParts("view, errors, messages");
		execute("ListFormat.select", "editor=List"); 
	}
	
	public void testReadOnlyOnCreate() throws Exception {
		changeModule("ColorWithReadOnlyOnCreate");
		assertValueInList(5, 2, "");
		execute("List.viewDetail", "row=5");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1"); 
		assertNoEditable("mixture.colorName2"); 
		
		execute("CRUD.new");
		assertEditable("name");
		assertEditable("usedTo");
		assertEditable("mixture.colorName1"); 
		assertEditable("mixture.colorName2"); 
		
		assertLoadedParts(
			"editor_name, editor_number," +
			"reference_editor_characteristicThing, reference_editor_usedTo, editor_sample," +
			"editor_mixture.colorName1,editor_mixture.colorName2," +
			"property_actions_mixture.colorName2," +
			"bottom_buttons, button_bar, errors, messages");
		
		execute("CRUD.new");
		assertLoadedParts("reference_editor_characteristicThing, errors, messages"); 
		
		execute("Navigation.first");
		assertNoEditable("mixture.colorName1"); 
		assertNoEditable("mixture.colorName2");
		assertValue("mixture.colorName1", ""); 
		assertValue("mixture.colorName2", "");
		execute("Navigation.next");
		assertNoEditable("mixture.colorName1"); 
		assertNoEditable("mixture.colorName2");
		assertValue("mixture.colorName1", ""); 
		assertValue("mixture.colorName2", "");
		assertLoadedParts(
			"editor_number, editor_name, editor_sample, editor_hexValue," +
			"reference_editor_usedTo, reference_editor_characteristicThing," +
			"errors, messages"); 
	}
	
	public void testDescriptionsListInElementCollectionDependsOnMainEntityProperty() throws Exception {  
		changeModule("ProductsEvaluation2");
		execute("CRUD.new"); 
		setValue("family.number", "1"); 
		assertLoadedParts( 
			"reference_editor_evaluations.0.product," +
			"reference_editor_evaluations.1.product," +
			"errors, messages");
	}
	
	public void testElementCollections() throws Exception { 
		// WARNING! Don't change the order of the below code, because the last case only fails with this order 
		changeModule("Quote");
		execute("List.viewDetail", "row=0");
		assertValue("year", "2014"); // This one ... 
		assertValue("number", "1");  // ... has 3 details
		setValueInCollection("details", 0, "quantity", "61");		
		assertLoadedParts( 
			"editor_details.0.amount," +
			"collection_total_0_4_details.," +
			"collection_total_2_4_details.," +
			"collection_total_3_4_details.," +	
			"editor_estimatedProfit," + 
			"errors, messages");
		setValueInCollection("details", 1, "product.number", "1");
		assertLoadedParts(
			"editor_details.1.product.description," + 
			"editor_details.1.unitPrice," + 
			"editor_details.1.amount," +
			"collection_total_0_4_details.," +
			"collection_total_2_4_details.," +
			"collection_total_3_4_details.," +
			"editor_estimatedProfit," + 
			"errors, messages");
		setValueInCollection("details", 2, "quantity", "61");		
		assertLoadedParts(
			"editor_details.2.amount," +
			"collection_total_0_4_details.," +
			"collection_total_2_4_details.," +
			"collection_total_3_4_details.," +
			"editor_estimatedProfit," + 
			"errors, messages");
		setValue("customer.number", "2"); // To test that the collection is not affected
		assertLoadedParts(
			"editor_customer.name," +
			"errors, messages");
		assertValueInCollection("details", 1, "product.number", "1");
		assertValueInCollection("details", 1, "unitPrice", "11.00"); 
		setValueInCollection("details", 1, "product.number", "5");
		assertValueInCollection("details", 1, "unitPrice", "11.00"); // Because product 5 has the same unit price than 1
		assertLoadedParts(
			"editor_details.1.product.description," +
			"errors, messages");
		
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_openxavatest_Quote__details___1"); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("i").get(0);
		removeIcon.click();

		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertLoadedParts( 
			"collection_total_0_4_details.," +
			"collection_total_2_4_details.," +
			"collection_total_3_4_details.," +
			"editor_estimatedProfit," + 
			"errors, messages");		
		
		// This case only fails with after the above steps in the exact order
		execute("Quote.setQuantitiesToSeven");
		assertLoadedParts(
			"collection_details.," +
			"frame_detailsheader," +	
			"errors, messages");		
	}
	
	public void testDescriptionsListInElementCollections() throws Exception { 
		changeModule("ProductExpenses");
		execute("CRUD.new");
		setValueInCollection("expenses", 0, "family.number", "1");
		assertLoadedParts( 
			"reference_editor_expenses.0.subfamily," + 
			"errors, messages");
	}
	
	public void testRemoveRowInElementCollections() throws Exception {  
		changeModule("Reallocation");
		execute("List.viewDetail", "row=0");
		assertLoadedParts("core");
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_openxavatest_Reallocation__details___1"); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("i").get(0); 
		removeIcon.click();		
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertLoadedParts("core"); // No AJAX call at all 			
	}

	
	public void testReferencesInElementCollections() throws Exception { 
		changeModule("Route");
		execute("CRUD.new");
		setValueInCollection("visits", 0, "customer.number", "1");
		assertLoadedParts( 
			"editor_visits.0.customer.name," + 
			"editor_visits.0.description," +
			"errors, messages");
	}
	
	public void testNotDuplicateDivOnLoadCollection() throws Exception { 
		changeModule("Seller");
		execute("List.viewDetail", "row=0");
		assertEquals(1, StringUtils.countMatches(getHtml(), "ox_openxavatest_Seller__collection_customers___"));
		execute("List.filter", "collection=customers"); 
		assertEquals(2, StringUtils.countMatches(getHtml(), "ox_openxavatest_Seller__collection_customers___")); 
	}
	
	public void testOnSelectElementNotReloadCollection() throws Exception { 
		changeModule("Formula");
		execute("List.viewDetail", "row=0");
		checkRowCollection("ingredients", 0); 
		assertLoadedParts(  
				"editor_selectedIngredientNames," +
				"editor_selectedIngredientSize," +
				"errors," +
				"messages,");
	}
	
	public void testDescriptionsListWithShowReferenceView() throws Exception { 
		changeModule("CustomerSellerAsDescriptionsListShowingReferenceView");
		execute("List.viewDetail", "row=0");
		HtmlElement sellerEditor = getHtmlPage().getHtmlElementById("ox_openxavatest_CustomerSellerAsDescriptionsListShowingReferenceView__reference_editor_seller");
		HtmlElement openSellerListIcon = sellerEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		openSellerListIcon.click();
		/* This way (using click()) does not work with HtmlUnit 2.32  
		HtmlElement menuItem = (HtmlElement) getHtmlPage().getElementById("ui-id-3");
		assertEquals("ui-menu-item", menuItem.getAttribute("class"));
		assertEquals("JUANVI LLAVADOR", menuItem.asText());
		menuItem.click();
		*/
		
		getHtmlPage().executeJavaScript("$('input[name=ox_openxavatest_CustomerSellerAsDescriptionsListShowingReferenceView__seller___number__CONTROL__]').data('ui-autocomplete')._trigger('select', 'autocompleteselect', {item:{value:2, label:'JUANVI LLAVADOR'}});");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);

		// We could optimize to exclude reference_editor_seller below. However, if we would optimize the combo of descriptions list
		// to load on open it, the performance overload of leaving reference_editor_seller would be minimal
		// We plan to optimize combo data loading: https://openxava.org/XavaProjects/o/OpenXava/m/Issue?detail=ff808081702156050170274028940005 
		assertLoadedParts("editor_seller___number, editor_seller___name, reference_editor_seller, errors, messages"); 
		
		execute("Mode.list");
		execute("List.viewDetail", "row=3");
		assertValue("name", "Cuatrero");
		
		execute("Navigation.next");
		assertValue("name", "Gonzalo Gonzalez");
		assertLoadedParts(
			"editor_number, editor_name, editor_type, " +
			"editor_seller.number, editor_seller.name, reference_editor_seller.level, " +
			"reference_editor_seller," +
			"errors, messages");
	}
	
	public void testChangingSelectedElementsOfACollectionTabByCodeNoReloadCollection() throws Exception {  
		changeModule("CarrierWithCollectionsTogether");
		execute("List.viewDetail", "row=0");
		assertAllCollectionUnchecked("fellowCarriers"); 
		assertRowCollectionUnchecked("fellowCarriers", 0);
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionUnchecked("fellowCarriers", 2);
		
		assertAllCollectionUnchecked("fellowCarriersCalculated");
		assertRowCollectionUnchecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 2);
				
		checkRowCollection("fellowCarriersCalculated", 0);
		assertRowCollectionChecked("fellowCarriers", 0); 
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionUnchecked("fellowCarriers", 2);
		assertRowCollectionChecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 2);
		assertLoadedParts("errors, messages");
				
		checkRowCollection("fellowCarriersCalculated", 2);
		assertRowCollectionChecked("fellowCarriers", 0);
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionChecked("fellowCarriers", 2);
		assertRowCollectionChecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionChecked("fellowCarriersCalculated", 2);
		assertLoadedParts("errors, messages");
		
		uncheckRowCollection("fellowCarriersCalculated", 2);
		assertRowCollectionChecked("fellowCarriers", 0);
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionUnchecked("fellowCarriers", 2);
		assertRowCollectionChecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 2);
		assertLoadedParts("errors, messages");
	}
	
	public void testChangingSelectedElementsOfACollectionTabByCodeNoReloadCollectionWithOldImplementation() throws Exception { 
		changeModule("CarrierWithCollectionsTogether");
		execute("List.viewDetail", "row=0");
		assertAllCollectionUnchecked("fellowCarriers");
		assertRowCollectionUnchecked("fellowCarriers", 0);
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionUnchecked("fellowCarriers", 2);
		
		assertAllCollectionUnchecked("fellowCarriersCalculated");
		assertRowCollectionUnchecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 2);
				
		setValue("oldSync", "true");
		
		checkRowCollection("fellowCarriersCalculated", 0);
		assertRowCollectionChecked("fellowCarriers", 0); 
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionUnchecked("fellowCarriers", 2);
		assertRowCollectionChecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 2);
		assertLoadedParts("errors, messages");
				
		checkRowCollection("fellowCarriersCalculated", 2);
		assertRowCollectionChecked("fellowCarriers", 0);
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionChecked("fellowCarriers", 2);
		assertRowCollectionChecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionChecked("fellowCarriersCalculated", 2);
		assertLoadedParts("errors, messages");
		
		uncheckRowCollection("fellowCarriersCalculated", 2);
		assertRowCollectionChecked("fellowCarriers", 0);
		assertRowCollectionUnchecked("fellowCarriers", 1);
		assertRowCollectionUnchecked("fellowCarriers", 2);
		assertRowCollectionChecked("fellowCarriersCalculated", 0);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 1);
		assertRowCollectionUnchecked("fellowCarriersCalculated", 2);
		assertLoadedParts("errors, messages");
	}
	
	public void testAddRemoveActionsForProperty() throws Exception { 
		changeModule("Author");
		execute("List.viewDetail", "row=0");
		assertNoAction("Author.addSuffix");
		execute("Author.showAddSuffix");		
		assertAction("Author.addSuffix");
		assertLoadedParts(
				"property_actions_author," + // Really this is the only part that we want to load 
				"editor_author," +	// This is not really needed, it can be improved 
						// but complicating the code too much, and this is not a very common 
						// case, moreover the performance improvement is not perceptible
				"collection_humans.," +	// The collection is loaded because author is loaded,							
				"frame_humansheader," + // and author is the key. Although in this case the performance
				"messages," +           // degradation is more noticeable, this is a very rare case. 
				"errors,");
	}
	
	public void testAlwaysReloadEditor() throws Exception { 
		changeModule("Warehouse");
		execute("CRUD.new");
		HtmlElement el = getHtmlPage().getHtmlElementById("ox_openxavatest_Warehouse__editor_time"); 
		String time = el.asNormalizedText().trim();		
		execute("CRUD.new");
		String otherTime = el.asNormalizedText().trim();		
		assertNotEquals("Time must be changed", time, otherTime);
		assertLoadedParts("editor_time, errors, messages");
	}
	
	
	public void testSetMemberEditable() throws Exception {
		changeModule("ChangeProductsPrice");
		execute("List.viewDetail", "row=0");
		assertNoEditable("description");
		execute("ChangeProductsPrice.editDescription");
		assertEditable("description");
		assertLoadedParts("editor_description, " +
				"errors, messages,");
	}
	
	public void testCollectionsInsideReferences() throws Exception { 
		changeModule("Office");
		execute("CRUD.new"); 
		assertCollectionRowCount("defaultCarrier.fellowCarriers", 0);
		assertCollectionRowCount("defaultCarrier.fellowCarriersCalculated", 0);
		setValue("defaultCarrier.number", "1");
		assertCollectionRowCount("defaultCarrier.fellowCarriers", 3); 
		assertCollectionRowCount("defaultCarrier.fellowCarriersCalculated", 3);		
		assertLoadedParts("collection_defaultCarrier.fellowCarriers.," + 
				"collection_defaultCarrier.fellowCarriersCalculated.," +
				"frame_defaultCarrier.fellowCarriersCalculatedheader," + 
				"frame_defaultCarrier.fellowCarriersheader," + 				
				"editor_defaultCarrier.calculated," +	 
				"editor_defaultCarrier.name," +
				"editor_defaultCarrier.warehouse.name," +
				"editor_defaultCarrier.warehouse.number," +
				"editor_defaultCarrier.warehouse.zoneNumber," +
				"editor_mainWarehouse.time," + // it's always-reload
				"errors, messages,");		
	}
	
	public void testRefreshViewProperties() throws Exception { 
		changeModule("Customer");
		execute("CRUD.new");
		assertValue("address.viewProperty", "");
		execute("Address.fillViewProperty", "xava.keyProperty=address.viewProperty"); 
		assertValue("address.viewProperty", "THIS IS AN ADDRESS");
		assertLoadedParts("errors, " +
				"editor_address.viewProperty," +
				"messages");		
	}
	
	public void testOnlyLoadModifiedParts() throws Exception { 
		changeModule("Customer"); 
		// assertLoadedParts(""); // When coreViaAJAX=false, currently not used
		assertLoadedParts("core"); // When coreViaAJAX=true, the default behavior 
		execute("List.filter");
		assertLoadedParts("errors, view, messages"); 
		execute("List.viewDetail", "row=0");
		assertLoadedParts("core, ");
		execute("Navigation.next");
		assertLoadedParts("editor_type, errors, " + 
				"editor_number, " +
				"editor_alternateSeller.number, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"editor_address.zipCode, " +
				"editor_alternateSeller.name, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_city, " +
				"editor_website, " +
				"editor_relationWithSeller, " +
				"reference_editor_address.state, " +
				"editor_email, " +
				"editor_telephone, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");		
		setValue("seller.number", "2");
		assertLoadedParts("errors, editor_seller.name, " +
				"messages,");
		setValue("seller.number", "1");
		assertLoadedParts("errors, editor_seller.name, " + 
			"messages,");
		execute("Customer.changeNameLabel");		
		assertLoadedParts("label_name, errors, messages, ");
		execute("CRUD.new");
		assertLoadedParts("errors," + 
				"button_bar, bottom_buttons," + // Because Refresh is removed 
				"editor_seller.name, " +
				"editor_number, " +
				"editor_type," + 
				"editor_alternateSeller.number, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"editor_address.zipCode, " +
				"editor_alternateSeller.name, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_city, " +
				"editor_relationWithSeller, " +
				"reference_editor_address.state, " +
				"editor_seller.number, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " +
				
				"messages,");
		setValue("number", "4"); 
		execute("CRUD.refresh");
		assertLoadedParts("errors," + 
				"button_bar, bottom_buttons," + // Because Refresh comes back 
				"editor_number, " +
				"editor_type, " + 
				"editor_address.asString, " + 
				"editor_address.city, " +
				"editor_address.zipCode, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_city, " +
				"editor_relationWithSeller, " +				
				"reference_editor_address.state, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");		
		// Collections 
		execute("List.orderBy", "property=name,collection=deliveryPlaces");
		assertLoadedParts("errors, collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"messages");		
		execute("List.orderBy", "property=name,collection=deliveryPlaces");
		assertLoadedParts("errors, collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"messages");
		execute("List.filter", "collection=deliveryPlaces");
		assertLoadedParts("errors, collection_deliveryPlaces., " + 
				"frame_deliveryPlacesheader, " + 
				"messages");
		execute("Collection.new", "viewObject=xava_view_deliveryPlaces");
		assertLoadedParts("dialog1");
		closeDialog();
		assertLoadedParts("core");
		execute("Collection.edit", "row=0,viewObject=xava_view_deliveryPlaces");		
		assertLoadedParts("dialog1");
		execute("List.orderBy", "property=name,collection=receptionists");
		assertLoadedParts("errors, collection_receptionists., " +
				"frame_receptionistsheader, " + 
				"messages");
		execute("List.filter", "collection=receptionists");
		assertLoadedParts("errors, collection_receptionists., " +
				"frame_receptionistsheader, " + 
				"messages");
		execute("Collection.new", "viewObject=xava_view_receptionists");
		assertLoadedParts("dialog2");
		closeDialog();
		assertLoadedParts("dialog1");
		execute("Collection.edit", "row=0,viewObject=xava_view_receptionists");
		assertLoadedParts("dialog2");
		closeDialog();
		assertLoadedParts("dialog1");
		closeDialog();
		assertLoadedParts("core");
		
		// Hide/show members
		execute("Customer.hideSeller");
		assertLoadedParts("errors, view, messages");
		execute("Customer.showSeller");
		assertLoadedParts("errors, view, messages");
		
		// Actions of properties are hidden when editable state changes
		assertAction("Customer.changeNameLabel"); 		
		execute("EditableOnOff.setOff");
		assertNoAction("Customer.changeNameLabel"); 
		assertLoadedParts("editor_type, " +
				"editor___ACTION__Customer_changeNameLabel, " +
				"editor_address.__ACTION__Address_addFullAddress, " +
				"editor_number, errors, " +
				"editor_alternateSeller.number, " +
				"property_actions_address.street, " +
				"editor_address.asString, " +
				"editor_address.city, " +
				"property_actions_seller.number, " +
				"editor_address.zipCode, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_address.viewProperty, " +
				"editor_city, " +
				"editor_website, " +
				"property_actions_address.viewProperty, " +
				"property_actions_alternateSeller.number, " +
				"editor_remarks, " +
				"editor_relationWithSeller, " +
				"editor_email, " +
				"reference_editor_address.state, " +
				"editor_seller.number, " +
				"editor_telephone, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");		
		
		execute("EditableOnOff.setOn");
		assertAction("Customer.changeNameLabel"); 
		assertLoadedParts("editor_type, " +
				"editor___ACTION__Customer_changeNameLabel, " +
				"editor_address.__ACTION__Address_addFullAddress, " +
				"editor_number, errors, " +
				"editor_alternateSeller.number, " +
				"property_actions_address.street, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"property_actions_seller.number, " +
				"editor_address.zipCode, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_address.viewProperty, " +
				"editor_city, " +
				"editor_website, " +
				"property_actions_address.viewProperty, " +
				"property_actions_alternateSeller.number, " +
				"editor_remarks, " +
				"editor_relationWithSeller, " +
				"editor_email, " +
				"reference_editor_address.state, " +
				"editor_seller.number, " +
				"editor_telephone, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");
		
		// Change view programatically
		execute("Customer.changeToSimpleView");
		assertLoadedParts("errors, view, messages");
	}
	
	public void testEditorForReference() throws Exception { 		
		changeModule("Product2");
		assertEditorForReference();		
		changeModule("Product2ColorWithFrame");
		assertEditorForReference();				
	}
	
	private void assertEditorForReference() throws Exception {
		execute("List.viewDetail", "row=4");
		assertValue("number", "5");
		assertValue("color.number", "1");
		assertLoadedPart("core");
		assertNotLoadedPart("reference_editor_color");
		
		execute("Navigation.next");
		assertValue("number", "6");
		assertValue("color.number", "1");
		assertNotLoadedPart("view");
		assertNotLoadedPart("core");
		assertLoadedPart("editor_number");
		assertNotLoadedPart("reference_editor_color");

		execute("Navigation.next");
		assertValue("number", "7");
		assertValue("color.number", "28");
		assertNotLoadedPart("view");
		assertNotLoadedPart("core");
		assertLoadedPart("editor_number");
		assertLoadedPart("reference_editor_color");		
	}
	
	public void testShowHideButtons() throws Exception {  
		changeModule("Applicant");
		execute("CRUD.new");
		execute("Applicant.showButtons", "xava.keyProperty=name");
		assertLoadedParts("errors, messages");
	}
	
	public void testEditorForCollection() throws Exception {
		changeModule("Blog");
		execute("List.viewDetail", "row=0");
		/* Before 4m2 when dialog was not used for editing collection details
		execute("Collection.new", "viewObject=xava_view_comments");
		assertLoadedParts("collection_comments., errors, messages,");
		setValue("comments.body", "Me too");
		execute("Collection.save", "viewObject=xava_view_comments");
		assertLoadedParts("collection_comments., errors, messages,");		
		*/
		// Since 4m2, with dialogs
		execute("Collection.new", "viewObject=xava_view_comments");
		assertLoadedParts("dialog1"); 
		setValue("body", "Me too");
		execute("Collection.save");
		assertLoadedParts("core");				
	}
 
	
	public void testDescriptionsList() throws Exception {
		changeModule("Customer");
		execute("List.viewDetail", "row=0");
		assertValue("number", "1"); 
		assertDescriptionValue("address.state.id", "NEW YORK");
		
		execute("Navigation.next");
		assertValue("number", "2");
		assertDescriptionValue("address.state.id", "COLORADO");		
		assertLoadedPart("reference_editor_address___state"); 
		
		execute("Navigation.next");
		assertValue("number", "3"); 	
		assertDescriptionValue("address.state.id", "NEW YORK");
		assertLoadedPart("reference_editor_address___state"); 
		
		execute("Navigation.next");
		assertValue("number", "4"); 
		assertDescriptionValue("address.state.id", "NEW YORK");
		assertNotLoadedPart("reference_editor_address___state");
		
		execute("Navigation.next");
		assertValue("number", "43");
		assertDescriptionValue("address.state.id", "KANSAS");				
		assertLoadedPart("reference_editor_address___state"); 
	}
			
	public void testDependentDescriptionsList_resetDescriptionsCache_setEditable() throws Exception {  
		changeModule("Product2"); 
		// Dependent descriptions list 
		// assertLoadedParts(""); // When coreViaAJAX=false, currently not used
		assertLoadedParts("core"); // When coreViaAJAX=false, the default behavior  
		execute("CRUD.new");
		assertLoadedParts("core, ");
		setValue("family.number", "1");
		setValue("family.number", "2");
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"messages");		
		setValue("family.number", "1");
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"messages");
		
		// Reset descriptions cache
		execute("Product2.changeLimitZone");
		assertLoadedParts("reference_editor_family, errors, " +
				"reference_editor_subfamily, " +
				"reference_editor_warehouse, " +
				"messages,");
		
		// setEditable
		execute("Product2.deactivateType");
		assertLoadedParts("reference_editor_family, errors, " +
				"reference_editor_subfamily, " +
				"messages,");
	}
		
	public void testShowingHiddingPartsReloadsFullView() throws Exception { 
		changeModule("Product2"); 
		execute("List.viewDetail", "row=0");
		assertNotExists("zoneOne"); 
		assertLoadedParts("core, ");
		execute("Navigation.next");
		assertExists("zoneOne");
		assertLoadedParts("errors, view, messages");
		execute("Navigation.next");
		assertExists("zoneOne");
		assertLoadedParts("reference_editor_family, errors, " +
				"reference_editor_color, " +
				"reference_editor_subfamily, " +
				"editor_photos, " +
				"editor_unitPriceInPesetas, " +
				"editor_unitPriceWithTax, " +
				"reference_editor_warehouse, " +
				"editor_description, " +
				"editor_number, " +
				"messages, editor_unitPrice, ");
		execute("Navigation.next");
		assertNotExists("zoneOne");
		assertLoadedParts("errors, view, messages");
	}
	
	public void testFirstDetailActionExecutingLoadAllButOnlyFirstTime() throws Exception {
		changeModule("Carrier"); 		
		execute("CRUD.new");
		assertLoadedParts("core, "); 
		execute("CRUD.new"); 
		assertLoadedParts("errors, messages"); 
		execute("Mode.list");
		execute("CRUD.new");
		assertLoadedParts("core, ");
		execute("CRUD.new");
		assertLoadedParts("errors, messages"); // Perfect: From now on only the needed parts are reloaded
	}
		
	public void testCollections() throws Exception { 
		changeModule("Carrier"); 
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("fellowCarriers", 3);  
		setValue("warehouse.number", "2");
		assertCollectionRowCount("fellowCarriers", 0); 
		setValue("warehouse.number", "1");
		assertCollectionRowCount("fellowCarriers", 3);
		assertLoadedParts("errors, collection_fellowCarriers., " + 
				"frame_fellowCarriersheader, " + 
				"messages, editor_warehouse.name, ");
		execute("Collection.edit", "row=0,viewObject=xava_view_fellowCarriersCalculated");  
		assertLoadedParts("dialog1");
		closeDialog();
		assertLoadedParts("core"); 
		execute("Navigation.next");
		assertLoadedParts("errors, editor_number, " +
				"editor_remarks, collection_fellowCarriers., " +
				"collection_fellowCarriersCalculated., " +
				"frame_fellowCarriersheader, " + 
				"frame_fellowCarriersCalculatedheader, " + 
				"editor_name, messages, ");		
		
		execute("Carrier.translateAll"); // The first time more changes because null in some db columns
		execute("Carrier.translateAll");
		assertLoadedParts("errors, collection_fellowCarriers., " +
				"collection_fellowCarriersCalculated., " +
				"frame_fellowCarriersheader, " + 
				"frame_fellowCarriersCalculatedheader, " + 				
				"messages");
	}
	
	public void testCollectionsTotals() throws Exception { 
		changeModule("InvoiceDetailsWithTotals");
		execute("List.viewDetail", "row=0");
		setValue("vatPercentage", "17");
		assertLoadedParts("ox_openxavatest_InvoiceDetailsWithTotals__messages, "
			+ "ox_openxavatest_InvoiceDetailsWithTotals__errors, "
			+ "ox_openxavatest_InvoiceDetailsWithTotals__editor_vatPercentage, "
			+ "ox_openxavatest_InvoiceDetailsWithTotals__collection_details___, " // Regular collection are loaded completely, a thing to improve 
			+ "ox_openxavatest_InvoiceDetailsWithTotals__collection_total_2_4_calculatedDetails___, "
			+ "ox_openxavatest_InvoiceDetailsWithTotals__frame_detailsheader, "
			+ "ox_openxavatest_InvoiceDetailsWithTotals__collection_total_1_4_calculatedDetails___,");
	}
	
	public void testCollectionCountInSectionLabel() throws Exception { 
		changeModule("InvoiceCalculatedDetailsInSection");
		execute("List.viewDetail", "row=0");
		assertLoadedParts("ox_openxavatest_InvoiceCalculatedDetailsInSection__core"); 
		execute("Customer.changeNameLabel");
		assertLoadedParts("messages, errors");
		execute("Sections.change", "activeSection=1");
		assertLoadedParts("messages, errors, sections_xava_view, xava_view_section1_section0_collectionSize");
		execute("Navigation.next");
		assertLoadedParts("messages, errors, frame_detailsheader, editor_year, collection_details___, editor_paid, editor_number, editor_date," 
			+ "xava_view_section1_section0_collectionSize, xava_view_section2_collectionSize, xava_view_section3_collectionSize"); 
	}
	
	public void testSections() throws Exception {
		changeModule("InvoiceNestedSections");
		execute("CRUD.new");
		execute("Sections.change", "activeSection=2");
		execute("Sections.change", "activeSection=1");
		assertLoadedParts("errors, messages, sections_xava_view, xava_view_section1_section0_collectionSize"); 
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1");
		assertLoadedParts("errors, sections_xava_view_section1, messages");
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1_section1");
		assertLoadedParts("errors, sections_xava_view_section1_section1, messages");		
	}
	
	public void testSectionsInsideSubview() throws Exception {
		changeModule("TransportCharge");
		execute("CRUD.new"); 
		execute("Sections.change", "activeSection=4,viewObject=xava_view_delivery"); 
		assertLoadedParts("errors, sections_xava_view_delivery, " +
				"messages, ");
	}
	
	public void testDetailCollection() throws Exception { 
		changeModule("Invoice");
		execute("List.viewDetail", "row=0"); 
		execute("Sections.change", "activeSection=1");
		execute("Collection.new", "viewObject=xava_view_section1_details"); 
		
		/* Until 4m1
		// First time that the detail is used all collection is reloaded
		assertLoadedParts("errors, collection_details., " +
				"messages");
		*/
		assertLoadedParts("dialog1"); // Since 4m2 

		setValue("product.number", "1");
		assertLoadedParts("errors, " + 
				"editor_product.warehouseKey, " +
				"editor_product.familyNumber, " +
				"editor_product.photos, " +
				"editor_product.unitPrice, " +
				"editor_product.subfamilyNumber, " +
				"editor_product.unitPriceInPesetas, " +
				"editor_product.description, " +
				"messages");		
		setValue("product.number", "2");
		assertLoadedParts("errors, " +
				"editor_product.warehouseKey, " +
				"editor_product.familyNumber, " +
				"editor_product.photos, " +
				"editor_product.subfamilyNumber, " +
				"editor_product.unitPrice, " +
				"editor_product.unitPriceInPesetas, " +
				"editor_product.description, " +
				"messages");		
	}	
	
	public void testEditorsWithErrorForPropertiesAndDescriptionsLists() throws Exception {  
		changeModule("Product2");
		execute("CRUD.new");
		execute("CRUD.save");
		assertErrorsCount(4);
		assertLoadedParts("errors, messages, reference_editor_subfamily");
		HtmlPage page = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();
		HtmlSpan description = (HtmlSpan) page.getElementById("ox_openxavatest_Product2__editor_description");
		assertTrue("description has no error style", description.getAttribute("class").contains("ox-error-editor")); 
		
		setValue("description", "z");
		execute("CRUD.save");
		assertErrorsCount(3);		
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"editor_description, " + // Needed to refresh it because a converter transforms it to uppercase
				"messages");
		assertFalse("description has error style", description.getAttribute("class").contains("ox-error-editor")); 
		
		HtmlSpan number = (HtmlSpan) page.getElementById("ox_openxavatest_Product2__editor_number");
		assertTrue("number has no error style", number.getAttribute("class").contains("ox-error-editor"));
		
		HtmlSpan subfamily = (HtmlSpan) page.getElementById("ox_openxavatest_Product2__reference_editor_subfamily");
		assertTrue("subfamily has no error style", subfamily.getAttribute("class").contains("ox-error-editor"));
		
		execute("CRUD.new");
		assertFalse("number has error style", number.getAttribute("class").contains("ox-error-editor")); 
		
		// To test that Number:999 is not used as member name
		setValue("number", "999");
		execute("CRUD.refresh");
		assertError("Object of type Product does not exists with key Number:999"); 
	}
	
	public void testChangingViewAndController() throws Exception {
		changeModule("Carrier");
		execute("CRUD.new");
		execute("WarehouseReference.createNewNoDialog"); 
		assertLoadedParts("errors, view, bottom_buttons, " +
				"button_bar, messages");
		execute("WarehouseCreation.saveNew");
		assertLoadedParts("errors, messages, editor_time,"); 
	}
	
	public void testShowDialog() throws Exception { 
		changeModule("Carrier");
		execute("CRUD.new");
		execute("Reference.createNew", "model=Warehouse,keyProperty=xava.Carrier.warehouse.number"); 
		assertLoadedParts("dialog1");
		execute("WarehouseCreation.saveNew");
		assertLoadedParts("errors, messages, editor_time,"); 
	}

	public void testHandmadeWebView() throws Exception {
		changeModule("SellerJSP");
		execute("List.viewDetail", "row=0");
		assertLoadedParts("core, "); 
		setValue("level.id", "B");
		assertLoadedParts("errors, view, messages");
		execute("Navigation.next");
		assertLoadedParts("errors, view, messages");
	}
	
	public void testChangingModelOfView() throws Exception {
		changeModule("Human");
		execute("List.viewDetail", "row=0");		
		assertLoadedParts("core,");
		
		execute("Navigation.next");		
		assertLoadedParts("editor_sex, errors, " + 
				"editor_name, " +
				"messages");				
		
		// Now the model changes...
		execute("Navigation.next"); 
		assertLoadedParts("errors, view, " + // ...so we reload the full view 
				"messages,");		
	}
	
	public void testCustomView_uploadFile() throws Exception {  
		changeModule("Product5"); 
		execute("List.viewDetail", "row=0");
		assertFilesCount("photos", 0);
		execute("Product5.addPhoto");
		assertLoadedParts("core, "); 
		String imageUrl = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newFile", imageUrl);
		execute("LoadPhotoIntoGallery.loadPhoto");
		assertNoErrors();
		assertMessage("Image added to the gallery"); 
		assertFilesCount("photos", 1);
		assertLoadedParts("core"); 
		
		assertAction("Product5.addPhoto");
		removeFile("photos", 0);
	}
	

	private void assertLoadedParts(String expected) throws Exception {
		assertEquals("Loaded parts are not the expected ones", order(expected), order(getLoadedParts()));
	}
	
	private String order(String parts) {		
		StringTokenizer st = new StringTokenizer(parts, ",");
		SortedSet ordered = new TreeSet();
		while (st.hasMoreTokens()) {
			String part = st.nextToken().trim();
			if (!"".equals(part)) ordered.add(Ids.undecorate(part));
		}
		StringBuffer result = new StringBuffer();
		for (Iterator it=ordered.iterator(); it.hasNext(); ) {
			result.append(it.next());
			result.append(",\n");
		}
		
		return result.toString();
	}

	private void assertLoadedPart(String expected) throws Exception {
		assertTrue("Loaded part not found", getLoadedParts().indexOf(expected + ",") >= 0);
	}
	
	private void assertNotLoadedPart(String expected) throws Exception {
		assertTrue("Loaded part found", getLoadedParts().indexOf(expected + ",") < 0);
	}
		
	private String getLoadedParts() { 
		HtmlPage page = getHtmlPage(); 
		HtmlInput input = (HtmlInput) page.getHtmlElementById(decorateId("loaded_parts"));
		return input.getValue();
	}
			
}
