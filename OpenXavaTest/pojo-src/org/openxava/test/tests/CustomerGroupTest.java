package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 *  
 * @author Javier Paniza
 */

public class CustomerGroupTest extends ModuleTestBase {
	
	public CustomerGroupTest(String testName) {
		super(testName, "CustomerGroup");		
	}
			
	public void testPropertyOfRerenceInsideAggregateInCascadeRemoveCollection() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValueInCollection("customers", 0, "address.state.name", "KANSAS");
	}
	
			
}
