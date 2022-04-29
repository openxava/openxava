package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class ReallocationDetailsNoProductTest extends ModuleTestBase {
	
	public ReallocationDetailsNoProductTest(String testName) {
		super(testName, "ReallocationDetailsNoProduct");		
	}	
	
	public void testDefaultValueCalculatorInANotIncludedPropertyInElementCollection() throws Exception {
		assertNoErrors(); // The @DefaultValueCalculator of product in ReallocationDetail does not fail, even if product is not in @DescriptionsList
	}
	
}
