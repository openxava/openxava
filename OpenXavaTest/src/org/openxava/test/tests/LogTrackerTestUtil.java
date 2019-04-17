package org.openxava.test.tests;

import java.io.*;
import java.util.*;

import org.openxava.application.meta.*;
import org.openxava.util.Is;
import org.openxava.util.Files;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */
public class LogTrackerTestUtil extends Assert {
	
	private static String fileName;

	private LogTrackerTestUtil() {
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
					fail("Expected " +  expectedEntries.length + " entries, but there are more"); 
				}
				assertEquals(expectedEntries[i++], line);
				line = r.readLine();
			}
			if (i < expectedEntries.length) {
				fail("Expected " +  expectedEntries.length + " entries, but there are " + i); 
			}
		}
		catch (FileNotFoundException ex) {
			if (expectedEntries.length > 0) {
				fail("Expected " + expectedEntries.length + " entries, but there is no log file"); 
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
