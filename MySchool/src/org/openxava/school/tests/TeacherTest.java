package org.openxava.school.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class TeacherTest extends ModuleTestBase {
	
	
	
	public TeacherTest(String testName) {
		super(testName, "MySchool", "Teacher");		
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		// Create
		execute("CRUD.new");
		setValue("id", "JU");
		setValue("name", "JUNIT Teacher");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("id", "");
		assertValue("name", "");
		
		// Read
		setValue("id", "JU");
		// tmp execute("CRUD.search");
		execute("CRUD.refresh"); // tmp
		assertValue("id", "JU");
		assertValue("name", "JUNIT Teacher");
		
		// Modify
		setValue("name", "JUNIT Teacher MODIFIED");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("id", "");
		assertValue("name", "");
		
		// Verify if modified
		setValue("id", "JU");
		// tmp execute("CRUD.search");
		execute("CRUD.refresh");
		assertValue("id", "JU");
		assertValue("name", "JUNIT Teacher MODIFIED");
		
		// Delete it
		execute("CRUD.delete");		
		assertMessage("Teacher deleted successfully");				
	}
	
}
