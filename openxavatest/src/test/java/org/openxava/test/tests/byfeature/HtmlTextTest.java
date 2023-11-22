package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
 * To test HtmlText related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class HtmlTextTest extends WebDriverTestBase {
	
	private WebDriver driver;
	
	public void setUp() throws Exception {
		//no era headless
		setHeadless(true);
		driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testEditLinkFromDialog() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/IncidentActivity");
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
	}
	
	public void testColor() throws Exception { 
		goTo(driver, "http://localhost:8080/openxavatest/m/Doc");
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
	}

}
