package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class CustomerRadioButtonTest extends ModuleTestBase {
	
	public CustomerRadioButtonTest(String testName) {
		super(testName, "CustomerRadioButton");		
	}
	
	public void testSelectTheLabelOnRadioButton_descriptionsListTooltip() throws Exception {
		execute("CRUD.new");
		
		HtmlElement select = getHtmlPage().getElementByName("ox_openxavatest_CustomerRadioButton__address___state___id__CONTROL__"); 
		String title = select.getAttribute("title");
		assertEquals("Address state", title);
		
		HtmlElement c = getHtmlPage().getHtmlElementById("ox_openxavatest_CustomerRadioButton__type1");	// steady  
		c.click();
		waitAJAX();
		assertValue("type", "1");	// steady
		
		c = getHtmlPage().getHtmlElementById("ox_openxavatest_CustomerRadioButton__type2");	// special  
		c.click();
		waitAJAX();
		assertValue("type", "2");	// special
	}
	
	public void testEditorByView_radioButton_radioButtonNoEditable() throws Exception { 
		// Really editor by property-view must be tested visually
		// and about radioButton we only test that it's possible to use in junit test,
		// because behaves equals that a combo. Hence it's needed to test visually
		execute("CRUD.new");
		setValue("number", "66");		
		setValue("type", "1"); // steady		
		setValue("name", "CUSTOMER JUNIT");
		setValue("address.street", "DOCTOR PESSET");
		setValue("address.zipCode", "46540");
		setValue("address.city", "EL PUIG");
		setValue("address.state.id", "CA");
		execute("CRUD.save");
		
		assertNoErrors(); 
		assertValue("number", "");
		assertValue("name", "");
		assertValue("type", "2"); // special, because a default calculator
		
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("name", "Customer Junit");
		assertValue("type", "1"); // steady
		
		execute("CustomerRadioButton.notEditableView");
		assertNoEditable("type");
		
		execute("CRUD.delete");
		assertMessage("Customer deleted successfully"); 
	}
		
}
