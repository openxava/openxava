package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */

public class InitDefaultYearTo2002Action extends BaseAction {

	@Inject
	private int defaultYear; // without getter and setter, to test it 
	
	@Inject
	private Tab tab;

	public void execute() throws Exception {
		defaultYear = 2002; // This changes defaultYear in context because of @Inject
		tab.setTitleVisible(true);
		tab.setTitleArgument(new Integer(2002));
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

}
