package org.openxava.test.tests.byfeature;

import java.time.*;

import org.apache.commons.logging.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * To test upload editor related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class UploadTest extends WebDriverTestBase {
	
	private static Log log = LogFactory.getLog(UploadTest.class);
	
	public void testOnChange() throws Exception {
		goModule("CustomerFramesOnSameRow");
		execute("List.viewDetail", "row=1"); // TMR FALLA
		uploadPhoto();		
		assertMessage("Photo changed", log); 
		
		execute("CRUD.save");
		execute("Navigation.previous");
		assertValue("name", "Javi");
		execute("Navigation.next");
		assertValue("name", "Juanillo");
		
		// Assert of there is a photo
		assertFalse(getDriver().findElements(By.className("filepond--image-preview-wrapper")).isEmpty());
		
		WebElement removeButton = getDriver().findElement(By.className("filepond--action-remove-item"));
		//removeButton.click(); // It works when testing with Selenium with headless=false, and by hand
		JavascriptExecutor executor = (JavascriptExecutor) getDriver();
		executor.executeScript("arguments[0].click();", removeButton);
		acceptInDialogJS(getDriver());
		wait(getDriver());
		Thread.sleep(100);
		assertMessage("Photo changed", log);
		
		execute("CRUD.save");
		execute("Navigation.previous");
		assertValue("name", "Javi");
		execute("Navigation.next");
		assertValue("name", "Juanillo");
		
		// Assert of there is no photo
		assertTrue(getDriver().findElements(By.className("filepond--image-preview-wrapper")).isEmpty());		
	}
	
	private void uploadPhoto() throws Exception {
		WebElement input = getDriver().findElement(By.className("filepond--browser"));
		input.sendKeys(System.getProperty("user.dir") + "/test-images/foto_javier.jpg"); 
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10)); 
        wait.until(ExpectedConditions.textToBe(By.className("filepond--file-status-main"), "Upload complete"));		
		wait(getDriver());
	}
			
}
