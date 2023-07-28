package org.openxava.test.tests;

import org.openxava.tests.*;

import org.htmlunit.html.*;
import org.htmlunit.javascript.host.event.*;

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
		assertLabelInList(2, "State full name with formula"); 
		assertValueInList(0, 2, "AZARIZONA");
		assertValueInList(1, 1, "TUCSON CITY"); 
		execute("List.viewDetail", "row=0");
		assertValue("name", "Phoenix");
		
		reload();
		assertValue("name", "Phoenix");
		
		getHtmlPage().executeJavaScript("window.open(window.location, '_blank')");
		HtmlPage newTabPage = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();
		waitAJAX(); 
		String tabText = newTabPage.asNormalizedText();
		assertTrue(tabText.contains("New Delete Generate PDF Generate Excel")); // In list mode 
		assertFalse(tabText.contains("New Save Delete Search Refresh")); // Not in detail mode
		
		execute("Navigation.next");
		assertValue("name", "Tucson");
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
	
	public void testCoordinatesValidator() throws Exception { 
		execute("CRUD.new");
		setValue("state.id", "CA");
		setValue("code", "6");
		setValue("name", "VALENCIA");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("state.id", "");
		assertValue("code", "");
		assertValue("name", "");

		setValue("state.id", "CA");
		setValue("code", "6");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("state.id", "CA");
		assertValue("code", "6");
		assertValue("name", "Valencia"); // Because of Name formatter, though we're not testing it here
		
		setValue("location", "In the coast");
		execute("CRUD.save");
		assertError("Location in City must be valid coordinates");
		assertLocationMarkedAsError();
		
		setValue("location", "34.42057860590464, -118.56423692792511");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("state.id", "");
		assertValue("code", "");
		assertValue("name", "");

		setValue("state.id", "CA");
		setValue("code", "6");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("state.id", "CA");
		assertValue("code", "6");
		assertValue("name", "Valencia");
		execute("CRUD.delete");
		assertNoErrors();
		
		execute("CRUD.new");
		assertLocationNotMarkedAsError(); 
		setValue("location", "In the coast");
		fireLocationChanged();
		assertLocationMarkedAsError();
		
		setValue("location", "34.42057860590464, -118.56423692792511");
		fireLocationChanged();
		assertLocationNotMarkedAsError();
		
		setValue("location", "In the coast");
		fireLocationChanged();
		assertLocationMarkedAsError();
		
		execute("Navigation.first");
		assertLocationNotMarkedAsError();
	}

	private void fireLocationChanged() { 
		HtmlElement input = getHtmlPage().getHtmlElementById("ox_openxavatest_City__location");
		input.fireEvent(Event.TYPE_CHANGE);
	}

	private void assertLocationMarkedAsError() throws Exception { 
		HtmlElement editor = getHtmlPage().getHtmlElementById("ox_openxavatest_City__editor_location");
		assertTrue("Location not marked as error", editor.getAttribute("class").contains("ox-error-editor"));	
	}
	
	private void assertLocationNotMarkedAsError() throws Exception { 
		HtmlElement editor = getHtmlPage().getHtmlElementById("ox_openxavatest_City__editor_location");
		assertFalse("Location marked as error", editor.getAttribute("class").contains("ox-error-editor"));	
	}
		
}
