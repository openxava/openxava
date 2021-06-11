package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

public class ArtistAgeReadOnlyTest extends ModuleTestBase {
	
	public ArtistAgeReadOnlyTest(String testName) {
		super(testName, "ArtistAgeReadOnly");		
	}
	
	public void testReadOnlyMembersInSectionChangingViewProgrammaticallyUsingViewInheritance() throws Exception {
		// TMP ME QUEDÉ POR AQUÍ: EL TEST ESTÁ HECHO, FALTA ARREGLAR EL BUG
		execute("List.viewDetail", "row=0");
		assertNoEditable("age");
	}
			
}
