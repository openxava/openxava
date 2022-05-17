package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */
public class FamilyRangeProductsReportTest extends ModuleTestBase {
	
	
	public FamilyRangeProductsReportTest(String testName) {
		super(testName, "FamilyRangeProductsReport");		
	}

	public void testDifferentLabelsForReferenceMembers_refiningSearchOnChangeAction_calculatedPropertiesInTransientModel() throws Exception {
		// Different labels for reference members
		assertLabel("subfamily.number", "Number");
		assertLabel("subfamilyTo.number", "Number to");
		
		// Refining search on change action
		setValue("subfamily.number", "0"); // The override search action change 0 by 1.
		assertNoErrors();
		assertValue("subfamily.number", "1"); 
		assertValue("subfamily.description", "DESARROLLO");		
		
		// Calculated properties on transient model
		assertValue("rangeDescription", "FROM SUBFAMILY 1 TO SUBFAMILY 0");
		setValue("subfamilyTo.number", "2");
		assertValue("rangeDescription", "FROM SUBFAMILY 1 TO SUBFAMILY 2");
	}
		
}
