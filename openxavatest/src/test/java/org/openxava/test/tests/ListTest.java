package org.openxava.test.tests;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

/**
 * tmr
 * To test lists and collections related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ListTest extends WebDriverTestBase {
	
	private WebDriver driver;

	public void setUp() throws Exception {
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testMoveColumns_noFilterInCollectionByDefault() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Author"); 
		wait(driver);
		assertMoveColumns();
		assertNoFilterInCollectionByDefault();
	}
	
	public void assertNoFilterInCollectionByDefault() throws Exception {
		execute(driver, "Author", "CRUD.new");		
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute(driver, "Author", "MyGoListMode.list");
		execute(driver, "Author", "List.viewDetail", "row=1");
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute(driver, "Author", "CRUD.new");
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__hide_filter_humans")).click();
		Thread.sleep(1000);
		assertCollectionFilterNotDisplayed();
	}
	
	private void assertMoveColumns() throws Exception { 
		// To test a specific bug moving columns
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");
		
		moveColumn(0, 1);
		assertLabelInList(0, "Biography"); 
		assertLabelInList(1, "Author");
		
		resetModule();
		assertLabelInList(0, "Biography");
		assertLabelInList(1, "Author");
		
		moveColumn(1, 0);
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");		
	}
	
	private void resetModule() throws Exception {
		driver.quit();
		driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/Author"); 
		wait(driver);
	}

	private void moveColumn(int sourceColumn, int targetColumn) throws Exception {
		driver.findElement(By.id("ox_openxavatest_Author__customize_list")).click();
		WebElement handle = getListHeader(sourceColumn).findElement(By.className("xava_handle")); 
		String classTargetPoint = sourceColumn > targetColumn?"xava_handle":"mdi-rename-box";
		WebElement targetPoint = getListHeader(targetColumn).findElement(By.className(classTargetPoint));
		Actions actions = new Actions(driver);
		actions.dragAndDrop(handle, targetPoint).build().perform();
	}

	private void assertLabelInList(int column, String expectedLabel) { 
		 String label = getListHeader(column).getText().trim();
		 assertEquals(expectedLabel, label);
	}
	
	private WebElement getListHeader(int column) {
		 WebElement table = driver.findElement(By.id("ox_openxavatest_Author__list"));
		 WebElement headerRow = table.findElement(By.tagName("tr"));
		 List<WebElement> headers = headerRow.findElements(By.tagName("th"));		
		 return headers.get(column + 2);
	}

	private void assertCollectionFilterDisplayed() { 
		assertTrue(driver.findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}
	
	private void assertCollectionFilterNotDisplayed() { 
		assertFalse(driver.findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}

		
}
