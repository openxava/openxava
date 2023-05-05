package org.openxava.test.tests;

import org.apache.commons.lang3.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *  
 * @author Javier Paniza
 */

public class DocTest extends ModuleTestBase {
	
	public DocTest(String testName) {
		super(testName, "Doc");		
	}
			
	public void testHtmlTextToolTip_htmlTextInCharts_htmlTextWithQuoteAndGreaterThanSymbol() throws Exception { 
		assertValueInList(0, 0, "DON QUIJOTE");
		
		String html = getHtml();
		assertTrue(html.contains("tres partes de su hacienda")); // The tooltip is complete
		assertEquals(2, StringUtils.countMatches(html, "En un lugar de la Mancha")); // The tooltip is not duplicated
		
		HtmlDivision div = (HtmlDivision) getHtmlPage().getByXPath("//div[@class='ox_openxavatest_Doc__tipable ox_openxavatest_Doc__list_col1 ox-width-100']").get(0);  
		String title = "En un lugar de la Mancha\'2, de cuyo nombre no quiero> acordarme3, <editor url=\"booleanWithSuffixEditor.jsp\"> </editor> no ha";
		assertTrue(div.getFirstElementChild().getAttribute("title").startsWith(title));
		
		execute("ListFormat.select", "editor=Charts");
		setValue("xColumn", "content");
		assertFalse(getHtmlPage().asNormalizedText().contains("/>"));		
		assertFalse(getHtmlPage().asNormalizedText().contains("de cuyo nombre no quiero"));
	}
	
}
