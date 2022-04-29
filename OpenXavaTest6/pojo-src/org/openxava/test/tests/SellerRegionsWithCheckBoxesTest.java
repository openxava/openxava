package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 

/**
 * 
 * @author Javier Paniza
 */
public class SellerRegionsWithCheckBoxesTest extends ModuleTestBase {
	
	public SellerRegionsWithCheckBoxesTest(String testName) {
		super(testName, "SellerRegionsWithCheckBoxes");		
	}
	
	public void testCheckBoxCustomEditorWithMultipleValuesFormatter() throws Exception {
		String [] emptyRegions = {};
		String [] regions = { "1", "3" };
		String [] oneRegion = { "2" };
		
		execute("CRUD.new");
		assertValues("regions", emptyRegions);
		setValue("number", "66");
		setValue("name", "SELLER JUNIT 66");
		setValues("regions", regions);
		assertValues("regions", regions);
		
		execute("CRUD.save");
		assertNoErrors();
		assertValues("regions", emptyRegions);		
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValues("regions", regions);
		
		setValues("regions", oneRegion);
		execute("CRUD.save");
		assertNoErrors();
		assertValues("regions", emptyRegions);

		setValue("number", "66");
		execute("CRUD.refresh");
		assertValues("regions", oneRegion);
		
		execute("CRUD.delete");
		assertMessage("Seller deleted successfully");
	}
	
}
