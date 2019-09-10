package org.openxava.actions;

import java.util.*;

import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

abstract public class GenerateIdForPropertyBaseAction extends ViewBaseAction {
	
	protected String generateIdForProperty(String property) throws Exception { 
		String oid = getView().getValueString(property);
		if (Is.emptyString(oid)) {
			UUIDCalculator cal = new UUIDCalculator();  
			oid = (String) cal.calculate();
			getView().setValue(property, oid);
			if (!getView().isKeyEditable()) { // Modifying
				updateOidInObject(property, oid);
			}
		}
		return oid;
	}

	private void updateOidInObject(String property, String oid) throws Exception { 
		Map values = new HashMap();
		values.put(property, oid);
		MapFacade.setValuesNotTracking(getView().getModelName(), getView().getKeyValues(), values); 
	}


}
