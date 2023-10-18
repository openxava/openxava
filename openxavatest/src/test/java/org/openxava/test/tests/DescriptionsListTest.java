package org.openxava.test.tests;

import org.openqa.selenium.*;

/**
 * To test @DescriptionsList related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class DescriptionsListTest extends WebDriverTestBase {
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
		// tmr setHeadless(true);
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		// tmr driver.quit();
	}
	
	public void testAutocomplete() throws Exception {
		// tmr setFamilyDescription(1, "SOFTWARÉ"); // To test a bug with accents 
		// tmr createWarehouseWithQuote(); // To test a bug with quotes

		goModule("Product2");
		execute("CRUD.new");
		
		driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_warehouse")); // Warehouse combo must be to test the "quotes" bug
		
		WebElement familyList = driver.findElement(By.id("ui-id-1"));
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.findElements(By.tagName("li")).size());
		WebElement familyEditor = driver.findElement(By.id("ox_openxavatest_Product2__reference_editor_family"));
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
		familyTextField.clear(); 
		assertEquals("", familyTextField.getAttribute("value")); 
		familyTextField.sendKeys("ware"); // TMR ME QUEDÉ POR AQUÍ: PIERDE EL FOCO
		/*
		assertEquals("ware", familyTextField.getAttribute("value"));
		Thread.sleep(700); 
		assertTrue(familyList.isDisplayed()); 
		assertFalse(openFamilyListIcon.isDisplayed());
		assertTrue(closeFamilyListIcon.isDisplayed());

		List<WebElement> familyListChildren = familyList.findElements(By.tagName("li")); 
		assertEquals(2, familyListChildren.size()); 
		assertEquals("SOFTWARÉ", familyListChildren.get(0).getText()); 
		assertEquals("HARDWARE", familyListChildren.get(1).getText());
		((HtmlElement) familyList.getFirstChild()).click(); // SOFTWARE
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		HtmlElement subfamilyEditor = getHtmlPage().getHtmlElementById("ox_openxavatest_Product2__reference_editor_subfamily");
		HtmlElement openSubfamilyListIcon = subfamilyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		openSubfamilyListIcon.click();
		HtmlElement subfamilyList = getHtmlPage().getHtmlElementById("ui-id-9");
		assertTrue(subfamilyList.isDisplayed());
		assertEquals(3, subfamilyList.getChildElementCount());
		assertEquals("DESARROLLO", subfamilyList.getFirstChild().asNormalizedText()); // TMR FALLA
		assertEquals("SISTEMA", subfamilyList.getLastChild().asNormalizedText());	
		
		((HtmlElement) subfamilyList.getFirstChild()).click(); // DESARROLLO
		HtmlInput subfamilyTextField = subfamilyEditor.getOneHtmlElementByAttribute("input", "class", "xava_select editor ui-autocomplete-input");
		assertEquals("DESARROLLO", subfamilyTextField.getValue()); 

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
		assertValueInList(0, "family.description", "SOFTWARÉ"); 
		assertValueInList(0, "subfamily.description", "DESARROLLO");
		
		execute("List.viewDetail", "row=0");
		assertValue("number", "66");
		assertValue("family.number", "1");
		assertDescriptionValue("family.number", "SOFTWARÉ"); 
		familyTextField =  getDescriptionsListTextField("family");
		assertEquals("SOFTWARÉ", familyTextField.getValue()); 
		assertValue("subfamily.number", "1");
		assertDescriptionValue("subfamily.number", "DESARROLLO");
		subfamilyTextField = getDescriptionsListTextField("subfamily");
		assertEquals("DESARROLLO", subfamilyTextField.getValue()); 
		execute("CRUD.delete");
		assertNoErrors();
		
		execute("CRUD.new");
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.setValue(""); 
		familyTextField.type("ware");
		assertEquals("ware", familyTextField.getValue()); 
		familyTextField.blur();
		assertEquals("", familyTextField.getValue()); 
		
		execute("CRUD.new");
		familyList = getHtmlPage().getHtmlElementById("ui-id-27"); 
		assertFalse(familyList.isDisplayed());
		assertEquals(0, familyList.getChildElementCount());
		familyTextField = getDescriptionsListTextField("family");
		familyTextField.setValue(""); 
		familyTextField.type(" \b");
		Thread.sleep(700); 
		assertTrue(familyList.isDisplayed()); 
		assertEquals(3, familyList.getChildElementCount());
				
		familyEditor = getHtmlPage().getHtmlElementById("ox_openxavatest_Product2__reference_editor_family");
		openFamilyListIcon = familyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		closeFamilyListIcon = familyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-up");		
		subfamilyEditor = getHtmlPage().getHtmlElementById("ox_openxavatest_Product2__reference_editor_subfamily");
		openSubfamilyListIcon = subfamilyEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		closeFamilyListIcon.click();
		openFamilyListIcon.click();
		assertTrue(familyList.isDisplayed());
		openSubfamilyListIcon.click();
		assertFalse(familyList.isDisplayed());
		
		// tmr setFamilyDescription(1, "SOFTWARE"); 
		// tmr removeWarehouseWithQuote(); 
		*/
	}
	
	private void execute(String action) throws Exception { // tmr Duplicado con ListTest
		execute(driver, module, action);
	}
	
	private void goModule(String module) throws Exception{ // tmr Duplicado con ListTest
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
	
}
