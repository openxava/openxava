package org.openxava.test.tests;

import java.text.*;
import java.util.*;
import org.openxava.test.model.*;
import com.gargoylesoftware.htmlunit.html.*;
import static org.openxava.util.Strings.multiline;

/**
 * 
 * @author Javier Paniza
 */

public class IncidentTest extends EmailNotificationsTestBase { 
	
	public IncidentTest(String testName) {
		super(testName, "Incident");		
	}
	
	public void testDiscussionEditor_defaultPropertiesForListWithoutTab_discussionEmailNotifications() throws Exception {  
		getWebClient().getOptions().setCssEnabled(true);
		
		subscribeToEmailNotifications(); 

		execute("Mode.list"); 
		assertDefaultPropertiesForListWithoutTab(); 
		
		assertListRowCount(0); 
		
		execute("CRUD.new");
		setValue("title", "THE JUNIT DISCUSSION");
		setValue("description", "This is the big jUnit discussion");
		
		assertDiscussionCommentsCount("discussion", 0);
		postDiscussionComment("discussion", "Hi, it's me");
		String timeFirstPost = getCurrentTime();
		assertDiscussionCommentsCount("discussion", 1); 
		assertDiscussionCommentText("discussion", 0, multiline("admin - Now", "Hi, it's me"));  
		
		execute("CRUD.save");
		String id = Incident.findFirst().getId(); 
		
		assertValue("title", "");
		assertValue("description", "");
		HtmlElement discussion = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Incident__editor_discussion"); 
		HtmlElement textarea = discussion.getElementsByTagName("textarea").get(0); 
		assertEquals("", textarea.getTextContent());
		
		assertDiscussionCommentsCount("discussion", 0);
			
		changeModule("SignIn");
		login("juan", "juan4");
		changeModule("Incident");
		
		execute("List.viewDetail", "row=0");

		assertValue("title", "THE JUNIT DISCUSSION");
		assertValue("description", "This is the big jUnit discussion");

		assertDiscussionCommentsCount("discussion", 1);
		assertDiscussionCommentText("discussion", 0, multiline("admin - " + timeFirstPost, "Hi, it's me"));
		postDiscussionComment("discussion", "Soy Juan"); 
		String timeSecondPost = getCurrentTime();
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");

		assertValue("title", "THE JUNIT DISCUSSION");
		assertValue("description", "This is the big jUnit discussion");
		assertDiscussionCommentsCount("discussion", 2);
		assertDiscussionCommentText("discussion", 0, multiline("admin - " + timeFirstPost, "Hi, it's me"));
		assertDiscussionCommentText("discussion", 1, multiline("juan - " + timeSecondPost, "Soy Juan"));

		assertEquals(1, discussion.getElementsByTagName("textarea").size());
		assertEquals(2, discussion.getElementsByAttribute("input", "type", "button").size());	
		execute("Incident.disableDiscussion");
		discussion = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Incident__editor_discussion");
		assertEquals(0, discussion.getElementsByTagName("textarea").size());
		assertEquals(0, discussion.getElementsByAttribute("input", "type", "button").size());
		
		execute("CRUD.delete");
		assertNoErrors();

		assertEmailNotifications(
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Incident, permalink=http://localhost:8080/OpenXavaTest/modules/Incident, changes=<ul><li><b>Discussion</b>: NEW COMMENT --> Hi, it's me</li></ul>",
			"CREATED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Incident, permalink=http://localhost:8080/OpenXavaTest/modules/Incident?detail=" + id,
			"MODIFIED: email=openxavatest1@getnada.com, user=juan, application=OpenXavaTest, module=Incident, permalink=http://localhost:8080/OpenXavaTest/modules/Incident?detail=" + id + ", changes=<ul><li><b>Discussion</b>: NEW COMMENT --> Soy Juan</li></ul>",
			"REMOVED: email=openxavatest1@getnada.com, user=juan, application=OpenXavaTest, module=Incident, url=http://localhost:8080/OpenXavaTest/modules/Incident, key={id=" + id + "}"
		);	
	}

	
	private void assertDefaultPropertiesForListWithoutTab() throws Exception {  
		assertListColumnCount(2);
		assertLabelInList(0, "Title");
		assertLabelInList(1, "Description");
		// Discussion is not shown
	}

	
	private String getCurrentTime() { 
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale(getLocale())).format(new java.util.Date());
	}
	
}
