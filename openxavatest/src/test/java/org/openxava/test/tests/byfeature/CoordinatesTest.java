package org.openxava.test.tests.byfeature;

import static org.junit.Assert.assertNotEquals;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openxava.util.*;

/**
 * To test @Coordinates related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class CoordinatesTest extends WebDriverTestBase {
	
	public CoordinatesTest(String testName) {
		super(testName);
	}
	
	
	public void testDependentCalculatedProperty_coordinatesFrameNotWrapNextFrame() throws Exception {
		goModule("CityMap");
		execute("List.viewDetail", "row=0");
		assertDependentCalculatedProperty();
		assertCoordinatesFrameNotWrapNextFrame();
	}


	private void assertCoordinatesFrameNotWrapNextFrame() {
		WebElement locationFrame = getDriver().findElement(By.id("ox_openxavatest_CityMap__frame_locationcontent"));
		WebElement calculatedDataFrame = getDriver().findElement(By.id("ox_openxavatest_CityMap__frame_group_calculatedDatacontent"));

		int locationFrameWidth = locationFrame.getSize().getWidth();
		int calculatedDataFrameWidth = calculatedDataFrame.getSize().getWidth();
		
		assertEquals(locationFrameWidth, calculatedDataFrameWidth);
	}


	private void assertDependentCalculatedProperty() throws Exception {
		String originalValue = assertCalculatedMatches();
		clickOnMap(50, 50);
		wait(getDriver());
		String changedValue = assertCalculatedMatches();
		assertNotEquals(changedValue, originalValue);
		
		clickOnMap(40, 40); 
		wait(getDriver());
		String changedValue2 = assertCalculatedMatches();
		assertNotEquals(changedValue, changedValue2);
	}	
	
	private void clickOnMap(int x, int y) {
		WebElement map = getDriver().findElement(By.className("xava_coordinates"));
		Point mapPosition = map.getLocation(); 
        Actions actions = new Actions(getDriver());
        actions.moveByOffset(mapPosition.getX() + x, mapPosition.getY() + y).click().build().perform();
	}


	private String assertCalculatedMatches() {
		WebElement coordinatesField = getDriver().findElement(By.id("ox_openxavatest_CityMap__location"));
		String value = coordinatesField.getAttribute("value");
		assertFalse(Is.emptyString(value));
		WebElement dependentField = getDriver().findElement(By.id("ox_openxavatest_CityMap__map"));
		String dependentValue = dependentField.getAttribute("value");
		assertEquals("https://www.google.com/maps?q=" + value, dependentValue);
		return value;
	}
			
}
