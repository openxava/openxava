package org.openxava.test.bymodule;


/**
 * This test case verifies the @Collapsed annotation applied to 
 * a reference member.
 * 
 * @author Paco Valsera
 *
 */
public class TeacherTest extends CollapsedMemberTestBase { 
	
	public TeacherTest(String testName) { 
		super(testName, "Teacher", "school");				
	}
}
