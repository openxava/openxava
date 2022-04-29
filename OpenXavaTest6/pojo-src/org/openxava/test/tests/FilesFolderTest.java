package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase; 


/**
 * 
 * @author Javier Paniza
 */
public class FilesFolderTest extends ModuleTestBase {
	
	public FilesFolderTest(String testName) {
		super(testName, "FilesFolder");		
	}
	
	public void testAddingChildrenWithCollectionElementsInACompositeWhoseParentReferenceNotNamedAsEntity() throws Exception { 
		execute("CRUD.new");
		setValue("name", "JUNIT FOLDER");
		execute("Collection.new", "viewObject=xava_view_subfolders");
		setValue("name", "JUNIT SUBFOLDER");
		execute("Collection.add", "viewObject=xava_view_files");
		checkAll();
		execute("AddToCollection.add");
		assertNoErrors();
		assertCollectionRowCount("files", 2);
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("subfolders", 1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testSetDescriptionsListToNull() throws Exception { 
		execute("List.viewDetail", "row=1");
		assertValue("name", "CHILD");
		assertValue("parent.id", "ff8080824b4ebd51014b4eca87ad0004"); // The parent id
		assertDescriptionValue("parent.id", "PARENT");
		setValue("parent.id", "");
		execute("CRUD.save");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=1");
		assertValue("name", "CHILD");
		assertValue("parent.id", "");
		assertDescriptionValue("parent.id", "");
		setValue("parent.id", "ff8080824b4ebd51014b4eca87ad0004"); // The parent id
		execute("CRUD.save");			
	}
	
	public void testParentNotIncludedInViewCreatingNewFromOneToManyNotCascadeCollection() throws Exception { 
		execute("CRUD.new");
		execute("Collection.new", "viewObject=xava_view_files");
		assertExists("name");
		assertNotExists("folder.name");
	}
	
}
