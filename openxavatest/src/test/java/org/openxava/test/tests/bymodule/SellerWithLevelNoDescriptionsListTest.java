package org.openxava.test.tests.bymodule;

/**
 * tmr
 * @author Javier Paniza
 */

public class SellerWithLevelNoDescriptionsListTest extends CustomizeListTestBase { 
		
	public SellerWithLevelNoDescriptionsListTest(String testName) {
		super(testName, "SellerWithLevelNoDescriptionsList");		
	}
	
	public void testOnChangeInReferenceWithTabWithBaseCondition() throws Exception {
		execute("CRUD.new");
		assertNoDialog();
		execute("Reference.search", "keyProperty=level.id"); 
		assertListRowCount(2); // So it has baseCondition
	}
				
}
