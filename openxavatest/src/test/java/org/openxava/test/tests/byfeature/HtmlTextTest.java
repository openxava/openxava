package org.openxava.test.tests.byfeature;

import java.util.*;
import org.openqa.selenium.*;
/**
 * To test HtmlText related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class HtmlTextTest extends WebDriverTestBase {
	
	public HtmlTextTest(String testName) {
		super(testName);
	}
	
	public void testEditLinkFromDialog() throws Exception {
		goModule("IncidentActivity");
		WebElement createNewIncidentButton = getDriver().findElement(By.cssSelector(".ox-image-link .mdi-plus-box-multiple"));
		createNewIncidentButton.click();
		wait(getDriver());
		
		wait(getDriver(), By.cssSelector("[title='Insert/edit link']")); 
		WebElement editLinkButton = getDriver().findElement(By.cssSelector("[title='Insert/edit link']"));
		editLinkButton.click();
		wait(getDriver());
		
		WebElement urlField = getDriver().findElement(By.cssSelector("input[type='url']"));
		urlField.sendKeys("openxava.org");
		assertEquals("openxava.org", urlField.getAttribute("value"));
	}
	
	public void testColor() throws Exception { 
		goModule("Doc");
		List<WebElement> editIcons = getDriver().findElements(By.cssSelector(".ox-image-link .mdi-border-color"));
		editIcons.get(1).click();
		wait(getDriver());
		
		WebElement titleField = getDriver().findElement(By.id("ox_openxavatest_Doc__title"));
		assertEquals("VERDE", titleField.getAttribute("value"));
		
        wait(getDriver(), By.id("ox_openxavatest_Doc__content_ifr"));
        getDriver().switchTo().frame("ox_openxavatest_Doc__content_ifr");
		wait(getDriver(), By.cssSelector("#tinymce p span"));
		WebElement coloredText = getDriver().findElement(By.cssSelector("#tinymce p span"));
		assertEquals("verde", coloredText.getText());
		assertEquals("rgba(22, 145, 121, 1)", coloredText.getCssValue("color"));
	}

}
