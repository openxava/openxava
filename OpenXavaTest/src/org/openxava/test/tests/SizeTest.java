package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class SizeTest extends ModuleTestBase {
	
	public SizeTest(String testName) {
		super(testName, "Size");		
	}
	
	// This test does not work in AS/400 because Hibernate does not support
	// sequence for as400, at least for Hiberanate 3.2.1 and JTOpen 5.3 
	public void testSequenceCalculator() throws Exception {		 
		execute("List.orderBy", "property=id");
		execute("List.orderBy", "property=id");
		String last = getValueInList(0, "id");	
		execute("CRUD.new");
		setValue("name", "JUNIT SIZE " + (int) (Math.random() * 200));
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		String next = String.valueOf(Integer.parseInt(last) + 1);
		assertValueInList(0, "id", next); 		
	}
	
}
