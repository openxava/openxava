package org.openxava.test.tests.bymodule;

import java.io.*;

import org.htmlunit.html.*;
import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class SellerJSPTest extends ModuleTestBase {
	
	public SellerJSPTest(String testName) {
		super(testName, "SellerJSP");		
	}
	
	public void testHandmadeWebView() throws Exception {
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("name", "");
		assertValue("level.id", "");
		assertValue("level.description", "");
		assertEditable("number");
		assertEditable("name");		
		assertEditable("level.id");
		assertNoEditable("level.description");
		setValue("number", "66");
		setValue("name", "JUNIT");
		assertValue("level.description", "");
		setValue("level.id", "A");
		assertValue("level.description", "MANAGER");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("number", "");
		assertValue("name", "");
		assertValue("level.id", "");
		assertValue("level.description", "");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT");
		assertValue("level.id", "A");
		assertValue("level.description", "MANAGER");		
		assertNoEditable("number");
		assertEditable("name");
		assertEditable("level.id");
		assertNoEditable("level.description");
		
		assertButtonTaglibWithArgv(); 
				
		execute("CRUD.delete");
		assertMessage("Seller deleted successfully");
	}

	private void assertButtonTaglibWithArgv() throws IOException, Exception {
		HtmlElement myButtonDiv = getHtmlPage().getHtmlElementById("mybutton");
		HtmlElement myButton = myButtonDiv.getOneHtmlElementByAttribute("input", "type", "button");
		myButton.click();
		waitAJAX();
		assertMessage("Seller modified successfully");
		assertValue("name", "JUNIT"); // So argv in xava:button works
	}
	
	public void testDisplaySizeOfReferenceMemberInHandmadeView() throws Exception { 
		execute("CRUD.new");		
		assertTrue("Size for seller level must be 25", getHtml().indexOf("maxlength=\"50\" size=\"25\"") >= 0);
	}
			
}
