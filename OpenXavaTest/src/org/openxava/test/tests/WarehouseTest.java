package org.openxava.test.tests;

import org.openxava.tests.*;
import org.openxava.util.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class WarehouseTest extends ModuleTestBase {
	
		
	public WarehouseTest(String testName) {
		super(testName, "Warehouse");		
	}
	
	public void testToolTip_defaultListLabels() throws Exception { 
		assertLabelInList(0, "Zone"); 
		assertLabelInList(1, "Warehouse number");
		assertLabelInList(2, "Name"); 
		execute("CRUD.new");	
		assertToolTip("number", "Id number of the warehouse");
		assertToolTip("zoneNumber", ""); 
	}

	private void assertToolTip(String property, String value) {
		HtmlElement number = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Warehouse__" + property); 
		assertEquals(value, number.getAttribute("title"));
	}
	
	public void testSortByTwoColumns() throws Exception {
		execute("List.orderBy", "property=number"); 
		execute("List.orderBy", "property=zoneNumber");
		assertValueInList(0, 0, "1"); assertValueInList(0, 1, "1");
		assertValueInList(1, 0, "1"); assertValueInList(1, 1, "2");
		assertValueInList(2, 0, "1"); assertValueInList(2, 1, "3");
		assertValueInList(3, 0, "2"); assertValueInList(3, 1, "1");
		assertValueInList(4, 0, "3"); assertValueInList(4, 1, "1");
		assertValueInList(5, 0, "4"); assertValueInList(5, 1, "2");
		assertValueInList(6, 0, "4"); assertValueInList(6, 1, "3");
		assertValueInList(7, 0, "4"); assertValueInList(7, 1, "4");
		assertValueInList(8, 0, "4"); assertValueInList(8, 1, "5");		
		assertValueInList(9, 0, "4"); assertValueInList(9, 1, "6");

		execute("List.orderBy", "property=zoneNumber");
		assertValueInList(0, 0, "10"); assertValueInList(0, 1, "10"); 
		assertValueInList(1, 0, "7"); assertValueInList(1, 1, "1"); 
		assertValueInList(2, 0, "7"); assertValueInList(2, 1, "2");
		assertValueInList(3, 0, "7"); assertValueInList(3, 1, "3");
		assertValueInList(4, 0, "7"); assertValueInList(4, 1, "4");
		assertValueInList(5, 0, "7"); assertValueInList(5, 1, "5");
		assertValueInList(6, 0, "7"); assertValueInList(6, 1, "6");
		assertValueInList(7, 0, "7"); assertValueInList(7, 1, "7");
		assertValueInList(8, 0, "7"); assertValueInList(8, 1, "8");		
		assertValueInList(9, 0, "7"); assertValueInList(9, 1, "9");

		execute("List.orderBy", "property=number");
		assertValueInList(0, 0, "7"); assertValueInList(0, 1, "1");
		assertValueInList(1, 0, "6"); assertValueInList(1, 1, "1");
		assertValueInList(2, 0, "5"); assertValueInList(2, 1, "1");
		assertValueInList(3, 0, "3"); assertValueInList(3, 1, "1");
		assertValueInList(4, 0, "2"); assertValueInList(4, 1, "1");
		assertValueInList(5, 0, "1"); assertValueInList(5, 1, "1");
		assertValueInList(6, 0, "7"); assertValueInList(6, 1, "2");
		assertValueInList(7, 0, "6"); assertValueInList(7, 1, "2");
		assertValueInList(8, 0, "5"); assertValueInList(8, 1, "2");		
		assertValueInList(9, 0, "4"); assertValueInList(9, 1, "2");
		
		execute("List.orderBy", "property=zoneNumber");
		assertValueInList(0, 0, "10"); assertValueInList(0, 1, "10");
		assertValueInList(1, 0, "7"); assertValueInList(1, 1, "1");
		assertValueInList(2, 0, "7"); assertValueInList(2, 1, "2");
		assertValueInList(3, 0, "7"); assertValueInList(3, 1, "3");
		assertValueInList(4, 0, "7"); assertValueInList(4, 1, "4");
		assertValueInList(5, 0, "7"); assertValueInList(5, 1, "5");
		assertValueInList(6, 0, "7"); assertValueInList(6, 1, "6");
		assertValueInList(7, 0, "7"); assertValueInList(7, 1, "7");
		assertValueInList(8, 0, "7"); assertValueInList(8, 1, "8");		
		assertValueInList(9, 0, "7"); assertValueInList(9, 1, "9");		
	}
	
	public void testChangePageRowCount() throws Exception { 
		assertChangeRowCount(10, 5); 		
		resetModule(); 
		assertChangeRowCount(5, 10); 
		
		String value5 = getValueInList(5, 2); 
		String value9 = getValueInList(9, 2);
		execute("List.goNextPage");
				
		assertChangeRowCount(10, 5);
		assertValueInList(0, 2, value5);
		assertValueInList(4, 2, value9);
		
		assertChangeRowCount(5, 10);
		
		execute("List.goPage", "page=7");
		assertListRowCount(3); 
		String value60 = getValueInList(0, 2);
		String value62 = getValueInList(2, 2);
		execute("List.goPage", "page=6");
		
		assertChangeRowCount(10, 5);
		execute("List.goPage", "page=11");
		
		assertChangeRowCount(5, 10, 3); 
		assertValueInList(0, 2, value60);
		assertValueInList(2, 2, value62);		
	}
	
	private void assertChangeRowCount(int initialRowCount, int finalRowCount) throws Exception, InterruptedException {
		assertChangeRowCount(initialRowCount, finalRowCount, -1);
	}

	private void assertChangeRowCount(int initialRowCount, int finalRowCount, int finalRowCountWithData) throws Exception, InterruptedException {
		if (finalRowCountWithData < 0) finalRowCountWithData = finalRowCount;
		HtmlSelect combo = (HtmlSelect) getHtmlPage().getElementById(decorateId("list_rowCount"));
		assertListRowCount(initialRowCount);
		String comboRowCount = combo.getSelectedOptions().get(0).getAttribute("value");
		assertEquals(String.valueOf(initialRowCount), comboRowCount);
		combo.setSelectedAttribute(String.valueOf(finalRowCount), true);
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000); 
		assertListRowCount(finalRowCountWithData);
		comboRowCount = combo.getSelectedOptions().get(0).getAttribute("value");
		assertEquals(String.valueOf(finalRowCount), comboRowCount);
	}
	
	public void testDefaultAction() throws Exception {
		assertListRowCount(10);
		setConditionValues(new String [] { "1" });
		executeDefaultAction();
		assertListRowCount(3); 
		
		execute("CRUD.new");
		executeDefaultAction();
		assertError("Value for Name in Warehouse is required"); // It tried to execute "CRUD.save", the default action
	}
	
	public void testChooseUnselectedRow() throws Exception { 
		checkRow(0);
		String warehouseName=getValueInList(1, "name");	
		assertTrue("Warehouse of row 1 must have name", !Is.empty(warehouseName));
		execute("List.viewDetail", "row=1");		
		assertNoErrors();
		assertValue("name", warehouseName);
	}
	
	public void testChangePageRowCountInTab_listTitle() throws Exception {
		assertListTitle("Warehouse report"); 
		assertListRowCount(10);
		execute("Warehouse.changePageRowCount");
		assertListRowCount(20);
		assertChangeRowCount(20, 10); 
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// Create
		execute("CRUD.new");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", "WAREHOUSE JUNIT");
		execute("CRUD.save");
		assertNoErrors();
		
		// Verifying form is clean
		assertValue("zoneNumber", "");
		assertValue("number", "");		
		assertValue("name", "");
		// Search
		setValue("zoneNumber", "66");
		setValue("number", "666");
		execute("CRUD.refresh");
		assertValue("zoneNumber", "66");
		assertValue("number", "666");		
		assertValue("name", "WAREHOUSE JUNIT");
		// Modify
		setValue("name", "WAREHOUSE JUNIT MODIFIED");
		execute("CRUD.save");
		// Verifying form is clean
		assertValue("zoneNumber", "");
		assertValue("number", "");		
		assertValue("name", "");
		// Verifying modified
		setValue("zoneNumber", "66");
		setValue("number", "666");
		execute("CRUD.refresh");
		assertValue("zoneNumber", "66");
		assertValue("number", "666");		
		assertValue("name", "WAREHOUSE JUNIT MODIFIED");
				
		// Delete
		execute("CRUD.delete");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");		
		assertMessage("Warehouse deleted successfully");
		
		// Verifying is deleted
		execute("CRUD.new");
		setValue("zoneNumber", "66");
		setValue("number", "666");				
		execute("CRUD.refresh");		
		assertError("Object of type Warehouse does not exists with key Warehouse number:666, Zone:66");
		assertErrorsCount(1);		
	}
	
	public void testNotLoseFilterOnChangeMode() throws Exception {
		assertListRowCount(10);
		setConditionValues(new String [] {"1"} );
		execute("List.filter");
		assertListRowCount(3);  
		execute("List.viewDetail", "row=0"); 
		execute("Mode.list");
		assertListRowCount(3);
	}
	
	public void testFilterFromNoFirstPage() throws Exception { 
		execute("List.goPage", "page=2");
		String [] condition = {
				"", "2"
		};
		setConditionValues(condition);
		execute("List.filter");
		assertListRowCount(5); 
	}
	
	public void testRememberListPage() throws Exception { 
		assertListRowCount(10);
		assertNoAction("List.goPreviousPage");
		execute("List.goPage", "page=2");
		assertListRowCount(10);
		assertAction("List.goPreviousPage"); 
		execute("List.viewDetail", "row=10");
		execute("Mode.list");
		assertListRowCount(10);
		assertAction("List.goPreviousPage");
	}
	
	public void testCheckUncheckRows() throws Exception { 
		checkRow(1);
		execute("List.goNextPage");
		assertNoErrors();
		checkRow(12);
		execute("List.goPreviousPage");
		assertNoErrors();
		assertRowChecked(1);
		uncheckRow(1);
		assertRowUnchecked(1);
		execute("List.goNextPage");
		assertNoErrors();
		assertRowChecked(12);
		execute("List.goPreviousPage");
		assertNoErrors();
		assertRowUnchecked(1);		
	}
	
	public void testSaveExisting() throws Exception {
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// Create
		execute("CRUD.new");		
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", "WAREHOUSE JUNIT");		
		execute("CRUD.save");		
		// Verifying form is clean
		assertValue("zoneNumber", "");
		assertValue("number", "");		
		assertValue("name", "");
		// Try to re-create
		execute("CRUD.new");		
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", "WAREHOUSE JUNIT");
		execute("CRUD.save");		
		
		assertError("Impossible to create: an object with that key already exists");
		
		// Delete
		setValue("zoneNumber", "66");
		setValue("number", "666");
		execute("CRUD.refresh");		
		execute("CRUD.delete");		
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");

		// Verifying is deleted
		execute("CRUD.new");		
		setValue("zoneNumber", "66");
		setValue("number", "666");				
		execute("CRUD.refresh");				
		assertError("Object of type Warehouse does not exists with key Warehouse number:666, Zone:66");		
	}
				
	public void testClickOneInListMode_noModeActions() throws Exception { 
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// No mode actions
		assertNoAction("Mode.detailAndFirst");
		assertNoAction("Mode.list");
		assertNoAction("Mode.split");

		String zoneNumber = getValueInList(3, "zoneNumber");
		String number = getValueInList(3, "number");
		String name = getValueInList(3, "name"); 
		execute("List.viewDetail", "row=3");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		assertValue("zoneNumber", zoneNumber);
		assertValue("number", number);
		assertValue("name", name);
		
		// Only Mode.list
		assertNoAction("Mode.detailAndFirst");
		assertAction("Mode.list");
		assertNoAction("Mode.split");
	}
	
	public void testListNavigation_ChooseVarious_NavigateInChoosed() throws Exception {		 
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		String zoneNumber1 = getValueInList(0, "zoneNumber");
		String number1 = getValueInList(0, "number");
		String name1 = getValueInList(0, "name"); 
		checkRow(0);
		execute("List.goNextPage");
		String zoneNumber2 = getValueInList(0, "zoneNumber");
		String number2 = getValueInList(0, "number");
		String name2 = getValueInList(0, "name");
		checkRow(10);
		execute("List.goNextPage");
		String zoneNumber3 = getValueInList(1, "zoneNumber");
		String number3 = getValueInList(1, "number");
		String name3 = getValueInList(1, "name");
		checkRow(21);
		String zoneNumber4 = getValueInList(3, "zoneNumber");
		String number4 = getValueInList(3, "number");
		String name4 = getValueInList(3, "name");
		checkRow(23); 

		String zoneNumberClicked = getValueInList(0, "zoneNumber");
		String numberClicked = getValueInList(0, "number");
		String nameClicked = getValueInList(0, "name");		
		execute("List.viewDetail", "row=20");
		assertValue("zoneNumber", zoneNumberClicked);
		assertValue("number", numberClicked);
		assertValue("name", nameClicked);		
		execute("Navigation.next");
		
		assertValue("zoneNumber", zoneNumber1);
		assertValue("number", number1);
		assertValue("name", name1);
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber2); 
		assertValue("number", number2);
		assertValue("name", name2);
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber3);
		assertValue("number", number3);
		assertValue("name", name3);
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber4);
		assertValue("number", number4);
		assertValue("name", name4);			
		execute("Navigation.next");
		assertError("No more elements in list");
		execute("Navigation.previous"); // In 3
		execute("Navigation.previous"); // In 2
		execute("Navigation.previous"); // In 1
		assertValue("zoneNumber", zoneNumber1);
		assertValue("number", number1);
		assertValue("name", name1);
		execute("Navigation.previous");
		assertError("We already are at the beginning of the list"); 
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber2);
		assertValue("number", number2);
		assertValue("name", name2);
		execute("Navigation.first");
		assertValue("zoneNumber", zoneNumber1);
		assertValue("number", number1);
		assertValue("name", name1);							
	}
	
	public void testRememberSelected() throws Exception { 
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		checkRow(0);
		execute("List.goNextPage");
		checkRow(10);
		checkRow(12);
		execute("List.goPreviousPage");
		assertRowChecked(0);
		execute("List.goNextPage");
		assertRowsChecked(10, 12);
	}
	
	public void testDefaulActionInListNotReturnToDetail() throws Exception {  
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		executeDefaultAction(); // Execute search and not new 
		assertNoErrors(); 
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
	}
	
	public void testValidation() throws Exception { 
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// Create
		execute("CRUD.new");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", ""); // and the name is required
		execute("CRUD.save");
		
		assertError("Value for Name in Warehouse is required");		
	}	
	
		
}
