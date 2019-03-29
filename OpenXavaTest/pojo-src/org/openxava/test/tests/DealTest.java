package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class DealTest extends ModuleTestBase {
	
	public DealTest(String testName) {
		super(testName, "Deal");		
	}
	
	public void testListWithOneToOneWithPrimaryKeyJoinColumn() throws Exception {
		assertValueInList(0, 0, "1"); 
		assertValueInList(0, 1, "THE BIG DEAL");
		assertValueInList(0, 2, "JUAN");
	}
	
	public void testIdInsideASection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoErrors(); // The first attempt does not fail when the test was written, but just in case it would fail 
		execute("Mode.list");		
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("id", "1");
		assertValue("name", "THE BIG DEAL");
	}
	
}
