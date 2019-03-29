package org.openxava.test.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.test.model.Color;
import org.openxava.tests.ModuleTestBase;

/**
 * Create on 05/03/2009 (10:17:49)
 * @author Ana Andres
 */
public class ColorWithGroupsAndChangeControllersTest extends ModuleTestBase {
	private static Log log = LogFactory.getLog(ColorWithGroupsAndChangeControllersTest.class);
	
	public ColorWithGroupsAndChangeControllersTest(String testName) {
		super(testName, "ColorWithGroupsAndChangeControllers");		
	}

	public void testViewGroupAndControllerOnChangeGroup_setFocusFromOnChangeAction() throws Exception {
		assertValue("group", usesAnnotatedPOJO()?"":"0"); 
		assertNotExists("property1");
		assertNotExists("property2");
		assertActions(new String[] {});
		setValue("group", String.valueOf(Color.Group.GROUP1.ordinal())); // For annotated POJOs
		// setValue("group", "1"); // For XML components   
		assertExists("property1");
		assertNotExists("property2");
		assertFocusOn("property1"); 
		assertActions(new String[] { "ReturnPreviousModule.return" });
		setValue("group", String.valueOf(Color.Group.GROUP2.ordinal())); // For annotated POJOs
		// setValue("group", "2"); // For XML components   
		assertNotExists("property1");
		assertExists("property2");
		assertActions(new String[] { "ReturnPreviousModule.return", "ActionWithImage.new" });
		setValue("group", usesAnnotatedPOJO()?"":"0");
		assertNotExists("property1");
		assertNotExists("property2");
		assertActions(new String[] {});
	}
}
