package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class SlotMachineTest extends ModuleTestBase {
	
	private boolean modulesLimit = true;
	
	public SlotMachineTest(String testName) {
		super(testName, "SlotMachine");		
	}
	
	// concept.accountForConcept.account fails because accountForConcept (the reference) contains account (the final field that is key) 
	public void testListWithKeyOf2levelReferenceWithNameIsContainedInTheReference() throws Exception {  	
		assertNoErrors();
		assertListRowCount(1);
		assertValueInList(0, 1, "1");
	}
		
			
}
