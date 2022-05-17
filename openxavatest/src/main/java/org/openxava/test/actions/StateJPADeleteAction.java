package org.openxava.test.actions;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;


/**
 * @author Javier Paniza
 */
public class StateJPADeleteAction extends ViewBaseAction{
	
	public void execute() throws Exception {		
		Query query = XPersistence.getManager().createQuery("select s from State as s where s.id=:id" );	
		query.setParameter("id", getView().getValueString("id"));
		State s =(State) query.getSingleResult();
		if (s==null) {
			addError("no_delete_not_exists");
		}
		else {
			XPersistence.getManager().remove(s);
		}
		getView().reset();
		getView().setKeyEditable(true);
		resetDescriptionsCache();
	}
	     	
}
