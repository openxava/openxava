package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class ProductRichDescriptionPhotosTest extends ModuleTestBase {
	
		
	public ProductRichDescriptionPhotosTest(String testName) {
		super(testName, "ProductRichDescriptionPhotos");		
	}
		
	public void testHtmlTextForAPropertyNamedDescriptionNotShowsItsContentInPhotosDialog() throws Exception { 
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "<P>THE JUNIT PRODUCT</P>");
		execute("Gallery.edit", "galleryProperty=photos");
		HtmlElement gallery = getHtmlPage().getBody().getElementsByAttribute("div", "class", "ox-images-gallery").get(0); 
		assertFalse(gallery.asText().contains("THE JUNIT PRODUCT"));
		
	}
	
}
