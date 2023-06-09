package org.openxava.test.tests;

import org.openqa.selenium.*;

/**
 * To test keyboard related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class KeyboardTest extends WebDriverTestBase {
	
	public void testDefaultAction() throws Exception {
		WebDriver driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/Mammal"); // Mammal has no records so we enter in detail mode
		wait(driver);
		
		WebElement nameField = driver.findElement(By.id("ox_openxavatest_Mammal__name"));
		nameField.sendKeys(Keys.ENTER);
		wait(driver);
		WebElement errors = driver.findElement(By.id("ox_openxavatest_Mammal__errors"));
		assertEquals("Value for Name in Mammal is required", errors.getText());

		driver.quit();
	}

}
