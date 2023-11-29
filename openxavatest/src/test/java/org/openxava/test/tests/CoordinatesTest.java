package org.openxava.test.tests;

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
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
		// tmr setHeadless(true); 
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		// tmr driver.quit();
	}
	
	public void testDependentCalculatedProperty() throws Exception {
		goModule("CityMap");
		execute("List.viewDetail", "row=0");
		
		WebElement coordinatesField = driver.findElement(By.id("ox_openxavatest_CityMap__location"));
		String originalValue = coordinatesField.getAttribute("value");
		assertFalse(Is.emptyString(originalValue));
		System.out.println("[CoordinatesTest.testDependentCalculatedProperty] originalValue=" + originalValue); // tmr
		WebElement dependentField = driver.findElement(By.id("ox_openxavatest_CityMap__map"));
		String originalDependentValue = dependentField.getAttribute("value");
		System.out.println("[CoordinatesTest.testDependentCalculatedProperty] originalDependentValue=" + originalDependentValue); // tmr
		assertEquals("https://www.google.com/maps?q=" + originalValue, originalDependentValue);
		
		WebElement map = driver.findElement(By.className("xava_coordinates"));
		Point mapPosition = map.getLocation(); 
        Actions actions = new Actions(driver);
        actions.moveByOffset(mapPosition.getX() + 50, mapPosition.getY() + 50).click().build().perform();
        
        String changedValue = coordinatesField.getAttribute("value");
        assertFalse(Is.emptyString(changedValue));
        System.out.println("[CoordinatesTest.testDependentCalculatedProperty] changedValue=" + changedValue); // tmr
        assertNotEquals(changedValue, originalValue);
        
        String changedDependentValue = dependentField.getAttribute("value");
        System.out.println("[CoordinatesTest.testDependentCalculatedProperty] changedDependentValue=" + changedDependentValue); // tmr
        assertEquals("https://www.google.com/maps?q=" + changedValue, changedDependentValue);
		
        // TMR ME QUEDÉ POR AQUÍ. LO QUE HAY HASTA ARRIBA FUNCIONA, PERO FALTA LA PARTE FINAL Y REFACTORIZAR
		// tmr Quitar métodos de abajo que no se usen
	}	
	
	private void execute(String action, String arguments) throws Exception { // Duplicated with ListTest, refactoring pending 
		execute(driver, module, action, arguments);
	}

	
	private void goModule(String module) throws Exception{ // Duplicated with ListTest, refactoring pending 
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
	
	private int getListRowCount() { // Duplicated with ListTest, refactoring pending
		return getCollectionRowCount("list") - 2;
	}

	private int getCollectionRowCount(String collection) { // Duplicated with ListTest, refactoring pending
		int rowCount = getTable(collection).findElements(By.tagName("tr")).size();
		return rowCount - 1;
	}
	
	private WebElement getTable(String collection) { // Duplicated with ListTest, refactoring pending
		return driver.findElement(By.id("ox_openxavatest_" + module + "__" + collection));
	}
		
}
