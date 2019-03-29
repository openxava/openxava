package org.openxava.actions;

import java.util.*;

import javax.ejb.*;



import org.openxava.model.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class UpdateAction extends UpdateReferenceBaseAction  {
	
	
	
	public void execute() throws Exception {		
		try {					
			// Update
			Map key = getView().getKeyValues();			
			MapFacade.setValues(getView().getModelName(), key, getValuesToSave());
			returnsToPreviousViewUpdatingReferenceView(key);
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}
		catch (ObjectNotFoundException ex) {
			addError("no_modify_no_exists");
		}
	}
	
}
