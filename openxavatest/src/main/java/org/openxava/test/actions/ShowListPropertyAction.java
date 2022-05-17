package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */
public class ShowListPropertyAction extends BaseAction {
	
	@Inject
	private Tab tab;
	private String property;
	private int index;

	public void execute() throws Exception {		
		getTab().addProperty(index, property);
	}

	public String getProperty() {
		return property;
	}
	public void setProperty(String propiedad) {
		this.property = propiedad;
	}
	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
