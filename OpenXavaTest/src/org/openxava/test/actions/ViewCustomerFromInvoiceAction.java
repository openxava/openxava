package org.openxava.test.actions;

import java.util.*;
import javax.ejb.*;
import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ViewCustomerFromInvoiceAction extends ViewBaseAction { 

	public void execute() throws Exception {				
		try {			
			Object number = getView().getValue("customer.number");
			Map key = new HashMap();
			key.put("number", number);
			showNewView();
			getView().setModelName("Customer");
			getView().setValues(key);
			getView().findObject();			
			getView().setKeyEditable(false);
			getView().setEditable(false);
			setControllers("Return");
		}
		catch (ObjectNotFoundException ex) {
			getView().clear();
			addError("object_not_found");
		}			
		catch (Exception ex) {
			ex.printStackTrace();
			addError("system_error");			
		}								
	}

}
