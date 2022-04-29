package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class ColorinColoradoTest extends ModuleTestBase {
	
	public ColorinColoradoTest(String testName) {
		super(testName, "ColorinColorado");		
	}
	
	public void testAfterEachRequestAction_permalinkNotForRegularActions_singleActionWithIconAndNoImageShownInTopBar() throws Exception {  
		assertValue("name", "NULLCOLORADO"); 
		setValue("name", "");
		execute("ColorinColorado.fillName");		
		assertValue("name", "COLORIN COLORADO");
		HtmlUnitUtils.assertPageURI(getHtmlPage(), "/ColorinColorado");
		
		String [] actions = {
			// No more actions because we need just one action with icon and no image for the top bar button, to test a case	
			"ColorinColorado.fillName",
			"Reference.createNew", 
			"Reference.modify"
		};
		assertActions(actions);
		HtmlElement buttonBar = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ColorinColorado__button_bar");
		assertEquals(1, buttonBar.getElementsByAttribute("a", "id", "ox_OpenXavaTest_ColorinColorado__ColorinColorado___fillName").size()); 
	}
	
}
