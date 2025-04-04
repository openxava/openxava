package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

/**
 * To test @Discussion related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class DiscussionTest extends WebDriverTestBase {
	
	public DiscussionTest(String testName) {
		super(testName);
	}
	
	protected WebDriver createWebDriver(String lang) {
		// If fails with Linux try to add -Dwebdriver.gecko.driver=/snap/bin/geckodriver
		// If fails with Windows 7 try to add -Dwebdriver.gecko.driver=C:\Users\javi\opt\geckodriver34.exe (v34 is the last supported for Windows 7)
		FirefoxOptions options = new FirefoxOptions();
		options.addArguments("--headless");
		return new FirefoxDriver(options);
	}
	
	public void testCommentButtonsNotRespondAfterFocusOnCommentAsFirstActionInDialog() throws Exception { 
		goModule("IncidentActivity"); 
		execute("Reference.createNew", "model=Incident,keyProperty=incident.id");
		wait(getDriver(), By.className("tox-edit-area")); 
		scrollToBottom();
		
		WebElement createButton = getDriver().findElement(By.id("ox_openxavatest_IncidentActivity__NewCreation___saveNew"));
		WebElement cancelCommentButton = getDriver().findElement(By.className("ox-discussion-cancel-button"));
		assertTrue(createButton.isDisplayed());
		assertFalse(cancelCommentButton.isDisplayed());
		
		WebElement commentArea = getDriver().findElements(By.className("tox-edit-area")).get(1);
		commentArea.click();
		assertFalse(createButton.isDisplayed());
		assertTrue(cancelCommentButton.isDisplayed());		
		 
		cancelCommentButton.click();
		Thread.sleep(200); 
		assertTrue(createButton.isDisplayed());
		assertFalse(cancelCommentButton.isDisplayed());		
	}	
	
	private void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
	
}
