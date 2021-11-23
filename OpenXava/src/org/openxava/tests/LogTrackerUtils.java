package org.openxava.tests;

import java.io.*;
import java.util.*;

import org.openxava.application.meta.*;
import org.openxava.util.*;

import junit.framework.Assert;

/**
 * Utility class to read the log tracker created by access tracking, to be use in junit tests. <p>
 * 
 * @since 6.6.1
 * @author Javier Paniza
 */
class LogTrackerUtils extends Assert {
	
	private static String fileName;

	private LogTrackerUtils() {
	}
	
	public static void assertAccessLog(String ... expectedEntries) throws Exception{
		assertLog(getFileName(), expectedEntries);
	}
		
	public static void assertLog(String file, String ... expectedEntries) throws Exception{
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(file));
			String line = r.readLine();
			int i=0;
			while (line != null) {
				if (i >= expectedEntries.length) {
					fail(XavaResources.getString("more_than_expected_entries", expectedEntries.length)); 
				}
				assertEquals(expectedEntries[i++], line);
				line = r.readLine();
			}
			if (i < expectedEntries.length) {
				fail(XavaResources.getString("not_the_expected_entries", expectedEntries.length, i)); 
			}
		}
		catch (FileNotFoundException ex) {
			if (expectedEntries.length > 0) {
				fail(XavaResources.getString("not_the_expected_entries_no_log_file", expectedEntries.length)); 
			}
		}
		finally {
			if (r != null) r.close();
		}
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
