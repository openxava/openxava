package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;



/**
 * @author Javier Paniza
 */

public class ToDoListTest extends ModuleTestBase {
	
	public ToDoListTest(String testName) {
		super(testName, "ToDoList");		
	}
	
	// Dependent collection == collection with cascade ALL or REMOVE 
	public void testReferenceToADependentCollectionElement() throws Exception {
		// This only for JPA. In XML components it's not possible 
		// reference to an aggregate from outside
		execute("List.viewDetail", "row=0");
		assertValue("name", "THE TO DO LIST");
		assertCollectionRowCount("tasks", 2);
		assertValueInCollection("tasks", 0, 0, "TASK 1");
		execute("Collection.edit", "row=0,viewObject=xava_view_tasks");
		assertCollectionRowCount("componentsTasks", 1);
		assertValueInCollection("componentsTasks", 0, 0, "COMPONENT 1");		
	}
	
	public void testSelectAndDeselectAllCollectionElements_notCutPasteInCollectionWhenDifferentType() throws Exception {
		execute("List.viewDetail", "row=0");
		
		assertAllCollectionUnchecked("tasks");
		checkAllCollection("tasks");
		assertAllCollectionUnchecked("components");
		assertAllCollectionChecked("tasks");
		assertRowCollectionChecked("tasks", 0);
		assertRowCollectionChecked("tasks", 1);
		assertAllCollectionUnchecked("components");
		assertRowCollectionUnchecked("components", 0);
		
		uncheckRowCollection("tasks", 0);
		assertAllCollectionUnchecked("tasks");
		checkRowCollection("tasks", 0);
		assertAllCollectionChecked("tasks");
		
		setConditionValues("tasks", new String[] { "f" });
		execute("List.filter", "collection=tasks");
		assertCollectionRowCount("tasks", 0);
		assertAllCollectionUnchecked("tasks");
		
		setConditionValues("tasks", new String[] { "" });
		execute("List.filter", "collection=tasks");
		assertAllCollectionChecked("tasks");
		
		// using 'order by'
		execute("List.orderBy", "property=name,collection=tasks");
		assertValueInCollection("tasks", 0, 0, "TASK 1");
		assertValueInCollection("tasks", 1, 0, "TASK 2");
		uncheckRowCollection("tasks", 0);
		execute("List.orderBy", "property=name,collection=tasks");
		assertValueInCollection("tasks", 0, 0, "TASK 2");
		assertValueInCollection("tasks", 1, 0, "TASK 1");
		assertRowCollectionUnchecked("tasks", 1);
		execute("List.orderBy", "property=name,collection=components");
		assertAllCollectionUnchecked("components");
		
		assertNoAction("CollectionCopyPaste.paste");
		execute("CollectionCopyPaste.cut", "row=0,viewObject=xava_view_tasks");
		assertNoAction("CollectionCopyPaste.paste", "viewObject=xava_view_components");
		reload();
		assertNoAction("CollectionCopyPaste.paste", "viewObject=xava_view_components");
	}
	
	public void testENTERForFilteringInListAndCollections() throws Exception { 
		assertListRowCount(1);
		assertValueInList(0, 0, "THE TO DO LIST");
		HtmlTextInput filterListText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ToDoList__conditionValue___0");
		filterListText.type("LISTA");
		filterListText.type('\r');
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);	
		assertListRowCount(0);
		assertNoErrors(); 
		filterListText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ToDoList__conditionValue___0");
		filterListText.setText("LIST");
		assertEquals("LIST", filterListText.getAttribute("value"));
		filterListText.type('\r');
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);	
		assertListRowCount(1);
		assertValueInList(0, 0, "THE TO DO LIST");
		assertNoErrors(); 
				
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("tasks", 2);
		assertValueInCollection("tasks", 0, 0, "TASK 1");
		assertValueInCollection("tasks", 1, 0, "TASK 2");
		assertCollectionRowCount("components", 2);
		assertValueInCollection("components", 0, 0, "COMPONENT 1");
		assertValueInCollection("components", 1, 0, "COMPONENT 2");
		
		HtmlElement filterTasksText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ToDoList__xava_collectionTab_tasks_conditionValue___0");
		filterTasksText.type("2");
		filterTasksText.type('\r');
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertNoErrors(); 
		assertCollectionRowCount("tasks", 1);
		assertValueInCollection("tasks", 0, 0, "TASK 2");
		assertCollectionRowCount("components", 2);
		assertValueInCollection("components", 0, 0, "COMPONENT 1");
		assertValueInCollection("components", 1, 0, "COMPONENT 2");
		
		HtmlElement filterCompomentsText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ToDoList__xava_collectionTab_components_conditionValue___0");
		filterCompomentsText.type("1");
		filterCompomentsText.type('\r');
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertNoErrors(); 
		assertCollectionRowCount("tasks", 1);
		assertValueInCollection("tasks", 0, 0, "TASK 2");
		assertCollectionRowCount("components", 1);
		assertValueInCollection("components", 0, 0, "COMPONENT 1");
	}
	
	protected BrowserVersion getBrowserVersion() throws Exception {
		return BrowserVersion.INTERNET_EXPLORER; 
	}
	
}
