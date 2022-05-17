package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class SellerJSPTest extends ModuleTestBase {
	
	public SellerJSPTest(String testName) {
		super(testName, "SellerJSP");		
	}
	
	public void testHandmadeWebView() throws Exception {
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("name", "");
		assertValue("level.id", "");
		assertValue("level.description", "");
		assertEditable("number");
		assertEditable("name");		
		assertEditable("level.id");
		assertNoEditable("level.description");
		setValue("number", "66");
		setValue("name", "JUNIT");
		assertValue("level.description", "");
		setValue("level.id", "A");
		assertValue("level.description", "MANAGER");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("number", "");
		assertValue("name", "");
		assertValue("level.id", "");
		assertValue("level.description", "");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT");
		assertValue("level.id", "A");
		assertValue("level.description", "MANAGER");		
		assertNoEditable("number");
		assertEditable("name");
		assertEditable("level.id");
		assertNoEditable("level.description");
				
		execute("CRUD.delete");
		assertMessage("Seller deleted successfully");
	}
	
	public void testDisplaySizeOfReferenceMemberInHandmadeView() throws Exception { 
		execute("CRUD.new");		
		assertTrue("Size for seller level must be 25", getHtml().indexOf("maxlength=\"50\" size=\"25\"") >= 0);
	}
			
}
