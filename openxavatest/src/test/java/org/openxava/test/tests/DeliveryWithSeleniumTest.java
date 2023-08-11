package org.openxava.test.tests;

import org.openqa.selenium.*;

public class DeliveryWithSeleniumTest extends WebDriverTestBase {
	
	private WebDriver driver;
	
	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void testNavigation() throws Exception {
		forTestEditableValidValuesWorksWithCSP();
	}

	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void forTestEditableValidValuesWorksWithCSP() throws Exception {  
		moveToListView(driver);
	}
	
}
