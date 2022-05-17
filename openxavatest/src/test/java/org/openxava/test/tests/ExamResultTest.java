package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class ExamResultTest extends ModuleTestBase {
	
	public ExamResultTest(String testName) {
		super(testName, "ExamResult");		
	}	
	
	public void testBooleanOnlyElementCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("name", "FIRST EXAM");
		assertCollectionRowCount("questionResults", 3);
		assertValueInCollection("questionResults", 0, "passed", "true");
		assertValueInCollection("questionResults", 1, "passed", "false");
		assertValueInCollection("questionResults", 2, "passed", "false");
		

		setValueInCollection("questionResults", 2, "passed", "true");
		assertMessage("[{passed=true}, {passed=false}, {passed=true}]");
	}
	
}
