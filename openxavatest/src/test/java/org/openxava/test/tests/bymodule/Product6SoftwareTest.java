package org.openxava.test.tests.bymodule;

/**
 * 
 * @author Javier Paniza
 */

public class Product6SoftwareTest extends CustomizeListTestBase { 
	
	public Product6SoftwareTest(String testName) {
		super(testName, "Product6Software");		
	}
	
	public void testBaseCondition3levelNotKeyProperties() throws Exception { 
		assertListRowCount(6);
		assertListColumnCount(1); // Just one column, to test that a baseCondition bug
		assertLabelInList(0, "Description"); // Not a property from baseCondition condition		
	}
	
}
