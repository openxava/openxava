package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class CustomerTwoSellersInListTest extends ModuleTestBase {

	
	public CustomerTwoSellersInListTest(String testName) {
		super(testName, "CustomerTwoSellersInList");				
	}
	
	public void test2ReferenceToSameModelInList_goingToURLofWEBURL() throws Exception { 
		assertListRowCount(5); 
		assertValueInList(0, "name", "Javi");
		assertValueInList(0, "seller.name", "MANUEL CHAVARRI");
		assertValueInList(0, "seller.level.description", "MANAGER");
		assertValueInList(0, "alternateSeller.name", "JUANVI LLAVADOR");		
		
		execute("CRUD.new");
		setValue("website", "http://www.openxava.org");
		HtmlElement websiteLink = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_CustomerTwoSellersInList__editor_website")
								.getElementsByTagName("a").get(0);
		websiteLink.click();
		assertEquals("http://www.openxava.org", websiteLink.getAttribute("href"));
		setValue("website", "https://www.openxava.org");
		websiteLink.click();
		assertEquals("https://www.openxava.org", websiteLink.getAttribute("href"));
	}	
				
}
