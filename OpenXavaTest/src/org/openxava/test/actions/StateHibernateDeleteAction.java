package org.openxava.test.actions;

import org.hibernate.*;
import org.openxava.actions.*;
import org.openxava.hibernate.*;
import org.openxava.test.model.*;


/**
 * @author Mª Carmen Gimeno
 */
public class StateHibernateDeleteAction extends ViewBaseAction{
	
	public void execute() throws Exception {		
		Query query = XHibernate.getSession().createQuery("select s from State as s where s.id=:id" );	
		query.setString("id",getView().getValueString("id"));
		State s =(State) query.uniqueResult();
		if (s==null) {
			addError("no_delete_not_exists");
		}
		else {
			XHibernate.getSession().delete(s);
		}
		getView().reset();
		getView().setKeyEditable(true);
		resetDescriptionsCache();
	}
	     	
}
