package org.openxava.test.tests.bymodule;

/**
 * 
 * @author Javier Paniza
 */

public class Product6Test extends CustomizeListTestBase { 
	
	public Product6Test(String testName) {
		super(testName, "Product6");		
	}
	
	public void testDefaultValueCalculatorInReferenceWithPropertyFrom_requiredImagesGalleryOnModify_baseCondition3levelProperties() throws Exception { 
		// WARNING! DON'T ADD TO Product6 ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
		assertListRowCount(5);
		assertListColumnCount(1); // Just one column, to test that a baseCondition bug
		assertLabelInList(0, "Description"); // Not a property from baseCondition condition
		
		execute("CRUD.new");
		assertValue("subfamily.number", "");
		setValue("family.number", "2");
		assertValue("subfamily.number", "2");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=4"); 
		assertValue("number", "7");	
		assertFilesCount("photos", 1); 
		removeFile("photos", 0);
		execute("CRUD.save");
		assertError("Value for Photos in Product 6 is required");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=4"); 
		assertValue("number", "7");	
		assertFilesCount("photos", 1);
		uploadFile("photos", "test-images/foto_javi.jpg");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=4"); 
		assertValue("number", "7");	
		assertFilesCount("photos", 2); 
		removeFile("photos", 1);
		removeFile("photos", 0);
		
		execute("Mode.list");
		execute("List.viewDetail", "row=4"); 
		assertValue("number", "7");	
		assertFilesCount("photos", 1); 		
	}
	
}
