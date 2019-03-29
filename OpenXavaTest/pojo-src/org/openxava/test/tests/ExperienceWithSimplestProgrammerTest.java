package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ExperienceWithSimplestProgrammerTest extends ModuleTestBase {
	
	public ExperienceWithSimplestProgrammerTest(String testName) {
		super(testName, "ExperienceWithSimplestProgrammer");		
	}
	
	public void testPolymorphicReferenceUsingReferenceView() throws Exception{
		execute("CRUD.new");
		assertExists("programmer.name");
		assertNotExists("programmer.sex");

		execute("Reference.search", "keyProperty=programmer.name");
		execute("List.orderBy", "property=name");
		execute("ReferenceSearch.choose", "row=1");
		assertValue("programmer.name", "JAVI"); 
		assertNotExists("programmer.sex");
		assertExists("programmer.favouriteFramework");

		execute("Reference.search", "keyProperty=programmer.name");
		execute("List.orderBy", "property=name"); 
		execute("ReferenceSearch.choose", "row=2");
		assertValue("programmer.name", "JUANJO"); 
		assertNotExists("programmer.sex");
		assertNotExists("programmer.favouriteFramework");
		
		execute("Reference.search", "keyProperty=programmer.name");
		execute("List.orderBy", "property=name"); 
		execute("ReferenceSearch.choose", "row=1");
		assertValue("programmer.name", "JAVI");
		assertNotExists("programmer.sex");
		assertExists("programmer.favouriteFramework");

		execute("CRUD.new");
		assertExists("programmer.name");
		assertNotExists("programmer.sex");
		assertNotExists("programmer.favouriteFramework");
	}
				
}
