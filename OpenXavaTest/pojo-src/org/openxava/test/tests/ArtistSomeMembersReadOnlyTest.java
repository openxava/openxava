package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ArtistSomeMembersReadOnlyTest extends ModuleTestBase {
	
	public ArtistSomeMembersReadOnlyTest(String testName) {
		super(testName, "ArtistSomeMembersReadOnly");		
	}
	
	public void testReadOnlyMembersInSectionAndGroupChangingViewProgrammaticallyUsingViewInheritance() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertNoEditable("age");
		assertNoEditable("level");
	}
			
}
