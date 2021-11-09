package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * tmr 
 * @author Javier Paniza
 */

public class ProjectTeamTest extends ModuleTestBase {
	
	public ProjectTeamTest(String testName) {
		super(testName, "ProjectTeam");		
	}
	
	public void testAssignToAnotherOrderColumnListWhenAddingToOrderColumnList() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("members", 0);
		execute("Collection.new", "viewObject=xava_view_members");
		setValue("name", "THE JUNIT PROGRAMMER");
		setValue("project.id", "ff8080824cfa5dcb014cfa634a1b0004"); // THE BIG PROJECT
		execute("Collection.save");
		assertCollectionRowCount("members", 1);
		assertValueInCollection("members", 0, 0, "THE JUNIT PROGRAMMER");
		
		changeModule("Project");
		execute("List.viewDetail", "row=0");
		assertValue("name", "THE BIG PROJECT");
		assertCollectionRowCount("members", 4);
		assertValueInCollection("members", 0, 0, "JOHN");
		assertValueInCollection("members", 1, 0, "JUAN");
		assertValueInCollection("members", 2, 0, "PETER");
		assertValueInCollection("members", 3, 0, "THE JUNIT PROGRAMMER");
		
		changeModule("ProjectMember");
		setConditionValues("JUNIT");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "THE JUNIT PROGRAMMER");
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
	}
	
	public void testDeleteFromModuleElementOfOrderColumnList() throws Exception { // tmr ¿Fusionar?Ç
		// TMR ME QUEDÉ POR AQUÍ: TEST TERMINADO, FALTA ARREGLAR EL BUG
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("members", 0);
		execute("Collection.new", "viewObject=xava_view_members");
		setValue("name", "THE JUNIT PROGRAMMER 1");
		execute("Collection.saveAndStay");
		setValue("name", "THE JUNIT PROGRAMMER 2");
		execute("Collection.saveAndStay");
		setValue("name", "THE JUNIT PROGRAMMER 3");
		execute("Collection.save");		
		assertCollectionRowCount("members", 3);
		assertValueInCollection("members", 0, 0, "THE JUNIT PROGRAMMER 1");
		assertValueInCollection("members", 1, 0, "THE JUNIT PROGRAMMER 2");
		assertValueInCollection("members", 2, 0, "THE JUNIT PROGRAMMER 3");
		
		changeModule("ProjectMember"); // tmr ¿refactorizar?
		setConditionValues("THE JUNIT PROGRAMMER 2");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "THE JUNIT PROGRAMMER 2");
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
		
		changeModule("ProjectTeam");
		assertCollectionRowCount("members", 2); 
		assertValueInCollection("members", 0, 0, "THE JUNIT PROGRAMMER 1");
		assertValueInCollection("members", 1, 0, "THE JUNIT PROGRAMMER 3");
		
		changeModule("ProjectMember"); // tmr ¿refactorizar?
		setConditionValues("THE JUNIT PROGRAMMER");
		execute("List.filter");
		assertListRowCount(2);
		assertValueInList(0, 0, "THE JUNIT PROGRAMMER 1");
		assertValueInList(1, 0, "THE JUNIT PROGRAMMER 3");
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
	}
				
}
