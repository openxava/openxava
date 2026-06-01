package org.openxava.test.actions;

import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.model.*;


/**
 * @author Javier Paniza
 */
public class SearchInvoiceFromDeliveryAction extends ViewBaseAction {
	
	public void execute() throws Exception {		 
		try {			
			Map<String, Object> key = (Map<String, Object>) getView().getKeyValues().get("invoice");		
			getView().setModelName("Invoice");			
			Map<String, Object> membersNames = getView().getMembersNames();
			Map<String, Object> values = MapFacade.getValues(getModelName(), key, membersNames);
			getView().setValues(values);			
			getView().setKeyEditable(false);
			getView().setEditable(true);
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
