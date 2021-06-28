package org.openxava.test.tests;

import org.openxava.tests.*;

/**
* Create on 27 mar. 2019 (17:24:59)
* @author Ana Andres
*/
public class ColorWithReadOnlyOnCreateTest extends ModuleTestBase {
	
	public ColorWithReadOnlyOnCreateTest(String testName) {
		super(testName, "ColorWithReadOnlyOnCreate");		
	}
		
	public void testPropertyAndReferenceReadOnlyWitOnCreateFalse() throws Exception{
		execute("CRUD.new");
		assertEditable("name");
		assertEditable("usedTo"); 
		assertEditable("mixture.colorName1"); // tmp
		
		execute("Navigation.first");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1"); // tmp

		execute("CRUD.new");
		assertEditable("name");
		assertEditable("usedTo");
		assertEditable("mixture.colorName1"); // tmp
		
		setValue("number", "77");
		setValue("name", "77");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		assertNoEditable("number");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1"); // tmp
		
		execute("CRUD.delete");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1"); // tmp
		assertEditable("hexValue");
	}
	
}
