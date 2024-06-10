package com.yourcompany.yourapp.tests;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class IssueTest extends ModuleTestBase {

	public IssueTest(String nameTest) {
		super(nameTest, "xavaprojects", "Issue");
	}
	
	public void testCreateNewIssue() throws Exception {
		login("admin", "admin"); 
		setValue("title", "JUnit Incident");
		String [][] types = {
			{ "", "" },
			{ "4028808d7eea19fe017eea61bec90024", "Bug" },
			{ "4028808d7eea19fe017eea61d4f20025", "Feature" }
		};
		assertValidValues("type.id", types);
		setValue("type.id", "4028808d7eea19fe017eea61bec90024"); // Bug 
		setValue("description", "This is a JUnit Incident");
		assertValidValuesCount("project.id", 2);
		assertDescriptionValue("project.id", "OpenXava"); 		
		assertValue("createdBy", "admin");
		assertNoEditable("createdBy");
		assertValue("createdOn", getCurrentDate());

		String [][] priorities = {
			{ "", "" },
			{ "7", "7 High" },
			{ "5", "5 Normal" },
			{ "3", "3 Low" }
		};
		assertValidValues("priority.level", priorities);
		assertValue("priority.level", "5");
		setValue("priority.level", "7");

		String [][] versions = {
			{ "", "" },
			{ "4028808d7eea19fe017eea5074130012", "2.0" },
			{ "4028808d7eea19fe017eea5057f30011", "1.0" }
		};
		assertValidValues("version.id", versions);
		setValue("version.id", "4028808d7eea19fe017eea5057f30011"); // 1.0 
			
		String [][] plans = {
			{ "", "" },	
			{ "4028808d7eea19fe017eea5b5534001e", "Javi 2019.10" },
			{ "4028808d7eea19fe017eea5b675b001f", "Javi 2019.11" }
		};
		assertValidValues("assignedTo.id", plans);
		setValue("assignedTo.id", "4028808d7eea19fe017eea5b675b001f"); // 2019.11 
		
		String [][] status = {
			{ "", "" },	
			{ "4028808d7eea19fe017eea2160df0002", "Done" },
			{ "4028808d7eea19fe017eea2272ae0003", "Not reproducible" },
			{ "4028808d7eea19fe017eea1e4ffb0001", "Pending" },
			{ "4028808d7eea19fe017eea2380970004", "Rejected" }
		};
		
		assertValidValues("status.id", status);
		assertValue("status.id", "4028808d7eea19fe017eea1e4ffb0001"); // Pending 
		
		String [][] customers = {
			{ "", "" },	
			{ "4028808d7ef9b160017ef9b24a960001", "Banco Santander" },
			{ "4028808d7ef9b160017ef9b28a510002", "Oracle Corporation" }			
		};
		
		assertValidValues("customer.id", customers);
		setValue("customer.id", "4028808d7ef9b160017ef9b24a960001"); // Banco Santander 

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
		assertValue("type.id", "4028808d7eea19fe017eea61bec90024"); // Bug  
		assertValue("description", "<p>This is a JUnit Incident</p>");
		assertDescriptionValue("project.id", "OpenXava"); 
		assertValue("createdBy", "admin");
		assertValue("createdOn", getCurrentDate()); // If fails revise the serverTimezone in MySQL url
		assertValue("priority.level", "7"); 
		assertValue("version.id", "4028808d7eea19fe017eea5057f30011"); // 1.0 
		assertValue("assignedTo.id", "4028808d7eea19fe017eea5b675b001f"); // 2019.11
		assertValue("status.id", "4028808d7eea19fe017eea1e4ffb0001"); // Pending  
		assertValue("customer.id", "4028808d7ef9b160017ef9b24a960001"); // Banco Santander
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
			{ "4028808d7eea19fe017eea61bec90024", "Bug" },
			{ "4028808d7eea19fe017eea61d4f20025", "Feature" }
		};
		assertValidValues("type.id", types);
		setValue("type.id", "4028808d7eea19fe017eea61bec90024"); // Bug
		setValue("description", "This is a JUnit Incident");
		
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		assertValueInList(0, 0, "JUnit Simple Incident");
		execute("List.viewDetail", "row=0");
		
		assertValue("title", "JUnit Simple Incident");
		assertValue("type.id", "4028808d7eea19fe017eea61bec90024"); 
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
