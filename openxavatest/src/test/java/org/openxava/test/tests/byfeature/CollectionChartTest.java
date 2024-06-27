package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
 * tmr
 * To test CollectionChart editor related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class CollectionChartTest extends WebDriverTestBase {
	
	@Override
	protected boolean isHeadless() { // tmr Quitar
		return false;
	}
	
	@Override
	protected void tearDown() throws Exception { // tmr Quitar
	}
	
	public void testCollectionChart() throws Exception {
		goModule("CorporationEmployeesChart");
		execute("List.viewDetail", "row=0");
		List<WebElement> rects = getDriver().findElements(By.cssSelector(".xava_collection_chart svg .c3-chart rect"));
		System.out.println("[CollectionChartTest.testCollectionChart] rects.size()=" + rects.size()); // tmr
		for (WebElement rect : rects) {
			System.out.println("[CollectionChartTest.assertOneBarByEachRow] rect=" + rect.getAttribute("outerHTML")); // tmr
		}
		// TMR ME QUEDÉ POR AQUÍ...
	}

	private void assertOneBarByEachRow() throws Exception { // tmr quitar
		int rowCount = getListRowCount(getDriver());
		execute("ListFormat.select", "editor=Charts");
		List<WebElement> rects = getDriver().findElements(By.cssSelector(".ox-chart-data svg .c3-chart rect")); 
		assertEquals(rowCount, rects.size());
		
	}
	
	private void assertMax120Bars() throws Exception { // tmr quitar
		String listInfo = getDriver().findElements(By.cssSelector("td.ox-list-info-detail")).get(1).getText();
		listInfo = listInfo.replaceAll("\\D", "");
        assertTrue(Integer.parseInt(listInfo) > 1000); // No matter the exact count, but it should be more than 1000
		execute("ListFormat.select", "editor=Charts");
		WebElement selectColumn = getDriver().findElement(By.id("ox_openxavatest_Color__columns___0___name"));
		selectColumn.click();
		selectColumn.findElement(By.cssSelector("option[value='number']")).click();
		wait(getDriver());
		List<WebElement> rects = getDriver().findElements(By.cssSelector(".ox-chart-data svg .c3-chart rect"));
		assertEquals(120, rects.size());		
	}
		
}
