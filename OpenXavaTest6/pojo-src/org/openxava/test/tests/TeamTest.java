package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class TeamTest extends ModuleTestBase {
	
	public TeamTest(String testName) {
		super(testName, "Team");		
	}
	
	public void testSubcontrollerInCollection() throws Exception {
		assertListRowCount(1);
		assertValueInList(0, 0, "ALFA");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("members", 2);
		
		assertAction("TeamMemberSub.viewNames");
		execute("TeamMemberSub.viewRoles", "viewObject=xava_view_members");
		assertInfo("CAP, DESARROLLO");
		assertNoErrors();

		HtmlElement container = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Team__sc-container-TeamMemberSub_detail"); 
		HtmlElement menu = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Team__sc-TeamMemberSub_detail"); 
		assertTrue("display:none;".equals(menu.getAttribute("style")));
		assertTrue(container.asText().contains("View names"));
		assertTrue(container.asText().contains("View roles"));
	}
	
	public void testSearchingWithInheritanceFromAEntityCollectionElement() throws Exception {  		
		execute("CRUD.new");
		setValue("name", "SOMETHING"); 
		execute("Collection.new", "viewObject=xava_view_members");
		execute("Team.searchPerson", "keyProperty=person.name"); 
		assertListRowCount(3); 
		execute("ReferenceSearch.choose", "row=0"); 
		assertValue("person.name", "JAVI"); // Must be JAVI because he's a JavaProgrammer, we need that to reproduce a bug
		execute("Team.searchPerson", "keyProperty=person.name");
		assertListRowCount(3);
	}
	
	public void testForwardInNewWindow() throws Exception {
		execute("Team.goMyPublicJSPInNewWindow");
		assertPopupCount(1);
		assertTrue(getPopupText().contains("The uri of this JSP"));
	}
	
	public void testForwardInNewWindowCalculated() throws Exception {
		execute("Team.goMyPublicJSPInNewWindowCalculated");
		assertPopupCount(1);
		assertTrue(getPopupText().contains("The uri of this JSP"));
	}
	
	public void testForwardInSameWindow() throws Exception {
		execute("Team.goMyPublicJSPInSameWindow");
		assertPopupCount(0);
		assertTrue(getHtmlPage().asText().contains("The uri of this JSP"));
	}
	
	public void testForwardInSameWindowCalculated() throws Exception {
		execute("Team.goMyPublicJSPInSameWindowCalculated");
		assertPopupCount(0);
		assertTrue(getHtmlPage().asText().contains("The uri of this JSP"));
	}
	
	public void testForwardToNullInNewWindow() throws Exception {
		execute("Team.goNoWhereInNewWindow");
		assertPopupCount(0);
		assertAction("Team.goNoWhereInNewWindow");
	}
			
}
