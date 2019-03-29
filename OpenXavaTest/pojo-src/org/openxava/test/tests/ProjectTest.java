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
		assertLinkOnNote(0, "Java Framework for Rapid Development of Enterprise Web Applications"); 
		assertLinkOnNote(1, "Diccionario de la lengua"); 
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
		assertValueInCollection("tasks", 0, 2, "3/20/15");
		
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_tasks");
		assertCollectionRowCount("tasks", 0);
		assertEquals(initialTasksCount, ProjectTask.count());
				
		execute("CRUD.delete");
		assertNoErrors();
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
	
	private void moveRow(String collection, int from, int to) throws Exception {
		moveRow(collection, from, to, false);
	}
	
	private void moveRow(String collection, int from, int to, boolean classRequired) throws Exception {   
		// This method does not work for all "from, to" combinations, at least with HtmlUnit 2.15
		HtmlTable table = getHtmlPage().getHtmlElementById(decorateId(collection));
		HtmlElement fromRow = table.getRow(from + 1);
		HtmlElement fromHandler = fromRow.getElementsByAttribute("i", "class", "xava_handle mdi mdi-swap-vertical ui-sortable-handle").get(0);
		fromHandler.mouseDown();
		HtmlElement toRow = table.getRow(to + 1);
		if (classRequired) assertTrue(toRow.getAttribute("class").contains("xava_sortable_element_row"));
		toRow.mouseMove();
		toRow.mouseUp();
		Thread.sleep(500); 		
	}

		
}
