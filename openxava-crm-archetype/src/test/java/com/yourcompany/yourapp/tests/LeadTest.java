package com.yourcompany.yourapp.tests;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class LeadTest extends ModuleTestBase {

	public LeadTest(String nameTest) {
		super(nameTest, "yourapp", "Lead");
	}
	
	public void testCreateNewLead() throws Exception {
		login("admin", "admin"); 
		setValue("name", "JUnit Lead");
		String [][] status = {
			{ "", "" },	
			{ "2c9c10818fca7db3018fca7e738e0000", "A Confirmed" },
			{ "2c9c10818fca7db3018fca7ea0370001", "B Pending" },
			{ "2c9c10818fca7db3018fca7ecb130002", "X Discarded" },
			{ "2c9c10818fca7db3018fca7ef2b90003", "Z Done" }
		};
		
		assertValidValues("status.id", status);
		setValue("status.id", "2c9c10818fca7db3018fca7ef2b90003"); // Done 
		setValue("email", "antonio.rodolfo.valentino.smith@thelargestcompanyinworld.com");
		setValue("description", "This is a JUnit Lead");
		
		execute("Sections.change", "activeSection=1");
		setValue("remarks", "This is a remark");
		
		execute("Sections.change", "activeSection=2");
		setValueInCollection("activities", 0, "description", "The first activity with JUnit Lead");
		
		execute("Sections.change", "activeSection=3");
		uploadFile("attachments", "test-files/notes.txt");
		
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		
		assertListRowCount(1);
		assertValueInList(0, 0, "JUnit Lead");
		assertValueInList(0, 4, "Finished");
		execute("List.viewDetail", "row=0");
		
		assertValue("name", "JUnit Lead");
		assertValue("status.id", "2c9c10818fca7db3018fca7ef2b90003"); // Done 
		assertValue("email", "antonio.rodolfo.valentino.smith@thelargestcompanyinworld.com");
		assertValue("lastTouch", getCurrentDate()); // If fails change the GMT+x in serverTimezone MySQL URL
		execute("Sections.change", "activeSection=0");
		assertValue("description", "<p>This is a JUnit Lead</p>");
		execute("Sections.change", "activeSection=1");
		assertValue("remarks", "<p>This is a remark</p>");
		execute("Sections.change", "activeSection=2");
		assertCollectionColumnCount("activities", 1);
		assertValueInCollection("activities", 0, "description", "The first activity with JUnit Lead");
		assertValueInCollection("activities", 0, "date", getCurrentDate());
		execute("Sections.change", "activeSection=3");
		assertFile("attachments", 0, "text/plain");
				
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private String getCurrentDate() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy")); 
	}

}