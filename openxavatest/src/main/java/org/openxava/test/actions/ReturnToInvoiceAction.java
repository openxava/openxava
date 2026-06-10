package org.openxava.test.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.actions.*;


/**
 * 
 * @author Javier Paniza
 */
public class ReturnToInvoiceAction extends ViewBaseAction implements INavigationAction {

	@Inject
	private Map<String, Object> invoiceValues;
	
	public void execute() throws Exception {
		getView().setModelName("Invoice");
		getView().setValues(invoiceValues);		
	}

	public String[] getNextControllers() {				
		return DEFAULT_CONTROLLERS;
	}

	public Map getInvoiceValues() {
		return invoiceValues;
	}

	public void setInvoiceValues(Map<String, Object> map) {
		invoiceValues = map;
	}

	public String getCustomView() throws Exception {
		return DEFAULT_VIEW;
	}

}
