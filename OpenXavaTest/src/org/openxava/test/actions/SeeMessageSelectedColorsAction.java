package org.openxava.test.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.tab.*;

/**
 * Create on 28/02/2013 (12:46:06)
 * @author Ana Andres
 */
public class SeeMessageSelectedColorsAction extends BaseAction{
	private static Log log = LogFactory.getLog(SeeMessageSelectedColorsAction.class);
	
	@Inject
	public Tab tab;
	
	public void execute() throws Exception {
		int[] selected = getTab().getSelected();	// test the old method
		Map[] selectedKeys = getTab().getSelectedKeys();
		if (selected == null || selectedKeys == null) return;
		String m = "";
		String o = "";
		for (int i = 0; i < selected.length; i++) m+="[" + selected[i] + "]";
		for (int i = 0; i < selectedKeys.length; i++) o+="[" + selectedKeys[i] + "]";
		addMessage("color_selected_old", m);
		addMessage("color_selected_new", "'" + o + "'");
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
}
