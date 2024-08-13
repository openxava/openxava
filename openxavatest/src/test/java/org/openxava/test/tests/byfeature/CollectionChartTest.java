package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
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
		execute("List.viewDetail", "row=0"); 
		assertCollectionBar(2, "MARTIN", "ANA", "MIGUEL");
		
		goModule("CorporationEmployeesRefinedChart");
		execute("List.viewDetail", "row=0"); 
		assertCollectionBar(1, "MARTIN FIERRO", "ANA LOPEZ", "MIGUEL SMITH HERRERO");
		
		WebElement salaryLegend = getDriver().findElement(By.cssSelector(".xava_collection_chart .c3-legend-item-Salario"));
		assertEquals("Salario", salaryLegend.getText());
		
		goModule("StaffDashboard");
		assertCollectionBar(2, "2020", "2021", "2022", "2023", "2024");
	}
	
	private void assertCollectionBar(int barCount, String ... expectedLabels) throws Exception { 
		
		List<WebElement> bars = getDriver().findElements(By.cssSelector(".xava_collection_chart .c3-chart-bars .c3-chart-bar"));
		assertEquals(barCount, bars.size());		
		
		List<WebElement> labels = getDriver().findElements(By.cssSelector(".xava_collection_chart svg .c3-axis.c3-axis-x .tick tspan"));
		assertEquals(expectedLabels.length, labels.size());
		for (int i=0; i<expectedLabels.length; i++) {
			assertEquals(expectedLabels[i], labels.get(i).getText());
		}
	}
		
}
