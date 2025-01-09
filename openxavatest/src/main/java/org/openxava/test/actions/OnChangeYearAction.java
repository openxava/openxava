package org.openxava.test.actions;

import org.openxava.actions.*;

public class OnChangeYearAction extends OnChangePropertyBaseAction {
	
    public void execute() throws Exception {
		String description = (String) getNewValue();
        if ("show".equalsIgnoreCase(description)) {
            getView().setHidden("sectionB", false);
        }
        else {
            getView().setHidden("sectionB", true);
        }
    }
    
}
