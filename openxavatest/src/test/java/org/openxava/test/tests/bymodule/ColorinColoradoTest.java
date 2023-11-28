package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.tests.*;

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
			"Reference.modify",
			"Reference.clear"
		};
		assertActions(actions);
		HtmlElement buttonBar = getHtmlPage().getHtmlElementById("ox_openxavatest_ColorinColorado__button_bar");
		assertEquals(1, buttonBar.getElementsByAttribute("a", "id", "ox_openxavatest_ColorinColorado__ColorinColorado___fillName").size()); 
	}
	
}
