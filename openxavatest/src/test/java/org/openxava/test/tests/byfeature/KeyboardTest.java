package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * To test keyboard related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class KeyboardTest extends WebDriverTestBase {
	
	public KeyboardTest(String testName) {
		super(testName);
	}
	
	public void testDefaultAction() throws Exception {
		goModule("Mammal"); // Mammal has no records so we enter in detail mode
		WebElement nameField = getDriver().findElement(By.id("ox_openxavatest_Mammal__name"));
		nameField.sendKeys(Keys.ENTER);
		wait(getDriver());
		WebElement errors = getDriver().findElement(By.id("ox_openxavatest_Mammal__errors"));
		assertEquals("Value for Name in Mammal is required", errors.getText());
	}

}
