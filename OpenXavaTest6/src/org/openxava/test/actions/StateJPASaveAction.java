package org.openxava.test.actions;

import org.openxava.test.model.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;


/**
 * @author Javier Paniza
 */
public class StateJPASaveAction extends ViewBaseAction{
	
	public void execute() throws Exception {
		if (getView().isKeyEditable()) {
			// Create
			State s = new State();
			s.setId(getView().getValueString("id"));
			s.setName(getView().getValueString("name"));
			XPersistence.getManager().persist(s);			
		}
		else {
			// Modify				
			State s = (State) XPersistence.getManager().find(State.class, getView().getValueString("id"));
			if (s==null) {
				addError("no_modify_no_exists");
			}
			else {
				s.setId(getView().getValueString("id"));
				s.setName(getView().getValueString("name"));
			}	
		}
		getView().reset();
		getView().setKeyEditable(true);
		resetDescriptionsCache();
	}
	     	
}
