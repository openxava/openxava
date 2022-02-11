package org.openxava.test.tests;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class ApplicantTest extends ModuleTestBase {
	
	private boolean modulesLimit = true;
	private boolean moduleURLOnRoot = false; 
	
	public ApplicantTest(String testName) {
		super(testName, "Applicant");		
	}
			
	public void testCancelAddColumnsFromSearhReference_changeListConfigurationFromSearchReference() throws Exception { 
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=skill.description");
		assertValueInList(0, 0, "PROGRAMMING");
		execute("List.addColumns");
		execute("AddColumns.cancel");
		assertNoAction("AddColumns.cancel");
		assertValueInList(0, 0, "PROGRAMMING");
		
		setConditionValues("P");
		execute("List.filter");
		execute("List.saveConfiguration");
		execute("SaveListConfiguration.save"); // In order List.changeConfiguration be available for the next tests 
		
		execute("List.changeConfiguration"); 
		assertExists("name");
		assertNoAction("List.filter");
		execute("ChangeListConfiguration.cancel");
		assertNotExists("name");
		assertAction("List.filter");
		
		execute("List.changeConfiguration");
		assertExists("name");
		assertNoAction("List.filter");
		execute("ChangeListConfiguration.change");
		assertNotExists("name");
		assertAction("List.filter");		
	}
	
	public void testGetEntityWithEmptyReferences_duplicateActionsNotAdded_keepAddedActionAfterCloseDialog_noPermalinkForNoModuleModel() throws Exception { // noPermalinkForNoModuleModel   		
		assertListRowCount(1);
		execute("CRUD.new");
		assertNoAction("JPACRUD.create");
		execute("Applicant.showCreate");
		assertAction("JPACRUD.create"); 
		assertActionsCount("JPACRUD.create", 1);
		setValue("name", "JUNIT APPLICANT");
		execute("JPACRUD.create");
		assertNoErrors();

		assertAction("JPACRUD.create");
		execute("Reference.createNew", "model=Skill,keyProperty=skill.description");
		execute("NewCreation.cancel");
		assertAction("JPACRUD.create");

		execute("Mode.list");
		assertListRowCount(2);
		assertValueInList(1, 0, "JUNIT APPLICANT CREATED"); // The CREATED is for a @PrePersist needed for the test testPolymorphicReferenceFromBaseClass_savingTwiceWithNoRefreshAfterAndHiddenKey()
		execute("CRUD.deleteRow", "row=1");
		assertListRowCount(1);
		
		execute("List.viewDetail", "row=0");
		HtmlUnitUtils.assertPageURI(getHtmlPage(), "/Applicant?detail=ff8080823dee26af013dee2998260001");
		execute("Applicant.setActingLevelA");
		HtmlUnitUtils.assertPageURI(getHtmlPage(), "/Applicant");
	}
		
	private void assertActionsCount(String action, int expectedCount) { 
		String inputName = "ox_OpenXavaTest_Applicant__action___" + action.replace(".", "___"); 
		assertEquals(expectedCount, getHtmlPage().getElementsByName(inputName).size());		
	}

	private void assertHelp(String language) throws Exception { 
		try {
			getHtmlPage().getAnchorByHref("http://www.openxava.org/OpenXavaDoc/docs/help_" + language + ".html");
		}
		catch (ElementNotFoundException ex) {		
			fail("Help link is not correct"); 
		}
	}
	
	public void testModulesOnTop() throws Exception { 
		modulesLimit = false;
		resetModule();		
		
		HtmlPage page = getHtmlPage();
		HtmlDivision moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest : Invoices Orders Applicant", moduleHeader.asText().trim());
				
		HtmlElement articleLink = page.getAnchorByHref("/OpenXavaTest/m/Article?init=true");
		page = articleLink.click();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest : Invoices Orders Articles Applicant", moduleHeader.asText().trim());
		List<HtmlElement> selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Articles", selectedElements.get(0).asText());
		
		HtmlElement artistLink = page.getAnchorByHref("/OpenXavaTest/m/Artist?init=true");
		page = artistLink.click();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest : Invoices Orders Artist Articles Applicant", moduleHeader.asText().trim());
		selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Artist", selectedElements.get(0).asText());		
		
		articleLink = moduleHeader.getOneHtmlElementByAttribute("a", "href", "/OpenXavaTest/m/Article?retainOrder=true");
		page = articleLink.click();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest : Invoices Orders Artist Articles Applicant", moduleHeader.asText().trim());		
		selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Articles", selectedElements.get(0).asText());		
		
		moduleURLOnRoot = true;
		resetModule();
		page = getHtmlPage();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest : Invoices Orders Articles Artist Applicant", moduleHeader.asText().trim());		
		selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Articles", selectedElements.get(0).asText());		
	}
	
	public void testModulesMenu_help() throws Exception { 
		modulesLimit = false;
		resetModule();
		
		assertModulesCount(30); 
		assertTrue(AbstractWall.class.isAnnotationPresent(MappedSuperclass.class)); // This should be the first one, but as it's MappedSupperclass in not shown
		assertFirstModuleInMenu("Academic year"); // Not in i18n, to test a case 

		HtmlElement searchBox = getHtmlPage().getHtmlElementById("search_modules_text");
		searchBox.type("INVOICE");
		assertEquals("INVOICE", searchBox.getAttribute("value"));		
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(30);
		assertFirstModuleInMenu("Accounting invoices");  
		
		HtmlAnchor loadMoreModules = (HtmlAnchor) getHtmlPage().getHtmlElementById("more_modules").getParentNode();
		loadMoreModules.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(59); // We have to adjust this when we add new modules that content "invoice" 
		
		
		searchBox.type(" \b");
		assertEquals("", searchBox.getAttribute("value"));
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(30); 
		
		loadMoreModules = (HtmlAnchor) getHtmlPage().getHtmlElementById("more_modules").getParentNode();
		loadMoreModules.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertTrue(getModulesCount() > 300);
		
		searchBox.type("artist");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(2); // Test 'Artist' not duplicate when module in application.xml 
		assertModuleInMenu(0, "Artist");  
		assertModuleInMenu(1, "Artist some members read only"); 
		
		assertHelp("en"); 
	}
	
	public void testChangeLocaleAffectsMenu() throws Exception {  
		modulesLimit = false;
		resetModule();
		
		assertLabels("Name", "Author"); 
		
		execute("Applicant.changeToSpanish");
		reload();
		
		assertLabels("Nombre", "Autor");
		assertHelp("es"); 
	}
	
	private void assertLabels(String propertyLabel, String moduleLabel) throws Exception {
		assertLabelInList(0, propertyLabel);
		assertModuleInMenu(17, moduleLabel); // Adapt the index if you add more modules on top of Author 
		
		HtmlElement searchBox = getHtmlPage().getHtmlElementById("search_modules_text");
		searchBox.type("aut");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(1);
		assertFirstModuleInMenu(moduleLabel);  		
	}

	private void assertFirstModuleInMenu(String expectedName) {
		assertModuleInMenu(0, expectedName);
	}
	
	private void assertModuleInMenu(int row, String expectedName) {
		HtmlElement module = getHtmlPage().getHtmlElementById("modules_list").getElementsByAttribute("div", "class", "module-row ").get(row); 
		HtmlElement moduleName = module.getElementsByAttribute("div", "class", "module-name").get(0);
		assertEquals(expectedName, moduleName.asText());
	}	
	
	public void testPolymorphicReferenceFromBaseClass_savingTwiceWithNoRefreshAfterAndHiddenKey_showHideButtons_labelsPut() throws Exception {  
		// Polymorphic reference from base class
		execute("List.viewDetail", "row=0");
		assertNoErrors(); 
		assertValue("name", "JUANILLO"); 
		assertValue("skill.description", "PROGRAMMING");
		assertValue("skill.language", "JAVA"); 
		assertValue("platform", "MULTIPLATFORM");
		
		// Labels.put()
		assertLabel("platform", "Platform"); // If it fails reinit the Tomcat cleaning the working directories 
		execute("Applicant.changePlatformLabel");
		reload();
		assertLabel("platform", "Target ecosystem");		
		
		// Saving twice with no refresh after and hidden key
		execute("CRUD.new");
		setValue("name", "PEPITO");
		execute("Applicant.saveNotRefresh");
		assertNoErrors();
		assertMessage("Applicant created successfully");
		assertValue("name", "PEPITO"); // Without CREATED suffix, so not refresh
		execute("Applicant.saveNotRefresh");
		assertNoErrors();
		assertMessage("Applicant modified successfully");
		assertValue("name", "PEPITO");	
		execute("CRUD.delete");
		assertNoErrors();		
		
		// Show/hide buttons
		assertAction("Applicant.hideButtons");
		execute("Applicant.hideButtons");
		assertNoAction("CRUD.new");
		assertNoAction("Applicant.hideButtons");
		execute("Reference.createNew", "model=ProgrammingSkill,keyProperty=skill.description");
		assertAction("NewCreation.saveNew");
		execute("NewCreation.cancel");
		execute("Applicant.showButtons", "xava.keyProperty=name");
		assertAction("CRUD.new");
		assertAction("Applicant.hideButtons");
	}
	
	public void testHtmlHeadNotDuplicated_excludedActionsInControllers_emails() throws Exception {  
		String html = getHtmlPage().getWebResponse().getContentAsString();
		assertEquals(1, StringUtils.countMatches(html, "<head>"));
		execute("CRUD.new"); 
		assertAction("Navigation.previous");
		assertAction("Navigation.next");
		assertAction("CRUD.refresh");
		assertNoAction("Navigation.first");
		assertNoAction("CRUD.save");		
		
		execute("Applicant.sendEmail");
		assertError("Impossible to execute Send email action: Couldn't connect to host, port: Host, 25; timeout -1"); // So, JavaMail is working, although it does not send the message because it's not configured
		assertAction("CRUD.refresh"); // In order to test that the UI is not broken
	}
	
	public void testListCustomizationWithTabDefaultOrder_tabAnnotationEditorAddedToTabsDefaultValuesXML() throws Exception {   
		assertAction("ListFormat.select", "editor=List"); 
		assertAction("ListFormat.select", "editor=Cards"); 
		assertNoAction("ListFormat.select", "editor=Charts"); 
		assertListCustomizationWithTabDefaultOrder(); 
		resetModule();
		assertListCustomizationWithTabDefaultOrder(); // Failed the second time after reseting module
	}
	
	
	public void testHideShowModulesMenu() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true);
		reload();
		
		HtmlElement modulesList = getHtmlPage().getHtmlElementById("modules_list");
		HtmlElement menuButton = getHtmlPage().getHtmlElementById("module_header_menu_button");
		HtmlElement extendedTitle = getHtmlPage().getHtmlElementById("module_extended_title");
		HtmlElement hideButton = getHtmlPage().getHtmlElementById("modules_list_hide");
		HtmlElement showButton = getHtmlPage().getHtmlElementById("modules_list_show");

		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		Thread.sleep(500);
		
		assertFalse(modulesList.isDisplayed());
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		showButton.click();
		Thread.sleep(500);		
		
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		Thread.sleep(500);
		
		assertFalse(modulesList.isDisplayed());
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		menuButton.click();
		Thread.sleep(500);		
		
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());		
	}

	private void assertListCustomizationWithTabDefaultOrder() throws Exception { 
		assertListColumnCount(1);
		assertListAllConfigurations("All"); 
		execute("List.addColumns");
		checkRow("selectedProperties", "skill.description"); 
		execute("AddColumns.addColumns");
		assertListColumnCount(2);
		assertListAllConfigurations("All"); 
		
		resetModule();
		assertListColumnCount(2);
		assertListAllConfigurations("All");  
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(1);
		assertListAllConfigurations("All"); 
	}
	
	protected String getModuleURL() {
		String root = "http://" + getHost() + ":" + getPort() + getContextPath();
		if (moduleURLOnRoot) return root;
		return modulesLimit?super.getModuleURL():root + "modules/Applicant";
	}
	
	private void assertModulesCount(int expectedCount) {
		assertEquals(expectedCount, getModulesCount());
	}
	
	private int getModulesCount() {
		return getHtmlPage().getHtmlElementById("modules_list").getElementsByAttribute("div", "class", "module-name").size();  
	}
			
}
