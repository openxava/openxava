package org.openxava.actions;

// tmp import org.apache.commons.logging.*;

/**
 * @author Javier Paniza
 */

public class OrderByAction extends TabBaseAction  {
	// tmp private static Log log = LogFactory.getLog(OrderByAction.class);
	
	private String property;
	
	public void execute() throws Exception {		
		getTab().orderBy(property);
		// tmp getTab().saveConfiguration();
		// tmp ini
		getTab().createConfiguration();
		// tmp fin
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

}
