package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
 * To test charts related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ChartsTest extends WebDriverTestBase {
	
	private WebDriver driver;

	public void setUp() throws Exception {
		setHeadless(true); 
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testCharts() throws Exception {
		goModule(driver, "Invoice");
		assertOneBarByEachRow();
		moveToListView(driver);
		
		goModule(driver, "Color");
		assertMax120Bars();
	}

	private void assertOneBarByEachRow() throws Exception {
		int rowCount = getListRowCount(driver);
		execute(driver, "ListFormat.select", "editor=Charts");
		List<WebElement> rects = driver.findElements(By.cssSelector(".ox-chart-data svg .c3-chart rect")); 
		assertEquals(rowCount, rects.size());
	}
	
	private void assertMax120Bars() throws Exception {
		String listInfo = driver.findElements(By.cssSelector("td.ox-list-info-detail")).get(1).getText();
		listInfo = listInfo.replaceAll("\\D", "");
        assertTrue(Integer.parseInt(listInfo) > 1000); // No matter the exact count, but it should be more than 1000
		execute(driver, "ListFormat.select", "editor=Charts");
		WebElement selectColumn = driver.findElement(By.id("ox_openxavatest_Color__columns___0___name"));
		selectColumn.click();
		selectColumn.findElement(By.cssSelector("option[value='number']")).click();
		wait(driver);
		List<WebElement> rects = driver.findElements(By.cssSelector(".ox-chart-data svg .c3-chart rect"));
		assertEquals(120, rects.size());		
	}
	

		
}
