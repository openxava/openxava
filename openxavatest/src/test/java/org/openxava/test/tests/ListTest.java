package org.openxava.test.tests;

import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * tmr
 * To test lists and collections related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ListTest extends WebDriverTestBase {
	
	private final static String ACTION_PREFIX = "action";
	
	private WebDriver driver;
	private String module; 

	public void setUp() throws Exception {
		// tmr setHeadless(true);
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testListAndCollection() throws Exception {
		goModule("Author");
		assertShowHideFilterInList();
		assertMoveColumns();
		assertRemoveColumnAfterFiltering();
		assertNoFilterInCollectionByDefault();
		
		goModule("Carrier");
		assertEnableDisableCustomizeList();
		assertCustomizeCollection();
		
		goModule("CustomerWithSection");
		assertCustomizeList();
		assertCustomizeList_addAndResetModule();
		
		goModule("Invoice");
		assertRemoveSeveralColumns();
	}
		
	private void assertNoFilterInCollectionByDefault() throws Exception {
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
		
		showCustomizeControls();
		moveColumn(0, 1);
		assertLabelInList(0, "Biography"); 
		assertLabelInList(1, "Author");
		
		resetModule();
		assertLabelInList(0, "Biography");
		assertLabelInList(1, "Author");
		
		showCustomizeControls();
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
		showCustomizeControls();
		removeColumn(1);
		assertListRowCount(1);
		assertListColumnCount(1); 
		execute("List.addColumns"); 
		execute("AddColumns.restoreDefault"); 
		assertListColumnCount(2);
		assertListRowCount(1);
		clearListCondition();
	}
	
	private void assertShowHideFilterInList() throws Exception {
		goModule("Author");
		assertFalse(getElementById("show_filter_list").isDisplayed()); 
		assertTrue(getElementById("hide_filter_list").isDisplayed()); 
		assertTrue(getElementById("list_filter_list").isDisplayed());
		getElementById("hide_filter_list").click();
		Thread.sleep(700); 
		assertTrue(getElementById("show_filter_list").isDisplayed());
		assertFalse(getElementById("hide_filter_list").isDisplayed());
		assertFalse(getElementById("list_filter_list").isDisplayed());  
		getElementById("show_filter_list").click();
		Thread.sleep(700); 
		assertFalse(getElementById("show_filter_list").isDisplayed()); 
		assertTrue(getElementById("hide_filter_list").isDisplayed());
		assertTrue(getElementById("list_filter_list").isDisplayed());
	}
	
	private void assertEnableDisableCustomizeList() throws Exception {
		WebElement addColumns = driver.findElement(By.id("ox_openxavatest_Carrier__List___addColumns")); 
		WebElement column0 = driver.findElement(By.id("ox_openxavatest_Carrier__list_col0"));		
		WebElement moveColumn0 = column0.findElement(By.cssSelector("i[class='xava_handle mdi mdi-cursor-move ui-sortable-handle']"));
		WebElement removeColumn0 = driver.findElement(By.cssSelector("a[onclicke=\"javascript:openxava.removeColumn('openxavatest', 'Carrier', 'ox_openxavatest_Carrier__list_col0', 'xava_tab')\"]"));
		WebElement column1 = driver.findElement(By.id("ox_openxavatest_Carrier__list_col1"));
		WebElement moveColumn1 = column1.findElement(By.cssSelector("i[class='xava_handle mdi mdi-cursor-move ui-sortable-handle']")); 
		WebElement removeColumn1 = driver.findElement(By.cssSelector("a[onclicke=\"javascript:openxava.removeColumn('openxavatest', 'Carrier', 'ox_openxavatest_Carrier__list_col1', 'xava_tab')\"]"));
		assertFalse(addColumns.isDisplayed());
		assertFalse(moveColumn0.isDisplayed());		
		assertFalse(removeColumn0.isDisplayed());
		assertFalse(moveColumn1.isDisplayed());
		assertFalse(removeColumn1.isDisplayed());		
		WebElement customize = driver.findElement(By.id("ox_openxavatest_Carrier__customize_list")); 
		customize.click();
		assertTrue(addColumns.isDisplayed());
		assertTrue(moveColumn0.isDisplayed());
		assertTrue(removeColumn0.isDisplayed());
		assertTrue(moveColumn1.isDisplayed());
		assertTrue(removeColumn1.isDisplayed());		
		customize.click();
		Thread.sleep(3000); // It needs time to fade out 
		assertFalse(addColumns.isDisplayed()); 
		assertFalse(moveColumn0.isDisplayed());		
		assertFalse(removeColumn0.isDisplayed());
		assertFalse(moveColumn1.isDisplayed()); 
		assertFalse(removeColumn1.isDisplayed());
	}
	
	private WebElement getElementById(String id) {
		return driver.findElement(By.id(Ids.decorate("openxavatest", module, id)));
	}

	private void assertCustomizeCollection() throws Exception {
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
		showCustomizeControls("fellowCarriers");
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
		assertNoAction("AddColumns.showMoreColumns"); // Because has not more than second level properties
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
		showCustomizeControls("fellowCarriers");
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
		showCustomizeControls("fellowCarriers");
		execute("List.addColumns", "collection=fellowCarriers"); 
		execute("AddColumns.addColumn", "property=warehouse.number");
		assertCollectionColumnCount("fellowCarriers", 5);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection("fellowCarriers", 4, "Warehouse number");
		
		// Restoring
		showCustomizeControls("fellowCarriers");
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.restoreDefault");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");
		
		showCustomizeControls("fellowCarriers");
		execute("List.changeColumnName", "property=name,collection=fellowCarriers");
		assertValue("name", "Carrier");
		setValue("name", "Name");
		execute("ChangeColumnName.change");
		assertLabelInCollection("fellowCarriers", 1, "Name");		
		
		// Cancel in AddColumns returns to detail (not list mode)
		showCustomizeControls("fellowCarriers");
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.cancel");
		assertValue("name", "UNO"); // In detail mode
	}
	
	private void assertCustomizeList_addAndResetModule() throws Exception {
		assertListColumnCount(7); 
		String value = getValueInList(0, 0);
		showCustomizeControls();
		execute("List.addColumns");		
		checkRow("selectedProperties", "number"); 		
		execute("AddColumns.addColumns");
		assertListColumnCount(8);
		assertValueInList(0, 0, value);
				
		resetModule();
		assertListColumnCount(8); 
		assertValueInList(0, 0, value);
		
		showCustomizeControls();
		removeColumn(7); 
		assertListColumnCount(7); 
	}
	
	public void assertRemoveSeveralColumns() throws Exception {
		assertListColumnCount(8); 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");

		showCustomizeControls();
		removeColumn(2);
		assertListColumnCount(7); 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "V.A.T.");
		assertLabelInList(4, "Details count");
		assertLabelInList(5, "Paid");
		assertLabelInList(6, "Importance");
		
		removeColumn(3); // VAT
		assertListColumnCount(6);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "Details count");
		assertLabelInList(4, "Paid");
		assertLabelInList(5, "Importance");
		
		execute("List.filter");
		assertListColumnCount(6);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Amounts sum");
		assertLabelInList(3, "Details count");
		assertLabelInList(4, "Paid");
		assertLabelInList(5, "Importance");

		showCustomizeControls();
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");		
	}
	
	public void assertCustomizeList() throws Exception {
		doTestCustomizeList_moveAndRemove(); 
		resetModule(); 
		doTestCustomizeList_generatePDF();
		resetModule(); 
		doTestRestoreColumns_addRemoveTabColumnsDynamically();
	}
	
	private void doTestCustomizeList_moveAndRemove() throws Exception {
		String [] listActions = {
			"Print.generatePdf",
			"Print.generateExcel",
			"ImportData.importData", 
			"ExtendedPrint.myReports",
			"CRUD.new",
			"CRUD.deleteSelected",
			"CRUD.deleteRow", 
			"List.addColumns",
			"List.filter",
			"List.orderBy",
			"List.viewDetail",
			"List.hideRows",
			"List.changeColumnName", 
			"ListFormat.select",
			"EmailNotifications.subscribe",
			"Customer.hideSellerInList",
			"Customer.showSellerInList",
			"Customer.startRefisher",
			"Customer.stopRefisher",	
			"Customer.disableAddress",
			"Customer.filterBySellerOne"
		};
		
		assertActions(listActions); 
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "Address city");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "Address state");
		assertLabelInList(6, "Web site");
		 
		assertTrue("It is needed customers for execute this test", getListRowCount() > 1);
		String name = getValueInList(0, 0);
		String type = getValueInList(0, 1);
		String seller = getValueInList(0, 2);
		String city = getValueInList(0, 3);
		String sellerLevel = getValueInList(0, 4);
		String state = getValueInList(0, 5);
		String site = getValueInList(0, 6);
		
		// move 0 to 2
		showCustomizeControls();
		moveColumn(0, 2);
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");
		assertLabelInList(2, "Name");
		assertLabelInList(3, "Address city");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "Address state");
		assertLabelInList(6, "Web site");		
		assertValueInList(0, 0, type);
		assertValueInList(0, 1, seller);
		assertValueInList(0, 2, name);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);						
		assertValueInList(0, 5, state);
		assertValueInList(0, 6, site);		
		
		// move 2 to 4
		moveColumn(2, 4); 
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");		
		assertLabelInList(2, "Address city");
		assertLabelInList(3, "Seller level");
		assertLabelInList(4, "Name");
		assertLabelInList(5, "Address state");
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, type);
		assertValueInList(0, 1, seller);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, sellerLevel);
		assertValueInList(0, 4, name);
		assertValueInList(0, 5, state);		
		assertValueInList(0, 6, site);		
		
		// remove column 3
		removeColumn(3); 
		assertNoErrors();
		assertListColumnCount(6);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");		
		assertLabelInList(2, "Address city");		
		assertLabelInList(3, "Name");
		assertLabelInList(4, "Address state");
		assertLabelInList(5, "Web site");
		assertValueInList(0, 0, type);
		assertValueInList(0, 1, seller);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, name);
		assertValueInList(0, 4, state);
		assertValueInList(0, 5, site);		
						
		assertActions(listActions);
	}
	
	private void doTestCustomizeList_generatePDF() throws Exception {
		// Trusts in that testCustomizeList_moveAndRemove is executed before
		assertListColumnCount(6);
		assertLabelInList(0, "Type");
		assertLabelInList(1, "Seller");		
		assertLabelInList(2, "Address city");		
		assertLabelInList(3, "Name");
		assertLabelInList(4, "Address state");
		assertLabelInList(5, "Web site");
		showCustomizeControls();
		removeColumn(3); 
		assertNoErrors();
		assertListColumnCount(5);		
		execute("Print.generatePdf"); 
		assertContentTypeForPopup("application/pdf");
	}
	
	private void doTestRestoreColumns_addRemoveTabColumnsDynamically() throws Exception {
		// Restoring initial tab setup
		showCustomizeControls();
		execute("List.addColumns");							
		execute("AddColumns.restoreDefault");		
		// End restoring
		
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "Address city");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "Address state"); 
		assertLabelInList(6, "Web site");
		assertTrue("Must to have customers for run this test", getListRowCount() > 1);
		String name = getValueInList(0, 0);
		String type = getValueInList(0, 1);
		String seller = getValueInList(0, 2);
		String city = getValueInList(0, 3);
		String sellerLevel = getValueInList(0, 4);
		String state = getValueInList(0, 5); 
		String site = getValueInList(0, 6);
		
		execute("Customer.hideSellerInList");
		assertNoErrors();
		assertListColumnCount(6);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Address city");
		assertLabelInList(3, "Seller level");
		assertLabelInList(4, "Address state"); 
		assertLabelInList(5, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, sellerLevel);
		assertValueInList(0, 4, state); 
		assertValueInList(0, 5, site);
		
		execute("Customer.showSellerInList");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");		
		assertLabelInList(3, "Address city");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "Address state"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, seller);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state); 
		assertValueInList(0, 6, site);
	}

	private void assertValue(String name, String value) {
		assertEquals(XavaResources.getString("unexpected_value", name), value, getValue(name));		
	}

	private String getValue(String name) {
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		return input.getAttribute("value");
	}
	
	private void setValue(String name, String value) { // tmr Duplicada con DescriptionsListTest
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		input.clear();
		input.sendKeys(value);	
	}

	private void assertNoAction(String qualifiedAction) {
		String [] action = qualifiedAction.split("\\.");
		String name = "ox_openxavatest_" + module + "__action___" + action[0] + "___" + action[1];
		assertTrue(XavaResources.getString("action_found_in_ui", action), driver.findElements(By.name(name)).isEmpty());
	}

	private void assertNoErrors() { // tmr Duplicado con DescriptionsListTest
		WebElement errors = driver.findElement(By.id("ox_openxavatest_" + module + "__errors"));
		assertEquals(XavaResources.getString("unexpected_messages", "Errors"), "", errors.getText());
	}

	private void goModule(String module) throws Exception{ // tmr Duplicado con DescriptionsListTest
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		this.module = module;
		wait(driver);
	}
	
	private void clearListCondition() throws Exception{
		driver.findElement(By.id("ox_openxavatest_Author__xava_clear_condition")).click();
		wait(driver);
	}

	private void execute(String action) throws Exception { // tmr Duplicado con DescriptionsListTest
		execute(driver, module, action);
	}
	
	private void execute(String action, String arguments) throws Exception { // tmr Duplicado con DescriptionsListTest
		execute(driver, module, action, arguments);
	}

	private void setConditionValues(String value) { // One argument by now, but we could evolution to String ... value 
		driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___0")).sendKeys(value); 		
	}

	private void assertListColumnCount(int expectedColumnCount) {
		assertCollectionColumnCount("list", expectedColumnCount);
	}
	
	private void assertCollectionColumnCount(String collection, int expectedColumnCount) {
		assertEquals(expectedColumnCount, getCollectionColumnCount(collection));
	}
	
	private int getCollectionColumnCount(String collection) {
		int columnCount = getTable(collection).findElement(By.tagName("tr")).findElements(By.tagName("th")).size();
		return columnCount - 2;
	}

	private void assertListRowCount(int expectedRowCount) {
		assertEquals(expectedRowCount, getListRowCount());
	}
	
	private void assertCollectionRowCount(String collection, int expectedRowCount) {
		assertEquals(expectedRowCount, getCollectionRowCount(collection));
	}
	
	private int getListRowCount() {
		return getCollectionRowCount("list") - 2;
	}

	private int getCollectionRowCount(String collection) {
		int rowCount = getTable(collection).findElements(By.tagName("tr")).size();
		return rowCount - 1;
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
	
	private void showCustomizeControls() {
		showCustomizeControls("list");
	}
	
	private void showCustomizeControls(String collection) {
		driver.findElement(By.id("ox_openxavatest_" + module + "__customize_" + collection)).click();
	}
	
	private void moveColumn(String collection, int sourceColumn, int targetColumn) throws Exception {
		WebElement handle = getHeader(collection, sourceColumn).findElement(By.className("xava_handle")); 
		String classTargetPoint = sourceColumn > targetColumn?"xava_handle":"mdi-close-circle"; 
		WebElement targetPoint = getHeader(collection, targetColumn).findElement(By.className(classTargetPoint));
		Actions actions = new org.openqa.selenium.interactions.Actions(driver);
		actions.dragAndDrop(handle, targetPoint).build().perform();
		wait(driver);
	}
	
	private void removeColumn(String collection, int columnIndex) throws Exception {
		WebElement removeButton = getHeader(collection, columnIndex).findElement(By.className("mdi-close-circle"));
		removeButton.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
		wait.until(ExpectedConditions.invisibilityOf(removeButton));
		wait(driver);
	}

	private void removeColumn(int columnIndex) throws Exception {
		removeColumn("list", columnIndex);
	}

	private void assertLabelInList(int column, String expectedLabel) { 
		assertLabelInCollection("list", column, expectedLabel);
	}
	
	private void assertLabelInCollection(String collection, int column, String expectedLabel) {
		String label = getHeader(collection, column).getText().trim();
		assertEquals(expectedLabel, label);
	}
	
	private void assertValueInList(int row, int column, String expectedValue) { 
		assertEquals(expectedValue, getValueInList(row, column));				
	}
	
	private void assertValueInCollection(String collection, int row, int column, String expectedValue) {
		assertEquals(expectedValue, getValueInCollection(collection, row, column));				
	}
	
	private String getValueInList(int row, int column) {
		return getValueInCollection("list", row + 1, column);
	}
	
	private String getValueInCollection(String collection, int row, int column) {
		return getCell(collection, row + 1, column).getText().trim();
	}
		
	private WebElement getHeader(String collection, int column) {
		return getCell(collection, 0, column);
	}
	
	private WebElement getCell(String collection, int row, int column) {
		WebElement tableRow = getTable(collection).findElements(By.tagName("tr")).get(row);
		String cellType = row == 0?"th":"td";
		List<WebElement> cells = tableRow.findElements(By.tagName(cellType));		
		return cells.get(column + 2);
	}
	
	private void assertCollectionFilterDisplayed() { 
		assertTrue(driver.findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}
	
	private void assertCollectionFilterNotDisplayed() { 
		assertFalse(driver.findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}
	
	private void checkRow(String id, String value) throws Exception {
		WebElement checkbox = driver.findElement(By.cssSelector("input[value='" + id + ":" + value + "']"));
		checkbox.click();
		wait(driver);
	}
	
	private void assertDialog() throws Exception { 
		assertTrue(XavaResources.getString("dialog_must_be_displayed"), getTopDialog() != null); 
	}
	
	private String getTopDialog() throws Exception { 
		int level = 0;
		for (level = 10; level > 0; level--) {
			try {
				WebElement el = driver.findElement(By.id(Ids.decorate("openxavatest", module, "dialog" + level)));
				if (el != null && !el.findElements(By.xpath("./*")).isEmpty()) break;
			}
			catch (NoSuchElementException ex) {
			}			
		}
		if (level == 0) return null;
		return "dialog" + level;		
	}
	
	protected void assertActions(String [] expectedActions) throws Exception {
		Collection<String> actionsInForm = getActions();		
		Collection<String> left = new ArrayList();		
		for (int i = 0; i < expectedActions.length; i++) {
			String expectedAction = expectedActions[i];
			if (actionsInForm.contains(expectedAction)) {
				actionsInForm.remove(expectedAction);
			}
			else {
				left.add(expectedAction);
			}
		}			

		if (!left.isEmpty()) {
			fail(XavaResources.getString("actions_expected", left));
		}
		if (!actionsInForm.isEmpty()) {
			fail(XavaResources.getString("actions_not_expected", actionsInForm));
		}
	} 
	
	private Collection<String> getActions() throws Exception { 
		String dialog = getTopDialog();
		if (dialog == null) return getActions(getElementById("core"));
		return getActions(getElementById(dialog));		
	}	
	
	private Collection<String> getActions(WebElement el) { 		
		Collection<WebElement> hiddens = driver.findElements(By.cssSelector("input[type='hidden']"));
		Set actions = new HashSet();		
		for (WebElement input: hiddens) {
			if (!input.getAttribute("name").startsWith(Ids.decorate("openxavatest", module, ACTION_PREFIX))) continue;
			String actionName = removeActionPrefix(input.getAttribute("name"));
			actions.add(removeActionPrefix(input.getAttribute("name")));
		}	
		return actions;				
	}
		
	private String removeActionPrefix(String action) {
		String bareAction = Ids.undecorate(action);
		return bareAction.substring(ACTION_PREFIX.length() + 1);
	}
	
	private void assertContentTypeForPopup(String expectedContentType) {
		for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
        }
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("embed")));
		String contentType = driver.findElement(By.tagName("embed")).getAttribute("type"); // This works for PDF with Chrome
		assertEquals(expectedContentType, contentType);
	}

}
