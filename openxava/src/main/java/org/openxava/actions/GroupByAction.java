package org.openxava.actions;

import org.openxava.tab.*;

/**
 * @since 5.8
 * @author Javier Paniza
 */

public class GroupByAction extends FilterTabBaseAction { 
	
	private String property;
	
	public void execute() throws Exception {	
		Tab tab = getTab();
		tab.groupBy(property);
		getManager().setActionsChanged(true);
		if (tab.getPage() > 1) tab.goPage(1);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

}
