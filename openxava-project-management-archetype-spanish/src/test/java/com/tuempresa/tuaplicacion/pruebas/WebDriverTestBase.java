package com.tuempresa.tuaplicacion.pruebas;

import java.time.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;
import org.openxava.web.*;

import junit.framework.*;

/**
 * Base class to test using Selenium WebDriver.
 */
abstract public class WebDriverTestBase extends TestCase {
	
	private boolean headless = true;
	private String module;
	private WebDriver driver;
	
	protected String getLang() { 
		return "en";
	}
	
	protected void setUp() throws Exception {
		driver = createWebDriver(getLang()); 
	}

	protected void tearDown() throws Exception {
		driver.quit();
	}
	
	protected WebDriver createWebDriver(String lang) {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=" + lang);
	    options.addArguments("--lang=" + lang); 
	    options.addArguments("--disable-search-engine-choice-screen");
	    options.addArguments("--window-size=900,850");
	    if (isHeadless()) {
		    options.addArguments("--headless"); 
		    options.addArguments("--disable-gpu");
	    }
		return new ChromeDriver(options);
	}
	
	protected void changeLanguage(String lang) throws Exception {
		tearDown();
		driver = createWebDriver(lang);
	}
	
	protected String getModule() {
		return this.module;
	}
	
	protected WebDriver getDriver() {
		return this.driver;
	}
		
	protected void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	protected void wait(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(300)); // 100 is too short, at least 300
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("xava_loading")));
		}
		catch (Exception ex) {
		}
		wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("xava_loading"))); //sometimes this code cause error in calendar, reset db and run again test
	}
	
	protected void wait(WebDriver driver, By expectedElement) { 
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3)); 
        wait.until(ExpectedConditions.visibilityOfElementLocated(expectedElement));		
	}
	
    protected void wait(WebDriver driver, WebElement expectedElement, boolean visible) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        if (visible) {
        	wait.until(ExpectedConditions.visibilityOf(expectedElement));
        } else {
        	wait.until(ExpectedConditions.invisibilityOf(expectedElement));
        }
    }

	protected void acceptInDialogJS(WebDriver driver) throws Exception {
		//use it after verifying that the test works well. 
		//it helps to avoid errors when starting the browser with the module.
		//can use too for click Accept case.
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			alert.dismiss();
			wait(driver);
		} catch(NoAlertPresentException e) {
		}
	}
	
	protected void execute(String action) throws Exception {
		executeWithoutArg(this.module, action);
	}
    
	protected void execute(String action, String arguments) throws Exception {
		executeWithArg(module, action, arguments);
	}
	
	protected void executeWithoutArg(String moduleName, String action) throws Exception {
		String[] actionS = action.split("\\.");
		WebElement button = driver.findElement
				(By.id("ox_tuaplicacion_" + moduleName + "__" + actionS[0] + "___" + actionS[1]));
		button.click();
		acceptInDialogJS(driver);
		wait(driver);
		//if back to CalendarView, need add another wait after this method
		//waitCalendarEvent(driver);
	}
	
	protected void executeWithArg(String moduleName, String action, String arguments) throws Exception { 
		try { 
			WebElement button = driver.findElement(By.cssSelector("a[data-action='" + action + "'][data-argv='" + arguments + "']"));
			try {
				button.click();
			}
			catch (ElementClickInterceptedException ex) {
				JavascriptExecutor executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", button);
			}
			wait(driver);
		}
		catch (NoSuchElementException ex) {
			try {
				WebElement button = driver.findElement(By.id(action));
				button.click();
				wait(driver);
			}
			catch (NoSuchElementException ex2) {
				fail(XavaResources.getString("button_not_found", action));
			}
		}
	}
	
	protected void goModule(String module) throws Exception {
		this.module = module;
		driver.get("http://localhost:8080/tuaplicacion/");
		wait(driver);
		try {
			WebElement menuButton = driver.findElement(By.id("menu_button"));
			menuButton.click();
			wait(driver);
		}
		catch (NoSuchElementException ex) {
		}
		WebElement moduleLink = driver.findElement(By.id(module));
		moduleLink.click();
		wait(driver);
	}
	
	protected boolean isHeadless() {
		return headless;
	}
	
	protected void setHeadless(boolean headless) {
		this.headless = headless;
	}
	
	protected void assertDescriptionValue(String name, String value) {
		assertEquals(XavaResources.getString("unexpected_value", name), value, getDescriptionValue(name));		
	}
	
	protected String getDescriptionValue(String name) {		
		WebElement input = driver.findElement(By.name(Ids.decorate("tuaplicacion", module, name + "__DESCRIPTION__")));
		return input.getAttribute("value");
	}
	
	protected void assertValue(String name, String value) {
		assertEquals(XavaResources.getString("unexpected_value", name), value, getValue(name));		
	}
	
	protected String getValue(String name) {
		WebElement input = driver.findElement(By.id(Ids.decorate("tuaplicacion", module, name)));
		return input.getAttribute("value");
	}
	
	protected String getText(String name) {
		WebElement input = driver.findElement(By.id(Ids.decorate("tuaplicacion", module, name)));
		return input.getText();
	}
	
	protected void setValue(String name, String value) throws Exception {
		WebElement input = driver.findElement(By.id(Ids.decorate("tuaplicacion", module, name)));
		input.clear();
		input.sendKeys(value);	
	}
	
	protected void assertNoErrors() {
		WebElement errors = driver.findElement(By.id("ox_tuaplicacion_" + module + "__errors"));
		assertEquals(XavaResources.getString("unexpected_messages", "Errors"), "", errors.getText());
	}
	
	protected void clearListCondition() throws Exception{
		driver.findElement(By.id("ox_tuaplicacion_" +  module + "__xava_clear_condition")).click();
		wait(driver);
	}

	protected void setConditionValue(String value, int column) {
		driver.findElement(By.id("ox_tuaplicacion_" + module + "__conditionValue___" + String.valueOf(column))).sendKeys(value); 		
	}
	
	protected void setConditionComparator(String value, int column) throws Exception {
		WebElement selectCondition = driver.findElement(By.id("ox_tuaplicacion_" + module + "__conditionComparator___" + String.valueOf(column)));
		Select select = new Select(selectCondition);
		boolean b = select.getFirstSelectedOption().getText().equals(value);
		select.selectByVisibleText(value);
		wait(driver);
		if (b) execute("List.filter");
	}
	
	protected void resetModule(WebDriver driver) throws Exception {
		driver.quit();
		WebDriver newDriver = createWebDriver("en");
		this.driver = newDriver;
		goModule(module);
	}
	
	protected void assertMessage(String expectedMessage, Log log) { 
		Collection<WebElement> messages = getDriver().findElements(By.cssSelector(".ox-messages .ox-message-box"));
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
	
	protected void assertMessage(String expectedMessage) {
		List<WebElement> messages = getDriver().findElements(By.className("ox-message-box"));
		assertEquals(expectedMessage, messages.get(messages.size()-1).getText());
	}
	
	protected void assertError(String expectedError) { 
		List<WebElement> errors = getDriver().findElements(By.cssSelector(".ox-errors .ox-message-box"));
		assertEquals(expectedError, errors.get(errors.size()-1).getText());
	}
		
	protected void assertNoMessages() {
		List<WebElement> messages = getDriver().findElements(By.className("ox-message-box"));
		assertTrue(messages.isEmpty());
	}
	
	protected void login(String user, String password) throws Exception {
		WebElement userField = driver.findElement(By.id("ox_tuaplicacion_SignIn__user"));
		userField.sendKeys(user);
		WebElement passwordField = driver.findElement(By.id("ox_tuaplicacion_SignIn__password"));
		passwordField.sendKeys(password);
		
		WebElement loginButton = driver.findElement(By.id("ox_tuaplicacion_SignIn__SignIn___signIn"));
		loginButton.click();
		wait(driver);
	}

	protected void logout() throws Exception { 
		WebElement loginLink = driver.findElement(By.cssSelector("#sign_in_out .sign-in"));
		loginLink.click();
		wait(driver);
	}
	
}
