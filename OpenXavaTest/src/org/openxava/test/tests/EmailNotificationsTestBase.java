package org.openxava.test.tests;

import static org.openxava.test.tests.EmailNotificationsTestUtil.removeAllEmailSubscriptions;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;
import org.openxava.util.impl.*;
import junit.framework.*;

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
		execute(EmailNotificationsTestUtil.getEmailSubscriptionAction());
		changeModule("SignIn");
		login("admin", "admin");
		changeModule(module);
	}
	
	protected void assertEmailNotifications(String ... expectedNotifications) throws Exception{
		EmailNotificationsTestUtil.assertEmailNotifications(expectedNotifications);
	}
	
	protected void assertEmailSubscriptions(String ... expectedSubscriptions) {
		EmailNotificationsTestUtil.assertEmailSubscriptions(expectedSubscriptions);
	}
	
	protected void removeAllEmailSubscriptions() throws Exception {
		EmailNotificationsTestUtil.removeAllEmailSubscriptions();
	}
		
		
}
