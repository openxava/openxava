package org.openxava.school.tests;

import org.openxava.jpa.*;
import org.openxava.school.model.*;
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
	
	public void testCreateReadUpdateDelete2() throws Exception { // tmp
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

	
	/* tmp
	public void testCreatingTestData() throws Exception { // tmp
		assertListRowCount(2);
		Teacher t = new Teacher();
		t.setId("X2");
		t.setName("The X Teacher");
		XPersistence.getManager().persist(t);
		XPersistence.commit();
		execute("List.filter");
		assertListRowCount(3);
		assertValueInList(2, 0, "X2");
		assertValueInList(2, 1, "The X Teacher");
		t = XPersistence.getManager().find(Teacher.class, "X2");
		XPersistence.getManager().remove(t);
	}
	*/
	
}
