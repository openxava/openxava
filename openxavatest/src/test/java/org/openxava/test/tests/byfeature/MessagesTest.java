package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * @author Chungyen Tsai
 */
public class MessagesTest extends WebDriverTestBase {
	
	public MessagesTest(String testName) {
		super(testName);
	}

	public void testMessagesHideAfterOpenDialog() throws Exception {
		goModule("Blog");
		execute("CRUD.new");
		setValue("title", "OpenXava: The best Java framework");
		setValue("body", "I think OpenXava is the best Java framework");
		execute("Blog.produceMessages");
		execute("Collection.new", "viewObject=xava_view_comments");
		Thread.sleep(1000); //wait html
	    String[] messages = {"messages", "errors", "warnings", "infos"};
	    for (String messageType : messages) {
	        assertTrue(isElementHidden(messageType));
	    }
	}
	
	public void testMessagesAutoClose() throws Exception {
		goModule("Blog");
		execute("CRUD.new");
		setValue("title", "OpenXava: The best Java framework");
		setValue("body", "I think OpenXava is the best Java framework");
		assertMessagesAutoClose();
		assertMessagesAutoClosePausedOnHover();
	}

	private void assertMessagesAutoClose() throws Exception {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("openxava.messagesAutoCloseDelay = 1500;");
		execute("Blog.produceMessages");
		String messagesId = "ox_openxavatest_Blog__messages";
		String errorsId = "ox_openxavatest_Blog__errors";
		Thread.sleep(600); // wait for fadeIn
		assertTrue(isMessagesContainerVisible(messagesId));
		assertTrue(isMessagesContainerVisible(errorsId));
		Thread.sleep(2000); // wait for auto-close + fadeOut
		assertFalse(isMessagesContainerVisible(messagesId));
		assertTrue(isMessagesContainerVisible(errorsId));
	}
	
	private void assertMessagesAutoClosePausedOnHover() throws Exception {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("openxava.messagesAutoCloseDelay = 1500;");
		execute("Blog.produceMessages");
		String messagesId = "ox_openxavatest_Blog__messages";
		Thread.sleep(600); // wait for fadeIn
		assertTrue(isMessagesContainerVisible(messagesId));
		js.executeScript("$('#" + messagesId + "').trigger('mouseenter');");
		Thread.sleep(2000); // longer than auto-close delay, should still be visible due to hover
		assertTrue(isMessagesContainerVisible(messagesId));
		js.executeScript("$('#" + messagesId + "').trigger('mouseleave');");
		Thread.sleep(2000); // wait for auto-close + fadeOut
		assertFalse(isMessagesContainerVisible(messagesId));
	}
	
	private boolean isMessagesContainerVisible(String id) {
		String style = getDriver().findElement(By.id(id)).getAttribute("style");
		return style == null || !style.contains("display: none");
	}

	private boolean isElementHidden(String messageType) {
	    String tableId = "ox_openxavatest_Blog__" + messageType + "_table__DISABLED__";
	    String style = getDriver().findElement(By.id(tableId)).getAttribute("style");
	    return style != null && style.contains("display: none;");
	}
}
