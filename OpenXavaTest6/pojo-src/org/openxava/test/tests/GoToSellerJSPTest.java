package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class GoToSellerJSPTest extends ModuleTestBase {
	
	public GoToSellerJSPTest(String testName) {
		super(testName, "GoToSellerJSP");		
	}
	
	public void testDialogInAModuleWithCustomizedViewCalledFromAnotherModule() throws Exception {
		assertValue("name", ""); 
		execute("GoToSellerJSP.goSellerJSP");
		execute("List.viewDetail", "row=0");
		assertValue("name", "MANUEL CHAVARRI"); 
		assertNoDialog();
		execute("SellerJSP.changeName");
		assertDialog();
		setValue("name", "MANOLO");
		execute("ChangeName.change");
		assertNoDialog();
		assertValue("name", "MANOLO");
	}
			
}
