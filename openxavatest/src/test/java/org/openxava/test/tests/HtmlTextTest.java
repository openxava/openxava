package org.openxava.test.tests;

import java.util.*;

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
		
		wait(driver, By.cssSelector("[title='Insert/edit link']")); 
		WebElement editLinkButton = driver.findElement(By.cssSelector("[title='Insert/edit link']"));
		editLinkButton.click();
		wait(driver);
		
		WebElement urlField = driver.findElement(By.cssSelector("input[type='url']"));
		urlField.sendKeys("openxava.org");
		assertEquals("openxava.org", urlField.getAttribute("value"));
	
		driver.quit();
	}
	
	public void testColor() throws Exception { 
		WebDriver driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/Doc"); 
		wait(driver);
		
		List<WebElement> editIcons = driver.findElements(By.cssSelector(".ox-image-link .mdi-border-color"));
		editIcons.get(1).click();
		wait(driver);
		
		WebElement titleField = driver.findElement(By.id("ox_openxavatest_Doc__title"));
		assertEquals("VERDE", titleField.getAttribute("value"));
		
        wait(driver, By.id("ox_openxavatest_Doc__content_ifr"));
		driver.switchTo().frame("ox_openxavatest_Doc__content_ifr");
		wait(driver, By.cssSelector("#tinymce p span"));
		WebElement coloredText = driver.findElement(By.cssSelector("#tinymce p span"));
		assertEquals("verde", coloredText.getText());
		assertEquals("rgba(22, 145, 121, 1)", coloredText.getCssValue("color"));

		driver.quit();
	}
	

}
