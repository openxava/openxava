package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class EmployeeTest extends ModuleTestBase {
	
	public EmployeeTest(String testName) {
		super(testName, "Employee");		
	}
	
	public void testListWithOneToOneWithPrimaryKeyJoinColumns_alphabeticForSearchingNumericColumnInList() throws Exception {
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "JUANITO");
		assertValueInList(0, 2, "DEVELOPER");
		assertValueInList(0, 3, "12 YEARS");
		
		setConditionValues ("J");
		execute("List.filter");
		String html = getHtml();
		assertFalse(html.contains("Errors trying to obtain data list"));
		assertTrue(html.contains("There are 0 records in list"));
		assertError("Impossible to filter data: Id must be numeric");
	}
	
}
