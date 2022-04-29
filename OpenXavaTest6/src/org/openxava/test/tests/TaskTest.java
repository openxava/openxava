package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class TaskTest extends ModuleTestBase {
	
	private static Log log = LogFactory.getLog(TaskTest.class);
			
	public TaskTest(String testName) {
		super(testName, "Task");		
	}
	
	public void testUsersAndUserFilter() throws Exception {
		if (!isPortalEnabled()) {
			log.warn("TaskTest is not executed. It needed to be tested against a portal");
			return;
		}

		// In order to run this test you need an user 'junit' in your portal

		login(getUserLoginName2(), "junit2");
		assertListRowCount(0);
		logout();
		
		login(getUserLoginName(), "junit");			
		assertValueInList(0, "user", getUserId());
		assertValueInList(0, "summary", "FOR USING IN JUNIT TEST");		
		execute("CRUD.new");
		assertValue("user", getUserId());
		assertValue("userGivenName", "JUnit Given Name");
		assertValue("userFamilyName", "JUnit Family Name");
		assertValue("userEMail", "junit@openxava.org");
		assertValue("jobTitle", "JUnit Job Title");
		assertValue("middleName", "JUnit Middle Name");
		assertValue("nickName", "junit");
		assertValue("birthDateYear", "2012");
		assertValue("birthDateMonth", "4");
		assertValue("birthDateDay", "30");
		setValue("summary", "JUNIT TEST");
		execute("Task.save");
		assertMessage("The given name is JUnit Given Name");
		execute("CRUD.delete");
		logout();
	}
	
	
	
	public void testLogoutResetPortletState() throws Exception {
		if (!isPortalEnabled()) {
			log.warn("TaskTest is not executed. It needed to be tested against a portal");
			return;
		}
		login(getUserLoginName(), "junit");
		assertNoAction("Mode.list");
		execute("CRUD.new");
		assertAction("Mode.list");
		logout();
		login(getUserLoginName2(), "junit2");		
		assertNoAction("Mode.list");
		logout();
	}
	
	
	private String getUserLoginName() {
		return isLiferayEnabled()?"junit@openxava.org":"junit";
	}
	private String getUserLoginName2() {
		return isLiferayEnabled()?"junit2@openxava.org":"junit2";
	}	
	private String getUserId() {
		if (!isLiferayEnabled()) return "junit";
		return XavaPreferences.getInstance().isEMailAsUserNameInPortal()?"junit@openxava.org":"11118"; 				
	}
			
}
