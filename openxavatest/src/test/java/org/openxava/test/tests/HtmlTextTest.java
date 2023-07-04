package org.openxava.test.tests;

import org.openqa.selenium.*;

/**
 * To test HtmlText related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class HtmlTextTest extends WebDriverTestBase {
	
	public void testEditLinkFromDialog() throws Exception {
		WebDriver driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/IncidentActivity"); 
		wait(driver);
		
		WebElement createNewIncidentButton = driver.findElement(By.cssSelector(".ox-image-link .mdi-library-plus"));
		createNewIncidentButton.click();
		wait(driver);
		
		WebElement editLinkButton = driver.findElement(By.cssSelector("[title='Insert/edit link']"));
		editLinkButton.click();
		wait(driver);
		
		WebElement urlField = driver.findElement(By.cssSelector("input[type='url']"));
		urlField.sendKeys("openxava.org");
		assertEquals("openxava.org", urlField.getAttribute("value"));
	
		driver.quit();
	}

}
