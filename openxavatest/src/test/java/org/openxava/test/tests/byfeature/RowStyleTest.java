package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

public class RowStyleTest extends WebDriverTestBase {

	public void testMultipleRowStyle() throws Exception {
		goModule("Customer");
		WebElement element = getDriver().findElement(By.cssSelector(".ox-list-pair.row-highlight.italic-label"));
	}
	
}
