package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * To test cards related issues with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class CardsTest extends WebDriverTestBase {
	
	public void testCardsStyle() throws Exception {
		goModule("Customer");
		execute("ListFormat.select", "editor=Cards");
		WebElement element = getDriver().findElement(By.cssSelector("div[data-action='List.viewDetail'][data-row='0']"));
		assertTrue(element.getAttribute("class").contains("row-highlight") &&
				element.getAttribute("class").contains("italic-label"));
		execute("ListFormat.select", "editor=List");
	}

		
}
