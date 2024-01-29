package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;

/**
 * 
 * Create on 09/06/2011 (16:04:34)
 * @author Ana Andres
 * @author Javier Paniza
 */
public class AuthorTest extends CustomizeListTestBase {
	
	private boolean modulesLimit = true; 
	
	public AuthorTest(String testName) {
		super(testName, "Author");		
	}
	
	public void testAddColumnFromCollectionInList() throws Exception { 
		assertListRowCount(2);
		assertListColumnCount(2);
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 2); 		
		assertValueInCollection("xavaPropertiesList",  0, 0, "Humans name");
		assertValueInCollection("xavaPropertiesList",  1, 0, "Humans sex");
		checkRow("selectedProperties", "humans.name");
		execute("AddColumns.addColumns");
		assertListRowCount(2);
		assertListColumnCount(3);
		assertLabelInList(0, "Author");
		assertLabelInList(1, "Biography");
		assertLabelInList(2, "Humans");
		setConditionValues("", "", "PEPE");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "MIGUEL DE CERVANTES");
		assertValueInList(0, 2, "Matching humans: 1");
	}
	
	public void testComparatorsShownOnDemand() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true);
		reload(); 
		
		assertListRowCount(2);
		
		HtmlSelect comparator = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionComparator___0");
		assertFalse(comparator.isDisplayed()); 
		HtmlInput value = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionValue___0");
		value.focus();
		assertTrue(comparator.isDisplayed()); 
		
		comparator.setSelectedAttribute("starts_comparator", true);
		waitAJAX();
		comparator = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionComparator___0");
		assertTrue(comparator.isDisplayed()); // Still displayed because it has not run the query, given the value is blank
		assertListRowCount(2);
		
		value = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionValue___0");
		value.focus();
		value.setValue("javi");
		comparator.setSelectedAttribute("contains_comparator", true);
		waitAJAX();
		assertListRowCount(1);
		
		comparator = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionComparator___0");
		value = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionValue___0");
		value.focus();
		value.setValue("");
		comparator.setSelectedAttribute("empty_comparator", true);
		waitAJAX();
		assertListRowCount(0);
		
		comparator = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionComparator___0");
		value = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__conditionValue___0");
		assertFalse(value.isDisplayed());
		assertEquals("", value.getValue());
		comparator.setSelectedAttribute("not_empty_comparator", true);
		waitAJAX();
		assertListRowCount(2);		
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
	
	public void testHTMLCharsInList_placeholder() throws Exception { 
		// HTML characters in list
		assertValueInList(0, "biography", "aaaa \" bbbb > cccc");

		// Placeholder
		execute("CRUD.new");
		String inputPlaceholder = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__author").getAttribute("placeholder");
		assertTrue("Author full name".equals(inputPlaceholder));
		String textareaPlaceholder = getHtmlPage().getHtmlElementById("ox_openxavatest_Author__biography").getAttribute("placeholder");
		assertTrue("Put here the biography of the author".equals(textareaPlaceholder));
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
	
	public void testOverwritingDefaultSearch_overwritingGoListAction() throws Exception { 
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

		assertNoAction("Mode.list");
		execute("MyGoListMode.list");
		assertMessage("Back to list");

		execute("List.viewDetail", "row=1");
		assertMessage("Showing author MIGUEL DE CERVANTES");
		assertValue("author", "MIGUEL DE CERVANTES");
		assertEquals("Author - MIGUEL DE CERVANTES", getHtmlPage().getHtmlElementById("module_extended_title").asNormalizedText()); 
		reload();
		assertEquals("Author - MIGUEL DE CERVANTES", getHtmlPage().getHtmlElementById("module_extended_title").asNormalizedText()); 
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
		return modulesLimit?super.getModuleURL():"http://" + getHost() + ":" + getPort() + getContextPath() + "m/Author"; // /m/ not /modules/ in order a test works 
	}
	
}
