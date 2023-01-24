package org.openxava.test.tests;

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
			
	public void testCalculationInElementCollection() throws Exception {
		// By now we have no records, so we enter directly in detail mode
		setValueInCollection("contributions", 0, "amount", "100");
		setValueInCollection("contributions", 0, "pieces", "2");
		assertValueInCollection("contributions", 0, "total", "200.00");
	}
				
}
