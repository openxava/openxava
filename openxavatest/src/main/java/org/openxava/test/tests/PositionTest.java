package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 

/**
 * 
 * @author Javier Paniza
 */
public class PositionTest extends ModuleTestBase {
	
	public PositionTest(String testName) {
		super(testName, "Position");		
	}
	
	public void testFloatDoublePrecision() throws Exception { 		
		execute("CRUD.new");
		setValue("name", "JUNIT POSITION");
		setValue("axisX", "1234.1234");
		setValue("axisY", "1234567.1234567");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(0, 0, "JUNIT POSITION");
		assertValueInList(0, 1, "1,234.1234");
		assertValueInList(0, 2, "1,234,567.1234567");
		execute("List.viewDetail", "row=0");
		assertValue("name", "JUNIT POSITION");
		assertValue("axisX", "1,234.1234");
		assertValue("axisY", "1,234,567.1234567");
		execute("CRUD.delete");
		assertNoErrors();
	}
	
}
