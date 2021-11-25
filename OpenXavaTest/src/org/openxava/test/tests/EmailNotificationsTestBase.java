package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
abstract public class EmailNotificationsTestBase extends ModuleTestBase {
	
	private String module;
	
	public EmailNotificationsTestBase(String testName, String module) {
		super(testName, module);
		this.module= module;
	}
	
	protected void subscribeToEmailNotifications() throws Exception { 
		removeAllEmailSubscriptions();
		changeModule("SignIn");
		login("openxavatest1@getnada.com", "test1");
		changeModule(module);
		execute(EmailNotificationsUtils.getEmailSubscriptionAction()); 
		changeModule("SignIn");
		login("admin", "admin");
		changeModule(module);
	}
	
	protected void assertEmailNotifications(String ... expectedNotifications) throws Exception{
		EmailNotificationsUtils.assertEmailNotifications(expectedNotifications); 
	}
	
	protected void assertEmailSubscriptions(String ... expectedSubscriptions) {
		EmailNotificationsUtils.assertEmailSubscriptions(expectedSubscriptions); 
	}
	
	protected void removeAllEmailSubscriptions() throws Exception {
		EmailNotificationsUtils.removeAllEmailSubscriptions(); 
	}
		
		
}
