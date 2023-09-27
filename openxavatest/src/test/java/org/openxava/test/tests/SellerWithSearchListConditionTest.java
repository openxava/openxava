package org.openxava.test.tests;

/**
 * 
 * @author Federico Alcantara
 */

public class SellerWithSearchListConditionTest extends CustomizeListTestBase {
		
	public SellerWithSearchListConditionTest(String testName) {
		super(testName, "SellerSearchListCondition");		
	}
	
	public void testSearchListCondition_moveColumns() throws Exception {
		changeModule("SellerSearchListCondition");
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=level.id");
		assertListRowCount(2);
		assertLabelInList(0, "Id");
		assertLabelInList(1, "Description");
		moveColumn(0, 1);
		assertLabelInList(0, "Description"); // TMR FALLA
		assertLabelInList(1, "Id");
		resetModule();
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=level.id");
		assertLabelInList(0, "Description");
		assertLabelInList(1, "Id");
		closeDialog();
		execute("Collection.add", "viewObject=xava_view_customers");
		assertListRowCount(4);
	}

	public void testSearchListConditionOff() throws Exception {  
		changeModule("SellerSearchListConditionOff");
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=level.id");
		assertListRowCount(3);
		closeDialog();

		execute("Collection.add", "viewObject=xava_view_customers");
		assertListRowCount(5); 
	}

	public void testSearchListConditionBlank() throws Exception {
		changeModule("SellerSearchListConditionBlank");
		execute("List.viewDetail", "row=0");
		execute("Reference.search", "keyProperty=level.id");
		assertListRowCount(3);
		closeDialog();

		execute("Collection.add", "viewObject=xava_view_customers");
		assertListRowCount(4);
	}
	
}
