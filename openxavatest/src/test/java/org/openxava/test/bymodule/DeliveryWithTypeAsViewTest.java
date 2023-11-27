package org.openxava.test.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryWithTypeAsViewTest extends ModuleTestBase {
	
	public DeliveryWithTypeAsViewTest(String testName) {
		super(testName, "DeliveryWithTypeAsView");		
	}
			
	public void testCompositeKeyWithReferencesWithReadOnlyInIdOfReferencedEntity() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoAction("Reference.search", "keyProperty=type.number");
		execute("CRUD.new");
		assertAction("Reference.search", "keyProperty=type.number");
		assertEditable("type.number");
	}

}
