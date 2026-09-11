package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

/**
 * To test @ElementCollection related issues with Selenium.
 *
 * @author Javier Paniza
 */

public class ElementCollectionTest extends WebDriverTestBase {
	
	public ElementCollectionTest(String testName) {
		super(testName);
	}
	
	public void testFocus() throws Exception { 
		goModule("Quote"); 
		execute("CRUD.new");
		setValueInCollection("details", 0, "product.number", "1\t");
		wait(getDriver());
		WebElement focusedElement = getDriver().switchTo().activeElement();
		assertEquals("ox_openxavatest_Quote__details___0___unitPrice", focusedElement.getAttribute("name"));
	}

	private void setValueInCollection(String collection, int row, String property, String value) throws Exception {
		setValue(collection + "." + row + "." + property, value);
	}
	
	public void testSetLabelIdPerformance() throws Exception { 
		goModule("Grade");
		long ini = System.currentTimeMillis();
		execute("Grade.addCalifications");
		long fn = System.currentTimeMillis();
		assertTrue((fn-ini) < 5000); // More than 9 seconds when failed 
	}
	
	public void testTotalCellsKeepColumnSizeAfterColumnResize() throws Exception {
		resetPreferences();
		goModule("CommercialDocument"); // Using CommercialDocument module directly
		execute("List.viewDetail", "row=0"); // View the first record
		
		// Find the specific element with ID ox_openxavatest_CommercialDocument__details_col4
		WebElement column = getDriver().findElement(By.id("ox_openxavatest_CommercialDocument__details_col4"));
		
		// Find the resize handle within this element
		WebElement resizeHandle = column.findElement(By.className("ui-resizable-handle"));
		
		// Make sure the element is visible
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].scrollIntoView(true);", resizeHandle);
		
		// Find the cell container divs to compare widths: data cell and total cell share the _col4 class
		String columnClass = "ox_openxavatest_CommercialDocument__details_col4";
		WebElement dataCellDiv = getDriver().findElement(By.cssSelector("td.ox-list-data-cell div." + columnClass));
		WebElement totalCellDiv = getDriver().findElement(By.cssSelector("td.ox-total-cell div." + columnClass));
		
		// Verify they have the same width initially
		assertEquals("Data and total cells should have the same width initially", dataCellDiv.getSize().getWidth(), totalCellDiv.getSize().getWidth());
		
		// Create an action to drag the handle
		Actions actions = new Actions(getDriver());
		
		// Move the handle 30 pixels to the left
		actions.clickAndHold(resizeHandle)
			.moveByOffset(-30, 0)
			.release()
			.perform();
		
		// Wait for the action to complete
		Thread.sleep(500);
		
		// Verify they still have the same width after resizing
		assertEquals("Data and total cells should still have the same width after resizing", dataCellDiv.getSize().getWidth(), totalCellDiv.getSize().getWidth());
		
		// Go back to list mode
		execute("Mode.list");
		
		// View the first record again
		execute("List.viewDetail", "row=0");
		
		// Find the cell container divs again to compare widths
		dataCellDiv = getDriver().findElement(By.cssSelector("td.ox-list-data-cell div." + columnClass));
		totalCellDiv = getDriver().findElement(By.cssSelector("td.ox-total-cell div." + columnClass));
		
		// Verify they still have the same width after reloading
		assertEquals("Data and total cells should still have the same width after reloading", dataCellDiv.getSize().getWidth(), totalCellDiv.getSize().getWidth());
	}

	public void testModuleLeftPartNotHiddenOnResizingWhenUsingElementCollectionWithSumAndTotals() throws Exception {
		setWindowWidth(1300);
		goModule("Receipt");

		WebElement groupBySelect = getDriver().findElement(By.cssSelector("select.xava_group_by"));
		
		assertTrue("xava_group_by should be visible at 1300px width", groupBySelect.isDisplayed());
		
		int elementRight = groupBySelect.getLocation().getX() + groupBySelect.getSize().getWidth();
		int windowWidth = getDriver().manage().window().getSize().getWidth();
		assertTrue("xava_group_by right side should be within browser window at 1300px. Element right: " + elementRight + ", window width: " + windowWidth,
			elementRight <= windowWidth);

		setWindowWidth(1200);
		
		groupBySelect = getDriver().findElement(By.cssSelector("select.xava_group_by"));
		
		assertTrue("xava_group_by should be visible at 1200px width", groupBySelect.isDisplayed());
		
		elementRight = groupBySelect.getLocation().getX() + groupBySelect.getSize().getWidth();
		windowWidth = getDriver().manage().window().getSize().getWidth();
		assertTrue("xava_group_by right side should be within browser window at 1200px. Element right: " + elementRight + ", window width: " + windowWidth,
			elementRight <= windowWidth);
	}

}
