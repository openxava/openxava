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
		
	protected String getLang() { 
		return "es"; // So we can test that translations work
	}
	
	public void testCollectionChart() throws Exception {
		goModule("CorporationEmployeesChart");
		assertCollectionBar(2, "MARTIN", "ANA", "MIGUEL");
		
		goModule("CorporationEmployeesRefinedChart");
		assertCollectionBar(1, "MARTIN FIERRO", "ANA LOPEZ", "MIGUEL SMITH HERRERO");
		
		WebElement salaryLegend = getDriver().findElement(By.cssSelector(".xava_collection_chart .c3-legend-item-Salario"));
		assertEquals("Salario", salaryLegend.getText());
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
