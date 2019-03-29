package org.openxava.util;

import java.util.*;

/**
 * Logic to execute from AccessTracker. <p>
 * 
 * You can have active several providers at same time. The list of active providers is defined in xava.properties, in this way:
 * <pre>
 * accessTrackerProvidersClasses=org.openxava.util.EmailNotificationsAccessTrackerProvider,org.openxava.util.LogAccessTrackerProvider
 * </pre>
 * 
 * @since 5.9
 * @author Javier Paniza
 */
public interface IAccessTrackerProvider {  
	
	void consulted(String modelName, Map key);
	
	void created(String modelName, Map key);
	
	void modified(String modelName, Map key, Map<String, Object> oldChangedValues, Map<String, Object> newChangedValues);
	
	void removed(String modelName, Map key);

}
