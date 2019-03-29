package org.openxava.actions;

import java.util.Arrays;
import javax.inject.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class AddColumnsAction extends ViewBaseAction implements INavigationAction, IChangeModeAction {
	
	private String property;  
	
	@Inject @Named("xava_customizingTab")
	private Tab tab;
	
	
	public void execute() throws Exception {
		if (!XavaPreferences.getInstance().isCustomizeList()) return;
		if (property == null) {
			String [] values = getRequest().getParameterValues("selectedProperties");
			if (values == null) { 
				addError("choose_columns_before_add");
				return; 
			}	
			getTab().addProperties(Arrays.asList(values));
		}
		else {
			getTab().addProperties(Arrays.asList(property));
		}
		closeDialog();
	}

	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public String[] getNextControllers() throws Exception {
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() throws Exception {
		return PREVIOUS_VIEW;
	}

	public String getNextMode() {
		return PREVIOUS_MODE;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
