package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

public class DeliveryEditableValidValuesTest extends ModuleTestBase {

	public DeliveryEditableValidValuesTest(String testName) {
		super(testName, "DeliveryEditableValidValues");		
	}
	
	public void testValidValues() throws Exception {  
		execute("CRUD.new");
		assertValue("shortcut", "");
		HtmlSpan span = (HtmlSpan) getHtmlPage().getElementById("ox_openxavatest_DeliveryEditableValidValues__editor_shortcut");
		String onchange = span.getLastElementChild().getAttribute("onchange");
		assertEquals("openxava.throwPropertyChanged('openxavatest','DeliveryEditableValidValues','ox_openxavatest_DeliveryEditableValidValues__shortcut')", onchange);
		execute("DeliveryEditableValidValues.addShortcutOptions");
		execute("Sections.change", "activeSection=1");
		execute("Sections.change", "activeSection=0");
		HtmlTextInput input = (HtmlTextInput) getHtmlPage().getElementById("ox_openxavatest_DeliveryEditableValidValues__shortcut");
		onchange = input.getAttribute("onchange");
		assertEquals("this.previousElementSibling.selectedIndex=0; openxava.throwPropertyChanged('openxavatest','DeliveryEditableValidValues','ox_openxavatest_DeliveryEditableValidValues__shortcut')", onchange);
	}
	
}
