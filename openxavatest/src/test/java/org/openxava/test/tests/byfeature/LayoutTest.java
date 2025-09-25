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
		
	public void testReferenceFrameFillAllSpaceWhenCombinedWithSimplePropertyWithComma() throws Exception {
		goModule("Order");
		execute("CRUD.new");
		
		WebElement customerFrame = getDriver().findElement(By.id("ox_openxavatest_Order__frame_customercontent"));
		WebElement detailsFrame = getDriver().findElement(By.id("ox_openxavatest_Order__frame_detailscontent"));

		int customerFrameWidth = customerFrame.getSize().getWidth();
		int detailsFrameWidth = detailsFrame.getSize().getWidth();
		
		assertEquals(customerFrameWidth, detailsFrameWidth);		

		// If the above fails because you decide it's good idea put frames and simple properties
		// in the same row, then test by hand CustomerCityWithGroup with flowLayout=true and
		// be sure that the collection (deliveryPlaces) is not inside the address frame
		goModule("CustomerCityWithGroup");
		execute("CRUD.new");
		
		WebElement addressFrame = getDriver().findElement(By.id("ox_openxavatest_CustomerCityWithGroup__frame_addresscontent"));
		WebElement cityFrame = getDriver().findElement(By.id("ox_openxavatest_CustomerCityWithGroup__frame_group_address___citycontent"));

		int addressFrameWidth = addressFrame.getSize().getWidth();
		int cityFrameWidth = cityFrame.getSize().getWidth();
				
		assertTrue(addressFrameWidth - cityFrameWidth < 30); // If margin/padding of frames changes maybe you have to adjust the 30, but the city frame	always fill all the space
	}

		
}
