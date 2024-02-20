package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * To test EditableValidValues editor related issues with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class EditableValidValuesTest extends WebDriverTestBase {
	
	public void testEditableValidValuesWorksWithCSP() throws Exception {  
		goModule("DeliveryEditableValidValues");
		execute("CRUD.new");
		execute("DeliveryEditableValidValues.addShortcutOptions");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0");
		Select selectElement = new Select (getDriver().findElement(By.xpath("//div[@class='ox-select-editable']//select")));
		selectElement.selectByValue("a");
		WebElement input = getDriver().findElement(By.id("ox_openxavatest_DeliveryEditableValidValues__shortcut"));
		input.click();
		input.clear(); 
		input.sendKeys("NR");
		input.sendKeys(Keys.TAB);
		wait(getDriver());
		String text = getText("remarks");
		assertEquals("No remarks", text);
		execute("Mode.list");
	}
	
	public void testDynamicValidValuesShowingCombo() throws Exception {
		goModule("Delivery");
		execute("CRUD.new");
		execute("Delivery.addShortcutOptions");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0");
        WebElement selectElement = getDriver().findElement(By.id("ox_openxavatest_Delivery__shortcut"));
        assertNotNull("Shortcut not showing as combo", selectElement);
        execute("Mode.list");
	}

}
