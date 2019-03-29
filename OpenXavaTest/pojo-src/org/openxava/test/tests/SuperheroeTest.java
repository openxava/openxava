package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class SuperheroeTest extends ModuleTestBase {
	
	public SuperheroeTest(String testName) {
		super(testName, "Superheroe");		
	}
	
	public void testHTMLFilterListFormatter() throws Exception {
		execute("CRUD.new");
		setValue("name", "<b>Super</b>man");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(0, 0, "<b>Super</b>man"); 
		execute("CRUD.deleteRow", "row=0");
	}
	
	public void testListConfigurationWithOrderWithOnlyOnePropertyAfterResetModule_notDuplicatedListConfigurations() throws Exception { 
		assertValueInList(0, 0, "ESCARIANO AVIESO");
		assertValueInList(1, 0, "SUPERLOPEZ");
		execute("List.orderBy", "property=name");
		execute("List.orderBy", "property=name");
		assertValueInList(0, 0, "SUPERLOPEZ");
		assertValueInList(1, 0, "ESCARIANO AVIESO");

		resetModule();
		
		assertValueInList(0, 0, "ESCARIANO AVIESO");
		assertValueInList(1, 0, "SUPERLOPEZ");
		selectListConfiguration("Ordered by name descending");
		assertValueInList(0, 0, "SUPERLOPEZ");
		assertValueInList(1, 0, "ESCARIANO AVIESO");
		
		assertListAllConfigurations("Ordered by name descending", "All");
		execute("List.orderBy", "property=name");
		execute("List.orderBy", "property=name");
		selectListConfiguration("All");
		assertListAllConfigurations("All", "Ordered by name descending");		
	}
	
}
