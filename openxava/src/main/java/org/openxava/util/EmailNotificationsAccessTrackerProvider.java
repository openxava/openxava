package org.openxava.util; 

import java.util.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */
public class EmailNotificationsAccessTrackerProvider implements IAccessTrackerProvider {
	
	public void consulted(String modelName, Map key) {
	}


	public void created(String modelName, Map key) {
		EmailNotifications.notifyCreated(modelName, key);
	}

	public void modified(String modelName, Map key, Map<String, Object> oldChangedValues, Map<String, Object> newChangedValues) {
		EmailNotifications.notifyModified(modelName, key, oldChangedValues, newChangedValues);
		
	}

	public void removed(String modelName, Map key) {
		EmailNotifications.notifyRemoved(modelName, key);
	}

}
