package org.openxava.test.tests;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * Create on 09/06/2011 (16:04:34)
 * @author Ana Andres
 */
public class AuthorTest extends CustomizeListTestBase {
	
	private boolean modulesLimit = true; 
	
	public AuthorTest(String testName) {
		super(testName, "Author");		
	}
	
	public void testComparatorsShownOnDemand_noFilterInCollectionByDefault() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true);
		
		assertFalse(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionComparator___0").isDisplayed());
		assertFalse(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionComparator___1").isDisplayed());
		setConditionValues("JAVI");
		assertTrue(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionComparator___0").isDisplayed()); 
		assertFalse(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionComparator___1").isDisplayed());
		
		assertTrue(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionValue___0").isDisplayed());
		setConditionValues("");
		setConditionComparators("empty_comparator");
		assertFalse(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionValue___0").isDisplayed());
		assertTrue(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionComparator___0").isDisplayed());
		
		setConditionComparators("not_empty_comparator");
		assertFalse(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionValue___0").isDisplayed());
		assertTrue(getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__conditionComparator___0").isDisplayed());		
		
		execute("CRUD.new");		
		assertCollectionFilterNotDisplayed();
		getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__show_filter_humans").click();
		assertCollectionFilterDisplayed();
		
		execute("Mode.list");
		execute("List.viewDetail", "row=1");
		assertCollectionFilterNotDisplayed();
		getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__show_filter_humans").click();
		assertCollectionFilterDisplayed();
		
		execute("CRUD.new");
		assertCollectionFilterNotDisplayed();
		getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__show_filter_humans").click();
		assertCollectionFilterDisplayed();
		getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__hide_filter_humans").click();
		Thread.sleep(1000);
		assertCollectionFilterNotDisplayed();
	}
	
	private void assertCollectionFilterDisplayed() { 
		assertTrue(getHtmlPage().getElementById("ox_OpenXavaTest_Author__xava_collectionTab_humans_conditionValue___0").isDisplayed());
	}
	
	private void assertCollectionFilterNotDisplayed() { 
		assertFalse(getHtmlPage().getElementById("ox_OpenXavaTest_Author__xava_collectionTab_humans_conditionValue___0").isDisplayed());
	}
	
	public void testModuleFromMenuReinitModule() throws Exception { 
		modulesLimit = false; 
		resetModule();
		
		assertNotExists("author");
		assertNoAction("CRUD.save");
		assertListRowCount(2);
		setConditionValues("JAVI");
		execute("List.filter");
		assertListRowCount(1);
		execute("CRUD.new");
		assertExists("author");
		assertAction("CRUD.save");
				
		HtmlAnchor link = (HtmlAnchor) getHtmlPage().getElementById("Author_module").getParentNode();
		link.click();
		
		waitAJAX();
		assertNotExists("author");
		assertNoAction("CRUD.save");
		assertListRowCount(2);
	}
	
	public void testMoveColumn_placeholder() throws Exception { 
		// To test a specific bug moving columns
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");
		moveColumn(0, 1);
		assertLabelInList(0, "Biography");
		assertLabelInList(1, "Author");
		resetModule();
		assertLabelInList(0, "Biography");
		assertLabelInList(1, "Author");

		// Placeholder
		execute("CRUD.new");
		String inputPlaceholder = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__author").getAttribute("placeholder");
		assertTrue("Author full name".equals(inputPlaceholder));
		String textareaPlaceholder = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Author__biography").getAttribute("placeholder");
		assertTrue("Put here the biography of the author".equals(textareaPlaceholder));
	}

	
	public void testRemoveColumnAfterFiltering() throws Exception {
		assertListRowCount(2);
		assertListColumnCount(2);		
		setConditionValues("J");
		execute("List.filter");
		assertListRowCount(1);
		removeColumn(1); 
		assertListRowCount(1);
		assertListColumnCount(1); 
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(2);
		assertListRowCount(1); 
	}
	
	
	public void testAddRemoveActionsForProperty() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertNoAction("Author.addSuffix");
		execute("Author.showAddSuffix");
		assertAction("Author.addSuffix");
		assertValue("author", "JAVIER PANIZA");
		execute("Author.addSuffix", "xava.keyProperty=author");
		assertValue("author", "JAVIER PANIZA LUCAS");
		assertAction("Author.addSuffix");
		execute("Author.hideAddSuffix");
		assertNoAction("Author.addSuffix");
	}
	
	public void testOverwritingDefaultSearch() throws Exception {
		execute("List.viewDetail", "row=0");
		assertMessage("Showing author JAVIER PANIZA");
		assertValue("author", "JAVIER PANIZA");
		execute("Navigation.next");
		assertMessage("Showing author MIGUEL DE CERVANTES"); 
		assertValue("author", "MIGUEL DE CERVANTES");
		execute("SearchForCRUD.search");  
		setValue("author", "JAVIER PANIZA");
		execute("Search.search");
		assertMessage("Showing author JAVIER PANIZA");
		assertValue("author", "JAVIER PANIZA");
		execute("Mode.list");
		execute("List.viewDetail", "row=1");
		assertMessage("Showing author MIGUEL DE CERVANTES");
		assertValue("author", "MIGUEL DE CERVANTES");
		assertEquals("Author - MIGUEL DE CERVANTES", getHtmlPage().getHtmlElementById("module_title").asText());
		reload();
		assertEquals("Author - MIGUEL DE CERVANTES", getHtmlPage().getHtmlElementById("module_title").asText());
	}
	

	public void testCollectionViewWithGroup_getMapValuesFromList_cutIOnlyKeysInCollections() throws Exception { 
		assertLabelInList(0, "Author");
		assertValueInList(1, 0, "MIGUEL DE CERVANTES"); 
		execute("List.viewDetail", "row=1");
		assertCollectionRowCount("humans", 2); 

		execute("Author.showAllAuthors", "viewObject=xava_view_humans");
		assertMessage("PEPE, MALE");
		assertMessage("JUANA, FEMALE"); 
		checkRowCollection("humans", 0);
		execute("Author.showSelectedAuthors", "viewObject=xava_view_humans");
		assertMessagesCount(1); 
		assertMessage("PEPE, MALE");
		execute("Author.showSelectedAuthors", "row=0,viewObject=xava_view_humans");
		assertMessage("PEPE, MALE");		
		
		execute("CollectionCopyPaste.cut", "row=1,viewObject=xava_view_humans");
		execute("Author.showCutRows");
		assertMessage("Cut rows: [{id=40288196178ce73201178ce7d7cb0002}]");
		checkRowCollection("humans", 0);
		execute("CollectionCopyPaste.cut", "viewObject=xava_view_humans");
		execute("Author.showCutRows");
		assertMessage("Cut rows: [{id=40288196178c79f301178c7a48080002}]");
		
		execute("Collection.edit", "row=0,viewObject=xava_view_humans"); 
		assertNoErrors();
		assertDialog();
	}
	 
	public void testCustomMessageWithBeanValidationJSR303_validationExceptionFromPreUpdate() throws Exception { 
		execute("CRUD.new");
		setValue("author", "PEPE");
		execute("CRUD.save");
		assertError("Sorry, but PEPE is not a good name for an author");
		
		execute("Navigation.first");
		assertValue("author", "JAVIER PANIZA");
		setValue("biography", "BORN IN VALENCIA");
		execute("CRUD.save");
		assertErrorsCount(1);
		assertError("Uppercase text is not allowed for Biography");
	}
	
	protected String getModuleURL() {  
		return modulesLimit?super.getModuleURL():"http://" + getHost() + ":" + getPort() + "/OpenXavaTest/m/Author"; // /m/ not /modules/ in order a test works
	}
	
}
