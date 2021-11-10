package org.openxava.test.tests;

import java.io.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProjectTest extends ModuleTestBase { 
	
	public ProjectTest(String testName) {
		super(testName, "Project");		
	}
	
	public void testWebURLEditor() throws Exception { 
		execute("List.viewDetail", "row=0");
		setValueInCollection("notes", 0, 2, "www.openxava.org");
		setValueInCollection("notes", 1, 2, "http://www.rae.es/"); 
		assertLinkOnNote(0, "The fastest development for Java"); 
		
		assertLinkOnNote(1, "Diccionario de la lengua"); // Fails with Java 8 with a problem with Cloudfare protection in RAE
	}

	private void assertLinkOnNote(int idx, String expectedText) throws IOException {
		HtmlAnchor link = (HtmlAnchor) getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Project__editor_notes___" + idx + "___docURL").getElementsByTagName("a").get(0); 
		HtmlPage page = link.click();
		assertTrue(page.asText().contains(expectedText));
	}
		
	public void testAddElementsToListWithOrderColumn_removeElementFromListWithOrder() throws Exception {  
		long initialTasksCount = ProjectTask.count(); 
		XPersistence.commit();
		
		execute("CRUD.new");
		setValue("name", "JUNIT PROJECT");
		
		assertCollectionRowCount("members", 0);	
		execute("Collection.add", "viewObject=xava_view_members");
		assertValueInList(4, 0, "ZOE"); 
		execute("AddToCollection.add", "row=4");
		assertNoErrors();
		assertCollectionRowCount("members", 1);
		assertValueInCollection("members", 0, 0, "ZOE");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_members");
		assertCollectionRowCount("members", 0);
		execute("Collection.add", "viewObject=xava_view_members");
		assertValueInList(4, 0, "ZOE");
		execute("AddToCollection.add", "row=4"); // We need to test that we can add the same item again
		assertNoErrors();
		assertCollectionRowCount("members", 1);
		assertValueInCollection("members", 0, 0, "ZOE");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_members");
		assertCollectionRowCount("members", 0);
		
		execute("Collection.new", "viewObject=xava_view_members");
		setValue("name", "AMANDA");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("members", 1);
		assertValueInCollection("members", 0, 0, "AMANDA");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_members");
		assertCollectionRowCount("members", 0);		
		
		assertCollectionRowCount("tasks", 0);	
		execute("Collection.new", "viewObject=xava_view_tasks");
		setValue("description", "THE JUNIT TASK");
		setValue("priority", "2");
		setValue("dueDate", "3/20/15");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("tasks", 1);
		assertValueInCollection("tasks", 0, 0, "THE JUNIT TASK");
		assertValueInCollection("tasks", 0, 1, "HIGH");
		assertValueInCollection("tasks", 0, 2, "3/20/2015");
		
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_tasks");
		assertCollectionRowCount("tasks", 0);
		assertEquals(initialTasksCount, ProjectTask.count());
				
		execute("CRUD.delete");
		assertNoErrors();
		
		XPersistence.getManager().createQuery("delete from ProjectMember m where m.name = 'AMANDA'").executeUpdate(); // tm
	}
		
	public void testMoveElementAfterAddingSeveralElementsInElementCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("notes", 3);
		assertValueInCollection("notes", 0, 0, "WE BEGIN");
		assertValueInCollection("notes", 1, 0, "WE WORK");
		assertValueInCollection("notes", 2, 0, "WE FINISH");
		
		setValueInCollection("notes", 3, "note", "A");
		setValueInCollection("notes", 4, "note", "B");
		setValueInCollection("notes", 5, "note", "C"); 
		
	
		assertCollectionRowCount("notes", 6);  
		assertValueInCollection("notes", 0, 0, "WE BEGIN");
		assertValueInCollection("notes", 1, 0, "WE WORK");
		assertValueInCollection("notes", 2, 0, "WE FINISH");
		assertValueInCollection("notes", 3, 0, "A");
		assertValueInCollection("notes", 4, 0, "B");
		assertValueInCollection("notes", 5, 0, "C");
		
		moveRow("notes", 1, 4, true);
		
		assertCollectionRowCount("notes", 6);		
		assertValueInCollection("notes", 0, 0, "WE BEGIN");		
		assertValueInCollection("notes", 2, 0, "WE FINISH");
		assertValueInCollection("notes", 3, 0, "A");
		assertValueInCollection("notes", 4, 0, "B");
		assertValueInCollection("notes", 1, 0, "WE WORK");
		assertValueInCollection("notes", 5, 0, "C");
	}
	
	public void testMoveElementInCollectionWithOrderColumn() throws Exception { 
		String [] membersElements = {"JOHN", "JUAN", "PETER"};
		moveElementInCollectionWithOrderColumn("members", membersElements, false); 
		assertTrue(getHtml().contains("class=\"xava_handle ")); // To verify that ProjectReadOnlyCollectionsTest.testReadOnlyListNotSortable() is valid
	}
	
	public void testMoveElementInAggregateCollectionWithOrderColumn() throws Exception { 
		String [] tasksElements = {"ANALYSIS", "DESIGN", "PROGRAMMING"};
		moveElementInCollectionWithOrderColumn("tasks", tasksElements, false); 
	}
	
	public void testMoveElementInElementCollectionWithOrderColumn() throws Exception {  
		String [] tasksElements = {"WE BEGIN", "WE WORK", "WE FINISH"};
		moveElementInCollectionWithOrderColumn("notes", tasksElements, true); 
	}
	
	public void testEditElementInCollectionAfterMoveWithOrderColumn() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("members", 3);
		assertValueInCollection("members", 0, 0, "JOHN");
		assertValueInCollection("members", 1, 0, "JUAN");
		assertValueInCollection("members", 2, 0, "PETER");
	
		moveRow("members", 2, 0);
		assertValueInCollection("members", 0, 0, "PETER");
		assertValueInCollection("members", 1, 0, "JOHN");
		assertValueInCollection("members", 2, 0, "JUAN");
		
		// With HtmlUnit in order to use the actual row 1
		HtmlTable table = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Project__members");
		HtmlTableRow row = table.getRow(1);
		HtmlTableCell cell = row.getCell(2);
		assertEquals("PETER", cell.asText().trim());
		HtmlElement link = cell.getElementsByTagName("a").get(0);
		link.click();
		waitAJAX();
		
		assertValue("name", "PETER");
		closeDialog();
		
		moveRow("members", 1, 0);
		moveRow("members", 2, 1);
		
	}
	
	private void moveElementInCollectionWithOrderColumn(String collection, String [] elements, boolean save) throws Exception {  
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount(collection, 3);
		assertValueInCollection(collection, 0, 0, elements[0]);
		assertValueInCollection(collection, 1, 0, elements[1]);
		assertValueInCollection(collection, 2, 0, elements[2]);
		moveRow(collection, 2, 0);
		if (save) execute("CRUD.save"); 
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount(collection, 3);
		assertValueInCollection(collection, 0, 0, elements[2]);
		assertValueInCollection(collection, 1, 0, elements[0]);
		assertValueInCollection(collection, 2, 0, elements[1]);		
		moveRow(collection, 1, 0);
		if (save) execute("CRUD.save"); 
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount(collection, 3);
		assertValueInCollection(collection, 0, 0, elements[0]);
		assertValueInCollection(collection, 1, 0, elements[2]);
		assertValueInCollection(collection, 2, 0, elements[1]);				
		moveRow(collection, 2, 1);
		if (save) execute("CRUD.save"); 
	}
		
}
