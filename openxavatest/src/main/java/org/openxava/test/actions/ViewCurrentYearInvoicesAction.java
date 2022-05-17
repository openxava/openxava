package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */
public class ViewCurrentYearInvoicesAction extends BaseAction implements INavigationAction {
	
	private String [] NEXT_CONTROLLERS = { "Return" };
	
	private Tab tab;

	public void execute() throws Exception {
		setTab(new Tab()); // For not alter the tab of list mode
		getTab().setModelName("Invoice");
		getTab().setTabName("Current");
	}

	public String[] getNextControllers() throws Exception {
		return NEXT_CONTROLLERS;
	}

	public String getCustomView() throws Exception {
		return "xava/list";
	}

	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}
}
