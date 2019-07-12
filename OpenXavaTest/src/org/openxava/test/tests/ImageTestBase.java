package org.openxava.test.tests;

import java.net.*;
import org.openxava.tests.*;
import com.gargoylesoftware.htmlunit.*;

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
		/* tmp
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
		*/
		// tmp ini
		String imageURL = (String) getHtmlPage().executeJavaScript(
			"var input = document.getElementsByName('" + decorateId(property) + "')[0];" +
			"imageEditor.getImageURL(input)"
		).getJavaScriptResult();
		System.out.println("[ImageTestBase.assertImage] property=" + property); // tmp
		System.out.println("[ImageTestBase.assertImage] decorateId(property)=" + decorateId(property)); // tmp
		System.out.println("[ImageTestBase.assertImage] imageURL=" + imageURL); // tmp
		URL url = getHtmlPage().getWebResponse().getWebRequest().getUrl(); 
		String urlPrefix = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/" + url.getPath().split("/")[1];
		imageURL = imageURL.replace("..", urlPrefix);
		// tmp fin
		WebResponse response = getWebClient().getPage(imageURL).getWebResponse();
		if (present) {
			assertTrue("Image not obtained", response.getContentAsString().length() > 0);
			// tmp assertEquals("Result is not an image", "image", response.getContentType());			
			assertTrue("Result is not an image", response.getContentType().startsWith("image")); // tmp
		}
		else {
			System.out.println("[ImageTestBase.assertImage] imageURL=" + imageURL); // tmp
			System.out.println("[ImageTestBase.assertImage] response.getContentType()=" + response.getContentType()); // tmp
			System.out.println("[ImageTestBase.assertImage] response.getContentAsString().length()=" + response.getContentAsString().length()); // tmp
			assertTrue("Image obtained", response.getContentAsString().length() == 0);
		}		
	}


	protected void changeImage(String property, String imageURL) throws Exception {
		/* tmp
		execute("ImageEditor.changeImage", "newImageProperty=" + property); 
		assertNoErrors();
		assertAction("LoadImage.loadImage");		
		String imageUrl = System.getProperty("user.dir") + imageURL;
		setFileValue("newImage", imageUrl);
		assertAction("LoadImage.cancel");
		execute("LoadImage.loadImage");
		assertNoErrors();
		*/
		// tmp ini
		// tmp ¿Envolver en ModuleTestBase? ¿Poner un nombre genérico (uploadFile)?
		// tmp En migration
		String imageAbsoluteURL = System.getProperty("user.dir") + imageURL;
		String decoratedProperty = decorateId(property); 
		setFileValue(decoratedProperty, imageAbsoluteURL);
		getHtmlPage().executeJavaScript(
			"var formData = new FormData();" +
			"var input = document.getElementsByName('" + decoratedProperty + "')[0];" +
			"formData.append('file', input.files[0]);" +
			"var xhr = new XMLHttpRequest();" +
			"xhr.open('POST', imageEditor.getUploadURL(input));" +
			"xhr.send(formData);"				
		);
		waitAJAX();
		// tmp fin
	}
		
}
