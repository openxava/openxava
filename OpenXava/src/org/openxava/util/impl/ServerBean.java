package org.openxava.util.impl;

import javax.ejb.*;



import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.calculators.*;
import org.openxava.ejbx.*;
import org.openxava.hibernate.*;
import org.openxava.util.*;


public class ServerBean extends SessionBase {
	
	private static Log log = LogFactory.getLog(ServerBean.class);
	
	public void ejbCreate() throws CreateException {
	}
	
	
	public Object calculate(ICalculator calculator) throws Exception {
		return calculate(calculator, true);
	}	
	
	public Object calculateWithoutTransaction(ICalculator calculator) throws Exception {
		XSystem._setOnServer(); // to secure it	
		return calculate(calculator, false);
	}
	
	private Object calculate(ICalculator calculator, boolean cmt) throws Exception {		
		XSystem._setOnServer(); // to secure it
		if (calculator instanceof IJDBCCalculator) {			
			((IJDBCCalculator) calculator).setConnectionProvider(getPortableContext());
		}
		boolean alreadyCMT = XHibernate.isCmt(); 
		try {
			if (cmt) XHibernate.setCmt(true);
			return calculator.calculate();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
		finally {
			if (!alreadyCMT && cmt) XHibernate.setCmt(false);
		}
	}	

	
	public IRemoteAction execute(IRemoteAction action) throws Exception {
		XSystem._setOnServer(); // to secure it
		try {
			XHibernate.setCmt(true);
			action.execute();
			return action;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			getSessionContext().setRollbackOnly();									
			throw ex;
		}
		finally {
			XHibernate.setCmt(false);
		}
	}	
			
}