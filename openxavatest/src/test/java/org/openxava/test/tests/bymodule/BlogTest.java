package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class BlogTest extends ModuleTestBase {
	
	public BlogTest(String testName) {
		super(testName, "Blog");		
	}
	
	public void testMessageTypes() throws Exception {
		execute("Blog.produceMessages");
		assertMessagesCount(1);
		assertMessage("This is a message");
		assertErrorsCount(1);
		assertError("This is an error");
		assertInfosCount(1);
		assertInfo("This is an info");
		assertWarningsCount(1);
		assertWarning("This is a warning");
		assertMessagesAutoClose();
		assertMessagesAutoClosePausedOnHover();
	}
	
	private void assertMessagesAutoClose() throws Exception {
		getHtmlPage().executeJavaScript("openxava.messagesAutoCloseDelay = 300;");
		execute("Blog.produceMessages");
		assertMessage("This is a message");
		assertError("This is an error");
		assertTrue(isMessagesVisible());
		assertTrue(isErrorsVisible());
		getWebClient().waitForBackgroundJavaScriptStartingBefore(5000);
		Thread.sleep(1000);
		getWebClient().waitForBackgroundJavaScriptStartingBefore(5000);
		assertFalse(isMessagesVisible());
		assertTrue(isErrorsVisible());
	}
	
	private void assertMessagesAutoClosePausedOnHover() throws Exception {
		execute("Blog.produceMessages");
		assertMessage("This is a message");
		String messagesId = decorateId("messages");
		getHtmlPage().executeJavaScript("$('#" + messagesId + "').trigger('mouseenter');");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(2000);
		Thread.sleep(1000);
		assertTrue(isMessagesVisible());
		getHtmlPage().executeJavaScript("$('#" + messagesId + "').trigger('mouseleave');");
		getWebClient().waitForBackgroundJavaScriptStartingBefore(5000);
		Thread.sleep(1000);
		getWebClient().waitForBackgroundJavaScriptStartingBefore(5000);
		assertFalse(isMessagesVisible());
	}
	
	private boolean isMessagesVisible() {
		String style = getHtmlPage().getHtmlElementById(decorateId("messages")).getAttribute("style");
		return style == null || !style.contains("display: none");
	}
	
	private boolean isErrorsVisible() {
		String style = getHtmlPage().getHtmlElementById(decorateId("errors")).getAttribute("style");
		return style == null || !style.contains("display: none");
	}
	
	public void testSetControllersAndRemoveActionsInTheSameAction() throws Exception {
		execute("Blog.login");
		assertDialog();
		assertAction("Login.login"); 
		assertAction("Dialog.cancel");
		assertNoAction("BlogLogin.notWanted");
	}
	
	public void testEditorForReferenceInEditorsXML() throws Exception {
		execute("CRUD.new");
		assertTrue(getHtml().indexOf("There are no comments") >= 0);
		assertTrue(getHtml().indexOf("These are the comments:") < 0);
		
		setValue("title", "OpenXava: The best Java framework");
		setValue("body", "I think OpenXava is the best Java framework");
		
		assertNoAction("Print.generatePdf"); // To test that default actions for collections
										// are not included when a collectionEditor.jsp uses
										// a listEditor. Because we are not sure that 
										// list editor uses a tab, and so it's usable from
										// default collection actions
		execute("Collection.new", "viewObject=xava_view_comments");		
		
		setValue("body", "I agree"); 
		execute("Collection.save");		
		assertNoErrors();		

		assertTrue(getHtml().indexOf("There are no comments") < 0);
		assertTrue(getHtml().indexOf("These are the comments:") >= 0);		
		assertTrue(getHtml().indexOf("I agree") >= 0);
		
		execute("CRUD.delete"); // In order to not populate the db too much
		assertNoErrors();
	}
	
}
