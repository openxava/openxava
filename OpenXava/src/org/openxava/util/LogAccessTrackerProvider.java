package org.openxava.util; 

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;

/**
 * 
 * @author Javier Paniza
 */
public class LogAccessTrackerProvider implements IAccessTrackerProvider {
	
	private static Log log = LogFactory.getLog(LogAccessTrackerProvider.class);  
	
	private static String fileName;

	public void consulted(String modelName, Map key) {
		log("CONSULTED: user=" + Users.getCurrent() +	", model=" + modelName + ", key=" + key);
	}


	public void created(String modelName, Map key) {
       	log("CREATED: user=" + Users.getCurrent() +	", model=" + modelName + ", key=" + key);
	}

	public void modified(String modelName, Map key, Map<String, Object> oldChangedValues, Map<String, Object> newChangedValues) {
		StringBuffer changes = new StringBuffer(); 
		for (String property: oldChangedValues.keySet()) {
			if (changes.length() > 0) changes.append(", ");
			changes.append(Labels.getQualified(property));
			changes.append(": ");
			changes.append(Strings.toString(oldChangedValues.get(property)));
			changes.append(" --> ");
			changes.append(Strings.toString(newChangedValues.get(property)));
		}
		log("MODIFIED: user=" + Users.getCurrent() +	", model=" + modelName + ", key=" + key + ", changes=" + changes);
	}

	public void removed(String modelName, Map key) {
		log("REMOVED: user=" + Users.getCurrent() +	", model=" + modelName + ", key=" + key);
	}
		
	private static void log(String line) {
		try {
			createFileIfNotExist();
			FileOutputStream f = new FileOutputStream(getFileName(), true); 
			PrintStream p = new PrintStream(f);
			p.println(line);
			p.close();
			f.close();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("log_tracker_log_failed"), ex);
		}
	}
	
	private static void createFileIfNotExist() throws Exception { 
		Files.createFileIfNotExist(getFileName()); 
	}

	private static String getFileName() {		
		if (fileName == null) {
			Collection applicationNames = MetaApplications.getApplicationsNames();
			String app = "openxava-app";
			if (!applicationNames.isEmpty()) app = applicationNames.iterator().next().toString().toLowerCase(); 
			fileName = Files.getOpenXavaBaseDir() + app + "-access.log"; 			
		}
		return fileName;
	}

}
