package org.openxava.test.tests.byfeature;

import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * To test correct and pretty layout of the UI using Selenium.
 * 
 * @author Javier Paniza
 */
public class LayoutTest extends WebDriverTestBase {
	
	public LayoutTest(String testName) {
		super(testName);
	}

	public void testCollectionsStayInsideFramesWhenResizing() throws Exception {
		goModule("WorkCost");
		moveToListView();
		execute("List.viewDetail", "row=0");

		Dimension originalSize = getDriver().manage().window().getSize();
		boolean modulesListVisible = getDriver().findElement(By.id("modules_list")).isDisplayed();
		try {
			setWindowWidth(900);
			setModulesListVisible(false);
			assertCollectionsFitAndFillFrames();
			setWindowWidth(700);
			assertCollectionsFitAndFillFrames();
			setModulesListVisible(true);
			assertCollectionsFitAndFillFrames();
			setWindowWidth(1400);
			assertCollectionsFitAndFillFrames();
		}
		finally {
			getDriver().manage().window().setSize(originalSize);
			setModulesListVisible(modulesListVisible);
		}
	}

	private void setModulesListVisible(boolean visible) {
		WebElement modulesList = getDriver().findElement(By.id("modules_list"));
		if (modulesList.isDisplayed() == visible) return;
		getDriver().findElement(By.id(visible ? "modules_list_show" : "modules_list_hide")).click();
		new WebDriverWait(getDriver(), Duration.ofSeconds(3)).until(driver ->
			Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
				"return !$('#modules_list').is(':animated');")) && modulesList.isDisplayed() == visible);
	}

	private void assertCollectionsFitAndFillFrames() {
		// Let resize handlers and the next paint finish before measuring the layout.
		((JavascriptExecutor) getDriver()).executeAsyncScript(
			"var done = arguments[arguments.length - 1];" +
			"requestAnimationFrame(function() { requestAnimationFrame(function() { done(); }); });");

		List<WebElement> collections = getDriver().findElements(By.className("ox_openxavatest_WorkCost__collection_scroll"));
		assertEquals(2, collections.size());
		boolean flowLayout = !getDriver().findElements(By.cssSelector(".ox-detail.ox-flow-layout")).isEmpty();
		for (WebElement scroll : collections) {
			WebElement frame = scroll.findElement(By.xpath("ancestor::div[contains(concat(' ', normalize-space(@class), ' '), ' ox-frame ')][1]"));
			assertTrue("Collection must be visible", scroll.isDisplayed());
			Rectangle listBounds = scroll.getRect();
			Rectangle frameBounds = frame.getRect();
			int listRight = listBounds.getX() + listBounds.getWidth();
			int frameRight = frameBounds.getX() + frameBounds.getWidth();
			String bounds = "Collection " + scroll.findElement(By.cssSelector("table")).getAttribute("id") +
				" spans [" + listBounds.getX() + ", " + listRight + "] but its frame spans [" +
				frameBounds.getX() + ", " + frameRight + "] at window width " +
				getDriver().manage().window().getSize().getWidth() + ", flowLayout=" + flowLayout;
			assertTrue(bounds, listBounds.getX() >= frameBounds.getX() - 1);
			assertTrue(bounds, listRight <= frameRight + 1);

			WebElement content = scroll.findElement(By.xpath("ancestor::div[contains(concat(' ', normalize-space(@class), ' '), ' ox-frame-content ')][1]"));
			int availableWidth = content.getRect().getWidth();
			assertEquals(bounds + "; collection must fill the frame's available width",
				(double) availableWidth, (double) listBounds.getWidth(), 1.0);
		}
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

		assertTrue(addressFrameWidth - cityFrameWidth < 70); // If margin/padding of frames changes maybe you have to adjust the 70, but the city frame always fill all the space
	}

		
}
