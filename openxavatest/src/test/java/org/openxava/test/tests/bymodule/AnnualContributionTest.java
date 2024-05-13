package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class AnnualContributionTest extends ModuleTestBase {
	
	private boolean modulesLimit = true;
	private boolean moduleURLOnRoot = false; 
	
	public AnnualContributionTest(String testName) {
		super(testName, "AnnualContribution");		
	}
			
	public void testCalculationInElementCollection_doubleNotAddExtraRow() throws Exception {
		// By now we have no records, so we enter directly in detail mode
		setValue("year", "2024");
		setValueInCollection("contributions", 0, "amount", "100");
		setValueInCollection("contributions", 0, "pieces", "2");
		assertValueInCollection("contributions", 0, "total", "200.00");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("contributions", 1);
		execute("CRUD.delete");
	}
				
}
