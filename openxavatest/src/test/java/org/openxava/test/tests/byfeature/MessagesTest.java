package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/*
 * @author Chungyen Tsai
 */
public class MessagesTest extends WebDriverTestBase {
	
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

	private boolean isElementHidden(String messageType) {
	    String tableId = "ox_openxavatest_Blog__" + messageType + "_table__DISABLED__";
	    String style = getDriver().findElement(By.id(tableId)).getAttribute("style");
	    return style != null && style.contains("display: none;");
	}
}
