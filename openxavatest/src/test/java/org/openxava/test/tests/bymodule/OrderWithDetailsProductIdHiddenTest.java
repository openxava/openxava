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
	
	public void testElementCollectionCanSaveWithHiddenKeyPrimitiveAndWrapper() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details", 0, "product.code", "1313");
		execute("CRUD.save");
		assertNoErrors();
		
		changeModule("OrderWithDetailsProductIdHiddenPrimitive");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details2", 0, "product.code", "1414");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details2", 0, "product.code", "1414");
		
	}

}
