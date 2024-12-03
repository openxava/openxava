package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

public class OrderWithDetailsProductIdHiddenPrimitiveTest extends ModuleTestBase {

	public OrderWithDetailsProductIdHiddenPrimitiveTest(String testName) {
		super(testName, "OrderWithDetailsProductIdHiddenPrimitive");		
	}
	
	public void testElementCollectionCanSaveWithHiddenKeyPrimitive() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details2", 0, "product.code", "1414");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details2", 0, "product.code", "1414");
	}
	
}
