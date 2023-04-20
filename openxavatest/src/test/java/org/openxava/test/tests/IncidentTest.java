package org.openxava.test.tests;

import java.util.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class IncidentTest extends EmailNotificationsTestBase { 
	
	public IncidentTest(String testName) {
		super(testName, "Incident");		
	}
	
	public void testDiscussionEditor_defaultPropertiesForListWithoutTab_discussionEmailNotifications() throws Exception {
		subscribeToEmailNotifications(); 

		execute("Mode.list"); 
		assertDefaultPropertiesForListWithoutTab(); 
		
		assertListRowCount(0); 
		
		execute("CRUD.new");
		setValue("title", "THE JUNIT DISCUSSION");
		setValue("description", "This is the big &ltjUnit&gt discussion"); 
		
		assertDiscussionCommentsCount("discussion", 0); 
		postDiscussionComment("discussion", "Hi, it's me");
		String timeFirstPost = getCurrentTime();
		assertDiscussionCommentsCount("discussion", 1); 
		assertDiscussionCommentText("discussion", 0, "admin - Now\nHi, it's me"); 
		assertDiscussionCommentContentText("discussion", 0, "Hi, it's me");
		
		execute("CRUD.save");
		String id = Incident.findFirst().getId(); 
		
		assertValue("title", "");
		assertValue("description", "");
		HtmlElement discussion = getHtmlPage().getHtmlElementById("ox_openxavatest_Incident__editor_discussion"); 
		HtmlElement textarea = discussion.getElementsByTagName("textarea").get(0); 
		assertEquals("", textarea.getTextContent());
		
		assertDiscussionCommentsCount("discussion", 0);
			
		changeModule("SignIn");
		login("juan", "juan4");
		changeModule("Incident");
		
		execute("List.viewDetail", "row=0");

		assertValue("title", "THE JUNIT DISCUSSION"); 
		assertValue("description", "<p>This is the big <<!-- -->jUnit> discussion</p>"); // The <!-- --> is added by OX to avoid a CKEditor bug that removes any %lt; followed by a character

		assertDiscussionCommentsCount("discussion", 1);
		assertDiscussionCommentText("discussion", 0, "admin - " + timeFirstPost + "\nHi, it's me"); 
		postDiscussionComment("discussion", "Soy Juan"); 
		String timeSecondPost = getCurrentTime();
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");

		assertValue("title", "THE JUNIT DISCUSSION");
		assertValue("description", "<p>This is the big <<!-- -->jUnit> discussion</p>"); // The <!-- --> is added by OX to avoid a CKEditor bug that removes any %lt; followed by a character 
		assertDiscussionCommentsCount("discussion", 2);
		assertDiscussionCommentText("discussion", 0, "admin - " + timeFirstPost + "\nHi, it's me");
		assertDiscussionCommentText("discussion", 1, "juan - " + timeSecondPost + "\nSoy Juan");

		assertEquals(1, discussion.getElementsByTagName("textarea").size());
		assertEquals(2, discussion.getElementsByAttribute("input", "type", "button").size());	
		execute("Incident.disableDiscussion");
		discussion = getHtmlPage().getHtmlElementById("ox_openxavatest_Incident__editor_discussion");
		assertEquals(0, discussion.getElementsByTagName("textarea").size());
		assertEquals(0, discussion.getElementsByAttribute("input", "type", "button").size());
		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("title", "THE JUNIT DISCUSSION");
		assertValue("description", "<p>This is the big <<!-- -->jUnit> discussion</p>"); // The <!-- --> is added by OX to avoid a CKEditor bug that removes any %lt; followed by a character
		
		execute("CRUD.delete");
		assertNoErrors();

		// data-property='DISCUSSION' instead of 'discussion' is something we don't care much for now
		assertEmailNotifications( 
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Incident, permalink=http://localhost:8080" + getContextPath() + "modules/Incident, changes=<ul><li data-property='DISCUSSION'><b>Discussion</b>: NEW COMMENT --> Hi, it's me</li></ul>",
			"CREATED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Incident, permalink=http://localhost:8080" + getContextPath() + "modules/Incident?detail=" + id,
			"MODIFIED: email=openxavatest1@getnada.com, user=juan, application=OpenXavaTest, module=Incident, permalink=http://localhost:8080" + getContextPath() + "modules/Incident?detail=" + id + ", changes=<ul><li data-property='discussion'><b>Discussion</b>: NEW COMMENT --> Soy Juan</li></ul>",
			"REMOVED: email=openxavatest1@getnada.com, user=juan, application=OpenXavaTest, module=Incident, url=http://localhost:8080" + getContextPath() + "modules/Incident, key={id=" + id + "}"				
		);			
	}
	
	protected void tearDown() throws Exception { 
		// Because the @PreRemove of Incident does not work, maybe a bug to solve in the future
		XPersistence.getManager().createQuery("delete from DiscussionComment").executeUpdate();
		super.tearDown();
	}
	
	private void assertDefaultPropertiesForListWithoutTab() throws Exception {  
		assertListColumnCount(2);
		assertLabelInList(0, "Title");
		assertLabelInList(1, "Description");
		// Discussion is not shown
	}

	
	private String getCurrentTime() { 
		return Dates.getDateTimeFormat(new Locale(getLocale())).format(new java.util.Date()); 
	}
	
}
