package org.openxava.test.tests;

import com.gargoylesoftware.htmlunit.html.*;


/**
 * 
 * @author Javier Paniza
 */

public class InvoiceCalculatedDetailsInSectionTest extends CustomizeListTestBase { 
	
	public InvoiceCalculatedDetailsInSectionTest(String testName) {
		super(testName, "InvoiceCalculatedDetailsInSection");		
	}
	
	public void testCollectionCountInSectionLabel() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertSectionsLabels("Customer Data Deliveries (1) Calculated details (2)");
		execute("Sections.change", "activeSection=1");
		assertSectionsLabels("Customer Data Deliveries (1) Calculated details (2) | Details (2) Amounts");
		execute("Navigation.next");		                 
		assertSectionsLabels("Customer Data Deliveries (2) Calculated details (1) | Details (1) Amounts");
	}
	
	private void assertSectionsLabels(String expectedLabels) { 
		String sections = "";
		for (HtmlElement section: getHtmlPage().getBody().getElementsByAttribute("div", "class", "ox-section")) {
			if (!sections.equals("")) sections += " | ";
			sections += section.asText(); 
		}
		assertEquals(expectedLabels, sections);
	}
	
}