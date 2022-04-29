package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */

public class Movie2Test extends CustomizeListTestBase { 
	
	public Movie2Test(String testName) {
		super(testName, "Movie2");		
	}
	
	public void testRequiredFilesOnModify() throws Exception { 
		execute("List.viewDetail", "row=2"); 
		assertValue("title", "NOVECENTO");	
		assertFilesCount("scripts", 1); 
		removeFile("scripts", 0);
		execute("CRUD.save");
		assertError("Value for Scripts in Movie 2 is required");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=2"); 
		assertValue("title", "NOVECENTO");	
		assertFilesCount("scripts", 1);
		uploadFile("scripts", "test-files/alternative-script.txt"); 
		
		execute("Mode.list");
		execute("List.viewDetail", "row=2"); 
		assertValue("title", "NOVECENTO");	
		assertFilesCount("scripts", 2); 
		removeFile("scripts", 1);
		removeFile("scripts", 0);
		
		execute("Mode.list");
		execute("List.viewDetail", "row=2"); 
		assertValue("title", "NOVECENTO");	
		assertFilesCount("scripts", 1);
	}
	
}
