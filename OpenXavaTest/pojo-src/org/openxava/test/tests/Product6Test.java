package org.openxava.test.tests;

/**
 * tmp
 * @author Javier Paniza
 */

public class Product6Test extends CustomizeListTestBase { 
	
	public Product6Test(String testName) {
		super(testName, "Product6");		
	}
	
	public void testDefaultValueCalculatorInReferenceWithPropertyFrom() throws Exception { 
		execute("CRUD.new");
		assertValue("subfamily.number", "");
		setValue("family.number", "2");
		assertValue("subfamily.number", "2");
	}
	
}
