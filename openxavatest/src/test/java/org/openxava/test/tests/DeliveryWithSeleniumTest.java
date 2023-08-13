package org.openxava.test.tests;

import org.openqa.selenium.*;

public class DeliveryWithSeleniumTest extends WebDriverTestBase {
	
	private WebDriver driver;
	
	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void testNavigation() throws Exception {
		//forTestEditableValidValuesWorksWithCSP();
		forTestDynamicValidValuesShowingCombo();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void forTestEditableValidValuesWorksWithCSP() throws Exception {  
		moveToListView(driver);
	}
	
	public void forTestDynamicValidValuesShowingCombo() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Delivery");
		wait(driver);
		acceptInDialogJS(driver);
		moveToListView(driver);
		createFromListView(driver, "Delivery");
		clickOnButtonWithId(driver, "ox_openxavatest_Delivery__Delivery___addShortcutOptions");
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_Delivery__label_xava_view_section1_sectionName");
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_Delivery__label_xava_view_section0_sectionName");
        WebElement selectElement = driver.findElement(By.id("ox_openxavatest_Delivery__shortcut"));
        Assert.assertNotNull("Shortcut not showing as combo", selectElement);
        goToListFromDetailView(driver, "Delivery");
	}
	
	
}
