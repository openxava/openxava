package org.openxava.test.tests;


/**
 * This test case verifies the @Collapsed annotation applied to 
 * a collection member.
 * 
 * @author Paco Valsera
 *
 */
public class SchoolTest extends CollapsedMemberTestBase {			
	
	public SchoolTest(String testName) {
		super(testName, "School", "teachers");				
	}

}
