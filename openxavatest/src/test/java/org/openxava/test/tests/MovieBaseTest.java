package org.openxava.test.tests;

import org.openxava.jpa.*;

/** 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
abstract public class MovieBaseTest extends EmailNotificationsTestBase { 
	
	private static final String MIME_UNKNOWN = "application/octet-stream";
	
	public MovieBaseTest(String testName, String moduleName) {
		super(testName, moduleName); 
	}
		
	public void testAddFile() throws Exception {
		addFile();
		assertFile("trailer", "text/html"); 
		removeFile(); 
	}
	
	public void testAddFilesInNewEntity() throws Exception { 
		execute("CRUD.new");
		setValue("title", "MATRIX");
		uploadFile("scripts", "reports/Corporation.html");
		saveAndReloadMovie("MATRIX");
		assertFilesCount("scripts", 1);
		assertFile("scripts", 0, "text/html");
		
		removeFile("scripts", 0);
		execute("CRUD.delete");
	}
	
	protected void addFile() throws Exception {
		execute("CRUD.new");
		setValue("title", "JUNIT");
		uploadFile("trailer", "reports/Corporation.html"); 
		saveAndReloadMovie("JUNIT");
	}
	
	protected void removeFile() throws Exception { 
		execute("CRUD.delete");
		XPersistence.getManager()
			.createQuery("delete from AttachedFile where name = 'Corporation.html'")
			.executeUpdate();
	}
	
	protected void saveAndReloadMovie(String title) throws Exception { 
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(2, 0, title);
		execute("List.viewDetail", "row=2");
		assertValue("title", title);		
	}
		
}