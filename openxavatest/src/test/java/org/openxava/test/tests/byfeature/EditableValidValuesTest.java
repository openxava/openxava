package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * To test EditableValidValues editor related issues with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class EditableValidValuesTest extends WebDriverTestBase {
	
	private WebDriver driver;
	private String module;
	
	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testEditableValidValuesWorksWithCSP() throws Exception {  
		goModule(driver, "DeliveryEditableValidValues");
		execute(driver, "DeliveryEditableValidValues", "CRUD.new");
		clickOnButtonWithId(driver, "ox_openxavatest_DeliveryEditableValidValues__DeliveryEditableValidValues___addShortcutOptions");
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_DeliveryEditableValidValues__label_xava_view_section1_sectionName");
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_DeliveryEditableValidValues__label_xava_view_section0_sectionName");
		Select selectElement = new Select (driver.findElement(By.xpath("//div[@class='ox-select-editable']//select")));
		selectElement.selectByValue("a");
		WebElement input = driver.findElement(By.id("ox_openxavatest_DeliveryEditableValidValues__shortcut"));
		
		input.click();
		input.clear(); 
		input.sendKeys("NR");
		input.sendKeys(Keys.TAB);
		wait(driver);
		WebElement textArea = driver.findElement(By.id("ox_openxavatest_DeliveryEditableValidValues__remarks"));
		String text = textArea.getAttribute("textContent");
		assertEquals("No remarks", text);
		execute(driver, "DeliveryEditableValidValues", "Mode.list");
	}
	
	public void testDynamicValidValuesShowingCombo() throws Exception {
		goModule(driver, "Delivery");
		execute(driver, "Delivery", "CRUD.new");
		clickOnButtonWithId(driver, "ox_openxavatest_Delivery__Delivery___addShortcutOptions");
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_Delivery__label_xava_view_section1_sectionName");
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_Delivery__label_xava_view_section0_sectionName");
        WebElement selectElement = driver.findElement(By.id("ox_openxavatest_Delivery__shortcut"));
        assertNotNull("Shortcut not showing as combo", selectElement);
        execute(driver, "Delivery", "Mode.list");
	}

}
