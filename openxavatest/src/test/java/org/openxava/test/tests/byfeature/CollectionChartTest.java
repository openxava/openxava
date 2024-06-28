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
		assertCollectionBar(2, "MARTIN", "ANA", "MIGUEL");
	}

	private void assertCollectionBar(int barCount, String label1, String label2, String label3) throws Exception {
		execute("List.viewDetail", "row=0");
		List<WebElement> bars = getDriver().findElements(By.cssSelector(".xava_collection_chart .c3-chart-bars .c3-chart-bar"));
		assertEquals(barCount, bars.size());		
		
		List<WebElement> labels = getDriver().findElements(By.cssSelector(".xava_collection_chart svg .c3-axis.c3-axis-x .tick tspan"));
		assertEquals(3, labels.size());
		assertEquals(label1, labels.get(0).getText());
		assertEquals(label2, labels.get(1).getText());
		assertEquals(label3, labels.get(2).getText());
	}
		
}
