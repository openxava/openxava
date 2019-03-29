package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerSomeMembersReadOnlyTest extends ModuleTestBase {
		
	public CustomerSomeMembersReadOnlyTest(String testName) {
		super(testName, "CustomerSomeMembersReadOnly");
	}
	
	public void testReadOnly() throws Exception { 
		execute("CRUD.new");
		assertEditable("type");
		assertNoEditable("name");
		assertNoEditable("seller.number");
		assertNoEditable("alternateSeller");
	}
	
	public void test2LevelsReferenceInDescriptionsList() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");
		assertDescriptionValue("alternateSeller.number", "MANAGER JUANVI LLAVADOR");
	}
		
}
