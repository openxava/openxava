package org.openxava.test.tests;

import static org.openxava.test.tests.EmailNotificationsTestUtil.*; 

import java.rmi.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class SellerTest extends CustomizeListTestBase { 
		
	private Customer customer2;
	private Customer customer1;

	public SellerTest(String testName) {
		super(testName, "Seller");		
	}
	
	public void testEmailNotificationsByModule_accessLog() throws Exception {
		Thread.sleep(1000); // To wait until preferences directory would be removed completely. This was an intermittent problem with Windows 7
		removeAllEmailSubscriptions(); 
		
		assertNoAction("EmailNotifications.unsubscribe");
		execute("EmailNotifications.subscribe");
		assertError("We have no your email address, so we cannot notify you");
		assertNoAction("EmailNotifications.unsubscribe");		
		
		changeModule("SignIn");
		login("openxavatest1@getnada.com", "test1");
		changeModule("Seller");
		assertNoAction("EmailNotifications.unsubscribe");
		assertEmailSubscriptions();
		execute("EmailNotifications.subscribe");
		assertEmailSubscriptions("openxavatest1@getnada.com=Seller");
		assertMessage("Now you are following Sellers. You will be notified via email");
		assertNoAction("EmailNotifications.subscribe");
		assertAction("EmailNotifications.unsubscribe");
		
		changeModule("SignIn");
		login("openxavatest2@getnada.com", "test2");
		changeModule("Seller");
		assertNoAction("EmailNotifications.unsubscribe");
		assertEmailSubscriptions("openxavatest1@getnada.com=Seller");
		execute("EmailNotifications.subscribe");
		assertEmailSubscriptions("openxavatest1@getnada.com=Seller", "openxavatest2@getnada.com=Seller");
		assertMessage("Now you are following Sellers. You will be notified via email");
		assertNoAction("EmailNotifications.subscribe");
		assertAction("EmailNotifications.unsubscribe");
		
		changeModule("Customer");
		assertNoAction("EmailNotifications.unsubscribe");
		execute("EmailNotifications.subscribe");
		assertEmailSubscriptions("openxavatest1@getnada.com=Seller", "openxavatest2@getnada.com=Customer", "openxavatest2@getnada.com=Seller");
		assertMessage("Now you are following Customers. You will be notified via email");
		assertNoAction("EmailNotifications.subscribe");
		assertAction("EmailNotifications.unsubscribe");
		
		assertEmailNotifications();
		execute("List.viewDetail", "row=0");
		execute("Customer.save");
		assertNoErrors();
		assertEmailNotifications(); // To verify that address is not notified
		
		changeModule("Seller");
		execute("CRUD.new");
		setValue("number", "66");
		setValue("name", "JUNIT SELLER");
		execute("CRUD.save");
		assertNoErrors();
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER");
		setValue("name", "JUNIT SELLER MODIFIED"); // Only one value, to test a case
		execute("CRUD.save");
		
		changeModule("SignIn");
		login("admin", "admin");
		changeModule("Seller");

		execute("CRUD.new");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("level.id", "");
		setValue("level.id", "A");
		String [] emptyRegions = {};
		assertValues("regions", emptyRegions);
		String [] regions = { "1", "3" };
		setValues("regions", regions);
		execute("CRUD.save");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("level.id", "A");
		setValue("level.id", "C");
		assertValues("regions", regions);
		String [] region = { "2" };
		setValues("regions", region);
		execute("CRUD.save");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		execute("CRUD.delete");
		
		execute("CRUD.new");
		setValue("number", "3");
		execute("CRUD.refresh");
		assertValue("number", "3");
		assertCollectionRowCount("customers", 0);
		execute("Collection.add", "viewObject=xava_view_customers");
		assertValueInList(3, 0, "Cuatrero");
		execute("AddToCollection.add", "row=3");
		assertCollectionRowCount("customers", 1); 
		assertValueInCollection("customers", 0, 0, "4");
		assertValueInCollection("customers", 0, 1, "Cuatrero");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_customers");
		assertCollectionRowCount("customers", 0);
		
		assertCreateElementInEntityCollection();
		
		assertEmailNotifications(  
			"CREATED: email=openxavatest1@getnada.com, user=openxavatest2@getnada.com, application=OpenXavaTest, module=Sellers, permalink=http://localhost:8080/OpenXavaTest/modules/Seller?detail=66",
			"MODIFIED: email=openxavatest1@getnada.com, user=openxavatest2@getnada.com, application=OpenXavaTest, module=Sellers, permalink=http://localhost:8080/OpenXavaTest/modules/Seller?detail=66, changes=<ul><li><b>Name</b>: JUNIT SELLER --> JUNIT SELLER MODIFIED</li></ul>",
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Sellers, permalink=http://localhost:8080/OpenXavaTest/modules/Seller?detail=66, changes=<ul><li><b>Regions</b>: {} --> {1,3}</li><li><b>Id of Level</b>:  --> A</li></ul>",
			"MODIFIED: email=openxavatest2@getnada.com, user=admin, application=OpenXavaTest, module=Sellers, permalink=http://localhost:8080/OpenXavaTest/modules/Seller?detail=66, changes=<ul><li><b>Regions</b>: {} --> {1,3}</li><li><b>Id of Level</b>:  --> A</li></ul>",
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Sellers, permalink=http://localhost:8080/OpenXavaTest/modules/Seller?detail=66, changes=<ul><li><b>Regions</b>: {1,3} --> {2}</li><li><b>Id of Level</b>: A --> C</li></ul>",
			"MODIFIED: email=openxavatest2@getnada.com, user=admin, application=OpenXavaTest, module=Sellers, permalink=http://localhost:8080/OpenXavaTest/modules/Seller?detail=66, changes=<ul><li><b>Regions</b>: {1,3} --> {2}</li><li><b>Id of Level</b>: A --> C</li></ul>",
			"REMOVED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Sellers, url=http://localhost:8080/OpenXavaTest/modules/Seller, key={number=66}",
			"REMOVED: email=openxavatest2@getnada.com, user=admin, application=OpenXavaTest, module=Sellers, url=http://localhost:8080/OpenXavaTest/modules/Seller, key={number=66}",
			"MODIFIED: email=openxavatest2@getnada.com, user=admin, application=OpenXavaTest, module=Customers, permalink=http://localhost:8080/OpenXavaTest/modules/Customer?detail=4, changes=<ul><li><b>Number of Seller</b>:  --> 3</li></ul>",
			"MODIFIED: email=openxavatest2@getnada.com, user=admin, application=OpenXavaTest, module=Customers, permalink=http://localhost:8080/OpenXavaTest/modules/Customer?detail=4, changes=<ul><li><b>Number of Seller</b>: 3 --> </li></ul>",
			"CREATED: email=openxavatest2@getnada.com, user=admin, application=OpenXavaTest, module=Customers, permalink=http://localhost:8080/OpenXavaTest/modules/Customer?detail=66"
		);		

		LogTrackerTestUtil.assertAccessLog( 
			"CONSULTED: user=openxavatest2@getnada.com, model=Customer, key={number=1}",
			"CONSULTED: user=openxavatest2@getnada.com, model=Customer, key={number=1}",
			"CREATED: user=openxavatest2@getnada.com, model=Seller, key={number=66}",
			"CONSULTED: user=openxavatest2@getnada.com, model=Seller, key={number=66}",
			"MODIFIED: user=openxavatest2@getnada.com, model=Seller, key={number=66}, changes=Name: JUNIT SELLER --> JUNIT SELLER MODIFIED",
			"CONSULTED: user=admin, model=Seller, key={number=66}",
			"MODIFIED: user=admin, model=Seller, key={number=66}, changes=Regions: {} --> {1,3}, Id of Level:  --> A",
			"CONSULTED: user=admin, model=Seller, key={number=66}",
			"MODIFIED: user=admin, model=Seller, key={number=66}, changes=Regions: {1,3} --> {2}, Id of Level: A --> C",
			"CONSULTED: user=admin, model=Seller, key={number=66}",
			"REMOVED: user=admin, model=Seller, key={number=66}",
			"CONSULTED: user=admin, model=Seller, key={number=1}",
			"CONSULTED: user=admin, model=Seller, key={number=3}",
			"MODIFIED: user=admin, model=Customer, key={number=4}, changes=Number of Seller:  --> 3",
			"MODIFIED: user=admin, model=Customer, key={number=4}, changes=Number of Seller: 3 --> ",
			"CREATED: user=admin, model=Customer, key={number=66}"
		);		

		changeModule("SignIn");
		login("openxavatest2@getnada.com", "test2");
		changeModule("Seller");

		execute("EmailNotifications.unsubscribe");
		assertEmailSubscriptions("openxavatest1@getnada.com=Seller", "openxavatest2@getnada.com=Customer", "openxavatest2@getnada.com=Seller::66");
		assertMessage("You no longer follow Sellers");
		assertNoAction("EmailNotifications.unsubscribe");
		
		deleteJUnitCustomer();
	}

	public void testEmailNotificationsByEntity() throws Exception { 
		removeAllEmailSubscriptions(); 
		
		execute("CRUD.new");
		setValue("number", "66");
		setValue("name", "JUNIT SELLER 66");
		execute("CRUD.save");
		assertNoErrors();
		
		changeModule("SignIn");
		login("openxavatest1@getnada.com", "test1");
		changeModule("Seller");

		execute("CRUD.new");
		setValue("number", "67");
		setValue("name", "JUNIT SELLER 67");
		execute("CRUD.save");
		assertNoErrors();		
		
		execute("CRUD.new");
		setValue("number", "68");
		setValue("name", "JUNIT SELLE 68");
		execute("CRUD.save");
		
		setValue("number", "68");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLE 68");
		setValue("name", "JUNIT SELLER 68"); 
		execute("CRUD.save");
		
		assertEmailNotifications();
		
		changeModule("SignIn");
		login("openxavatest2@getnada.com", "test2");
		changeModule("Seller");		
		
		execute("CRUD.new");		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 66");
		setValue("name", "JUNIT SELLER 66 MODIFIED"); 
		execute("CRUD.save");
		assertEmailNotifications();
		
		setValue("number", "67");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 67");
		setValue("name", "JUNIT SELLER 67 MODIFIED"); 
		execute("CRUD.save");
		
		setValue("number", "68");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 68");
		setValue("name", "JUNIT SELLER 68 MODIFIED"); 
		execute("CRUD.save");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 66 MODIFIED");
		execute("CRUD.delete");
		
		execute("CRUD.new");
		setValue("number", "67");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 67 MODIFIED");
		execute("CRUD.delete");
		
		execute("CRUD.new");
		setValue("number", "69");
		setValue("name", "JUNIT SELLER 69");
		execute("CRUD.save");
		
		execute("CRUD.new");
		setValue("number", "70");
		setValue("name", "JUNIT SELLER 70");
		execute("CRUD.save");
		
		execute("CRUD.new");
		setValue("number", "71");
		setValue("name", "JUNIT SELLER 71");
		execute("CRUD.save");

		String unsubscribeBaseURL="http://localhost:8080/OpenXavaTest/xava/unsubscribe.jsp?";
		String unsubscribeAllURL1=unsubscribeBaseURL + "email=openxavatest1@getnada.com&module=Seller";
		String unsubscribeAllURL2=unsubscribeBaseURL + "email=openxavatest2@getnada.com&module=Seller";
		String unsubscribeOneURL67=unsubscribeAllURL1 + "&key=::67";
		String unsubscribeOneURL68=unsubscribeAllURL1 + "&key=::68";
		String unsubscribeOneURL69=unsubscribeAllURL2 + "&key=::69";
		String unsubscribeOneURL70=unsubscribeAllURL2 + "&key=::70";
		String unsubscribeOneURL71=unsubscribeAllURL2 + "&key=::71";

		unsubscribe(unsubscribeOneURL70);
		
		changeModule("SignIn");
		login("openxavatest1@getnada.com", "test1");
		changeModule("Seller");
		
		execute("CRUD.new");
		setValue("number", "68");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 68 MODIFIED");
		setValue("name", "JUNIT SELLER 68 MODIFIED AGAIN"); 
		execute("CRUD.save");
		
		setValue("number", "69");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 69");
		setValue("name", "JUNIT SELLER 69 MODIFIED"); 
		execute("CRUD.save");
		
		setValue("number", "70");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 70");
		setValue("name", "JUNIT SELLER 70 MODIFIED"); 
		execute("CRUD.save");
		
		setValue("number", "71");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 71");
		setValue("name", "JUNIT SELLER 71 MODIFIED"); 
		execute("CRUD.save");
		
		unsubscribe(unsubscribeAllURL2);
		
		setValue("number", "68");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 68 MODIFIED AGAIN");
		execute("CRUD.delete");
		
		execute("CRUD.new");
		setValue("number", "69");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 69 MODIFIED");
		execute("CRUD.delete");
		
		execute("CRUD.new");
		setValue("number", "70");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 70 MODIFIED");
		execute("CRUD.delete");
		
		execute("CRUD.new");
		setValue("number", "71");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT SELLER 71 MODIFIED");
		execute("CRUD.delete");
		
		assertEmailNotifications(
			"MODIFIED: email=openxavatest1@getnada.com, user=openxavatest2@getnada.com, application=OpenXavaTest, module=Sellers, unsubscribeAllURL=" + unsubscribeAllURL1 + ", unsubscribeOneURL=" + unsubscribeOneURL67,
			"MODIFIED: email=openxavatest1@getnada.com, user=openxavatest2@getnada.com, application=OpenXavaTest, module=Sellers, unsubscribeAllURL=" + unsubscribeAllURL1 + ", unsubscribeOneURL=" + unsubscribeOneURL68,
			"REMOVED: email=openxavatest1@getnada.com, user=openxavatest2@getnada.com, application=OpenXavaTest, module=Sellers, unsubscribeAllURL=" + unsubscribeAllURL1 + ", unsubscribeOneURL=" + unsubscribeOneURL67,
			"MODIFIED: email=openxavatest2@getnada.com, user=openxavatest1@getnada.com, application=OpenXavaTest, module=Sellers, unsubscribeAllURL=" + unsubscribeAllURL2 + ", unsubscribeOneURL=" + unsubscribeOneURL69,
			"MODIFIED: email=openxavatest2@getnada.com, user=openxavatest1@getnada.com, application=OpenXavaTest, module=Sellers, unsubscribeAllURL=" + unsubscribeAllURL2 + ", unsubscribeOneURL=" + unsubscribeOneURL71
		);
			
	}
	
	private void unsubscribe(String unsubscribeURL) throws Exception { 
		WebClient client = new WebClient(getBrowserVersion());
		client.getPage(unsubscribeURL);
	}
	
	public void testCutPasteElementsInEntityCollection() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("name", "MANUEL CHAVARRI");
		assertCollectionRowCount("customers", 2);
		assertValueInCollection("customers", 0, 0, "1");
		assertValueInCollection("customers", 1, 0, "2");
		checkAllCollection("customers");
		assertNoCutRowStyle(0);
		assertNoCutRowStyle(1);
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_customers");
		assertMessage("2 rows cut from Customers");
		assertCollectionRowCount("customers", 2);
		assertCutRowStyle(0);
		assertCutRowStyle(1);
		
		changeModule("SellerCannotCreateCustomer");
		execute("List.viewDetail", "row=1");
		assertValue("name", "JUANVI LLAVADOR");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 0, "43");
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_section0_customers");
		assertMessage("2 rows pasted into Customers");
		assertCollectionRowCount("customers", 3);
		assertValueInCollection("customers", 0, 0, "1");
		assertValueInCollection("customers", 1, 0, "2");
		assertValueInCollection("customers", 2, 0, "43");
		
		assertNoCutRowStyle(0);
		assertNoCutRowStyle(1);
		assertNoCutRowStyle(2);
		execute("CollectionCopyPaste.cut", "row=0,viewObject=xava_view_section0_customers");
		assertMessage("1 row cut from Customers");
		assertCollectionRowCount("customers", 3);
		assertCutRowStyle(0);
		assertNoCutRowStyle(1);
		assertNoCutRowStyle(2);
		
		changeModule("Seller");
		assertValue("name", "MANUEL CHAVARRI");
		assertCollectionRowCount("customers", 0);
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_customers");
		assertMessage("1 row pasted into Customers");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 0, "1");
		
		changeModule("SellerCannotCreateCustomer");
		assertValue("name", "JUANVI LLAVADOR");
		assertCollectionRowCount("customers", 2);
		assertValueInCollection("customers", 0, 0, "2");
		assertValueInCollection("customers", 1, 0, "43");
		checkRowCollection("customers", 0);
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_section0_customers");
		assertMessage("1 row cut from Customers");
		assertCollectionRowCount("customers", 2);
		
		changeModule("Seller");
		assertValue("name", "MANUEL CHAVARRI");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 0, "1");
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_customers");
		assertMessage("1 row pasted into Customers");
		assertCollectionRowCount("customers", 2);
		assertValueInCollection("customers", 0, 0, "1");
		assertValueInCollection("customers", 1, 0, "2");
	}
	
	private void assertCutRowStyle(int row) { 
		assertTrue(hasCutRowStyle(row));
	}
	
	private void assertNoCutRowStyle(int row) { 
		assertFalse(hasCutRowStyle(row));
	}
	
	private boolean hasCutRowStyle(int row) { 
		String prefix = getModuleURL().contains("/SellerCannotCreateCustomer")?
			"ox_OpenXavaTest_SellerCannotCreateCustomer__xava_collectionTab_customers_":
			"ox_OpenXavaTest_Seller__xava_collectionTab_customers_";
		HtmlElement tr = getHtmlPage().getHtmlElementById(prefix + row);
		return tr.getAttribute("class").contains("ox-cut-row");
	}

	public void testEditCreateAndRemoveElementInEntityCollection() throws Exception { 
		execute("List.viewDetail", "row=2");
		assertValue("name", "ELISEO FERNANDEZ");
		assertCollectionRowCount("customers", 0); 
		assertCreateElementInEntityCollection();		
		assertEditElementInEntityCollection();
		assertRemoveElementInEntityCollection();
		deleteJUnitCustomer();
	}

	private void deleteJUnitCustomer() {
		Customer junitCustomer = XPersistence.getManager().find(Customer.class, 66);
		XPersistence.getManager().remove(junitCustomer);
	}

	private void assertRemoveElementInEntityCollection() throws Exception {
		execute("Collection.edit", "row=0,viewObject=xava_view_customers");
		assertValue("name", "Cliente Junit");
		execute("Collection.remove");
		assertCollectionRowCount("customers", 0);
	}

	private void assertEditElementInEntityCollection() throws Exception {
		execute("Collection.edit", "row=0,viewObject=xava_view_customers");
		assertValue("number", "66");
		assertValue("name", "Junit Customer");
		assertNoEditable("number");
		assertEditable("name");
		setValue("name", "Cliente Junit");
		execute("Collection.save");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 1, "Cliente Junit");
	}

	private void assertCreateElementInEntityCollection() throws Exception {
		execute("Collection.new", "viewObject=xava_view_customers");
		assertEditable("number");
		assertEditable("name");
		setValue("number", "66");
		setValue("name", "Junit Customer");
		setValue("address.street", "JUNIT STREET");
		setValue("address.zipCode", "44666");
		setValue("address.city", "JUNIT CITY");
		setValue("address.state.id", "CA");
		execute("Collection.save");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 1, "Junit Customer");
	}
		
	public void testCollectionWithListPropertiesStoresPreferences() throws Exception {  
		execute("CRUD.new");
		assertCollectionColumnCount("customers", 7);
		removeColumn("customers", 6); 
		assertCollectionColumnCount("customers", 6); 
		resetModule();
		execute("CRUD.new");
		assertCollectionColumnCount("customers", 6); 
		
		execute("List.addColumns", "collection=customers");
		execute("AddColumns.restoreDefault");
		assertCollectionColumnCount("customers", 7);		
	}
	
	public void testNotRemoveRowWhenAddingCollectionElements_addingWithNoElements() throws Exception { 
		execute("List.viewDetail", "row=0");
		execute("Collection.add", "viewObject=xava_view_customers");
		
		// NotRemoveRowWhenAddingCollectionElements
		assertAction("AddToCollection.add");
		assertNoAction("Collection.removeSelected");
		
		// AddingWithNoElements
		execute("AddToCollection.add");
		assertDialog();
		assertAction("AddToCollection.add");
		assertError("Please, choose an element before pressing 'Add'"); 
	}
	
	public void testRowStyleInCollections() throws Exception {		
		execute("List.viewDetail", "row=0");
		assertValue("number", "1"); 
		assertValue("name", "MANUEL CHAVARRI");
				
		int c = getCollectionRowCount("customers");
		boolean found = false;		
		for (int i=0; i<c; i++) {
			String type = getValueInCollection("customers", i, "type");		
			if ("Steady".equals(type)) {				
				assertRowStyleInCollection("customers", i, "row-highlight"); 				
				found = true;
			}
			else {
				assertNoRowStyleInCollection("customers", i);				
			}						
		}
		if (!found) {
			fail("It is required at least one Customer of 'Steady' type for run this test");
		}
	}

	
	public void testListFeaturesInCollection() throws Exception { 
		// The correct elements
		execute("List.viewDetail", "row=1");
		assertValue("number", "2"); 
		assertValue("name", "JUANVI LLAVADOR");
		assertCollectionRowCount("customers", 1);		
		execute("Navigation.previous");
		assertValue("number", "1");
		assertValue("name", "MANUEL CHAVARRI");
		assertCollectionRowCount("customers", 2);
		
		// The properties in list
		assertLabelInCollection("customers", 0, "Number");
		assertLabelInCollection("customers", 1, "Name");
		assertLabelInCollection("customers", 2, "Remarks");
		assertLabelInCollection("customers", 3, "Relation with seller");
		assertLabelInCollection("customers", 4, "Seller level");
		
		// The values in collection
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 0, "name", "Javi");
		assertValueInCollection("customers", 0, "remarks", "");
		assertValueInCollection("customers", 0, "relationWithSeller", "BUENA");
		assertValueInCollection("customers", 0, "seller.level.description", "MANAGER");
		assertValueInCollection("customers", 0, "address.state.name", "NEW YORK"); 
		
		assertValueInCollection("customers", 1, "number", "2");
		assertValueInCollection("customers", 1, "name", "Juanillo");
		assertValueInCollection("customers", 1, "remarks", "");
		assertValueInCollection("customers", 1, "relationWithSeller", "");
		assertValueInCollection("customers", 1, "seller.level.description", "MANAGER");
		assertValueInCollection("customers", 1, "address.state.name", "COLORADO"); 
		
		// Order by column
		execute("List.orderBy", "property=number,collection=customers");
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 1, "number", "2");
		execute("List.orderBy", "property=number,collection=customers");
		assertValueInCollection("customers", 0, "number", "2"); 
		assertValueInCollection("customers", 1, "number", "1");
		
		// Filter  
		String [] condition = { "1" }; 
		setConditionValues("customers", condition);		
		execute("List.filter", "collection=customers");		
		assertCollectionRowCount("customers", 1);		
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 0, "name", "Javi");
		
		// Reset collection filter when main object changes
		execute("Navigation.next");
		assertValue("number", "2");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, "number", "43");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");
		assertCollectionRowCount("customers", 2);
				
		setConditionValues("customers", condition);		
		execute("List.filter", "collection=customers");		
		assertCollectionRowCount("customers", 1);		
		assertValueInCollection("customers", 0, "number", "1");
	}
	
	public void testOverwriteCollectionControllers_defaultListActionsForCollections_tabActionsForCollections() throws Exception { 
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertValue("name", "MANUEL CHAVARRI");
		execute("Collection.edit", "row=0,viewObject=xava_view_customers"); 
		execute("Collection.hideDetail");
		assertMessage("Detail is hidden");
		
		execute("Print.generatePdf", "viewObject=xava_view_customers"); 
		assertContentTypeForPopup("application/pdf");
		
		execute("Print.generateExcel", "viewObject=xava_view_customers");				
		assertContentTypeForPopup("text/x-csv");				
	}

	
	
	public void testCustomEditorWithMultipleValuesFormatter_arraysInList() throws Exception {

		// Arrays in list
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "MANUEL CHAVARRI");
		assertValueInList(0, 2, "1/3"); // This is a String [] 
		
		// Multiple values formatters
		String [] emptyRegions = {};
		String [] regions = { "1", "3" };
		String [] oneRegion = { "2" };
		
		execute("CRUD.new");
		assertValues("regions", emptyRegions);
		setValue("number", "66"); 
		setValue("name", "SELLER JUNIT 66");
		setValue("level.id", "A");
		setValues("regions", regions);
		assertValues("regions", regions);
		
		execute("CRUD.save");
		assertNoErrors(); 
		assertValues("regions", emptyRegions);		
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValues("regions", regions);
		
		setValues("regions", oneRegion);
		execute("CRUD.save");
		assertNoErrors();
		assertValues("regions", emptyRegions);

		setValue("number", "66");
		execute("CRUD.refresh");
		assertValues("regions", oneRegion);
		
		execute("CRUD.delete");
		assertMessage("Seller deleted successfully");
	}
	
	public void testKeepsOrderAfterClosingDialog() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("number", "1"); 
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 1, "number", "2");
		execute("List.orderBy", "property=number,collection=customers");
		execute("List.orderBy", "property=number,collection=customers");
		assertValueInCollection("customers", 0, "number", "2"); 
		assertValueInCollection("customers", 1, "number", "1");

		execute("Collection.edit", "row=1,viewObject=xava_view_customers"); 
		
		assertDialog();
		closeDialog();
		assertValueInCollection("customers", 0, "number", "2");
		assertValueInCollection("customers", 1, "number", "1");
	}
	
	public void testCustomizeListSupportsRecursiveReferences() throws Exception { 
		execute("List.addColumns");
		assertAction("AddColumns.addColumns");
	}
	
	public void testOnChangeListDescriptionReferenceWithStringSingleKey_justCreatedObjectPresentWhenNavigating() throws Exception { 
		assertListRowCount(3); 
		assertOnChangeListDescriptionReferenceWithStringSingleKey();
		assertJustCreatedObjectPresentWhenNavigating();
	}
	
	private void assertOnChangeListDescriptionReferenceWithStringSingleKey() throws Exception { 
		execute("CRUD.new");
		setValue("level.id", "A"); 
		assertNoErrors();
		setValue("level.id", "");
		assertNoErrors();
	}
	
	private void assertJustCreatedObjectPresentWhenNavigating() throws Exception { 
		setValue("number", "66");
		setValue("name", "SELLER JUNIT 66");
		execute("CRUD.save");
		
		execute("Navigation.first");
		assertValue("number", "1");
		assertValue("name", "MANUEL CHAVARRI");
		
		execute("Navigation.next");
		assertValue("number", "2");
		assertValue("name", "JUANVI LLAVADOR");
		
		execute("Navigation.next");
		assertValue("number", "3");
		assertValue("name", "ELISEO FERNANDEZ");
	
		execute("Navigation.next");
		assertValue("number", "66");
		assertValue("name", "SELLER JUNIT 66");

		execute("CRUD.delete");
		assertMessage("Seller deleted successfully");		 
	}

	public void testEntityReferenceCollections() throws Exception { 		
		createCustomers(); 
		createSeller66(); 
		createSeller67();
		verifySeller66();
		deleteCustomers(); 
		deleteSeller("66");
		deleteSeller("67");					
	}
	
	public void testOpenFirstDescriptionsListInADialogWithOnChangeNotThrowEvent() throws Exception { 
		execute("ShowSellerDialog.showSellerDialog");
		assertNoMessages();
		HtmlElement editor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Seller__reference_editor_level"); 
		HtmlElement handler = editor.getElementsByTagName("i").get(0);
		handler.click();
		waitAJAX();
		assertNoMessages();
	}

	
	/* Since v2.2 this does not apply. See at testEntityReferenceCollections to
	 * see the current entity collection behaviour
	public void testSearchElementInReferencesCollectionUsingList() throws Exception {
		execute("CRUD.new");
		execute("Collection.new", "viewObject=xava_view_customers");
		execute("Reference.search", "keyProperty=xava.Seller.customers.number"); 
		String name = getValueInList(1, 0);
		execute("ReferenceSearch.choose", "row=1");
		assertValue("customers.name", name);
	}
	*/

	/* Since v2.2 this does not apply. See at testEntityReferenceCollections to
	 * see the current entity collection behaviour
	public void testCreateElementInReferencesCollectionUsingList() throws Exception {
		execute("CRUD.new");
		execute("Collection.new", "viewObject=xava_view_customers");
		execute("Reference.createNew", "model=Customer,keyProperty=xava.Seller.customers.number");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
	}
	*/
	
	private void createSeller66() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("name", "SELLER JUNIT 66");
		setValue("level.id", "A");

		assertNoDialog();
		execute("Collection.add", "viewObject=xava_view_customers");
		assertDialog();
		assertDialogTitle("Add elements to 'Customers of Seller'");
		assertValueInList(5, 0, getCustomer1().getName());
		assertValueInList(6, 0, getCustomer2().getName());
		checkRow(5);
		checkRow(6);
		execute("AddToCollection.add");
		assertNoDialog();
		assertNoErrors();
		assertMessage("2 element(s) added to Customers of Seller");
		assertCollectionRowCount("customers",2);
						
		refreshCustomers();				
		
		assertValueInCollection("customers", 0, 0, getCustomerNumber1());
		assertValueInCollectionIgnoringCase("customers", 0, 1, getCustomer1().getName());
		assertValueInCollection("customers", 0, 2, getCustomer1().getRemarks());
		assertValueInCollection("customers", 0, 3, getCustomer1().getRelationWithSeller());
		assertValueInCollection("customers", 0, 4, getCustomer1().getSeller().getLevel().getDescription());
		
		assertValueInCollection("customers", 1, 0, getCustomerNumber2());
		assertValueInCollectionIgnoringCase("customers", 1, 1, getCustomer2().getName());
		assertValueInCollection("customers", 1, 2, getCustomer2().getRemarks());
		assertValueInCollection("customers", 1, 3, getCustomer2().getRelationWithSeller());
		assertValueInCollection("customers", 1, 4, getCustomer2().getSeller().getLevel().getDescription());
	}
	
	private void refreshCustomers() {
		customer1 = refresh(customer1);
		customer2 = refresh(customer2);
	}
	
	private Customer refresh(Customer object) {
		if (object == null) return null;
		if (!XPersistence.getManager().contains(object)) {
			object = XPersistence.getManager().merge(object);
		}
		XPersistence.getManager().refresh(object);
		return object;
	}

	private void createSeller67() throws Exception {
		execute("CRUD.new");
		setValue("number", "67");
		setValue("name", "SELLER JUNIT 67");
		setValue("level.id", "B");

		assertCollectionRowCount("customers",0);
		execute("Collection.add", "viewObject=xava_view_customers");		
		assertValueInList(6, 0, getCustomer2().getName());
		execute("AddToCollection.add", "row=6");		
		assertMessage("1 element(s) added to Customers of Seller");		
		assertCollectionRowCount("customers",1);
		
		XPersistence.getManager().refresh(getCustomer2());
		
		assertValueInCollection("customers", 0, 0, getCustomerNumber2());
		assertValueInCollectionIgnoringCase("customers", 0, 1, getCustomer2().getName());
		assertValueInCollection("customers", 0, 2, getCustomer2().getRemarks());
		assertValueInCollection("customers", 0, 3, getCustomer2().getRelationWithSeller());
		assertValueInCollection("customers", 0, 4, getCustomer2().getSeller().getLevel().getDescription());
	}
	
	private void verifySeller66() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 0, getCustomerNumber1());
		assertValueInCollectionIgnoringCase("customers", 0, 1, getCustomer1().getName());
		assertValueInCollection("customers", 0, 2, getCustomer1().getRemarks());
		assertValueInCollection("customers", 0, 3, getCustomer1().getRelationWithSeller());
		assertValueInCollection("customers", 0, 4, getCustomer1().getSeller().getLevel().getDescription());
		
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_customers"); 
		assertMessage("Association between Customer and Seller has been removed, but Customer is still in database");
		assertNoErrors();
		assertCollectionRowCount("customers", 0);		
	}
		
	private void deleteSeller(String number) throws Exception {
		execute("CRUD.new");
		setValue("number", number);
		execute("CRUD.refresh");
		assertNoErrors();

		execute("CRUD.delete");											
		assertNoErrors();
		assertMessage("Seller deleted successfully");
		assertExists("number"); // A bug did that the screen remained in blank after delete		
	}
	
	private void deleteCustomers() throws RemoteException, Exception {
		XPersistence.getManager().remove(getCustomer1());
		XPersistence.getManager().remove(getCustomer2());
		XPersistence.commit();
	}

	
	
	private Customer getCustomer1() throws Exception {
		if (customer1 == null) {
			createCustomers();
		}
		return customer1;
	}
	
	private Customer getCustomer2() throws Exception {
		if (customer2 == null) {
			createCustomers();
		}
		return customer2;
	}
	
		
	private void createCustomers() throws Exception {
		customer1 = new Customer();
		customer1.setNumber(66);
		customer1.setName("Customer Junit 66");
		// customer1.setType(1); // For XML components  
		customer1.setType(Customer.Type.NORMAL); // For annotated POJOs
		customer1.setAddress(createAddress());
		customer1.setRemarks("REMARKS JUNIT 66");
		customer1.setRelationWithSeller("RELATION JUNIT 66");
		XPersistence.getManager().persist(customer1);		

		customer2 = new Customer();
		customer2.setNumber(67);
		customer2.setName("Customer Junit 67");
		// customer2.setType(1); // For XML components  
		customer2.setType(Customer.Type.NORMAL); // For annotated POJOs
		customer2.setAddress(createAddress());
		customer2.setRemarks("REMARKS JUNIT 67");
		customer2.setRelationWithSeller("RELATION JUNIT 67");
		XPersistence.getManager().persist(customer2);
	
		XPersistence.commit();
	}
	
	private Address createAddress() { 
		Address address = new Address();
		address.setCity("EL PUIG");
		address.setStreet("MI CALLE");
		address.setZipCode(46540);
		State state = new State();
		state.setId("CA");
		address.setState(state);
		return address;
	}

	private String getCustomerNumber1() throws Exception {
		return String.valueOf(getCustomer1().getNumber());
	}
	
	private String getCustomerNumber2() throws Exception {
		return String.valueOf(getCustomer2().getNumber());
	}
				
}
