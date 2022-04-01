package org.openxava.actions;

/**
 * @since 5.8
 * @author Javier Paniza
 */

public class GroupByAction extends FilterTabBaseAction { 
	
	private String property;
	
	public void execute() throws Exception {		
		getTab().groupBy(property);
		getManager().setActionsChanged(true);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

}
