package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class GoShowSellerTest extends ModuleTestBase {
	
	public GoShowSellerTest(String testName) {
		super(testName, "GoShowSeller");		
	}
	
	public void testTwoLevelDialogFromJSPViewCalledFromAnotherModule() throws Exception { 
		assertNotExists("number"); 
		assertNoAction("ShowSeller.show");
		execute("ShowSeller.goShowSeller");
		assertExists("number");
		setValue("number", "1");		
		execute("ShowSeller.show");
		assertDialog();
		assertValue("number", "1"); 
		assertValue("name", "MANUEL CHAVARRI");
		assertAction("ModifySeller.modify"); 
		execute("Reference.modify", "model=SellerLevel,keyProperty=level.id");
		assertValue("id", "A");
		assertValue("description", "MANAGER");
		assertNoAction("ModifySeller.modify");
		assertAction("Modification.update");
		execute("Modification.cancel");
		assertValue("number", "1");		
		assertExists("name"); // This was the fail, that did not restore the correct view
		assertValue("name", "MANUEL CHAVARRI");
		assertNoAction("Modification.update");
		assertAction("ModifySeller.modify");
		execute("Dialog.cancel");
		assertNoDialog();
		assertValue("number", "1");
		assertNotExists("name");
		assertNoAction("Modification.update");
		assertNoAction("ModifySeller.modify");
		assertAction("ShowSeller.show");
	}
		
}
