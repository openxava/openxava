package org.openxava.test.tests.bymodule;

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
	public void testListWithKeyOf2levelReferenceWithNameIsContainedInTheReference_viewInModuleFromParentButNotInCurrentEntity() throws Exception {  	
		// The view in module from parent but not in current entity case fails breaking the module on starting
		
		assertNoErrors();
		assertListRowCount(1);
		assertValueInList(0, 1, "1");
	}
		
			
}
