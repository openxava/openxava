package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ExperienceTest extends ModuleTestBase {
	
	public ExperienceTest(String testName) {
		super(testName, "Experience");		
	}
	
	public void testPolymorphicReference() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("name", "SICAL");
		assertValue("programmer.name", "JUANJO");
		execute("Reference.search", "keyProperty=programmer.name");
		assertListRowCount(3);
		execute("List.orderBy", "property=name");
		assertValueInList(1, 0, "JAVI"); 
		execute("ReferenceSearch.choose", "row=1");
		assertValue("programmer.name", "JAVI"); 
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		execute("CRUD.new");
		assertValue("name", "");
		assertNotExists("programmer.favouriteFramework"); 
		execute("Navigation.first");
		assertValue("name", "SICAL");
		assertValue("programmer.name", "JAVI"); 
		execute("Reference.search", "keyProperty=programmer.name");
		assertListRowCount(3);
		execute("List.orderBy", "property=name"); 
		assertValueInList(2, 0, "JUANJO"); 
		execute("ReferenceSearch.choose", "row=2");
		assertValue("programmer.name", "JUANJO");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
	}
	
	public void testNavigatingForObjectsWithPolymorphicReference() throws Exception { 
		assertListRowCount(1); 
		execute("CRUD.new");
		setValue("name", "OPENXAVA");
		assertNotExists("programmer.favouriteFramework");
		setValue("programmer.name", "JAVI");
		assertExists("programmer.favouriteFramework"); 
		assertValue("programmer.favouriteFramework", "OPENXAVA");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(2);
		execute("List.viewDetail", "row=0");
		assertValue("name", "SICAL");
		assertValue("programmer.name", "JUANJO"); 
		assertNotExists("programmer.favouriteFramework");
		execute("Navigation.next");
		assertNoErrors();
		assertValue("name", "OPENXAVA");
		assertValue("programmer.name", "JAVI");
		assertExists("programmer.favouriteFramework");
		assertValue("programmer.favouriteFramework", "OPENXAVA");
		execute("Navigation.first");
		assertNoErrors();
		assertValue("name", "SICAL");
		assertValue("programmer.name", "JUANJO");
		assertNotExists("programmer.favouriteFramework");
		execute("Navigation.next");
		assertNoErrors();
		assertValue("name", "OPENXAVA");
		assertValue("programmer.name", "JAVI");
		assertExists("programmer.favouriteFramework");
		assertValue("programmer.favouriteFramework", "OPENXAVA");
		execute("CRUD.delete");
		assertNoErrors();
	}
			
}
