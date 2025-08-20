package org.openxava.test.tests.byfeature;

import java.util.*;

import javax.persistence.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
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
		// TMR ME QUEDÉ POR AQUÍ: AÑADIR TEST PARA COMPROBAR QUE EL COMPARADOR DE LISTA SOLO SACA 200 ELEMENTOS Y TIENE UN -- ETC --
		// TMR   PROBARLO A MANO EN OTROS DESCRIPTIONS LIST CORTOS
		
		goModule("Traveler"); 

		// On demand server side fetch. If it takes more than 7 seconds, all the records have been loaded
		long start = System.currentTimeMillis();
		execute("CRUD.new");
		long elapsed = System.currentTimeMillis() - start;
		assertTrue("CRUD.new should take less than 6 seconds, but took " + elapsed + " ms", elapsed < 6000);

		// On entering Traveler it is in detail mode by default (no records)
		// Verify that no autocomplete option items are loaded yet
		List<WebElement> items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(0, items.size());

		// Open lastJourney descriptions list
		WebElement lastJourneyEditor = getDriver().findElement(By.id("ox_openxavatest_Traveler__reference_editor_lastJourney"));
		WebElement openIcon = lastJourneyEditor.findElement(By.className("mdi-menu-down"));
		openIcon.click();
		Thread.sleep(700); // wait for the first page to load

		// Now options should be loaded on demand (first page size expected: 60)
		items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(60, items.size());

		// Close and reopen the list, it should still show 60 items
		WebElement closeIcon = lastJourneyEditor.findElement(By.className("mdi-menu-up"));
		closeIcon.click();
		Thread.sleep(300); // wait for list to close
		openIcon = lastJourneyEditor.findElement(By.className("mdi-menu-down"));
		openIcon.click();
		Thread.sleep(700); // wait for the first page to load again
		items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(60, items.size());

		// Scroll to load next page and verify 120 items
		WebElement list = getDriver().findElement(By.id(getListId(0)));
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", list);
		Thread.sleep(900); // wait for the next page to load
		items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(120, items.size());

		// --- Incomplete simulation of mouse-wheel bug ---
		// We mimic user interaction that previously exposed a wheel-related bug by:
		// 1) Hovering a deep item (35) as a real user would (Actions + native events)
		// 2) Forcing another lazy-load page via programmatic scrollTop change
		// Note: This does not generate a real wheel event; it's a pragmatic approximation.
		// Move to and then synthetically dispatch mouse events on a deeper list item (index 35)
		WebElement itemToHover = items.get(65);
		hoverLikeRealMouse(itemToHover);

		// Then scroll again using the same working method to load next page and verify 90 items
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", list);
		Thread.sleep(900); // wait for the next page (third) to load
		items = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		assertEquals(180, items.size());
		// --- End incomplete simulation block ---

		// Type filter '24' in the input and verify only the expected suggestions are shown
		WebElement lastJourneyInput = lastJourneyEditor.findElement(By.className("ui-autocomplete-input"));
		lastJourneyInput.clear();
		lastJourneyInput.sendKeys("24");
		Thread.sleep(9000); // Given it's calculated property all the records are loaded in server to filter, so it costs
		List<WebElement> filtered = getDriver().findElements(By.cssSelector("li.ui-menu-item"));
		List<String> filteredTexts = new java.util.ArrayList<>();
		for (WebElement it : filtered) filteredTexts.add(it.getText());
		List<String> expected = java.util.Arrays.asList(
			"JORNEY 24",
			"JORNEY 124",
			"JORNEY 224",
			"JORNEY 240",
			"JORNEY 241"
		);
		assertEquals(expected, filteredTexts);

		// Select one value from suggestions (JORNEY 224), set name, save, go to list and verify presence
		for (WebElement opt : filtered) {
			if ("JORNEY 224".equals(opt.getText())) { opt.click(); break; }
		}
		// Ensure input shows the chosen value
		assertEquals("JORNEY 224", lastJourneyEditor.findElement(By.className("ui-autocomplete-input")).getAttribute("value"));

		// Test filtering for nextJourney reference (uses persistent property 'name' instead of calculated 'slowName')
		WebElement nextJourneyEditor = getDriver().findElement(By.id("ox_openxavatest_Traveler__reference_editor_nextJourney"));
		WebElement nextJourneyOpenIcon = nextJourneyEditor.findElement(By.className("mdi-menu-down"));
		nextJourneyOpenIcon.click();
		Thread.sleep(700); // wait for the first page to load

		// Type filter '24' in nextJourney input and verify filtering with persistent property
		WebElement nextJourneyInput = nextJourneyEditor.findElement(By.className("ui-autocomplete-input"));
		nextJourneyInput.clear();
		nextJourneyInput.sendKeys("24");
		Thread.sleep(1000); // Less time needed since it's filtering by persistent property, not calculated
		WebElement nextJourneyList = getDriver().findElement(By.id(getListId(1))); // Get the second combo list (index 1)
		List<WebElement> nextJourneyFiltered = nextJourneyList.findElements(By.tagName("li"));
		List<String> nextJourneyFilteredTexts = new java.util.ArrayList<>();
		for (WebElement it : nextJourneyFiltered) nextJourneyFilteredTexts.add(it.getText());
		List<String> nextJourneyExpected = java.util.Arrays.asList(
			"JORNEY 24",
			"JORNEY 124", 
			"JORNEY 224",
			"JORNEY 240",
			"JORNEY 241"
		);
		assertEquals(nextJourneyExpected, nextJourneyFilteredTexts);

		// Select JORNEY 241 from nextJourney suggestions
		for (WebElement opt : nextJourneyFiltered) {
			if ("JORNEY 241".equals(opt.getText())) { opt.click(); break; }
		}
		// Ensure nextJourney input shows the chosen value
		assertEquals("JORNEY 241", nextJourneyEditor.findElement(By.className("ui-autocomplete-input")).getAttribute("value"));

		setValue("name", "YESICA");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");

		// Verify in list view that YESICA, JORNEY 224 (lastJourney) and JORNEY 241 (nextJourney) appear
		assertValueInList(0, 0, "YESICA");
		assertValueInList(0, 1, "JORNEY 224");
		assertValueInList(0, 3, "JORNEY 241");

		// Open first row detail and verify both lastJourney and nextJourney values, and that combo has 0 loaded items
		start = System.currentTimeMillis();
		execute("List.viewDetail", "row=0");
		elapsed = System.currentTimeMillis() - start;
		assertTrue("List.viewDetail take less than 6 seconds, but took " + elapsed + " ms", elapsed < 6000);

		WebElement lastJourneyTextField = getDescriptionsListTextField("lastJourney");
		assertEquals("JORNEY 224", lastJourneyTextField.getAttribute("value"));
		WebElement nextJourneyTextField = getDescriptionsListTextField("nextJourney");
		assertEquals("JORNEY 241", nextJourneyTextField.getAttribute("value"));
		WebElement listAfterDetail = getDriver().findElement(By.id(getListId(0)));
		assertFalse(listAfterDetail.isDisplayed());
		assertEquals(0, listAfterDetail.findElements(By.tagName("li")).size());

		// Reset data
		execute("CRUD.delete");
		assertNoErrors();
	}

    // --- Helpers ---
    private void hoverLikeRealMouse(WebElement element) throws InterruptedException {
        // Position pointer with Actions, then dispatch native mouse events with coordinates
        new Actions(getDriver()).moveToElement(element).perform();
        Thread.sleep(150);
        ((JavascriptExecutor) getDriver()).executeScript(
            "(function(el){\n" +
            "  const r = el.getBoundingClientRect();\n" +
            "  const o = {bubbles:true, cancelable:true, view:window, clientX:r.left+r.width/2, clientY:r.top+r.height/2};\n" +
            "  ['mouseover','mouseenter','mousemove'].forEach(t => el.dispatchEvent(new MouseEvent(t, o)));\n" +
            "})(arguments[0]);\n",
            element
        );
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
