package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class PerishableArticleTest extends ModuleTestBase {
	
	public PerishableArticleTest(String testName) {
		super(testName, "PerishableArticle");		
	}
	
	public void testNoCalendarForHiddenDate() throws Exception { 
		assertNoAction("ListFormat.select", "editor=Calendar");
	}
		
}
