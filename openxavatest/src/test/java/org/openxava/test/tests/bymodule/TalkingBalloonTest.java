package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class TalkingBalloonTest extends ModuleTestBase {
	
	public TalkingBalloonTest(String testName) {
		super(testName, "TalkingBalloon");		
	}
	
	public void testSearchReferenceWithNestedCompositeKeysWithOnlyReferences() throws Exception { 
		execute("Reference.search", "keyProperty=balloon.color.number");
		assertDialogTitle("Choose a new value for Balloon"); // So we search in balloons
	}
	
}
