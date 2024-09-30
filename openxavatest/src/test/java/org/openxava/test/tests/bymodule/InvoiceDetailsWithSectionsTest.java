package org.openxava.test.tests.bymodule;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailsWithSectionsTest extends CustomizeListTestBase {
	
	public InvoiceDetailsWithSectionsTest(String testName) {
		super(testName, "InvoiceDetailsWithSections");		
	}
	
	public void testFocusInDialogWithAllMembersInSections_indexOfOutBoundInList_onChangeOnceWhenSectionWithGroupsInCollectionElementDialog() throws Exception {  
		// In the next order to reproduce a bug in the second assert that only occurs if we execute the first assert before
		assertIndexOfOutBoundInList(); 
		assertFocusInDialogWithAllMembersInSections_onChangeOnceWhenSectionWithGroupsInCollectionElementDialog();
	}
	
	public void testMovingColumnsInListWithCalculatedProperties() throws Exception { 
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count"); 	
		assertLabelInList(6, "Paid");
		assertListRowCount(9); 
		setConditionValues("", "1");	
		execute("List.filter");
		assertListRowCount(3);
		execute("List.saveConfiguration");
		execute("SaveListConfiguration.save");
		// moveColumn() is better than JavaScript, but for this case it didn't work, so we use JS + reload, enough to reproduce the error
		getHtmlPage().executeJavaScript("Tab.moveProperty('ox_openxavatest_InvoiceDetailsWithSections__list', 6, 1)"); 
		waitAJAX();
		reload();
		
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Paid");
		assertLabelInList(2, "Number");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "Amounts sum");
		assertLabelInList(5, "V.A.T.");
		assertLabelInList(6, "Details count");
		
		selectListConfiguration("All");
		assertListRowCount(9);
		selectListConfiguration("Number = 1"); 
		assertListRowCount(3);
		
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Paid");
		assertLabelInList(2, "Number");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "Amounts sum");
		assertLabelInList(5, "V.A.T.");
		assertLabelInList(6, "Details count");
		
		moveColumn(4, 5);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Paid");
		assertLabelInList(2, "Number");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "V.A.T."); 
		assertLabelInList(5, "Amounts sum");
		assertLabelInList(6, "Details count");
		
		resetModule();
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Paid");
		assertLabelInList(2, "Number");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Amounts sum");
		assertLabelInList(6, "Details count");
		
		moveColumn(0, 1); 
		assertLabelInList(0, "Paid");
		assertLabelInList(1, "Year");
		assertLabelInList(2, "Number");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Amounts sum");
		assertLabelInList(6, "Details count");
		
		assertListRowCount(3); // Because latest Number = 1
		setConditionComparators("", "<", "=", "=");
		setConditionValues("true", "2007", "", "");
		execute("List.filter");
		assertListRowCount(6);
		
		clearCondition();
		removeColumn(1);
		reload(); // Remove column does not work completely with HtmlUnit so a reload is needed()
		assertLabelInList(0, "Paid");
		assertLabelInList(1, "Number"); 
		assertLabelInList(2, "Date");
		assertLabelInList(3, "V.A.T.");
		assertLabelInList(4, "Amounts sum");
		assertLabelInList(5, "Details count");
		
		assertListRowCount(9); 
		setConditionComparators("", "=", "=");
		setConditionValues("true", "1", "");
		execute("List.filter");
		assertListRowCount(3);
	}
	
	public void testOnChangeSearchActionNotAddingNonExistentRecordOfTabUsingId() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=customer.number");
		assertListRowCount(3);
		assertValueInList(2, 0, "3");
		execute("ReferenceSearch.cancel");
		setValue("customer.number", "4");
		assertListRowCount(3);
		execute("ReferenceSearch.choose","row=0");
		assertValue("customer.number", "1");
	}
	
	private void assertFocusInDialogWithAllMembersInSections_onChangeOnceWhenSectionWithGroupsInCollectionElementDialog() throws Exception {   
		execute("List.viewDetail", "row=0");
		execute("Invoice.editDetailWithSections", "row=0,viewObject=xava_view_details");
		assertFocusOn("serviceType");
		
		setValue("remarks", "A good product");
		assertMessage("OnChangeVoidAction executed");
		assertMessagesCount(1); 
	}
	
	private void assertIndexOfOutBoundInList() throws Exception { 
		// A bug only reproducible following the next EXACT steps
		
		assertListColumnCount(8);
		removeColumn(7);  
		execute("ListFormat.select", "editor=Charts");
		execute("Chart.selectType", "chartType=PIE");
		setValueInCollection("columns", 0, "name", "vatPercentage");
		execute("ListFormat.select", "editor=List");
		assertListColumnCount(7);
		execute("ListFormat.select", "editor=Charts");
		execute("ListFormat.select", "editor=List");
		assertListColumnCount(7);
	}
				
}