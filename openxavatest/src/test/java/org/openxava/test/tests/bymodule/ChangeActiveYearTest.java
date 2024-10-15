package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class ChangeActiveYearTest extends ModuleTestBase {
	
	public ChangeActiveYearTest(String testName) {
		super(testName, "ChangeActiveYear");		
	}
	
	public void testCalculatedCollectionInATransientClass_changeModuleToPreviousModuleExecutesReinitAction() throws Exception { // tmr changeModuleToPreviousModuleExecutesReinitAction
		assertCollectionRowCount("months", 12);
		assertValueInCollection("months", 0, 0, "ENERO");
		assertValueInCollection("months", 0, 1, "31");
		assertValueInCollection("months", 1, 0, "FEBRERO");
		assertValueInCollection("months", 1, 1, "28");		
		
		// tmr ini
		assertMessage("The active year is 2004");
		execute("ChangeActiveYear.goInvoiceActiveYear");
		execute("ReturnPreviousModuleWithReinit.return");
		assertMessage("The active year is 2004");
		// tmr fin
	}
	
}
