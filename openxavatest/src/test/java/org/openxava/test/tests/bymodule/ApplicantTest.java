package org.openxava.test.tests.bymodule;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang.*;
import org.htmlunit.*;
import org.htmlunit.html.*;
import org.openxava.controller.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

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
		String inputName = "ox_openxavatest_Applicant__action___" + action.replace(".", "___"); 
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
		assertEquals("OpenXavaTest :\nInvoices\nOrders\nApplicant", moduleHeader.asNormalizedText().trim());
				
		HtmlElement articleLink = page.getAnchorByHref(getContextPath() + "m/Article?init=true");
		page = articleLink.click();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest :\nInvoices\nOrders\nArticles\nApplicant", moduleHeader.asNormalizedText().trim());
		List<HtmlElement> selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Articles", selectedElements.get(0).asNormalizedText());
		
		HtmlElement artistLink = page.getAnchorByHref(getContextPath() + "m/Artist?init=true"); 
		page = artistLink.click();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest :\nInvoices\nOrders\nArtist\nArticles\nApplicant", moduleHeader.asNormalizedText().trim());
		selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Artist", selectedElements.get(0).asNormalizedText());		
		
		articleLink = moduleHeader.getOneHtmlElementByAttribute("a", "href", getContextPath() + "m/Article?retainOrder=true"); 
		page = articleLink.click();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest :\nInvoices\nOrders\nArtist\nArticles\nApplicant", moduleHeader.asNormalizedText().trim());		
		selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Articles", selectedElements.get(0).asNormalizedText());		
		
		moduleURLOnRoot = true;
		resetModule();
		page = getHtmlPage();
		moduleHeader = page.getHtmlElementById("module_header_left");
		assertEquals("OpenXavaTest :\nInvoices\nOrders\nArticles\nArtist\nApplicant", moduleHeader.asNormalizedText().trim());		
		selectedElements = moduleHeader.getElementsByAttribute("span", "class", "selected");
		assertEquals(1, selectedElements.size());
		assertEquals("Articles", selectedElements.get(0).asNormalizedText());	
	}
	
	public void testModulesMenu_help() throws Exception { 
		modulesLimit = false;
		resetModule();
		
		assertModulesCount(30); 
		assertTrue(AbstractWall.class.isAnnotationPresent(MappedSuperclass.class)); // This should be the first one, but as it's MappedSupperclass in not shown
		assertFirstModuleInMenu("Academic year"); // Not in i18n, to test a case 

		HtmlInput searchBox = getHtmlPage().getHtmlElementById("search_modules_text");
		searchBox.type("INVOICE");
		assertEquals("INVOICE", searchBox.getValue()); 
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(30);
		assertFirstModuleInMenu("Accounting invoices");  
		
		HtmlAnchor loadMoreModules = (HtmlAnchor) getHtmlPage().getHtmlElementById("more_modules").getParentNode();
		
		loadMoreModules.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(63); // We have to adjust this when we add new modules that content "invoice" 
		
		
		searchBox.type(" \b");
		assertEquals("", searchBox.getValue()); 
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(30); 
		
		loadMoreModules = (HtmlAnchor) getHtmlPage().getHtmlElementById("more_modules").getParentNode();
		loadMoreModules.click();
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertTrue(getModulesCount() > 300);
		
		searchBox.type("artist");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		assertModulesCount(3); // Test 'Artist' not duplicate when module in application.xml 
		assertModuleInMenu(0, "Artist");  
		assertModuleInMenu(1, "Artist some members read only");
		assertModuleInMenu(2, "Artist snake"); 
		
		assertHelp("en"); 
	}
	
	public void testChangeLocaleAffectsMenu_assertCSSInLatestVersionAndIcons() throws Exception {  
		modulesLimit = false;
		resetModule();
		assertLabels("Name", "Author"); 
		
		execute("Applicant.changeToSpanish");
		reload();
		
		assertLabels("Nombre", "Autor");
		assertHelp("es"); 
		
		HtmlPage page = getHtmlPage();
		assertCSSWellUploaded(page, false);
		assertCSSWellUploaded(page, true);
		HtmlElement cssHref = page.getAnchorByHref("?theme=pink.css");
		page = cssHref.click();
		assertCSSWellUploaded(page, false);
		assertResorcesWellReaded(page); 
	}
	
	private void assertLabels(String propertyLabel, String moduleLabel) throws Exception {
		assertLabelInList(0, propertyLabel);
		assertModuleInMenu(22, moduleLabel); // Adapt the index if you add more modules on top of Author
		
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
		assertEquals(expectedName, moduleName.asNormalizedText());
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
	
	private void assertCSSWellUploaded(HtmlPage page, boolean custom) throws IOException {
		HtmlElement head = (HtmlElement) page.getHead();
		DomElement linkCSS = head.getChildElements().iterator().next()
								 .getNextElementSibling()
								 .getNextElementSibling()
								 .getNextElementSibling()
								 .getNextElementSibling()
								 .getNextElementSibling();

		String urlCSS = page.getUrl().getProtocol() + "://" 
						+ page.getUrl().getHost() + ":"
						+ page.getUrl().getPort() 
						+ linkCSS.getAttribute("href");
		
		URL url;
		BufferedReader in;
		if (custom) {
			urlCSS = urlCSS.replace("terra", "custom");
			url = new URL(urlCSS);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			assertEquals(".corporation-employee-list-select {", in.readLine()); 
			in.close();
		} else {
			url = new URL(urlCSS);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			assertEquals("@import 'base.css?ox=" + ModuleManager.getVersion() + "';", in.readLine()); 
			in.close();
		}
	}
	
	private void assertResorcesWellReaded(HtmlPage page) throws IOException {
		String iconUrl = page.getUrl().getProtocol() + "://" 
			+ page.getUrl().getHost() + ":"
			+ page.getUrl().getPort() + "/openxavatest/xava/style/smoothness/images/ui-bg_glass_55_fbf9ee_1x400.png"; 
		double imageSizeInKB = 0;
        URL url = new URL(iconUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            double imageSizeInBytes = connection.getContentLength();
            imageSizeInKB = imageSizeInBytes / 1024;
        }
        connection.disconnect();
        assertTrue(imageSizeInKB > 0.0);
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
