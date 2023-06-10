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
	
	protected void moveToListView(WebDriver driver) throws InterruptedException {
		WebElement tabList = driver.findElement(By.cssSelector(".mdi.mdi-table-large"));
		WebElement tabListParent = tabList.findElement(By.xpath(".."));
		String title = tabListParent.getAttribute("class");
		if (!(title != null && title.equals("ox-selected-list-format"))) {
			tabList.click();
		}	
	}
	
	private void moveToCalendarView(WebDriver driver) throws InterruptedException {
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		WebElement tabCalendarParent = tabCalendar.findElement(By.xpath(".."));
		String title = tabCalendarParent.getAttribute("class");
		if (!(title != null && title.equals("ox-selected-list-format"))) {
			tabCalendar.click();
		}
	}

}
