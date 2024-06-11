package com.yourcompany.yourapp.tests;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class IssueTest extends ModuleTestBase {

	public IssueTest(String nameTest) {
		super(nameTest, "yourapp", "Issue");
	}
	
	public void testCreateNewIssue() throws Exception {
		login("admin", "admin"); 
		setValue("title", "JUnit Incident");
		String [][] types = {
			{ "", "" },
			{ "2c94f081900875e801900896f25b0008", "Bug" },
			{ "2c94f081900875e80190089701170009", "Feature" }
		};
		assertValidValues("type.id", types);
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug 
		setValue("description", "This is a JUnit Incident");
		assertValidValuesCount("project.id", 2);
		assertDescriptionValue("project.id", "OpenXava"); 		
		assertValue("createdBy", "admin");
		assertNoEditable("createdBy");
		assertValue("createdOn", getCurrentDate());

		String [][] priorities = {
			{ "", "" },
			{ "7", "High" },
			{ "5", "Normal" },
			{ "3", "Low" }
		};
		assertValidValues("priority.level", priorities);
		assertValue("priority.level", "5");
		setValue("priority.level", "7");

		String [][] versions = {
			{ "", "" },
			{ "2c94f081900856030190085ec7cc0002", "2.0" },
			{ "2c94f081900856030190085eb1610001", "1.0" }
		};
		assertValidValues("version.id", versions);
		setValue("version.id", "2c94f081900856030190085eb1610001"); // 1.0 
			
		String [][] plans = {
			{ "", "" },	
			{ "2c94f081900875e80190089c0080000a", "Javi 2024.10" },
			{ "2c94f081900875e80190089c1211000b", "Javi 2024.11" }
		};
		assertValidValues("assignedTo.id", plans);
		setValue("assignedTo.id", "2c94f081900875e80190089c1211000b"); // Javi 2024.11 
		
		String [][] status = {
			{ "", "" },	
			{ "2c94f081900875e80190088afdc30001", "Done" },
			{ "2c94f081900875e80190088ce9d30002", "Not reproducible" },
			{ "2c94f081900875e80190088a559a0000", "Pending" },
			{ "2c94f081900875e80190088d8fb90003", "Rejected" }
		};
		
		assertValidValues("status.id", status);
		assertValue("status.id", "2c94f081900875e80190088a559a0000"); // Pending 
		
		String [][] customers = {
			{ "", "" },	
			{ "2c94f081900875e8019008a7349e0010", "Europe Software Corporation" },
			{ "2c94f081900875e8019008a637e8000f", "Ministry of Industry" }			
		};
		
		assertValidValues("customer.id", customers);
		setValue("customer.id", "2c94f081900875e8019008a637e8000f"); // Ministry of Industry 

		assertValue("hours", "");
		setValue("minutes", "90");
		assertValue("hours", "1.50");
		assertNoEditable("hours");
		
		uploadFile("attachments", "test-files/notes.txt");
		postDiscussionComment("discussion", "I agree");
		
		execute("CRUD.save");
		execute("Mode.list");
		
		changeModule("Version");
		assertValueInList(1, 0, "OpenXava");
		assertValueInList(1, 1, "1.0");
		execute("List.viewDetail", "row=1");
		assertDescriptionValue("project.id", "OpenXava");  
		assertValue("name", "1.0");
		assertCollectionRowCount("issues", 1);
		assertValueInCollection("issues", 0, 0, "JUnit Incident");
		
		changeModule("Issue");
		assertListRowCount(1);
		assertValueInList(0, 0, "JUnit Incident");
		execute("List.viewDetail", "row=0");
		
		assertValue("title", "JUnit Incident");
		assertValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug  
		assertValue("description", "<p>This is a JUnit Incident</p>");
		assertDescriptionValue("project.id", "OpenXava"); 
		assertValue("createdBy", "admin");
		assertValue("createdOn", getCurrentDate()); // If fails revise the serverTimezone in MySQL url
		assertValue("priority.level", "7"); 
		assertValue("version.id", "2c94f081900856030190085eb1610001"); // 1.0 
		assertValue("assignedTo.id", "2c94f081900875e80190089c1211000b"); // Javi 2024.11
		assertValue("status.id", "2c94f081900875e80190088a559a0000"); // Pending  
		assertValue("customer.id", "2c94f081900875e8019008a637e8000f"); // Ministry of Industry
		assertValue("minutes", "90");
		assertValue("hours", "1.50");		
		
		assertFile("attachments", 0, "text/plain");
		assertDiscussionCommentsCount("discussion", 1);
		assertDiscussionCommentContentText("discussion", 0, "I agree");
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testMinimalIssue() throws Exception {
		login("admin", "admin"); 
		setValue("title", "JUnit Simple Incident");
		String [][] types = {
			{ "", "" },
			{ "2c94f081900875e801900896f25b0008", "Bug" },
			{ "2c94f081900875e80190089701170009", "Feature" }
		};
		assertValidValues("type.id", types);
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug
		setValue("description", "This is a JUnit Incident");
		
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		assertValueInList(0, 0, "JUnit Simple Incident");
		execute("List.viewDetail", "row=0");
		
		assertValue("title", "JUnit Simple Incident");
		assertValue("type.id", "2c94f081900875e801900896f25b0008"); 
		assertValue("description", "<p>This is a JUnit Incident</p>");
		assertValue("createdBy", "admin");
		assertValue("createdOn", getCurrentDate());

		execute("CRUD.delete");
		assertNoErrors();		
	}
	
	private String getCurrentDate() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy")); 
	}

}
