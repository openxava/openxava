package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProgrammerTest extends ModuleTestBase {
	
	public ProgrammerTest(String testName) {
		super(testName, "Programmer");		
	}
	
	public void testInheritedEntityCRUD() throws Exception { 
		execute("CRUD.new");
		setValue("name", "JUNIT PROGRAMMER");
		setValue("sex", "1");
		setValue("mainLanguage", "EIFFEL");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("name", "");
		assertValue("sex", "");
		assertValue("mainLanguage", "");
		setValue("name", "JUNIT PROGRAMMER");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT PROGRAMMER");
		assertValue("sex", "1");
		assertValue("mainLanguage", "EIFFEL");
		execute("CRUD.delete");
		assertMessage("Programmer deleted successfully");
	}
	
	public void testInheritedEntityWithChildrenList() throws Exception {
		assertListColumnCount(5);  
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Sex");
		assertLabelInList(2, "Author of Favorite author"); 
		assertLabelInList(3, "Biography of Favorite author");
		assertLabelInList(4, "Main language");
		
		assertListRowCount(3);
		assertValueInList(0, 0, "JAVI"); 
		assertValueInList(1, 0, "JUANJO"); 				
		assertValueInList(2, 0, "DANI");
	}
	
	public void testInheritedEntityWithCollection() throws Exception { 
		execute("List.viewDetail", "row=0");
		setModel("JavaProgrammer");  
		assertValue("name", "JAVI");
		assertCollectionRowCount("experiences", 0); 
		execute("Collection.new", "viewObject=xava_view_experiences");
		assertExists("name");
		assertExists("description");
		assertNotExists("programmer.name"); 
		setValue("name", "OpenXava");
		setValue("description", "The model-driven framework");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("experiences", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_experiences");
		execute("Collection.remove");
		assertNoErrors();
		assertCollectionRowCount("experiences", 0);
	}
		
}
