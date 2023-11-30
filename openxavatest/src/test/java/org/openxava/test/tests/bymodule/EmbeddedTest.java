/**
 * 
 */
package org.openxava.test.tests.bymodule;

import java.util.regex.*;

import org.htmlunit.html.*;
import org.openxava.tests.*;


/**
 * @author Federico Alcantara
 *
 */
public class EmbeddedTest extends ModuleTestBase {
	private Pattern keyPropertyPattern =  Pattern.compile("keyProperty=(.+)"); 

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
			if (element.getAttribute("data-action").equals("Reference.search")) { 
				returnValue = element;
				break;
			}
		}
		
		return returnValue;
	}
	
	private String extractKeyProperty(HtmlElement element) {
		String href = element.getAttribute("data-argv"); 
		Matcher matcher = keyPropertyPattern.matcher(href);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
}
