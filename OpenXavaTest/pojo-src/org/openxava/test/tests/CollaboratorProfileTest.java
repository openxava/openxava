package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CollaboratorProfileTest extends ModuleTestBase {
	
	public CollaboratorProfileTest(String testName) {
		super(testName, "CollaboratorProfile");		
	}
	
	public void testCompositeKeyReferenceAsDescriptionsList() throws Exception { 
		assertListRowCount(2);
		setConditionValues("", "[.2.222.]:_:PROFILE 222");
		assertListRowCount(1);
		assertValueInList(0, 0, "COLLABORATOR 22");
		assertValueInList(0, 1, "PROFILE 222");
		assertListConfigurationsWithCompositeKeyReferenceAsDescriptionsList(); 
	}
	
	private void assertListConfigurationsWithCompositeKeyReferenceAsDescriptionsList() throws Exception { 
		assertListSelectedConfiguration("Profile = profile 222");		
		assertListAllConfigurations("Profile = profile 222", "All"); 
		assertListRowCount(1);		

		selectListConfiguration("All");
		assertListSelectedConfiguration("All");		
		assertListAllConfigurations("All", "Profile = profile 222");
		assertListRowCount(2);
		
		selectListConfiguration("Profile = profile 222");
		assertListSelectedConfiguration("Profile = profile 222");		
		assertListAllConfigurations("Profile = profile 222", "All"); 
		assertListRowCount(1);		
	}
		
}
