package org.openxava.test.tests;

import java.util.*;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerSellerAsDescriptionsListShowingReferenceViewTest extends ModuleTestBase {
	
	public CustomerSellerAsDescriptionsListShowingReferenceViewTest(String testName) {
		super(testName, "CustomerSellerAsDescriptionsListShowingReferenceView");				
	}
	
	public void testDescriptionsListWithShowReferenceView() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("name", "Javi");
		assertValue("seller.number", "1");
		assertValue("seller.name", "MANUEL CHAVARRI");
		assertNoEditable("seller.number");
		assertNoEditable("seller.name");
		assertNoEditable("seller.level");
		HtmlElement sellerEditor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_CustomerSellerAsDescriptionsListShowingReferenceView__reference_editor_seller");
		HtmlElement openSellerListIcon = sellerEditor.getOneHtmlElementByAttribute("i", "class", "mdi mdi-menu-down");
		openSellerListIcon.click();
		/* This way (using click()) does not work with HtmlUnit 2.32
		HtmlElement menuItem = (HtmlElement) getHtmlPage().getElementById("ui-id-3");
		assertEquals("ui-menu-item", menuItem.getAttribute("class"));
		assertEquals("JUANVI LLAVADOR", menuItem.asText());
		menuItem.click();
		*/
		getHtmlPage().executeJavaScript("$('input[name=ox_OpenXavaTest_CustomerSellerAsDescriptionsListShowingReferenceView__seller___number__CONTROL__]').data('ui-autocomplete')._trigger('select', 'autocompleteselect', {item:{value:2, label:'JUANVI LLAVADOR'}});"); 
		getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		Collection sellerNumberEditors = getHtmlPage().getElementsByName("ox_OpenXavaTest_CustomerSellerAsDescriptionsListShowingReferenceView__seller___number");
		HtmlTextInput sellerNumberTextInput = null; 
		for (Object editor: sellerNumberEditors) {
			if (editor instanceof HtmlTextInput) {
				sellerNumberTextInput = (HtmlTextInput) editor;
				break;
			}
		}
		assertEquals("2", sellerNumberTextInput.getValueAttribute()); 
		assertValue("seller.name", "JUANVI LLAVADOR");
		assertValue("seller.level.id", "A");
		assertDescriptionValue("seller.level.id", "MANAGER");
		assertNoEditable("seller.number");
		assertNoEditable("seller.name");
		assertNoEditable("seller.level");
		
		execute("Mode.list");
		execute("CRUD.new");
		assertAction("Customer.hideSeller");
	}
		
}
