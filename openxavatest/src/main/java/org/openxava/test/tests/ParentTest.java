package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Federico Alcántara
 */
public class ParentTest extends ModuleTestBase {
	public ParentTest(String nameTest) {
		super (nameTest, "Parent");
	}
	

	@Override
	public void setUp() throws Exception {
		super.setUp();
		removeEntities();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		removeEntities();
	}

	/**
	 * Tests for normal CRUD operation at parent level.
	 * @throws Exception
	 */
	public void testEmbeddedCollectionFromParentCRUD() throws Exception {
		execute("CRUD.new");
		setValue("id","FATHER");
		setValue("description", "THE FATHER");
		execute("CRUD.save");
		assertNoErrors();
		execute("CRUD.new");
		setValue("id","FATHER");
		execute("CRUD.refresh");
		assertValue("id","FATHER");
		assertValue("description", "THE FATHER");
		// now the delete
		execute("CRUD.delete");
		assertNoErrors();
		execute("CRUD.new");
		setValue("id","FATHER");
		execute("CRUD.refresh");
		assertValue("description", "");
	}
	
	/**
	 * Tests for normal CRUD operation at child level.
	 * @throws Exception
	 */
	public void testEmbeddedCollectionFromChildrenCRUD() throws Exception { 
		execute("CRUD.new");
		setValue("id","FATHER");
		setValue("description", "THE FATHER");
		execute("CRUD.save");
		assertNoErrors();
		execute("CRUD.new");
		setValue("id","FATHER");
		execute("CRUD.refresh");
		execute("Collection.new", "viewObject=xava_view_children");
		setValue("id", "JANE");
		setValue("description", "THIS IS JANE");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("children", 1);
		// test modification of Children
		execute("Collection.edit", "row=0,viewObject=xava_view_children");
		assertValue("id", "JANE");
		assertValue("description", "THIS IS JANE");
		setValue("description", "THIS IS LITTLE JANE");
		execute("Collection.save");
		assertNoErrors();
		assertValueInCollection("children", 0, "description", "THIS IS LITTLE JANE");
		// Add a new child
		execute("Collection.new", "viewObject=xava_view_children");
		setValue("id", "JOHN");
		setValue("description", "THIS IS JOHN");
		execute("Collection.save");
		assertNoErrors();
		// Delete from within dialog
		execute("Collection.edit", "row=1,viewObject=xava_view_children");
		assertValue("id", "JOHN");
		assertValue("description", "THIS IS JOHN");
		execute("Collection.remove");
		assertNoErrors();
		// Delete from collection list 
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_children");
	}
	
	/**
	 * Tests if you can add children directly when having a new
	 * parent record.
	 * @throws Exception
	 */
	public void testEmbeddedCollectionFromChildrenDirectCRUD() throws Exception {
		execute("CRUD.new");
		setValue("id","FATHER");
		setValue("description", "THE FATHER");
		execute("Collection.new", "viewObject=xava_view_children");
		setValue("id", "JOHN");
		setValue("description", "THIS IS JOHN");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("children", 1);
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_children");
	}
	
	private void removeEntities() throws Exception {
		XPersistence.getManager().createQuery("delete from Child").executeUpdate();
		XPersistence.getManager().createQuery("delete from Parent").executeUpdate();
		XPersistence.commit();
	}
	
}
