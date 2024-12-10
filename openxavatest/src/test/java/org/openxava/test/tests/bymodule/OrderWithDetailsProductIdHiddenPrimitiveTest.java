package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

public class OrderWithDetailsProductIdHiddenPrimitiveTest extends ModuleTestBase {

	public OrderWithDetailsProductIdHiddenPrimitiveTest(String testName) {
		super(testName, "OrderWithDetailsProductIdHiddenPrimitive");		
	}
	
	public void testElementCollectionCanSaveWithHiddenKeyPrimitive_elementCollectioncanSaveWithHiddenKeyUsingInput() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details2", 0, "product.code", "1414");
		setValueInCollection("details2", 1, "product.code", "1515");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("details2", 0, "product.code", "1414");
		assertValueInCollection("details2", 1, "product.code", "1515");
		setValueInCollection("details2", 1, "product.code", "");
		execute("CRUD.save");
	}
	
}
