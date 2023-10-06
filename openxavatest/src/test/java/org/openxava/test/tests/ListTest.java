package org.openxava.test.tests;

import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;

/**
 * tmr
 * To test lists and collections related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ListTest extends WebDriverTestBase {
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		// tmr driver.quit();
	}
	
	public void testListAndCollection() throws Exception {
		goModule("Author");
		assertMoveColumns();
		assertRemoveColumnAfterFiltering();
		assertNoFilterInCollectionByDefault();
	}
	
	public void assertNoFilterInCollectionByDefault() throws Exception {
		execute("CRUD.new");		
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute("MyGoListMode.list");
		execute("List.viewDetail", "row=1");
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute("CRUD.new");
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
	
	private void assertRemoveColumnAfterFiltering() throws Exception {
		assertListRowCount(2);
		assertListColumnCount(2);
		setConditionValues("J");
		execute("List.filter");
		assertListRowCount(1);
		removeColumn(1);
		assertListRowCount(1);
		assertListColumnCount(1); 
		execute("List.addColumns"); 
		execute("AddColumns.restoreDefault"); 
		assertListColumnCount(2);
		assertListRowCount(1);
		clearListCondition();
	}
	
	public void testCustomizeCollection() throws Exception {
		goModule("Carrier");
		
		// Original status		
		assertListColumnCount(3);
		assertLabelInList(0, "Calculated");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Name");
		execute("List.viewDetail", "row=0");
		
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");		
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");

		// Customize the collection
		moveColumn("fellowCarriers", 2, 3);
		assertNoErrors();
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated"); 
		assertLabelInCollection("fellowCarriers", 3, "Remarks");		
		
		// The main list not modified
		execute("Mode.list");
		assertListColumnCount(3);
		assertLabelInList(0, "Calculated");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Name");

		// The collection continues modified
		execute("List.viewDetail", "row=0");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");		

		// Add columns
		showCustomizeControls("fellowCarriers");
		execute("List.addColumns", "collection=fellowCarriers");
		assertNoAction("AddColumns.showMoreColumns"); // Because has not more than second level properties // TMR ME QUEDÉ POR AQUÍ, FALLA
		/*
		assertCollectionRowCount("xavaPropertiesList", 6);
		assertValueInCollection("xavaPropertiesList",  0, 0, "Driving licence description"); 
		assertValueInCollection("xavaPropertiesList",  1, 0, "Driving licence level");
		assertValueInCollection("xavaPropertiesList",  2, 0, "Driving licence type");
		assertValueInCollection("xavaPropertiesList",  3, 0, "Warehouse name");
		assertValueInCollection("xavaPropertiesList",  4, 0, "Warehouse number");
		assertValueInCollection("xavaPropertiesList",  5, 0, "Warehouse zone"); 
		checkRow("selectedProperties", "warehouse.name");		
 		execute("AddColumns.addColumns");

		assertCollectionColumnCount("fellowCarriers", 5);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");				
		assertLabelInCollection("fellowCarriers", 4, "Warehouse"); // This is "Name of Warehouse" with label optimized 
		 		
		// Other customizations
		moveColumn("fellowCarriers", 3, 4); 
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection("fellowCarriers", 4, "Remarks");
						
		removeColumn("fellowCarriers", 4); 
		assertCollectionColumnCount("fellowCarriers", 4); 
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		
		// Changing column name
		execute("List.changeColumnName", "property=name,collection=fellowCarriers");
		assertDialog();
		assertValue("name", "Name");
		setValue("name", "Carrier");
		execute("ChangeColumnName.change");
		assertLabelInCollection("fellowCarriers", 1, "Carrier");
		
		// Adding clicking in row
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.addColumn", "property=warehouse.number");
		assertCollectionColumnCount("fellowCarriers", 5);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection("fellowCarriers", 4, "Warehouse number");
		
		// Restoring		
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.restoreDefault");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");
		
		execute("List.changeColumnName", "property=name,collection=fellowCarriers");
		assertValue("name", "Carrier");
		setValue("name", "Name");
		execute("ChangeColumnName.change");
		assertLabelInCollection("fellowCarriers", 1, "Name");		
		
		// Cancel in AddColumns returns to detail (not list mode)
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.cancel");
		assertValue("name", "UNO"); // In detail mode
		*/ 
	}
	
	private void assertNoAction(String qualifiedAction) {
		String [] action = qualifiedAction.split(".");
		String name = "ox_openxavatest_" + module + "__action___" + action[0] + "___" + action[1];
		assertTrue(XavaResources.getString("action_found_in_ui", action), driver.findElements(By.name(name)).isEmpty());
	}

	private void assertNoErrors() {
		WebElement errors = driver.findElement(By.id("ox_openxavatest_" + module + "__errors"));
		assertEquals(XavaResources.getString("unexpected_messages", "Errors"), "", errors.getText());
	}

	private void goModule(String module) throws Exception{
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
	
	private void clearListCondition() throws Exception{
		driver.findElement(By.id("ox_openxavatest_Author__xava_clear_condition")).click();
		wait(driver);
	}

	private void execute(String action) throws Exception {
		execute(driver, module, action);
	}
	
	private void execute(String action, String arguments) throws Exception {
		execute(driver, module, action, arguments);
	}

	private void setConditionValues(String value) { // One argument by now, but we could evolution to String ... value 
		driver.findElement(By.id("ox_openxavatest_Author__conditionValue___0")).sendKeys(value); 		
	}

	private void assertListColumnCount(int expectedColumnCount) {
		assertCollectionColumnCount("list", expectedColumnCount);
	}
	
	private void assertCollectionColumnCount(String collection, int expectedColumnCount) {
		int columnCount = getTable(collection).findElement(By.tagName("tr")).findElements(By.tagName("th")).size();
		assertEquals(expectedColumnCount, columnCount - 2);
	}

	private void assertListRowCount(int expectedRowCount) {
		int rowCount = getTable("list").findElements(By.tagName("tr")).size();
		assertEquals(expectedRowCount, rowCount - 3);
	}
	
	private WebElement getTable(String collection) {
		return driver.findElement(By.id("ox_openxavatest_" + module + "__" + collection));
	}
	
	private void resetModule() throws Exception {
		driver.quit();
		driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/" + module); 
		wait(driver);
	}

	private void moveColumn(int sourceColumn, int targetColumn) throws Exception {
		moveColumn("list", sourceColumn, targetColumn);
	}
	
	private void showCustomizeControls(String collection) {
		driver.findElement(By.id("ox_openxavatest_" + module + "__customize_" + collection)).click();
	}
	
	private void moveColumn(String collection, int sourceColumn, int targetColumn) {
		showCustomizeControls(collection);
		driver.findElement(By.id("ox_openxavatest_" + module + "__customize_" + collection)).click();
		WebElement handle = getHeader(collection, sourceColumn).findElement(By.className("xava_handle")); 
		String classTargetPoint = sourceColumn > targetColumn?"xava_handle":"mdi-rename-box";
		WebElement targetPoint = getHeader(collection, targetColumn).findElement(By.className(classTargetPoint));
		Actions actions = new Actions(driver);
		actions.dragAndDrop(handle, targetPoint).build().perform();
	}
	
	private void removeColumn(int columnIndex) {
		driver.findElement(By.id("ox_openxavatest_" + module + "__customize_list")).click();
		WebElement removeButton = getHeader("list", columnIndex).findElement(By.className("mdi-close-circle"));
		removeButton.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
		wait.until(ExpectedConditions.invisibilityOf(removeButton));
	}


	private void assertLabelInList(int column, String expectedLabel) { 
		assertLabelInCollection("list", column, expectedLabel);
	}
	
	private void assertLabelInCollection(String collection, int column, String expectedLabel) {
		String label = getHeader(collection, column).getText().trim();
		assertEquals(expectedLabel, label);
	}
		
	private WebElement getHeader(String collection, int column) {
		WebElement headerRow = getTable(collection).findElement(By.tagName("tr"));
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
