package org.openxava.test.bymodule;

import org.openxava.test.services.*;
import org.openxava.tests.*;

import org.htmlunit.*;

/**
 * 
 * @author Javier Paniza
 */

public class BookTest extends ModuleTestBase {
	
	private class MyAlertHandler implements AlertHandler {
		
		private String message;

		public void handleAlert(Page page, String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}

	}
	
	public BookTest(String testName) {
		super(testName, "Book");		
	}
	
	public void testTabSetConditionValueBoolean_validatorAnnotationMessage() throws Exception { 
		assertListRowCount(2);
		execute("Book.showOutOfPrint");
		assertNoErrors();
		assertListRowCount(1);
		assertValueInList(0, "outOfPrint", "Out of print");
		
		execute("CRUD.new");
		execute("CRUD.save");
		assertError("Sorry, but you need to enter the book title"); 
		assertError("Please, enter a synopsis for the book");
		
		setValue("title", "EL QUIJOTE");
		setValue("synopsis", "JAVA PROGRAMMING GUIDE");
		execute("CRUD.save");
		assertError("The synopsis does not match with the title");
		
		setValue("title", "RPG: THE MOST INNOVATIVE IBM LANGUAGE");
		execute("CRUD.save");
		assertError("Books about RPG are not allowed");
	}
	
	public void testReferenceNameMatchesIdOfReferencedEntityName_callRESTService_showCustomActionInReferenceSearchAction() throws Exception { 
		execute("CRUD.new");
		execute("Book.addAction", "keyProperty=author.author");	
		assertListNotEmpty();
		assertAction("Book.doNothing");
		String author = getValueInList(0, 0);		
		execute("ReferenceSearch.choose", "row=0");
		assertNoErrors(); 				
		assertValue("author.author", author); 
		
		assertValue("title", "");
		execute("Book.fillTitle");
		assertValue("title", "THE MYTHICAL MAN-MONTH"); // Jersey fails with Tomcat 7. Test it with a Tomcat 8 + Java 8 at least. 
		assertEquals("The Mythical Man-Month", BookService.get().getTitle()); // To verify you can use REST from JUnit tests
	}
	
	// This test fails in PostgreSQL, but not in Hypersonic
	public void testListFilterByBooleanColumnInDB_XSSNotJSInListFilterValues_bigIntegarAsNumeric() throws Exception {  
		MyAlertHandler alertHandler = new MyAlertHandler();
		getWebClient().setAlertHandler(alertHandler);
		
		assertAction("List.sumColumn", "property=pages"); // So pages is numeric 

		assertListRowCount(2); 
		setConditionComparators ("=", "=", "=", "=");
		setConditionValues ("", "", "", "true");
		execute("List.filter");
		assertListRowCount(1);
		
		setConditionValues("<script>alert('hello');</script>");
		execute("List.filter");
		assertNotEquals("Message not expected", "hello", alertHandler.getMessage());
	}
		
}
