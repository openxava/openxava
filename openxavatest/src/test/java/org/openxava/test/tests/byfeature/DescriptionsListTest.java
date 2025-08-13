package org.openxava.test.tests.byfeature;

import java.util.*;

import javax.persistence.*;

import org.openqa.selenium.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 * To test @DescriptionsList related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class DescriptionsListTest extends WebDriverTestBase {

	public DescriptionsListTest(String testName) {
		super(testName);
	}
	
	@Override
	protected boolean isHeadless() { // tmr
		return false; 
	}
	
	@Override
	protected void tearDown() throws Exception { // tmr
	}

	protected void setUp() throws Exception {
		super.setUp();
		XPersistence.reset(); 
		XPersistence.setPersistenceUnit("junit");
	}
	
	public void testLargeDatasetLoadedOnDemand() throws Exception {
		goModule("Traveler");
		// On entering Traveler it is in detail mode by default (no records)
		// Verify that no autocomplete option items are loaded yet
		List<WebElement> items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(0, items.size());

		// Open lastJourney descriptions list
		WebElement lastJourneyEditor = getDriver().findElement(By.id("ox_openxavatest_Traveler__reference_editor_lastJourney"));
		WebElement openIcon = lastJourneyEditor.findElement(By.className("mdi-menu-down"));
		openIcon.click();
		Thread.sleep(700); // wait for the first page to load

		// Now options should be loaded on demand (first page size expected: 30)
		items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(30, items.size());

		// TMR ME QUEDÉ POR AQUÍ. TENGO QUE PROBAR CERRAR Y ABRIR EL COMBO PARA VERIFICAR QUE SIGUI HABIENDO 30
		// TMR   PROBARLO A MANO, SI NO FALLA NO TESTEARLO. PERO CREO QUE SÍ FALLA
	}

	public void _testDropDownWhenValuesHasBackSlash() throws Exception { // tmr
		goModule("Carrier");
		execute("CRUD.new");
		WebElement drivingLicense = getDriver().findElement(By.cssSelector("i.mdi.mdi-menu-down"));
		drivingLicense.click();
		WebElement dropDown = getDriver().findElement(By.cssSelector("ul.ui-menu.ui-widget.ui-widget-content.ui-autocomplete.ui-front"));
		assertFalse(dropDown.getAttribute("style").contains("display: none;"));
	}

	public void _testAutocomplete() throws Exception { // tmr
		setFamilyDescription(1, "SOFTWARÉ"); // To test a bug with accents 
		createWarehouseWithQuote(); // To test a bug with quotes

		goModule("Product2");
		execute("CRUD.new");
		
		getDriver().findElement(By.id("ox_openxavatest_Product2__reference_editor_warehouse")); // Warehouse combo must be to test the "quotes" bug

		WebElement familyList = getDriver().findElement(By.id(getListId(0))); 
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.findElements(By.tagName("li")).size());
		WebElement familyEditor = getDriver().findElement(By.id("ox_openxavatest_Product2__reference_editor_family"));
		WebElement openFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-down"));
		WebElement closeFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-up"));
		assertTrue(openFamilyListIcon.isDisplayed());
		assertFalse(closeFamilyListIcon.isDisplayed()); 
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed());
		assertEquals(3, familyList.findElements(By.tagName("li")).size());
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());	

		closeFamilyListIcon.click();
		assertFalse(familyList.isDisplayed());
		assertTrue(openFamilyListIcon.isDisplayed());
		assertFalse(closeFamilyListIcon.isDisplayed());	

		WebElement familyTextField = familyEditor.findElement(By.className("ui-autocomplete-input"));
		assertEquals("HARDWARE", familyTextField.getAttribute("value"));
		familyTextField.sendKeys(Keys.CONTROL + "a");
		familyTextField.sendKeys("\b");
		assertEquals("", familyTextField.getAttribute("value"));
		
		familyTextField.sendKeys("ware"); 
		assertEquals("ware", familyTextField.getAttribute("value"));
		Thread.sleep(700); 
		assertTrue(familyList.isDisplayed()); 
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());
		
		List<WebElement> familyListChildren = familyList.findElements(By.tagName("li")); 
		assertEquals(2, familyListChildren.size()); 
		assertEquals("SOFTWARÉ", familyListChildren.get(0).getText()); 
		assertEquals("HARDWARE", familyListChildren.get(1).getText());
		
		familyListChildren.get(0).click(); // SOFTWARE
		wait(getDriver());
		WebElement subfamilyEditor = getDriver().findElement(By.id("ox_openxavatest_Product2__reference_editor_subfamily"));
		WebElement openSubfamilyListIcon = subfamilyEditor.findElement(By.className("mdi-menu-down"));
		openSubfamilyListIcon.click();
		WebElement subfamilyList = getDriver().findElement(By.id(getListId(1))); 
		assertTrue(subfamilyList.isDisplayed());
		List<WebElement> subfamilyListChildren = subfamilyList.findElements(By.tagName("li")); 
		assertEquals(3, subfamilyListChildren.size());
		assertEquals("DESARROLLO", subfamilyListChildren.get(0).getText()); 
		assertEquals("SISTEMA", subfamilyListChildren.get(2).getText());	
		
		subfamilyListChildren.get(0).click(); // DESARROLLO
		WebElement subfamilyTextField = subfamilyEditor.findElement(By.className("ui-autocomplete-input"));
		assertEquals("DESARROLLO", subfamilyTextField.getAttribute("value")); 

		setValue("number", "66");
		setValue("description", "JUNIT PRODUCT");
		setValue("unitPrice", "66");
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=number");
		assertValueInList(0, 0, "66"); 
		assertValueInList(0, 1, "JUNIT PRODUCT");
		assertValueInList(0, 2, "SOFTWARÉ"); 
		assertValueInList(0, 3, "DESARROLLO");
		
		execute("List.viewDetail", "row=0");
		assertValue("number", "66");
		assertValue("family.number", "1");
		assertDescriptionValue("family.number", "SOFTWARÉ");
		familyTextField =  getDescriptionsListTextField("family");
		assertEquals("SOFTWARÉ", familyTextField.getAttribute("value")); 
		assertValue("subfamily.number", "1");
		assertDescriptionValue("subfamily.number", "DESARROLLO");
		subfamilyTextField = getDescriptionsListTextField("subfamily");
		assertEquals("DESARROLLO", subfamilyTextField.getAttribute("value")); 
		execute("CRUD.delete");
		assertNoErrors();

		
		execute("CRUD.new");
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.sendKeys(Keys.CONTROL + "a");
		familyTextField.sendKeys("\b");		
		familyTextField.sendKeys("ware");
		assertEquals("ware", familyTextField.getAttribute("value"));
		familyTextField.sendKeys(Keys.TAB);
		assertEquals("", familyTextField.getAttribute("value")); 
		
		execute("CRUD.new");
		familyList = getDriver().findElement(By.id(getListId(0)));  
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.findElements(By.tagName("li")).size());
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.sendKeys(Keys.CONTROL + "a");
		familyTextField.sendKeys("\b");
		familyTextField.sendKeys(" \b");
		Thread.sleep(700); 
		assertTrue(familyList.isDisplayed()); 
		assertEquals(3, familyList.findElements(By.tagName("li")).size());
				
		familyEditor = getDriver().findElement(By.id("ox_openxavatest_Product2__reference_editor_family"));
		openFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-down"));
		closeFamilyListIcon = familyEditor.findElement(By.className("mdi-menu-up"));		
		closeFamilyListIcon.click();
		wait(getDriver()); 
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed()); 
		subfamilyEditor = getDriver().findElement(By.id("ox_openxavatest_Product2__reference_editor_subfamily"));
		openSubfamilyListIcon = subfamilyEditor.findElement(By.className("mdi-menu-down"));		
		openSubfamilyListIcon.click(); 
		assertFalse(familyList.isDisplayed());
		
		setFamilyDescription(1, "SOFTWARE"); 
		removeWarehouseWithQuote(); 
	}
	
	private String getListId(int orderInUI) throws Exception { 
		return getDriver().findElements(By.className("ui-menu")).get(orderInUI).getAttribute("id");
	}
	
	private void setFamilyDescription(int number, String newDescription) { 
		Family2 software = XPersistence.getManager().find(Family2.class, number);
		software.setDescription(newDescription);
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
		XPersistence.commit();
	}
	
	private WebElement getDescriptionsListTextField(String reference) {
		WebElement referenceEditor = getDriver().findElement(By.id("ox_openxavatest_" + getModule() + "__reference_editor_" + reference));
		return referenceEditor.findElement(By.className("ui-autocomplete-input"));
	}
	
}
