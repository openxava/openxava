package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product3WithGroupTest extends ModuleTestBase {
	
	public Product3WithGroupTest(String testName) {
		super(testName, "Product3WithGroup");		
	}
	
	public void testNotOnChangeActionWhenSearch() throws Exception { 
		execute("CRUD.new");
		assertNoErrors();
		execute("Product3.showDescription"); // description is hide in a init action for test purpose
		setValue("number", "77");
		execute("CRUD.refresh");
		assertValue("description", "ANATHEMA");
		assertEditable("description"); // well: on-change for make this not editable not throw
	}
	
	public void testDescriptionsListWithHiddenKeyThrowsChanged() throws Exception { 
		execute("CRUD.new");
		assertNoErrors();
		execute("Product3.showDescription"); // description is hide in a init action for test purpose
		assertNoErrors();
		assertValue("comments", "");
		setValue("family.oid", "1037101892379");
		assertValue("comments", "Family changed");
	}
	
	public void testSetValueNotifyingOnReferenceWithHiddenKeyNotResetGroup() throws Exception { 
		execute("CRUD.new");
		assertNoErrors();
		execute("Product3.showDescription"); // description is hide in a init action for test purpose
		setValue("description", "HOLA"); 
		execute("Product3.changeFamily");
		assertValue("description", "HOLA");
	}
						
}
