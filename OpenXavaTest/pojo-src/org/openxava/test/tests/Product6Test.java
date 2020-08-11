package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */

public class Product6Test extends CustomizeListTestBase { 
	
	public Product6Test(String testName) {
		super(testName, "Product6");		
	}
	
	public void testDefaultValueCalculatorInReferenceWithPropertyFrom_requiredUploadEditorOnModify() throws Exception { // tmp requiredUploadEditorOnModify
		// WARNING! DON'T ADD TO Product6 ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
		execute("CRUD.new");
		assertValue("subfamily.number", "");
		setValue("family.number", "2");
		assertValue("subfamily.number", "2");
		
		// tmp ini
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("number", "1");	
		assertFilesCount("photos", 0); 
		uploadFile("photos", "test-images/cake.gif"); 
		removeFile("photos", 0);
		execute("CRUD.save");
		assertError("Value for Photos in Product 6 is required");
		// tmp fin
	}
	
}
