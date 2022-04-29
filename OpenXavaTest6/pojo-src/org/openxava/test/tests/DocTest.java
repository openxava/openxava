package org.openxava.test.tests;

import org.apache.commons.lang3.*;
import org.openxava.tests.*;

/**
 *  
 * @author Javier Paniza
 */

public class DocTest extends ModuleTestBase {
	
	public DocTest(String testName) {
		super(testName, "Doc");		
	}
			
	public void testHtmlTextToolTip_htmlTextInCharts() throws Exception { 
		assertValueInList(0, 0, "DON QUIJOTE");
		
		String html = getHtml();
		assertTrue(html.contains("tres partes de su hacienda")); // The tooltip is complete
		assertEquals(2, StringUtils.countMatches(html, "En un lugar de la Mancha")); // The tooltip is not duplicated
		
		assertTrue(getValueInList(0, 1).startsWith("En un lugar de la Mancha"));
		execute("ListFormat.select", "editor=Charts");
		setValue("xColumn", "content");
		assertFalse(getHtmlPage().asText().contains("/>"));		
		assertFalse(getHtmlPage().asText().contains("de cuyo nombre no quiero"));
	}
	
}
