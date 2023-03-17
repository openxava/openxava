package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

public class InputMaskTest extends ModuleTestBase {

	public InputMaskTest(String testName) {
		super(testName, "InputMask");		
	}
	
	public void testMask() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("staticMask", "01.Jun1 !");
		assertValue("numerical", "12345678");
		assertValue("alphabetical", "Ju NIT");
		assertValue("alphanumeric", "JuN 17");
		assertValue("specialChars", "1 +-");
		execute("CRUD.new");
		
		HtmlInput staticMask = (HtmlInput) getHtmlPage().getElementById("ox_openxavatest_InputMask__staticMask");
		staticMask.focus();
		staticMask.type("2 Te5t+-");
		staticMask.blur();
		assertValue("staticMask", "2 .Te5t+-");
		
		execute("InputMask.setOFF");
		assertNoEditable("staticMask");
	}
	
}
