package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerGroupAndPropertySameRowTest extends ModuleTestBase {
	
	public CustomerGroupAndPropertySameRowTest(String testName) {
		super(testName, "CustomerGroupAndPropertySameRow");		
	}
	
	public void testGroupAndPropertyInSameLineNotBreakLayout() throws Exception { 
		execute("CRUD.new");
		assertAction("EditableOnOff.setOn");
		reload();
		assertAction("EditableOnOff.setOn");
	}
					
}
