package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * To test keyboard related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class KeyboardTest extends WebDriverTestBase {
	
	private WebDriver driver;
	
	public void setUp() throws Exception {
		setHeadless(true);
		driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testDefaultAction() throws Exception {
		goModule(driver, "Mammal"); // Mammal has no records so we enter in detail mode
		WebElement nameField = driver.findElement(By.id("ox_openxavatest_Mammal__name"));
		nameField.sendKeys(Keys.ENTER);
		wait(driver);
		WebElement errors = driver.findElement(By.id("ox_openxavatest_Mammal__errors"));
		assertEquals("Value for Name in Mammal is required", errors.getText());
	}

}
