package org.openxava.tests;

import java.util.*;

import junit.framework.*;

import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * Utility class for easier use of HtmlUnit.
 * 
 * @since 6.0
 * @author Javier Paniza
 */

public class HtmlUnitUtils {
	
	public static HtmlElement getAnchor(HtmlPage page, String url) throws Exception { 
		return getAnchors(page, url).get(0); 		
	}
	
	public static List<HtmlElement> getAnchors(HtmlPage page, String url) throws Exception { 
		List<HtmlElement> anchors = getAnchors(page, "a", url);
		if (anchors.isEmpty()) anchors = getAnchors(page, "div", url);
		return anchors;
	}	
	
	private static List<HtmlElement> getAnchors(HtmlPage page, String element, String url) throws Exception {
		if ("a".equals(element)) {
			List<HtmlElement> result = page.getBody().getElementsByAttribute(element, "href", url);
			if (!result.isEmpty()) return result;
		}
		return page.getBody().getElementsByAttribute(element, "onclick", "window.location='" + url + "'");
	}

	public static void assertElementExists(HtmlPage page, String id) { 
		try {
			page.getHtmlElementById(id);			
		}
		catch (ElementNotFoundException ex) {
			TestCase.fail(XavaResources.getString("must_exist", id));
		}
	}

	public static void assertElementNotExist(HtmlPage page, String id) { 
		try {
			page.getHtmlElementById(id);
			TestCase.fail(XavaResources.getString("must_not_exist", id));
		}
		catch (ElementNotFoundException ex) {
		}
	}
	
	public static void assertPageURI(HtmlPage page, String value) { 
		if (!page.getUrl().toString().endsWith(value)) {
			TestCase.fail(XavaResources.getString("unexpected_uri_string"));
		}
	}
	
	public static BrowserVersion getAndroidBrowser() throws Exception { 
		return getBrowser("Mozilla/5.0 (Linux; U; Android 4.1.1; es-es; Vodafone 875 Build/JRO03C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.1 Mobile Safari/534.30 SVN/050HCG5"); 
	}
	
	public static BrowserVersion getBrowser(String userAgent) throws Exception {
		return new BrowserVersion.BrowserVersionBuilder(BrowserVersion.BEST_SUPPORTED)
			.setUserAgent(userAgent)
			.build();
	}
		
}