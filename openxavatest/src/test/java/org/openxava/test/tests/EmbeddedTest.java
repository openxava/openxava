/**
 * 
 */
package org.openxava.test.tests;

import java.util.regex.*;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;


/**
 * @author Federico Alcantara
 *
 */
public class EmbeddedTest extends ModuleTestBase {
	private Pattern keyPropertyPattern =  Pattern.compile(".+'keyProperty=(.+)'.+");

	public EmbeddedTest(String nameTest) {
		super(nameTest,"ClassA");
	}

	public void testSearch() throws Exception {
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
			if (HtmlUnitUtils.getHrefAttribute(element).contains("Reference.search")) { 
				returnValue = element;
				break;
			}
		}
		
		return returnValue;
	}
	
	private String extractKeyProperty(HtmlElement element) {
		String href = HtmlUnitUtils.getHrefAttribute(element); 
		Matcher matcher = keyPropertyPattern.matcher(href);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
}
