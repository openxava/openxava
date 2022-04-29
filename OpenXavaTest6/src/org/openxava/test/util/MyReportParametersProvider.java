package org.openxava.test.util;

import org.openxava.util.*;

/**
 * Create on 11/03/2011 (13:30:32)
 * @author Ana Andres
 */
public class MyReportParametersProvider implements IReportParametersProvider {
	
	public String getOrganization() {
		return "report to " + Users.getCurrent();
	}
	
}