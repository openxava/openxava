package org.openxava.test.tests.byfeature;

import java.time.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import junit.framework.*;

/**
 * Base class to test using Selenium WebDriver
 * 
 * @author Javier Paniza
 */
abstract public class WebDriverTestBase extends TestCase {
	
	private boolean headless = false;
	protected String module;
	
	protected WebDriver createWebDriver() {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=en");
	    options.addArguments("--lang=en"); 
	    
	    //Sometime needs set path and update manually chromedriver when chrome just been updated
	    //https://googlechromelabs.github.io/chrome-for-testing/
	    //System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
	    if (isHeadless()) {
		    options.addArguments("--headless"); 
		    options.addArguments("--disable-gpu"); 	    	
	    }
		return new ChromeDriver(options);
	}
	
	protected WebDriver createWebDriver(String lang) {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=" + lang);

		return new ChromeDriver(options);
	}
		
	protected void wait(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("xava_loading")));
		}
		catch (Exception ex) {
		}
		wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("xava_loading"))); 
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
	
	protected void execute(WebDriver driver, String action) throws Exception {
		executeWithoutArg(driver, this.module, action);
	}
    
	protected void execute(WebDriver driver, String action, String arguments) throws Exception {
		executeWithArg(driver, module, action, arguments);
	}
	
	protected void executeWithoutArg(WebDriver driver, String moduleName, String action) throws Exception {
		String[] actionS = action.split("\\.");
		WebElement button = driver.findElement
				(By.id("ox_openxavatest_" + moduleName + "__" + actionS[0] + "___" + actionS[1]));
		button.click();
		acceptInDialogJS(driver);
		wait(driver);
		//if back to CalendarView, need add another wait after this method
		//waitCalendarEvent(driver);
	}
	
	protected void executeWithArg(WebDriver driver, String moduleName, String action, String arguments) throws Exception { 
		try { 
			WebElement button = driver.findElement(By.cssSelector("a[data-action='" + action + "'][data-argv='" + arguments + "']"));
			button.click();
			acceptInDialogJS(driver);
			wait(driver);
		}
		catch (NoSuchElementException ex) {
			if (arguments.startsWith(",")) throw ex;
			executeWithArg(driver, moduleName, action, "," + arguments);
		}
	}
	
	protected void clickOnButtonWithId(WebDriver driver, String id) throws Exception {
		WebElement button = driver.findElement(By.id(id));
		button.click();
		wait(driver);
	}

	protected void clickOnSectionWithChildSpanId(WebDriver driver, String id) throws Exception {
		WebElement span = driver.findElement(By.id(id));
		WebElement a = span.findElement(By.xpath(".."));
		a.click();
		wait(driver);
	}
	
	protected void insertValueToInput(WebDriver driver, String id, String value, boolean delete) {
		WebElement inputElement = driver.findElement(By.id(id));
		if (delete == true) inputElement.clear();
        inputElement.sendKeys(value);
	}

	protected boolean isHeadless() {
		return headless;
	}

	protected void setHeadless(boolean headless) {
		this.headless = headless;
	}
	
	protected void blur(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].blur();", element);
    }
	
	protected void goModule(WebDriver driver, String module) throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		wait(driver);
		acceptInDialogJS(driver);
		this.module = module;
	}
	
}
