package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class SkillTest extends ModuleTestBase {
	
	public SkillTest(String testName) {
		super(testName, "Skill");		
	}
	
	public void testXSSProtection() throws Exception {
		execute("CRUD.new");
		setValue("description", "START<iframe src='http://openxava.org'/>END");
		execute("CRUD.save");
		execute("Mode.list");
		assertFalse(getHtml().contains("iframe"));
		assertValueInList(1, 0, "STARTEND"); 
		execute("CRUD.deleteRow", "row=1");
	}
	
}
