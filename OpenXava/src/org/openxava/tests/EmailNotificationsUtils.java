package org.openxava.tests;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.util.impl.*;

import junit.framework.Assert;

/**
 * tmr Doc
 * @author Javier Paniza
 */
public class EmailNotificationsUtils extends Assert {
	
	private EmailNotificationsUtils() {
	}
	
	public static void assertEmailNotifications(String ... expectedNotifications) throws Exception{
		LogTrackerUtils.assertLog(getFileName(), expectedNotifications);
	}
	
	public static void assertEmailSubscriptions(String ... expectedSubscriptions) { 
		XPersistence.commit();
		Query query = XPersistence.getManager().createQuery("from EmailSubscription s order by s.email");
		Collection<EmailSubscription> subscriptions = query.getResultList();
		assertEquals(expectedSubscriptions.length, subscriptions.size());
		int i=0;
		for (EmailSubscription subscription: subscriptions) {
			assertEquals(expectedSubscriptions[i++], toString(subscription));
		}
	}
	
	public static void removeAllEmailSubscriptions() throws Exception { 
		Query query = XPersistence.getManager().createQuery("delete from EmailSubscription");
		query.executeUpdate();
		XPersistence.commit();
	}
	
	public static String getEmailSubscriptionAction() {
		return "EmailNotifications.subscribe";
	}
	
	private static String toString(EmailSubscription subscription) { 
		return subscription.getEmail() + "=" + subscription.getModule();
	}
	
	private static String getFileName() { 		
		return Files.getOpenXavaBaseDir() + "email-notifications.log"; 			
	}
		
}
