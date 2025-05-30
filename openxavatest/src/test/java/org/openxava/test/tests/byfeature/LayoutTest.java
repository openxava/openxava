package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * To test correct and pretty layout of the UI using Selenium.
 * 
 * @author Javier Paniza
 */
public class LayoutTest extends WebDriverTestBase {
	
	public LayoutTest(String testName) {
		super(testName);
	}
		
	public void testReferenceFrameFillAllSpaceAfterSimplePropertyWithComma() throws Exception {
		goModule("Order");
		execute("CRUD.new");
		
		WebElement customerFrame = getDriver().findElement(By.id("ox_openxavatest_Order__frame_customercontent"));
		WebElement detailsFrame = getDriver().findElement(By.id("ox_openxavatest_Order__frame_detailscontent"));

		int customerFrameWidth = customerFrame.getSize().getWidth();
		int detailsFrameWidth = detailsFrame.getSize().getWidth();
		
		assertEquals(customerFrameWidth, detailsFrameWidth);		
	}

		
}
