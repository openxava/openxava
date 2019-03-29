package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class VersionTest extends ModuleTestBase {
	
	private static Log log = LogFactory.getLog(VersionTest.class);
	
	public VersionTest(String testName) {
		super(testName, "Version");				
	}
		
	protected void setUp() throws Exception {
	}
		
	protected void tearDown() throws Exception {
	}
	
	public void testCustomPortlet() throws Exception {
		if (!isPortalEnabled()) {
			log.warn("VersionTest is not executed. It needed to be tested against a portal");
			return;
		}
		WebClient client = new WebClient();
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);  
		client.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage page = (HtmlPage) client.getPage(getModuleURL());
		String html = page.asXml();		
		assertTrue(html.indexOf("The version of OpenXava is")>=0);
	}
	
}
