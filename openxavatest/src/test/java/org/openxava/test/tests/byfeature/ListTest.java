package org.openxava.test.tests.byfeature;

import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * To test lists and collections related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ListTest extends WebDriverTestBase {
	
	private final static String ACTION_PREFIX = "action";
	
	private WebDriver driver;

	public void setUp() throws Exception {
		setHeadless(true); 
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testListAndCollection() throws Exception {
		goModule(driver, "Author");
		assertShowHideFilterInList();
		assertMoveColumns();
		assertRemoveColumnAfterFiltering(); 
		assertNoFilterInCollectionByDefault();

		goModule(driver, "Carrier");
		assertEnableDisableCustomizeList(); 
		assertCustomizeCollection();

		goModule(driver, "CustomerWithSection");
		assertCustomizeList();
		assertCustomizeList_addAndResetModule();
		
		goModule(driver, "Invoice");
		assertRemoveSeveralColumns();
	}
		
	private void assertNoFilterInCollectionByDefault() throws Exception {
		execute(driver, "CRUD.new");		
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute(driver, "MyGoListMode.list");
		execute(driver, "List.viewDetail", "row=1");
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		
		execute(driver, "CRUD.new");
		assertCollectionFilterNotDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__show_filter_humans")).click();
		assertCollectionFilterDisplayed();
		driver.findElement(By.id("ox_openxavatest_Author__hide_filter_humans")).click();
		Thread.sleep(1000);
		assertCollectionFilterNotDisplayed();
	}
	
	private void assertMoveColumns() throws Exception { 
		// To test a specific bug moving columns
		assertLabelInList(driver, 0, "Author");
		assertLabelInList(driver, 1, "Biography");
		
		showCustomizeControls();
		moveColumn(driver, 0, 1);
		assertLabelInList(driver, 0, "Biography"); 
		assertLabelInList(driver, 1, "Author");
		
		resetModule();
		assertLabelInList(driver, 0, "Biography");
		assertLabelInList(driver, 1, "Author");
		
		showCustomizeControls();
		moveColumn(driver, 1, 0);
		assertLabelInList(driver, 0, "Author");
		assertLabelInList(driver, 1, "Biography");		
	}
	
	private void assertRemoveColumnAfterFiltering() throws Exception {
		assertListRowCount(driver, 2);
		assertListColumnCount(driver, 2);
		setConditionValues("J");
		execute(driver, "List.filter");
		assertListRowCount(driver, 1);
		showCustomizeControls();
		removeColumn(driver, 1);
		assertListRowCount(driver, 1);
		assertListColumnCount(driver, 1); 
		execute(driver, "List.addColumns"); 
		execute(driver, "AddColumns.restoreDefault"); 
		assertListColumnCount(driver, 2);
		assertListRowCount(driver, 1);
		clearListCondition();
	}
	
	private void assertShowHideFilterInList() throws Exception {
		goModule(driver, "Author");
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
		WebElement removeColumn0 = driver.findElement(By.cssSelector(".xava_remove_column[data-column='ox_openxavatest_Carrier__list_col0']"));
		WebElement column1 = driver.findElement(By.id("ox_openxavatest_Carrier__list_col1"));
		WebElement moveColumn1 = column1.findElement(By.cssSelector("i[class='xava_handle mdi mdi-cursor-move ui-sortable-handle']")); 
		WebElement removeColumn1 = driver.findElement(By.cssSelector(".xava_remove_column[data-column='ox_openxavatest_Carrier__list_col1']"));
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
		assertListColumnCount(driver, 3);
		assertLabelInList(driver, 0, "Calculated");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Name");
		execute(driver, "List.viewDetail", "row=0");
		
		assertCollectionColumnCount(driver, "fellowCarriers", 4);
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");		
		assertLabelInCollection(driver, "fellowCarriers", 2, "Remarks");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Calculated");

		// Customize the collection
		showCustomizeControls("fellowCarriers");
		moveColumn(driver, "fellowCarriers", 2, 3);
		assertNoErrors();
		assertCollectionColumnCount(driver, "fellowCarriers", 4);
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");
		assertLabelInCollection(driver, "fellowCarriers", 2, "Calculated"); 
		assertLabelInCollection(driver, "fellowCarriers", 3, "Remarks");		
		
		// The main list not modified
		execute(driver, "Mode.list");
		assertListColumnCount(driver, 3);
		assertLabelInList(driver, 0, "Calculated");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Name");

		// The collection continues modified
		execute(driver, "List.viewDetail", "row=0");
		assertCollectionColumnCount(driver, "fellowCarriers", 4);
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");
		assertLabelInCollection(driver, "fellowCarriers", 2, "Calculated");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Remarks");		

		// Add columns
		showCustomizeControls("fellowCarriers");
		execute(driver, "List.addColumns", "collection=fellowCarriers");
		assertNoAction("AddColumns.showMoreColumns"); // Because has not more than second level properties
		assertCollectionRowCount(driver, "xavaPropertiesList", 6);
		assertValueInCollection(driver, "xavaPropertiesList",  0, 0, "Driving licence description"); 
		assertValueInCollection(driver, "xavaPropertiesList",  1, 0, "Driving licence level");
		assertValueInCollection(driver, "xavaPropertiesList",  2, 0, "Driving licence type");
		assertValueInCollection(driver, "xavaPropertiesList",  3, 0, "Warehouse name");
		assertValueInCollection(driver, "xavaPropertiesList",  4, 0, "Warehouse number");
		assertValueInCollection(driver, "xavaPropertiesList",  5, 0, "Warehouse zone");
		checkRow("selectedProperties", "warehouse.name");
 		execute(driver, "AddColumns.addColumns");

		assertCollectionColumnCount(driver, "fellowCarriers", 5);
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");
		assertLabelInCollection(driver, "fellowCarriers", 2, "Calculated");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Remarks");				
		assertLabelInCollection(driver, "fellowCarriers", 4, "Warehouse"); // This is "Name of Warehouse" with label optimized 
		 		
		// Other customizations
		showCustomizeControls("fellowCarriers");
		moveColumn(driver, "fellowCarriers", 3, 4); 
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");
		assertLabelInCollection(driver, "fellowCarriers", 2, "Calculated");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection(driver, "fellowCarriers", 4, "Remarks");
 
		removeColumn(driver, "fellowCarriers", 4);
		assertCollectionColumnCount(driver, "fellowCarriers", 4); 
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");
		assertLabelInCollection(driver, "fellowCarriers", 2, "Calculated");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		
		// Changing column name
		execute(driver, "List.changeColumnName", "property=name,collection=fellowCarriers");
		assertDialog();
		assertValue("name", "Name");
		setValue("name", "Carrier");
		execute(driver, "ChangeColumnName.change");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Carrier");
		
		// Adding clicking in row
		showCustomizeControls("fellowCarriers");
		execute(driver, "List.addColumns", "collection=fellowCarriers"); 
		execute(driver, "AddColumns.addColumn", "property=warehouse.number");
		assertCollectionColumnCount(driver, "fellowCarriers", 5);
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection(driver, "fellowCarriers", 2, "Calculated");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Warehouse"); // This is "Name of Warehouse" with label optimized
		assertLabelInCollection(driver, "fellowCarriers", 4, "Warehouse number");
		
		// Restoring
		showCustomizeControls("fellowCarriers");
		execute(driver, "List.addColumns", "collection=fellowCarriers");
		execute(driver, "AddColumns.restoreDefault");
		assertCollectionColumnCount(driver, "fellowCarriers", 4);
		assertLabelInCollection(driver, "fellowCarriers", 0, "Number");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Carrier"); 
		assertLabelInCollection(driver, "fellowCarriers", 2, "Remarks");
		assertLabelInCollection(driver, "fellowCarriers", 3, "Calculated");
		
		showCustomizeControls("fellowCarriers");
		execute(driver, "List.changeColumnName", "property=name,collection=fellowCarriers");
		assertValue("name", "Carrier");
		setValue("name", "Name");
		execute(driver, "ChangeColumnName.change");
		assertLabelInCollection(driver, "fellowCarriers", 1, "Name");		
		
		// Cancel in AddColumns returns to detail (not list mode)
		showCustomizeControls("fellowCarriers");
		execute(driver, "List.addColumns", "collection=fellowCarriers");
		execute(driver, "AddColumns.cancel");
		assertValue("name", "UNO"); // In detail mode
	}
	
	private void assertCustomizeList_addAndResetModule() throws Exception {
		assertListColumnCount(driver, 7); 
		String value = getValueInList(driver, 0, 0);
		showCustomizeControls();
		execute(driver, "List.addColumns");		
		checkRow("selectedProperties", "number"); 		
		execute(driver, "AddColumns.addColumns");
		assertListColumnCount(driver, 8);
		assertValueInList(driver, 0, 0, value);
				
		resetModule();
		assertListColumnCount(driver, 8); 
		assertValueInList(driver, 0, 0, value);
		
		showCustomizeControls();
		removeColumn(driver, 7); 
		assertListColumnCount(driver, 7); 
	}
	
	private void assertRemoveSeveralColumns() throws Exception {
		assertListColumnCount(driver, 8); 
		assertLabelInList(driver, 0, "Year");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Date");
		assertLabelInList(driver, 3, "Amounts sum");
		assertLabelInList(driver, 4, "V.A.T.");
		assertLabelInList(driver, 5, "Details count");
		assertLabelInList(driver, 6, "Paid");
		assertLabelInList(driver, 7, "Importance");

		showCustomizeControls();
		removeColumn(driver, 2);
		assertListColumnCount(driver, 7); 
		assertLabelInList(driver, 0, "Year");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Amounts sum");
		assertLabelInList(driver, 3, "V.A.T.");
		assertLabelInList(driver, 4, "Details count");
		assertLabelInList(driver, 5, "Paid");
		assertLabelInList(driver, 6, "Importance");
		
		removeColumn(driver, 3); // VAT
		assertListColumnCount(driver, 6);
		assertLabelInList(driver, 0, "Year");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Amounts sum");
		assertLabelInList(driver, 3, "Details count");
		assertLabelInList(driver, 4, "Paid");
		assertLabelInList(driver, 5, "Importance");
		
		execute(driver, "List.filter");
		assertListColumnCount(driver, 6);
		assertLabelInList(driver, 0, "Year");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Amounts sum");
		assertLabelInList(driver, 3, "Details count");
		assertLabelInList(driver, 4, "Paid");
		assertLabelInList(driver, 5, "Importance");

		showCustomizeControls();
		execute(driver, "List.addColumns");
		execute(driver, "AddColumns.restoreDefault");
		assertListColumnCount(driver, 8);
		assertLabelInList(driver, 0, "Year");
		assertLabelInList(driver, 1, "Number");
		assertLabelInList(driver, 2, "Date");
		assertLabelInList(driver, 3, "Amounts sum");
		assertLabelInList(driver, 4, "V.A.T.");
		assertLabelInList(driver, 5, "Details count");
		assertLabelInList(driver, 6, "Paid");
		assertLabelInList(driver, 7, "Importance");		
		Thread.sleep(50000);
	}
	
	private void assertCustomizeList() throws Exception {
		doTestCustomizeList_moveAndRemove(); 
		setHeadless(false); // Because we test PDF generation that in headless works different, saving the file in the file system instead of show a windows
		resetModule(); 
		doTestCustomizeList_generatePDF();
		setHeadless(true); 
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
		assertListColumnCount(driver, 7); // 7 before
		assertLabelInList(driver, 0, "Name");
		assertLabelInList(driver, 1, "Type");
		assertLabelInList(driver, 2, "Seller");
		assertLabelInList(driver, 3, "Address city");
		assertLabelInList(driver, 4, "Seller level");
		assertLabelInList(driver, 5, "Address state");
		assertLabelInList(driver, 6, "Web site");
		 
		assertTrue("It is needed customers for execute this test", getListRowCount(driver) > 1);
		String name = getValueInList(driver, 0, 0);
		String type = getValueInList(driver, 0, 1);
		String seller = getValueInList(driver, 0, 2);
		String city = getValueInList(driver, 0, 3);
		String sellerLevel = getValueInList(driver, 0, 4);
		String state = getValueInList(driver, 0, 5);
		String site = getValueInList(driver, 0, 6);
		
		// move 0 to 2
		showCustomizeControls();
		moveColumn(driver, 0, 2);
		assertNoErrors();
		assertListColumnCount(driver, 7);
		assertLabelInList(driver, 0, "Type");
		assertLabelInList(driver, 1, "Seller");
		assertLabelInList(driver, 2, "Name");
		assertLabelInList(driver, 3, "Address city");
		assertLabelInList(driver, 4, "Seller level");
		assertLabelInList(driver, 5, "Address state");
		assertLabelInList(driver, 6, "Web site");		
		assertValueInList(driver, 0, 0, type);
		assertValueInList(driver, 0, 1, seller);
		assertValueInList(driver, 0, 2, name);
		assertValueInList(driver, 0, 3, city);
		assertValueInList(driver, 0, 4, sellerLevel);						
		assertValueInList(driver, 0, 5, state);
		assertValueInList(driver, 0, 6, site);		
		
		// move 2 to 4
		moveColumn(driver, 2, 4); 
		assertNoErrors();
		assertListColumnCount(driver, 7);
		assertLabelInList(driver, 0, "Type");
		assertLabelInList(driver, 1, "Seller");		
		assertLabelInList(driver, 2, "Address city");
		assertLabelInList(driver, 3, "Seller level");
		assertLabelInList(driver, 4, "Name");
		assertLabelInList(driver, 5, "Address state");
		assertLabelInList(driver, 6, "Web site");
		assertValueInList(driver, 0, 0, type);
		assertValueInList(driver, 0, 1, seller);
		assertValueInList(driver, 0, 2, city);
		assertValueInList(driver, 0, 3, sellerLevel);
		assertValueInList(driver, 0, 4, name);
		assertValueInList(driver, 0, 5, state);		
		assertValueInList(driver, 0, 6, site);		
		
		// remove column 3
		removeColumn(driver, 3); 
		assertNoErrors();
		assertListColumnCount(driver, 6);
		assertLabelInList(driver, 0, "Type");
		assertLabelInList(driver, 1, "Seller");		
		assertLabelInList(driver, 2, "Address city");		
		assertLabelInList(driver, 3, "Name");
		assertLabelInList(driver, 4, "Address state");
		assertLabelInList(driver, 5, "Web site");
		assertValueInList(driver, 0, 0, type);
		assertValueInList(driver, 0, 1, seller);
		assertValueInList(driver, 0, 2, city);
		assertValueInList(driver, 0, 3, name);
		assertValueInList(driver, 0, 4, state);
		assertValueInList(driver, 0, 5, site);		
						
		assertActions(listActions);
	}
	
	private void doTestCustomizeList_generatePDF() throws Exception {
		// Trusts in that testCustomizeList_moveAndRemove is executed before
		assertListColumnCount(driver, 6);
		assertLabelInList(driver, 0, "Type");
		assertLabelInList(driver, 1, "Seller");		
		assertLabelInList(driver, 2, "Address city");		
		assertLabelInList(driver, 3, "Name");
		assertLabelInList(driver, 4, "Address state");
		assertLabelInList(driver, 5, "Web site");
		showCustomizeControls();
		removeColumn(driver, 3); 
		assertNoErrors();
		assertListColumnCount(driver, 5);		
		execute(driver, "Print.generatePdf"); 
		assertContentTypeForPopup("application/pdf");
	}
	
	private void doTestRestoreColumns_addRemoveTabColumnsDynamically() throws Exception {
		// Restoring initial tab setup
		showCustomizeControls();
		execute(driver, "List.addColumns");							
		execute(driver, "AddColumns.restoreDefault");		
		// End restoring
		
		assertListColumnCount(driver, 7);
		assertLabelInList(driver, 0, "Name");
		assertLabelInList(driver, 1, "Type");
		assertLabelInList(driver, 2, "Seller");
		assertLabelInList(driver, 3, "Address city");
		assertLabelInList(driver, 4, "Seller level");
		assertLabelInList(driver, 5, "Address state"); 
		assertLabelInList(driver, 6, "Web site");
		assertTrue("Must to have customers for run this test", getListRowCount(driver) > 1);
		String name = getValueInList(driver, 0, 0);
		String type = getValueInList(driver, 0, 1);
		String seller = getValueInList(driver, 0, 2);
		String city = getValueInList(driver, 0, 3);
		String sellerLevel = getValueInList(driver, 0, 4);
		String state = getValueInList(driver, 0, 5); 
		String site = getValueInList(driver, 0, 6);
		
		execute(driver, "Customer.hideSellerInList");
		assertNoErrors();
		assertListColumnCount(driver, 6);
		assertLabelInList(driver, 0, "Name");
		assertLabelInList(driver, 1, "Type");
		assertLabelInList(driver, 2, "Address city");
		assertLabelInList(driver, 3, "Seller level");
		assertLabelInList(driver, 4, "Address state"); 
		assertLabelInList(driver, 5, "Web site");
		assertValueInList(driver, 0, 0, name);
		assertValueInList(driver, 0, 1, type);
		assertValueInList(driver, 0, 2, city);
		assertValueInList(driver, 0, 3, sellerLevel);
		assertValueInList(driver, 0, 4, state); 
		assertValueInList(driver, 0, 5, site);
		
		execute(driver, "Customer.showSellerInList");
		assertNoErrors();
		assertListColumnCount(driver, 7);
		assertLabelInList(driver, 0, "Name");
		assertLabelInList(driver, 1, "Type");
		assertLabelInList(driver, 2, "Seller");		
		assertLabelInList(driver, 3, "Address city");
		assertLabelInList(driver, 4, "Seller level");
		assertLabelInList(driver, 5, "Address state"); 
		assertLabelInList(driver, 6, "Web site");
		assertValueInList(driver, 0, 0, name);
		assertValueInList(driver, 0, 1, type);
		assertValueInList(driver, 0, 2, seller);
		assertValueInList(driver, 0, 3, city);
		assertValueInList(driver, 0, 4, sellerLevel);
		assertValueInList(driver, 0, 5, state); 
		assertValueInList(driver, 0, 6, site);
	}

	private void assertValue(String name, String value) { // Duplicated with DescriptionsListTest, refactoring pending
		assertEquals(XavaResources.getString("unexpected_value", name), value, getValue(name));		
	}

	private String getValue(String name) { // Duplicated with DescriptionsListTest, refactoring pending
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		return input.getAttribute("value");
	}
	
	private void setValue(String name, String value) { // Duplicated with DescriptionsListTest, refactoring pending
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		input.clear();
		input.sendKeys(value);	
	}

	private void assertNoAction(String qualifiedAction) {
		String [] action = qualifiedAction.split("\\.");
		String name = "ox_openxavatest_" + module + "__action___" + action[0] + "___" + action[1];
		assertTrue(XavaResources.getString("action_found_in_ui", action), driver.findElements(By.name(name)).isEmpty());
	}

	private void assertNoErrors() { // Duplicated with DescriptionsListTest, refactoring pending
		WebElement errors = driver.findElement(By.id("ox_openxavatest_" + module + "__errors"));
		assertEquals(XavaResources.getString("unexpected_messages", "Errors"), "", errors.getText());
	}

	private void clearListCondition() throws Exception{
		driver.findElement(By.id("ox_openxavatest_Author__xava_clear_condition")).click();
		wait(driver);
	}

	private void setConditionValues(String value) { // One argument by now, but we could evolution to String ... value 
		driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___0")).sendKeys(value); 		
	}
	
	private void resetModule() throws Exception {
		driver.quit();
		driver = createWebDriver();
		driver.get("http://localhost:8080/openxavatest/m/" + module); 
		wait(driver);
	}


	
	private void showCustomizeControls() {
		showCustomizeControls("list");
	}
	
	private void showCustomizeControls(String collection) {
		driver.findElement(By.id("ox_openxavatest_" + module + "__customize_" + collection)).click();
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
		Collection<String> left = new ArrayList<>();		
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
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(3000));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("embed")));
		String contentType = driver.findElement(By.tagName("embed")).getAttribute("type"); // This works for PDF with Chrome
		assertEquals(expectedContentType, contentType);
	}

}
