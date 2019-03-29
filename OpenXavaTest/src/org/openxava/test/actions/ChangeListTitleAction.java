package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */
public class ChangeListTitleAction extends BaseAction {
	
	private String titleId;
	
	@Inject
	private Tab tab;

	public void execute() throws Exception {
		getTab().setTitleVisible(true);
		getTab().setTitleId(getTitleId());	
	}

	public String getTitleId() {
		return titleId;
	}
	public void setTitleId(String title) {
		this.titleId = title;
	}
	
	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}
}
