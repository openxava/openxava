package org.openxava.test.tests;

import java.net.*;

import org.openxava.tests.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class ImageTestBase extends ModuleTestBase {
	
	public ImageTestBase(String testName, String moduleName) {
		super(testName, moduleName);		
	}
	
    /**	
	 * After calling this method the regular test methods do not work until you do a reload(). 
	 */
	protected void assertImage(String property) throws Exception {
		assertImage(property, true);
	}
	
	protected void assertNoImage(String property) throws Exception {
		assertImage(property, false);
	}
	
	private void assertImage(String property, boolean present) throws Exception {
		HtmlPage page = getHtmlPage();		
		URL url = page.getWebResponse().getWebRequest().getUrl(); 
		String urlPrefix = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
		
		HtmlImage image = (HtmlImage) page.getElementsByName(decorateId(property)).get(0); 
		String imageURL = null;
		if (image.getSrcAttribute().startsWith("/")) {
			imageURL = urlPrefix + image.getSrcAttribute();
		}
		else {
			String urlBase = Strings.noLastToken(url.getPath(), "/");
			imageURL = urlPrefix + urlBase + image.getSrcAttribute();
		}	
		WebResponse response = getWebClient().getPage(imageURL).getWebResponse();
		if (present) {
			assertTrue("Image not obtained", response.getContentAsString().length() > 0);
			assertEquals("Result is not an image", "image", response.getContentType());			
		}
		else {
			assertTrue("Image obtained", response.getContentAsString().length() == 0);
		}
	}


	protected void changeImage(String property, String imageURL) throws Exception {
		execute("ImageEditor.changeImage", "newImageProperty=" + property); 
		assertNoErrors();
		assertAction("LoadImage.loadImage");		
		String imageUrl = System.getProperty("user.dir") + imageURL;
		setFileValue("newImage", imageUrl);
		assertAction("LoadImage.cancel");
		execute("LoadImage.loadImage");
		assertNoErrors();
	}
	
}
