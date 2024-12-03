package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 *
 * @author Chungyen Tsai
 *
 */

public class OrderWithDetailsProductIdHiddenTest extends ModuleTestBase {
	
	public OrderWithDetailsProductIdHiddenTest(String testName) {
		super(testName, "OrderWithDetailsProductIdHidden");		
	}
	
	public void testElementCollectionCanSaveWithHiddenKeyWrapper() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details", 0, "product.code", "1313");
		execute("CRUD.save");
		assertNoErrors();
	}

}
