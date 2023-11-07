/**
 * 
 */
package org.openxava.test.tests;

import java.util.regex.*;

import org.htmlunit.html.*;
import org.openxava.tests.*;


/**
 * @author Federico Alcantara
 *
 */
public class EmbeddedTest extends ModuleTestBase {
	// tmr private Pattern keyPropertyPattern =  Pattern.compile(".+'keyProperty=(.+)'.+");
	private Pattern keyPropertyPattern =  Pattern.compile("keyProperty=(.+)"); // tmr

	public EmbeddedTest(String nameTest) {
		super(nameTest,"ClassA");
	}

	public void testSearch_moduleWorksWithNonExistentTable() throws Exception {  
		assertAction("CRUD.save"); // So it enters in detail 
		assertNoErrors(); 
		execute("CRUD.new");
		HtmlElement element = getSearchElement();
		String keyProperty = extractKeyProperty(element); 
		execute("Reference.search", "keyProperty=" + keyProperty);
		assertNoErrors(); 
		execute("ReferenceSearch.cancel");
		execute("Sections.change", "activeSection=1,viewObject=xava_view_b");
		element = getSearchElement();
		keyProperty = extractKeyProperty(element);
		execute("Reference.search", "keyProperty=" + keyProperty);
		assertNoErrors();
		execute("ReferenceSearch.cancel");
	}
	
	@SuppressWarnings("rawtypes")
	private HtmlElement getSearchElement() {
		HtmlElement returnValue = null;
		DomNodeList list = getHtmlPage().getElementsByTagName("a");
		for (int index = 0; index < list.getLength(); index++) {
			HtmlElement element = (HtmlElement) list.get(index);
			// tmr if (HtmlUnitUtils.getHrefAttribute(element).contains("Reference.search")) { 
			if (element.getAttribute("data-action").equals("Reference.search")) { // tmr
				returnValue = element;
				break;
			}
		}
		
		return returnValue;
	}
	
	private String extractKeyProperty(HtmlElement element) {
		// tmr String href = HtmlUnitUtils.getHrefAttribute(element); 
		String href = element.getAttribute("data-argv"); // tmr
		Matcher matcher = keyPropertyPattern.matcher(href);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
}
