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
	
	public void testSearchReferenceWithNestedCompositeKeysWithOnlyReferences_onChangeWithNestedCompositeKeysWithOnlyReferences() throws Exception { // tmr onChangeWithNestedCompositeKeysWithOnlyReferences  
		execute("Reference.search", "keyProperty=balloon.color.number");
		assertDialogTitle("Choose a new value for Balloon"); // So we search in balloons
		
		// tmr ini
		execute("ReferenceSearch.choose", "row=0");
		assertMessage("OnChangeVoidAction executed");
		// tmr fin
	}
	
}
