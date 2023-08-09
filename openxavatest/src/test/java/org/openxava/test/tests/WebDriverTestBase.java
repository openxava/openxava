package org.openxava.test.tests;

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
	
	protected WebDriver createWebDriver() {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=en");
	    //Sometime needs set path and update manually chromedriver when chrome just been updated
	    //https://googlechromelabs.github.io/chrome-for-testing/
	    //System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");  
	    
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

	protected void waitCalendarEvent(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000));
		try {
			wait.until(ExpectedConditions.jsReturnsValue("return calendarEditor.requesting === false;"));
			Thread.sleep(500);
		} catch (Exception ex) {
		}
	}
	
	protected void moveToListView(WebDriver driver) throws Exception {
		WebElement tabList = driver.findElement(By.cssSelector(".mdi.mdi-table-large"));
		WebElement tabListParent = tabList.findElement(By.xpath(".."));
		String title = tabListParent.getAttribute("class");
		if (!(title != null && title.equals("ox-selected-list-format"))) {
			tabList.click();
		}
		wait(driver);
	}
	
	protected void moveToCalendarView(WebDriver driver) throws Exception {
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		WebElement tabCalendarParent = tabCalendar.findElement(By.xpath(".."));
		String title = tabCalendarParent.getAttribute("class");
		if (!(title != null && title.equals("ox-selected-list-format"))) {
			tabCalendar.click();
		}
		wait(driver);
		waitCalendarEvent(driver);
	}
	
	protected void acceptInDialogJS(WebDriver driver) throws Exception {
		//use it after verifying that the test works well. 
		//it helps to avoid errors when starting the browser with the module.
		//can use too for click Accept case.
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			wait(driver);
		} catch(NoAlertPresentException e) {
			
		}
	}
	
	protected void goToListFromDetailView(WebDriver driver, String modelName) throws Exception {
		WebElement buttonList = driver.findElement(By.id("ox_openxavatest_" + modelName + "__Mode___list"));
		buttonList.click();
		wait(driver);
		//if back to CalendarView, need add another wait after this method
		//waitCalendarEvent(driver);
	}
	
	protected void createFromListView(WebDriver driver, String modelName) throws Exception {
		WebElement buttonNew = driver.findElement(By.id("ox_openxavatest_" + modelName + "__CRUD___new"));
		buttonNew.click();
		wait(driver);
	}
	
	protected void saveFromDetailView(WebDriver driver, String modelName) throws Exception {
		WebElement buttonSave = driver.findElement(By.id("ox_openxavatest_" + modelName + "__CRUD___save"));
		buttonSave.click();
		wait(driver);
	}
	
	protected void deleteFromDetailView(WebDriver driver, String modelName) throws Exception {
		WebElement buttonDelete = driver.findElement(By.id("ox_openxavatest_" + modelName + "__CRUD___delete"));
		buttonDelete.click();
		wait(driver);
		acceptInDialogJS(driver);
		wait(driver);
	}

}
