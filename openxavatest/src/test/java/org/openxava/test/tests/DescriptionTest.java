package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class DescriptionTest extends ModuleTestBase {
	
	private static Log log = LogFactory.getLog(DescriptionTest.class);
	
	public DescriptionTest(String testName) {
		super(testName, "Description");		
	}
	
	public void testDocModule() throws Exception { 
		if (!isPortalEnabled()) {
			log.warn("DescriptionTest is not executed. It needed to be tested against a portal");
			return;
		}
		assertTrue(getHtml().indexOf("application is used to test all OpenXava features")>=0);
	}
	
}
