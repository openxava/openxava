package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import junit.framework.*;

public class PublicJSPTest extends TestCase {
	
	public void testPublicJSP() throws Exception {
		WebClient client = new WebClient();
	    HtmlPage page = (HtmlPage) client.getPage("http://" + getHost() + ":" + getPort() + "/" + getApplication() + "/public/myPublicJSP.jsp" ); 
	    assertTrue(page.asText().startsWith("The uri of this JSP is"));
	}
	
	private static String getPort() {						
		return ModuleTestBase.getXavaJUnitProperty("port", "8080"); 
	}
	
	private static String getHost() {
		return ModuleTestBase.getXavaJUnitProperty("host", "localhost"); 
	}	
	
	private static String getApplication() {
		return ModuleTestBase.getXavaJUnitProperty("application", "OpenXavaTest"); 
	}

}
