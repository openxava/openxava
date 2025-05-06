package org.openxava.test.tests.byfeature;

import java.util.List;
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
	
	public void _testFocus() throws Exception { // tmr
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
	
	public void _testSetLabelIdPerformance() throws Exception { // tmr
		goModule("Grade");
		long ini = System.currentTimeMillis();
		execute("Grade.addCalifications");
		long fn = System.currentTimeMillis();
		assertTrue((fn-ini) < 5000); // More than 9 seconds when failed 
	}
	
	public void testTotalInputsKeepColumnSizeAfterColumnResize() throws Exception {
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
		
		// Find the input elements to compare widths
		WebElement detailsAmountInput = getDriver().findElement(By.id("ox_openxavatest_CommercialDocument__details___0___amount"));
		WebElement totalAmountInput = getDriver().findElement(By.id("ox_openxavatest_CommercialDocument__totalAmount"));
		
		// Get initial widths
		int detailsAmountInitialWidth = detailsAmountInput.getSize().getWidth();
		int totalAmountInitialWidth = totalAmountInput.getSize().getWidth();
		
		// Verify they have the same width initially
		assertEquals("Both inputs should have the same width initially", detailsAmountInitialWidth, totalAmountInitialWidth);
		
		// Create an action to drag the handle
		Actions actions = new Actions(getDriver());
		
		// Move the handle 30 pixels to the left
		actions.clickAndHold(resizeHandle)
			.moveByOffset(-30, 0)
			.release()
			.perform();
		
		// Wait for the action to complete
		Thread.sleep(500);
		
		// Get widths after resizing
		int detailsAmountFinalWidth = detailsAmountInput.getSize().getWidth();
		int totalAmountFinalWidth = totalAmountInput.getSize().getWidth();
		
		// Verify they still have the same width after resizing
		assertEquals("Both inputs should still have the same width after resizing", detailsAmountFinalWidth, totalAmountFinalWidth);
		
		// Go back to list mode
		execute("Mode.list");
		
		// View the first record again
		execute("List.viewDetail", "row=0");
		
		// Find the input elements again to compare widths
		WebElement detailsAmountInputAfterReload = getDriver().findElement(By.id("ox_openxavatest_CommercialDocument__details___0___amount"));
		WebElement totalAmountInputAfterReload = getDriver().findElement(By.id("ox_openxavatest_CommercialDocument__totalAmount"));
		
		// Get widths after reloading
		int detailsAmountWidthAfterReload = detailsAmountInputAfterReload.getSize().getWidth();
		int totalAmountWidthAfterReload = totalAmountInputAfterReload.getSize().getWidth();
		
		// Verify they still have the same width after reloading
		assertEquals("Both inputs should still have the same width after reloading", detailsAmountWidthAfterReload, totalAmountWidthAfterReload);
	}
}
