package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza 
 */

public class StateJPASearchAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		State s = (State) XPersistence.getManager().find(State.class, getView().getValueString("id"));
		if (s==null) {
		  	addError("object_not_found");
		}
		else {
			getView().setValue("id",s.getId());
			getView().setValue("name",s.getName());
			getView().setEditable(true);	
			getView().setKeyEditable(false);
		}				
	}
				
}
