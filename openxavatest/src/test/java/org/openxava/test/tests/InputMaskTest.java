package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

public class InputMaskTest extends ModuleTestBase {

	public InputMaskTest(String testName) {
		super(testName, "InputMask");		
	}

	
	public void testMaskedValue_inputWithMask_setEditable() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("staticMask", "12.345.678");
		assertValue("numerical", "12345678");
		assertValue("alphabetical", "alpALP");
		assertValue("alphanumeric", "Al 123");
		execute("CRUD.new");
		
		HtmlInput staticMask = (HtmlInput) getHtmlPage().getElementById("ox_openxavatest_InputMask__staticMask");
		staticMask.focus();
		staticMask.type("12345678");
		staticMask.blur();
		assertValue("staticMask", "12.345.678");
		HtmlInput alphanumeric = (HtmlInput) getHtmlPage().getElementById("ox_openxavatest_InputMask__alphanumeric");
		alphanumeric.focus();
		alphanumeric.type("Al 123");
		alphanumeric.blur();
		assertValue("alphanumeric", "Al 123");
		
		execute("InputMask.setOFF");
		assertNoEditable("staticMask");
	}
	
}
