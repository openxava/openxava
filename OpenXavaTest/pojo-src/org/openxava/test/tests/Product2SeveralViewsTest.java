package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product2SeveralViewsTest extends ModuleTestBase {
	
		
	public Product2SeveralViewsTest(String testName) {
		super(testName, "Product2SeveralViews");		
	}
		
	public void testSimpleView() throws Exception {  
		assertSimpleView();
		
		execute("Product2SeveralViews.changeToNotSimple9");
		assertNotSimpleView();
		
		execute("Product2SeveralViews.changeToSimple8");
		assertSimpleView();
		
		execute("Product2SeveralViews.changeToNotSimpleReference");
		assertNotSimpleView();
		
		execute("Product2SeveralViews.changeToSimple8");
		assertSimpleView();
		
		execute("Product2SeveralViews.changeToNotSimpleGroup");
		assertNotSimpleView();
		
		execute("Product2SeveralViews.changeToSimple8");
		assertSimpleView();
		
		execute("Product2SeveralViews.changeToNotSimpleSection");
		assertNotSimpleView();
		
		execute("Product2SeveralViews.changeToSimple8");
		assertSimpleView();
		
		execute("Product2SeveralViews.changeToNotSimpleFramedEditor");
		assertNotSimpleView();
	}

	private void assertSimpleView() throws Exception{ 
		assertTrue(getHtml().contains("ox-simple-layout"));
	}
	
	private void assertNotSimpleView() throws Exception{ 
		assertFalse(getHtml().contains("ox-simple-layout"));
	}
	
}
