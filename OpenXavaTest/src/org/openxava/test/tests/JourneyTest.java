package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;


/**
 * @author Javier Paniza
 */

public class JourneyTest extends ModuleTestBase {
	
	public JourneyTest(String testName) {
		super(testName, "Journey");		
	}	
	
	public void testCardsPaging() throws Exception { 
		execute("ListFormat.select", "editor=Cards");
		assertCardsPaging241();
		deleteJorney(241);
		resetModule();
		assertCardsPaging240(); 
		createJorney(241);
		execute("ListFormat.select", "editor=List");
	}
	
	public void testPage25InList() throws Exception {
		assertNoAction("List.goPage", "page=25");
		execute("List.goPage", "page=13");
		execute("List.goPage", "page=25");
		assertListRowCount(1); 
		execute("CRUD.new");
		execute("Mode.list");
		assertListRowCount(1);
	}
	
	public void testNavigateInListWithALotOfObjects() throws Exception {   
		assertListRowCount(10);
		execute("List.goPage", "page=13");
		assertListRowCount(10);
		execute("List.goPage", "page=24");
		assertListRowCount(10);
		execute("List.goNextPage");
		assertListRowCount(1); // It assumes 241 objects  
	}


	
	private void createJorney(int number) {
		Journey journey = new Journey();
		journey.setName("JORNEY " + number);
		journey.setDescription("THIS IS THE JOURNEY NUMBER " + number);
		XPersistence.getManager().persist(journey);
		XPersistence.commit();
	}

	private void deleteJorney(int number) throws Exception {
		Query query = XPersistence.getManager().createQuery("from Journey as o where o.name = :name"); 
		query.setParameter("name", "JORNEY " + number); 			
		Journey jorney = (Journey) query.getSingleResult();		  		
		XPersistence.getManager().remove(jorney);
		XPersistence.commit();
	}

	private void assertCardsPaging241() throws Exception { 
		assertTrue(getHtmlPage().getElementById("xava_loading_more_elements") != null);
		assertListRowCount(120);
		assertFalse(getHtml().contains("JORNEY 125"));
		
		scrollDown();		
		assertTrue(getHtmlPage().getElementById("xava_loading_more_elements") != null);
		assertListRowCount(240);
		assertTrue(getHtml().contains("JORNEY 125"));
		assertFalse(getHtml().contains("JORNEY 241"));
			
		scrollDown();		
		assertTrue(getHtmlPage().getElementById("xava_loading_more_elements") == null); 
		assertListRowCount(241);
		assertTrue(getHtml().contains("JORNEY 125"));
		assertTrue(getHtml().contains("JORNEY 241"));
	}
	
	public void assertCardsPaging240() throws Exception { 
		assertTrue(getHtmlPage().getElementById("xava_loading_more_elements") != null);
		assertListRowCount(120);
		assertFalse(getHtml().contains("JORNEY 125"));
		
		scrollDown();		
		assertTrue(getHtmlPage().getElementById("xava_loading_more_elements") == null);
		assertListRowCount(240);
		assertTrue(getHtml().contains("JORNEY 125"));
		assertTrue(getHtml().contains("JORNEY 240"));
	}
	
	private void scrollDown() throws Exception {
		// HtmlUnit (at least for 2.15) does not support scroll events, so we call directly to corresponding JS code
		HtmlElement loadMore = getHtmlPage().getHtmlElementById("xava_loading_more_elements");
		HtmlElement script = (HtmlElement) loadMore.getNextElementSibling();
		String loadScript = "openxava.executeAction('OpenXavaTest', 'Journey', false, false, 'Cards.loadMoreCards')";
		assertTrue(script.asXml().contains(loadScript));
		getHtmlPage().executeJavaScript(loadScript);
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
	}
	
	public void testSearchKeyWithReferences() throws Exception {
		execute("CRUD.new");
		
		assertEditable("averageSpeed.driver.number");
		assertEditable("averageSpeed.vehicle.code");
		assertNoEditable("averageSpeed.speed");
		
		setValue("averageSpeed.driver.number", "1");
		assertValue("averageSpeed.driver.name", "ALONSO");
		
		assertFocusOn("averageSpeed.vehicle.code");		
		setValue("averageSpeed.vehicle.code", "VLV40");
		assertValue("averageSpeed.vehicle.model", "S40 T5");
		
		assertValue("averageSpeed.speed", "240");
		assertFocusOn("description");
	}
	
}
