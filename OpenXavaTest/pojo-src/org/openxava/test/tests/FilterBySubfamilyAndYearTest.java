package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class FilterBySubfamilyAndYearTest extends ModuleTestBase {
	
	public FilterBySubfamilyAndYearTest(String testName) {
		super(testName, "FilterBySubfamilyAndYear");		
	}
	
	public void testTransientClassInheritance() throws Exception { 
		assertExists("year"); 
		assertExists("subfamily.number");		
	}
		
}
