package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * Create on 27/02/2012 (10:56:25)
 * @author Ana Andres
 * @author Javier Paniza
 */
public class CityTest extends ModuleTestBase{

	public CityTest(String testName) {
		super(testName, "City");
	}
	
	public void testStateFullNameWithFormulaFromAReference_listFormatter_isolateModuleSessionForEachBrowserTab() throws Exception {
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "PHOENIX CITY"); 
		assertLabelInList(2, "Full name with formula of State");
		assertValueInList(0, 2, "AZARIZONA");
		execute("List.viewDetail", "row=0");
		assertValue("name", "Phoenix");
		
		reload();
		assertValue("name", "Phoenix");
		
		getHtmlPage().executeJavaScript("window.open(window.location, '_blank')");
		HtmlPage newTabPage = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();
		String tabText = newTabPage.asText();
		assertTrue(tabText.contains("New Delete Generate PDF Generate Excel")); // In list mode 
		assertFalse(tabText.contains("New Save Delete Search Refresh")); // Not in detail mode		
	}
	
	public void testChangeDescriptionsListCondition() throws Exception{
		execute("CRUD.new");
		assertValidValuesCount("state.id", 50);	// 49 + void
		setValue("stateCondition", "lor");
		assertValidValuesCount("state.id", 3);	// 2 + void
		setValue("stateCondition", "ar");
		assertValidValuesCount("state.id", 7);	// 6 + void
		setValue("stateCondition", "");
		assertValidValuesCount("state.id", 50);	// 49 + void
	}
		
}
