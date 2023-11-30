package org.openxava.test.tests.byfeature;

import static org.junit.Assert.assertNotEquals;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openxava.util.*;

/**
 * tmr
 * To test @Coordinates related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class CoordinatesTest extends WebDriverTestBase {
	

	public void setUp() throws Exception {
		setHeadless(false); // tmr 
	    super.setUp();
	}
	
	
	public void testDependentCalculatedProperty() throws Exception {
		goModule("CityMap");
		execute("List.viewDetail", "row=0");
		
		WebElement coordinatesField = getDriver().findElement(By.id("ox_openxavatest_CityMap__location"));
		String originalValue = coordinatesField.getAttribute("value");
		assertFalse(Is.emptyString(originalValue));
		System.out.println("[CoordinatesTest.testDependentCalculatedProperty] originalValue=" + originalValue); // tmr
		WebElement dependentField = getDriver().findElement(By.id("ox_openxavatest_CityMap__map"));
		String originalDependentValue = dependentField.getAttribute("value");
		System.out.println("[CoordinatesTest.testDependentCalculatedProperty] originalDependentValue=" + originalDependentValue); // tmr
		assertEquals("https://www.google.com/maps?q=" + originalValue, originalDependentValue);
		
		WebElement map = getDriver().findElement(By.className("xava_coordinates"));
		Point mapPosition = map.getLocation(); 
        Actions actions = new Actions(getDriver());
        actions.moveByOffset(mapPosition.getX() + 50, mapPosition.getY() + 50).click().build().perform();
        
        String changedValue = coordinatesField.getAttribute("value");
        assertFalse(Is.emptyString(changedValue));
        System.out.println("[CoordinatesTest.testDependentCalculatedProperty] changedValue=" + changedValue); // tmr
        assertNotEquals(changedValue, originalValue);
        
        String changedDependentValue = dependentField.getAttribute("value");
        System.out.println("[CoordinatesTest.testDependentCalculatedProperty] changedDependentValue=" + changedDependentValue); // tmr
        assertEquals("https://www.google.com/maps?q=" + changedValue, changedDependentValue);
		
        // TMR ME QUEDÉ POR AQUÍ. LO QUE HAY HASTA ARRIBA FUNCIONA, PERO FALTA LA PARTE FINAL Y REFACTORIZAR
	}	
	

			
}
