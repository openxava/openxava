package org.openxava.test.tests.bymodule;

/**
 * 
 * @author Chungyen Tsai
 */

public class SellerWithSearchListConditionReformattedTest extends CustomizeListTestBase {
		
	public SellerWithSearchListConditionReformattedTest(String testName) {
		super(testName, "SellerSearchListConditionReformatted");		
	}
	
	public void testSearchListCondition_moveColumns() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Collection.add", "viewObject=xava_view_customers"); 
		assertListRowCount(1);
		assertValueInList(0, 0, "Javi");
	}
	
}
