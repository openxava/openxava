package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

public class DeliveryEditableValidValuesTest extends ModuleTestBase {

	public DeliveryEditableValidValuesTest(String testName) {
		super(testName, "DeliveryEditableValidValues");		
	}
	
	public void testInputValidValues() throws Exception {  
		execute("CRUD.new");
		execute("DeliveryEditableValidValues.addShortcutOptions");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0");
		HtmlTextInput input = (HtmlTextInput) getHtmlPage().getElementById("ox_openxavatest_DeliveryEditableValidValues__shortcut");
		input.focus();
		input.type("DY");
		input.blur();

		HtmlSelect select = (HtmlSelect) ((HtmlElement)getHtmlPage().getFirstByXPath("//div[@class='ox-select-editable']")).getFirstElementChild();
		assertEquals(select.getOption(0).getValueAttribute(), "DY");
		waitAJAX();
		assertValue("remarks", "Delayed");
	}
	
}
