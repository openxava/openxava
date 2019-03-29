package org.openxava.test.tests;

import org.openxava.tests.*;
import com.gargoylesoftware.htmlunit.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class TestServletTest extends TestCase {
	
	public void testTestServlet() throws Exception {
		WebClient client = new WebClient();
		Page page = client.getPage("http://" + getHost() + ":" + getPort() + "/OpenXavaTest/test"); 
		String content = page.getWebResponse().getContentAsString();		
		assertTrue(content.indexOf("Hello, I'm a test servlet from OpenXava") >= 0);
	}
	
	private String getPort() { 
		return ModuleTestBase.getXavaJUnitProperty("port", "8080"); 
	}
	
	private String getHost() { 
		return ModuleTestBase.getXavaJUnitProperty("host", "localhost"); 
	}		

}
