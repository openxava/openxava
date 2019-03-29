package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CompositeTest extends ModuleTestBase {
	
	public CompositeTest(String testName) {
		super(testName, "Composite");		
	}
	
	public void testSelfReferenceCollectionWithCascadeRemove() throws Exception {
		setValue("name", "SER VIVO"); 
		assertNoErrors();
		assertCollectionRowCount("children", 0);
		execute("Collection.new", "viewObject=xava_view_children");			
		assertNoErrors();
		
		assertCollectionRowCount("children", 0);
		setValue("name", "INVERTEBRADO"); 		
		execute("Collection.new", "viewObject=xava_view_children");
		assertCollectionRowCount("children", 0);
		setValue("name", "MOLUSCO"); 
		execute("Collection.save");
		
		assertValue("name", "INVERTEBRADO");
		assertCollectionRowCount("children", 1);
		assertValueInCollection("children", 0, 0, "MOLUSCO");
		execute("Collection.save");

		assertValue("name", "SER VIVO"); 
		assertCollectionRowCount("children", 1);
		assertValueInCollection("children", 0, 0, "INVERTEBRADO");
		
		execute("Mode.list");
		assertListRowCount(3);
		execute("CRUD.new");
		setValue("name", "SER VIVO");
		execute("CRUD.refresh");
		assertCollectionRowCount("children", 1);
		assertValueInCollection("children", 0, 0, "INVERTEBRADO");
		
		execute("Collection.edit", "row=0,viewObject=xava_view_children");		
		assertValue("name", "INVERTEBRADO");
		assertCollectionRowCount("children", 1);
		assertValueInCollection("children", 0, 0, "MOLUSCO");
		
		execute("Collection.edit", "row=0,viewObject=xava_view_children");
		assertValue("name", "MOLUSCO");
		assertCollectionRowCount("children", 0);
		
		closeDialog();
		closeDialog();
		assertValue("name", "SER VIVO");
		
		execute("CRUD.delete");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(0);		
	}		
		
}
