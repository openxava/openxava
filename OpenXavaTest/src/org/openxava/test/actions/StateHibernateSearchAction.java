package org.openxava.test.actions;

import org.openxava.test.model.*;
import org.openxava.actions.*;
import org.openxava.hibernate.*;


/**
 * @author Mª Carmen Gimeno 
 */

public class StateHibernateSearchAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		//Query query = session.createQuery("select f from Family as f where f.oid=:oid" );	
		//query.setString("oid",getView().getValueString("oid"));
		//Family f =(Family) query.uniqueResult();
		State s = (State) XHibernate.getSession().get(State.class,getView().getValueString("id"));
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
