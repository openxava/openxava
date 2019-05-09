package org.openxava.test.tests;

import java.math.*;
import javax.persistence.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class Product2Test extends EmailNotificationsTestBase { 
	
	public Product2Test(String testName) {
		super(testName, "Product2");		
	}
	
	public void testCustomDialog() throws Exception {
		// In detail mode
		execute("CRUD.new");
		assertCustomDialog(); 
		
		// In list mode
		execute("Mode.list");
		assertCustomDialog();
		
		// Using generateExcel that does not hide the dialog
		assertNoDialog();
		assertAction("Product2.reportBySubfamily");
		assertNoAction("FamilyProductsReport.generateExcel");
		execute("Product2.reportBySubfamily");
		assertDialog();
		assertNoAction("Product2.reportBySubfamily");
		assertAction("FamilyProductsReport.generateExcel");
		setValue("subfamily.number", "2");
		// The next line fails on WebSphere Portal 6.1 because of the 
		// WebSphere6-1/PortalServer/lwo/prereq.odc/shared/app/jakarta-poi.jar file.
		// Just remove this file and this test will pass. Changing classloading policy of war does not work
		execute("FamilyProductsReport.generateExcel");  
		assertNoErrors();
		assertContentTypeForPopup("application/vnd.ms-excel"); 
		assertDialog(); 
		assertNoAction("Product2.reportBySubfamily");
		assertAction("FamilyProductsReport.generateExcel");
	}
	
	private void assertCustomDialog() throws Exception { 
		assertNoDialog();
		assertAction("Product2.reportBySubfamily");
		assertNoAction("FamilyProductsReport.generatePdf");
		execute("Product2.reportBySubfamily");
		assertDialog();
		assertNoAction("Product2.reportBySubfamily");
		assertAction("FamilyProductsReport.generatePdf");		
		execute("FamilyProductsReport.generatePdf");
		assertError("Value for Subfamily in Filter by subfamily is required");		
		setValue("subfamily.number", "2");
		execute("FamilyProductsReport.generatePdf");
		assertNoErrors(); // If it does not find the font try to set net.sf.jasperreports.awt.ignore.missing.font=true in jasperreports jar
		assertContentTypeForPopup("application/pdf");
		assertNoDialog();
		assertAction("Product2.reportBySubfamily"); // As secondary effect it tests a bug when we reload a page in an aligned by column view, the buttons disappeared 
		assertNoAction("FamilyProductsReport.generatePdf");
	}
		
	public void testFormula() throws Exception {
		assertValueInList(0, "unitPrice", "11.00");
		assertValueInList(0, "unitPriceWithTax", "12.76");
		
		assertListRowCount(7); // We rely in that there are 7 products,  
								// you can adapt this number if needed 
		setConditionValues(new String [] {"", "", "", "", "", "12.76"});
		execute("List.filter");
		
		assertListRowCount(2); // We rely in that there are 2 products 
				// with 11.00 as price, you can adapt this number if needed
		assertValueInList(0, "unitPriceWithTax", "12.76");
		assertValueInList(1, "unitPriceWithTax", "12.76");
		
		execute("List.viewDetail", "row=0");
		assertValue("unitPrice", "11.00");
		assertValue("unitPriceWithTax", "12.76");
		assertEditable("unitPrice");
		assertNoEditable("unitPriceWithTax");
	}
	
	public void testEditorForReferenceInEditorsXML() throws Exception {
		execute("List.viewDetail", "row=0");
		setValue("color.number", "1");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("description", "");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("color.number", "1");
		
		setValue("color.number", "28");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("description", "");		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("color.number", "28");
				
		assertTrue(
				getHtml().indexOf(
					"<input name=\"ox_OpenXavaTest_Product2__color___number\" value=\"0\" type=\"radio\"") 
					>= 0
		);
		
		assertTrue(getHtml().indexOf("Color Frame Editor:") < 0);
	}
	
	public void testImagesGallery() throws Exception {
		subscribeToEmailNotifications(); 
		
		// We remove oid from product 1 in order to test that images gallery works well in the first attemp.
		Product2.findByNumber(1).setPhotos("");
		XPersistence.commit();
		// Verifying product 1 has no images
		assertTrue("At least 2 products are required to run this test", getListRowCount() >= 2);
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");		
		execute("Gallery.edit", "galleryProperty=photos");
		assertNoErrors(); 
		assertMessage("No images");
		assertNoAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertNoAction("Gallery.removeImage");
		assertEquals("Images count does not match", 0, getForm().getInputsByName("xava.GALLERY.images").size());
		
		// Canceling the adding of and image
		execute("Gallery.addImage");
		assertNoErrors();
		String imageUrl = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newImage", imageUrl);
		execute("LoadImageIntoGallery.cancel");
		assertNoErrors();
		assertNoAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertNoAction("Gallery.removeImage");
		assertEquals("Images count does not match", 0, getForm().getInputsByName("xava.GALLERY.images").size());
		
		// Adding one image		
		execute("Gallery.addImage");
		assertNoErrors();
		imageUrl = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newImage", imageUrl);
		execute("LoadImageIntoGallery.loadImage");
		assertNoErrors();
		assertMessage("Image added to the gallery");
		assertAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertAction("Gallery.removeImage");
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());		
		
		// Returning to the main entity
		execute("Gallery.close"); 
		//execute("CRUD.save"); It's not needed explicit saving of the main entity
		assertNoErrors();
		
		// Verifying that product 2 has no images
		execute("Navigation.next");
		assertValue("number", "2");
		execute("Gallery.edit", "galleryProperty=photos");
		assertNoErrors();
		assertMessage("No images"); 
		assertNoAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertNoAction("Gallery.removeImage");
		assertEquals("Images count does not match", 0, getForm().getInputsByName("xava.GALLERY.images").size());		
		execute("Gallery.close"); 
		
		// Verifying that product 1 has the added image
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertNoErrors();
		execute("Gallery.edit", "galleryProperty=photos");
		assertNoErrors();
		assertNoMessages();
		assertAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertAction("Gallery.removeImage");
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());
		String imageOid = getForm().getInputByName("xava.GALLERY.images").getValueAttribute();
		
		// Maximizing the image
		execute("Gallery.maximizeImage", "oid="+imageOid);
		assertNoErrors();
		assertNoAction("Gallery.maximizeImage"); 
		assertAction("Gallery.minimizeImage");
		assertNoAction("Gallery.removeImage");		
		
		// Minimizing the image
		execute("Gallery.minimizeImage");
		assertNoErrors();
		assertAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertAction("Gallery.removeImage");
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());
		
		// Verifying read-only
		execute("Gallery.close"); 
		execute("EditableOnOff.setOff");
		execute("Gallery.edit", "galleryProperty=photos");
		assertNoErrors();
		assertNoMessages();
		assertNoAction("Gallery.addImage");
		assertAction("Gallery.maximizeImage"); 
		assertNoAction("Gallery.minimizeImage");
		assertNoAction("Gallery.removeImage");
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());
		execute("Close.close");
		execute("EditableOnOff.setOn");
		
		// Removing the image
		execute("Gallery.edit", "galleryProperty=photos");
		assertEquals("Images count does not match", 1, getForm().getInputsByName("xava.GALLERY.images").size());
		execute("Gallery.removeImage", "oid="+imageOid);
		assertNoErrors();
		assertNoAction("Gallery.maximizeImage");
		assertNoAction("Gallery.minimizeImage");
		assertNoAction("Gallery.removeImage");
		assertEquals("Images count does not match", 0, getForm().getInputsByName("xava.GALLERY.images").size());
		
		// Verifying that product 1 has no images
		execute("Gallery.close"); 
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertNoErrors();
		execute("Gallery.edit", "galleryProperty=photos");
		assertNoErrors();
		assertMessage("No images");
		assertEquals("Images count does not match", 0, getForm().getInputsByName("xava.GALLERY.images").size());

		assertEmailNotifications(
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Products 2, permalink=http://localhost:8080/OpenXavaTest/modules/Product2?detail=1, changes=<ul><li><b>Photos</b>: NEW IMAGES ADDED --> foto_javi.jpg</li></ul>",
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Products 2, permalink=http://localhost:8080/OpenXavaTest/modules/Product2?detail=1, changes=<ul><li><b>Photos</b>: IMAGE REMOVED --> One image removed</li></ul>"
		);	
	}
	
	public void testReferencesAsDescriptionListUsesFilterOfDefaultTab() throws Exception {
		execute("CRUD.new");
		execute("Product2.changeLimitZone");
		
		Warehouse key1 = new Warehouse();
		key1.setZoneNumber(1);
		key1.setNumber(1);
		Warehouse key2 = new Warehouse();
		key2.setZoneNumber(1);
		key2.setNumber(2);
		Warehouse key3 = new Warehouse();
		key3.setZoneNumber(1);
		key3.setNumber(3);
		
		String [][] warehouses = {
				{ "", "" },
				{ toKeyString(key1), "CENTRAL VALENCIA" },
				{ toKeyString(key3), "VALENCIA NORTE" },
				{ toKeyString(key2), "VALENCIA SURETE" } 
		};		
		assertValidValues("warehouse.KEY", warehouses); 
	}
	
	public void testDefaultValueCalculatorForReferences_genericI18nForTabs() throws Exception {
		assertLabelInList(2, "Family");
		assertLabelInList(3, "Subfamily");
		
		execute("CRUD.new");
		assertValue("family.number", "2");		
		assertValue("warehouse.KEY", "[.4.4.]");
	}
	
	public void testFocusMoveToReferenceAsDescriptionsList() throws Exception { 
		execute("CRUD.new");
		setValue("family.number", "1");
		assertFocusOn("subfamily.number");
	}
	
	/*
	// Since 2.2.1 "Mode.list" is not available when navigating to
	// another view than main one, then this case is impossible
	public void testListToDetailAlwaysMainView() throws Exception {
		execute("CRUD.new");		
		assertExists("unitPrice");
		execute("Reference.createNew", "model=Family2,keyProperty=xava.Product2.family.number");
		assertNotExists("unitPrice");
		execute("Mode.list"); // Since 2.2.1 this link is not here
		execute("CRUD.new");		
		assertExists("unitPrice");				
	}
	*/
	
	public void testSetEditableOnReferencesAsDescriptionsList() throws Exception {		
		execute("CRUD.new");
		assertEditable("family");
		execute("Product2.deactivateType");
		assertNoEditable("family");		
	}
	
	public void testOnChangeDescriptionsListReferenceMultipleKey() throws Exception {		
		execute("CRUD.new");
		assertNotExists("zoneOne"); 
		
		Warehouse warehouseKeyZone1 = new Warehouse();
		warehouseKeyZone1.setNumber(1);
		warehouseKeyZone1.setZoneNumber(1); 
		setValue("warehouse.KEY", toKeyString(warehouseKeyZone1));		
		assertExists("zoneOne");
		
		Warehouse warehouseKeyZone2 = new Warehouse();
		warehouseKeyZone2.setNumber(1);
		warehouseKeyZone2.setZoneNumber(2); 				
		setValue("warehouse.KEY", toKeyString(warehouseKeyZone2));
		assertNotExists("zoneOne");
		
		createProduct(66, "JUNIT ZONE 1", 1); 
		createProduct(67, "JUNIT ZONE 2", 2);
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("description", "JUNIT ZONE 1");
		assertExists("zoneOne");
		
		execute("CRUD.new");		
		setValue("number", "67");		
		execute("CRUD.refresh");		
		assertNoErrors();		
		assertValue("description", "JUNIT ZONE 2"); 
		assertNotExists("zoneOne");
		
		setValue("warehouse.KEY", "");
		assertValue("warehouse.KEY", "");
				
		deleteProduct(66);
		deleteProduct(67);
	}

	

	public void testDescriptionsListReferenceDependents() throws Exception {
		
		execute("CRUD.new");
	
		// Verifying initial state		
		String [][] familyValues = {
			{ "", "" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" }	
		};
		
		assertValue("family.number", "2"); // 2 is the default value		
		assertValidValues("family.number", familyValues);
		setValue("family.number", "");
		
		String [][] voidValues = {
			{ "", "" }
		};
		
		assertValue("subfamily.number", "");		
		assertValidValues("subfamily.number", voidValues);
		
		// Change value
		setValue("family.number", "2");
		String [][] hardwareValues = {
			{ "", ""},
			{ "12", "PC"},
			{ "13", "PERIFERICOS"},			
			{ "11", "SERVIDORES"}						
		};
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", hardwareValues);
		
		// Changing the value again
		setValue("family.number", "1");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", softwareValues);										
	}
	
	public void testNavigationWithDescriptionsListReferenceDependents() throws Exception {		
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");
		assertValue("family.number", "1");
		assertValue("subfamily.number", "2");		
		execute("Navigation.next");		
		assertValue("number", "2");
		assertValue("family.number", "2");
		assertValue("subfamily.number", "11");		
		execute("Navigation.next");
		assertValue("number", "3");
		assertValue("family.number", "1");
		assertValue("subfamily.number", "1");						
	}
	
	public void testCreateModifyAndReadWithDescriptionsListReference() throws Exception {
				
		// Create
		execute("CRUD.new");		
		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		setValue("family.number", "2");
		assertNoErrors();
		setValue("subfamily.number", "12");
		Warehouse warehouseKey = new Warehouse();
		warehouseKey.setNumber(1);
		warehouseKey.setZoneNumber(2); 
		setValue("warehouse.KEY", toKeyString(warehouseKey));
		setValue("unitPrice", "125.66");
		assertNoErrors();
		assertNoEditable("unitPriceInPesetas");
		execute("CRUD.save");				
		assertNoErrors(); 
				
		// Search for verify
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		assertValue("family.number", "2");
		assertValue("subfamily.number", "12");
		assertValue("warehouse.KEY", toKeyString(warehouseKey));
		assertValue("unitPrice", "125.66");
		
		// Modify
		setValue("subfamily.number", "13");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("number", "");
		assertValue("description", "");
		
		// Verifying just modified
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "66");
		assertValue("description", "JUNIT PRODUCT");
		assertValue("family.number", "2");
		assertValue("subfamily.number", "13");

				
		// Delete
		execute("CRUD.delete");
		assertMessage("Product deleted successfully");		
	}
					
	public void testReferencesInListMode() throws Exception {				
		assertValueInList(1, "number", "2");
		assertValueInList(1, "family.description", "HARDWARE");
		assertValueInList(1, "subfamily.description", "SERVIDORES");
	}
	
	public void testCreateReferencesFromDescriptionsList() throws Exception {
		
		execute("CRUD.new");		

		// Verifying initial state		
		String [][] familyValues = {
			{ "", "" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" }	
		};
		assertValidValues("family.number", familyValues);
		
		execute("Reference.createNew", "model=Family2,keyProperty=xava.Product2.family.number");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
		execute("NewCreation.cancel");	
		execute("Reference.createNew", "model=Family2,keyProperty=xava.Product2.family.number");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
		execute("NewCreation.saveNew");
		assertError("Value for Number in Family is required");
		assertError("Value for Description in Family is required");
		setValue("Family2", "number", "1");
		setValue("Family2", "description", "JUNIT TEST");
		execute("NewCreation.saveNew");
		assertError("Impossible to create: an object with that key already exists");
		setValue("Family2", "number", "66");
		execute("NewCreation.saveNew");
		assertNoErrors();		
		
		// Test just added
		String [][] familyValuesUpdated = {
			{ "", "" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" },	
			{ "66", "JUNIT TEST" }
		};				
		assertValidValues("family.number", familyValuesUpdated);
		assertValue("family.number", "66"); // The just created family is automatically selected
		
		// Delete it
		Family2 f = XPersistence.getManager().find(Family2.class, 66);		
		XPersistence.getManager().remove(f);		
	}
	
	
	
	public void testDescriptionsListReferenceValidation() throws Exception {						
		execute("CRUD.new");	
		setValue("family.number", ""); // because has a default value
		execute("CRUD.save");				
		assertError("Value for Family in Product is required");
		assertError("Value for Subfamily in Product is required");
	}
	
	public void testAutocompleteInDescriptionsList() throws Exception { 
		createWarehouseWithQuote(); // To test a bug with quotes 

		getWebClient().getOptions().setCssEnabled(true);
		execute("CRUD.new");
		
		getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Product2__reference_editor_warehouse"); // Warehouse combo must be to test the "quotes" bug
		
		HtmlElement familyList = getHtmlPage().getHtmlElementById("ui-id-1");
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.getChildElementCount());
		
		HtmlElement familyEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Product2__reference_editor_family");
		HtmlElement openFamilyListIcon = familyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		HtmlElement closeFamilyListIcon = familyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-up");
		assertTrue(openFamilyListIcon.isDisplayed());
		assertFalse(closeFamilyListIcon.isDisplayed());
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed());
		assertEquals(3, familyList.getChildElementCount());
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());	
		
		closeFamilyListIcon.click();
		assertFalse(familyList.isDisplayed());
		assertTrue(openFamilyListIcon.isDisplayed());
		assertFalse(closeFamilyListIcon.isDisplayed());	
		
		HtmlElement familyTextField = familyEditor.getOneHtmlElementByAttribute("input", "class", "xava_select editor ui-autocomplete-input");
		assertEquals("HARDWARE", familyTextField.getAttribute("value"));
		familyTextField.setAttribute("value", "");
		assertEquals("", familyTextField.getAttribute("value"));
		familyTextField.type("ware");
		assertEquals("ware", familyTextField.getAttribute("value"));
		Thread.sleep(600);
		assertTrue(familyList.isDisplayed()); 
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());
		assertEquals(2, familyList.getChildElementCount());
		assertEquals("SOFTWARE", familyList.getFirstChild().asText());
		assertEquals("HARDWARE", familyList.getLastChild().asText());
		
		((HtmlElement) familyList.getFirstChild()).click(); // SOFTWARE
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		HtmlElement subfamilyEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Product2__reference_editor_subfamily");
		HtmlElement openSubfamilyListIcon = subfamilyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		openSubfamilyListIcon.click();
		HtmlElement subfamilyList = getHtmlPage().getHtmlElementById("ui-id-9");
		assertTrue(subfamilyList.isDisplayed());
		assertEquals(3, subfamilyList.getChildElementCount());
		assertEquals("DESARROLLO", subfamilyList.getFirstChild().asText());
		assertEquals("SISTEMA", subfamilyList.getLastChild().asText());	
		
		((HtmlElement) subfamilyList.getFirstChild()).click(); // DESARROLLO
		HtmlElement subfamilyTextField = subfamilyEditor.getOneHtmlElementByAttribute("input", "class", "xava_select editor ui-autocomplete-input");
		assertEquals("DESARROLLO", subfamilyTextField.getAttribute("value"));

		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		setValue("unitPrice", "66");
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		assertValueInList(0, "number", "66"); 
		assertValueInList(0, "description", "JUNIT PRODUCT");
		assertValueInList(0, "family.description", "SOFTWARE");
		assertValueInList(0, "subfamily.description", "DESARROLLO");
		
		execute("List.viewDetail", "row=0");
		assertValue("number", "66");
		assertValue("family.number", "1");
		assertDescriptionValue("family.number", "SOFTWARE");
		familyTextField =  getDescriptionsListTextField("family");
		assertEquals("SOFTWARE", familyTextField.getAttribute("value"));
		assertValue("subfamily.number", "1");
		assertDescriptionValue("subfamily.number", "DESARROLLO");
		subfamilyTextField = getDescriptionsListTextField("subfamily");
		assertEquals("DESARROLLO", subfamilyTextField.getAttribute("value"));		
		execute("CRUD.delete");
		assertNoErrors();
		
		execute("CRUD.new");
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.setAttribute("value", "");
		familyTextField.type("ware");
		assertEquals("ware", familyTextField.getAttribute("value"));
		familyTextField.blur();
		assertEquals("", familyTextField.getAttribute("value")); 
		
		execute("CRUD.new");
		familyList = getHtmlPage().getHtmlElementById("ui-id-27");
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.getChildElementCount());
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.setAttribute("value", "");
		familyTextField.type(" \b");
		Thread.sleep(500); 
		assertTrue(familyList.isDisplayed()); 
		assertEquals(3, familyList.getChildElementCount());
				
		familyEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Product2__reference_editor_family");
		openFamilyListIcon = familyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		closeFamilyListIcon = familyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-up");		
		subfamilyEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Product2__reference_editor_subfamily");
		openSubfamilyListIcon = subfamilyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		closeFamilyListIcon.click();
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed());
		openSubfamilyListIcon.click();
		assertFalse(familyList.isDisplayed());
		
		
		removeWarehouseWithQuote(); 
	}
	
	private void createWarehouseWithQuote() {
		Warehouse warehouse = new Warehouse();
		warehouse.setZoneNumber(10);
		warehouse.setNumber(11);
		warehouse.setName("ALMACEN \"EL REBOLLAR\"");
		XPersistence.getManager().persist(warehouse);
		XPersistence.commit();
	}

	private void removeWarehouseWithQuote() { 
 		Query query = XPersistence.getManager().createQuery("from Warehouse as o where o.zoneNumber = 10 and number = 11"); // We don't use Warehouse.findByZoneNumberNumber(10, 11) in order to work with XML/Hibernate version 
 		Warehouse warehouse = (Warehouse) query.getSingleResult();
		XPersistence.getManager().remove(warehouse);
	}

	private HtmlElement getDescriptionsListTextField(String reference) {
		HtmlElement familyEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Product2__reference_editor_" + reference);
		return  familyEditor.getOneHtmlElementByAttribute("input", "class", "xava_select editor ui-autocomplete-input");
	}
	
	private void createProduct(int number, String description, int zone) throws Exception {
		Product2 p = new Product2();
		p.setNumber(number);
		p.setDescription(description);
		Family2 f = new Family2();
		f.setNumber(1);
		p.setFamily(f);
		Subfamily2 sf = new Subfamily2();
		sf.setNumber(1);
		p.setSubfamily(sf);
		Warehouse w = new Warehouse();
		w.setNumber(1);
		w.setZoneNumber(zone);
		p.setWarehouse(w);
		p.setUnitPrice(new BigDecimal("1.00"));
		XPersistence.getManager().persist(p);
		XPersistence.commit();
	}
			
	private void deleteProduct(long number) throws Exception {
		Product2 k = XPersistence.getManager().find(Product2.class, number);		
		XPersistence.getManager().remove(k);
		XPersistence.commit();
	}
						
}
