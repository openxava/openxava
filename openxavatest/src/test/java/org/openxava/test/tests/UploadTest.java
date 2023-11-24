package org.openxava.test.tests;

import java.time.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;

/**
 * tmr
 * To test upload editor related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class UploadTest extends WebDriverTestBase {
	
	private static Log log = LogFactory.getLog(UploadTest.class);
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
		// tmr setHeadless(true); 
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		// tmr driver.quit();
	}
	
	public void testOnChange() throws Exception {
		goModule("CustomerFramesOnSameRow");
		execute("List.viewDetail", "row=1");
		uploadPhoto();		
		assertMessage("Photo changed"); // tmr
		
		execute("CRUD.save");
		execute("Navigation.previous");
		execute("Navigation.next");
		
		// Assert of there is a photo
		assertFalse(driver.findElements(By.className("filepond--image-preview-wrapper")).isEmpty());
		
		// TMR ME QUEDÉ POR AQUÍ, EL TEST DE ARRIBA FUNCIONA. 
		// TMR CONTINUARLO HASTA QUE SEA COMO EL DE UploadText.txt
		// TMR HE DE PASAR MANUALMENTE UploadText.txt
		// TMR HE DE QUITAR LO QUE SE TESETEA A QUÍ DE UploadText.txt
		// TMR Y POR SUPUESTO PASAR LA SUITE
	}

	private void uploadPhoto() throws Exception {
		WebElement input = driver.findElement(By.className("filepond--browser"));
		input.sendKeys(System.getProperty("user.dir") + "/test-images/foto_javier.jpg"); 
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        wait.until(ExpectedConditions.textToBe(By.className("filepond--file-status-main"), "Upload complete"));		
		wait(driver);
	}
	
	private void assertMessage(String expectedMessage) { // tmr Indicar a Chungyen que lo mueva arriba
		Collection<WebElement> messages = driver.findElements(By.cssSelector(".ox-messages .ox-message-box"));
		StringBuffer producedMessages = new StringBuffer();
		for (WebElement message: messages) {
			String messageText = message.getText().trim();
			if (messageText.equals(expectedMessage)) return;
			producedMessages.append(messageText);
			producedMessages.append('\n');
		}
		log.error(XavaResources.getString("messages_produced", producedMessages));
		fail(XavaResources.getString("message_not_found", expectedMessage)); 
	}
	
	private void execute(String action) throws Exception { // Duplicated with DescriptionsListTest, refactoring pending
		execute(driver, module, action);
	}

	private void execute(String action, String arguments) throws Exception { // Duplicated with ListTest, refactoring pending 
		execute(driver, module, action, arguments);
	}

	private void goModule(String module) throws Exception{ // Duplicated with ListTest, refactoring pending 
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
			
}
