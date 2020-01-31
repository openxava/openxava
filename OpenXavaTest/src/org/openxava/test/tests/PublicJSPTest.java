package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import junit.framework.*;

public class PublicJSPTest extends TestCase {
	
	public void testPublicJSP() throws Exception {
		WebClient client = new WebClient();
	    // tmp HtmlPage page = (HtmlPage) client.getPage("http://" + getHost() + ":" + getPort() + "/" + getApplication() + "/public/myPublicJSP.jsp" );  
		HtmlPage page = (HtmlPage) client.getPage("http://" + getHost() + ":" + getPort() + getContext() + "public/myPublicJSP.jsp" ); // tmp
	    assertTrue(page.asText().startsWith("The uri of this JSP is"));
	}
	
	private static String getPort() {						
		return ModuleTestBase.getXavaJUnitProperty("port", "8080"); 
	}
	
	private static String getHost() {
		return ModuleTestBase.getXavaJUnitProperty("host", "localhost"); 
	}	

	/* tmp
	private static String getApplication() {
		return ModuleTestBase.getXavaJUnitProperty("application", "OpenXavaTest"); 
	}
	*/
	
	protected static String getContext() { // tmp 
		return ModuleTestBase.getXavaJUnitProperty("context", "/OpenXavaTest/");
	}


}
