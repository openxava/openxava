package org.openxava.test.tests.bymodule;

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
		assertEditable("mixture.colorName1");
		assertEditable("mixture.colorName2");
		assertAction("Reference.search", "keyProperty=mixture.colorName2");
		
		execute("Navigation.first");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1");
		assertNoEditable("mixture.colorName2");
		assertNoAction("Reference.search", "keyProperty=mixture.colorName2");

		execute("CRUD.new");
		assertEditable("name");
		assertEditable("usedTo");
		assertEditable("mixture.colorName1");
		assertEditable("mixture.colorName2");
		assertAction("Reference.search", "keyProperty=mixture.colorName2");
		
		setValue("number", "77");
		setValue("name", "77");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		assertNoEditable("number");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1");
		assertNoEditable("mixture.colorName2");
		assertNoAction("Reference.search", "keyProperty=mixture.colorName2");
		
		execute("CRUD.delete");
		assertNoEditable("name");
		assertNoEditable("usedTo");
		assertNoEditable("mixture.colorName1");
		assertNoEditable("mixture.colorName2");
		assertNoAction("Reference.search", "keyProperty=mixture.colorName2");
		assertEditable("hexValue");
	}
	
}
