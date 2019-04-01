package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailsWithSectionsTest extends CustomizeListTestBase {
	
	public InvoiceDetailsWithSectionsTest(String testName) {
		super(testName, "InvoiceDetailsWithSections");		
	}
	
	public void testFocusInDialogWithAllMembersInSections_indexOfOutBoundInList() throws Exception { 
		// In the next order to reproduce a bug in the second assert that only occurs if we execute the first assert before
		assertIndexOfOutBoundInList(); 
		assertFocusInDialogWithAllMembersInSections();
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
		// moveColumn() is better than JavaScript, but for this case it didn't work, so we use JS + reload, enough to reproduce the error
		getHtmlPage().executeJavaScript("Tab.moveProperty('ox_OpenXavaTest_InvoiceDetailsWithSections__list', 6, 1)"); 
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
	}
	
	private void assertFocusInDialogWithAllMembersInSections() throws Exception {   
		execute("List.viewDetail", "row=0");
		execute("Invoice.editDetailWithSections", "row=0,viewObject=xava_view_details");
		assertFocusOn("serviceType");
	}
	
	private void assertIndexOfOutBoundInList() throws Exception { 
		// A bug only reproducible following the next EXACT steps
		
		assertListColumnCount(8);
		removeColumn(7);
		assertListColumnCount(7);
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