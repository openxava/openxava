package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */

public class Product6Test extends CustomizeListTestBase { 
	
	public Product6Test(String testName) {
		super(testName, "Product6");		
	}
	
	public void testDefaultValueCalculatorInReferenceWithPropertyFrom() throws Exception {
		// WARNING! DON'T ADD TO Product6 ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
		execute("CRUD.new");
		assertValue("subfamily.number", "");
		setValue("family.number", "2");
		assertValue("subfamily.number", "2");
	}
	
}
