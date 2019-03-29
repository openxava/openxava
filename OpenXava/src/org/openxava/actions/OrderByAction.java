package org.openxava.actions;

import org.apache.commons.logging.*;

/**
 * @author Javier Paniza
 */

public class OrderByAction extends TabBaseAction  {
	private static Log log = LogFactory.getLog(OrderByAction.class);
	
	private String property;
	
	public void execute() throws Exception {		
		getTab().orderBy(property);
		getTab().saveConfiguration();
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

}
