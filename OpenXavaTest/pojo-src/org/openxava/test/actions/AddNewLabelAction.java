package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/** 
 * tmp
 * 
 * @author Javier Paniza
 */
public class AddNewLabelAction extends BaseAction {
	
	private String key;
	private String label;

	public void execute() throws Exception {
		// tmp Labels.put(getKey(), Locales.getCurrent(), getLabel());
		Labels.put("ProgrammerApplicant.platform", Locales.getCurrent(), getLabel()); // tmp		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
