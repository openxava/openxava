package org.openxava.util; 

import java.util.*;
import org.apache.commons.logging.*;

/**
 * Use to notify of changes in the data. <p>
 * 
 * All changes done using MapFacade are tracked automatically. 
 * If you changes data using JPA, JDBC, etc. you should call the
 * method in this class to track the database changes.
 * 
 * @since 5.9
 * @author Javier Paniza
 */

public class AccessTracker { 
	
	private static Log log = LogFactory.getLog(AccessTracker.class); 
	private static Collection<IAccessTrackerProvider> trackers;

	public static void consulted(String modelName, Map key) {
		for (IAccessTrackerProvider tracker: getTrackers()) {
			try {
				tracker.consulted(modelName, key);
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("access_tracker_failed", tracker.getClass(), "consult"), ex);
			}
		}		
	}
	
	public static void created(String modelName, Map key) {
		for (IAccessTrackerProvider tracker: getTrackers()) {
			try {
				tracker.created(modelName, key);
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("access_tracker_failed", tracker.getClass(), "creation"), ex);
			}
		}
	}

	/**
	 * You have to put the same property in both maps, oldChangeValues and newChangedValues, with old and new value. An example:
	 * <pre>
	 * Map<String, Object> oldChangedValues = new HashMap<String, Object>();
	 * oldChangedValues.put("address", "C/. Doctor Pesset"); // The old address
	 * Map<String, Object> newChangedValues = new HashMap<String, Object>();
	 * newChangedValues.put("address", "C/. Almudaina"); // The new address
	 * AccessTracking.modified("Customer", key, oldChangedValues, newChangedValues);
	 * </pre>
	 * If oldChangeValues and newChangedValues are empty the tracker can ignore the call. 
	 */
	public static void modified(String modelName, Map key, Map<String, Object> oldChangedValues, Map<String, Object> newChangedValues) {
		for (IAccessTrackerProvider tracker: getTrackers()) {
			try {
				tracker.modified(modelName, key, oldChangedValues, newChangedValues);
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("access_tracker_failed", tracker.getClass(), "modification"), ex);
			}
		}	
	}
	
	public static void removed(String modelName, Map key) {
		for (IAccessTrackerProvider tracker: getTrackers()) {
			try {
				tracker.removed(modelName, key);
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("access_tracker_failed", tracker.getClass(), "removing"), ex);
			}
		}
	}
	
	private static Collection<IAccessTrackerProvider> getTrackers() {
		if (trackers == null) {
			trackers = new ArrayList();
			for (String trackerClass: XavaPreferences.getInstance().getAccessTrackerProvidersClasses().split(",")) {
				if (Is.emptyString(trackerClass)) continue;
				try {
					IAccessTrackerProvider tracker = (IAccessTrackerProvider) Class.forName(trackerClass.trim()).newInstance();
					trackers.add(tracker);
				}
				catch (Exception ex) {
					log.warn(XavaResources.getString("access_tracker_provider_creation_error", trackerClass), ex);
				}
			}
		}
		return trackers;
	}

}
