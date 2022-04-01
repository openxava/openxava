package org.openxava.actions;

import java.util.*;

import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class GenerateIdForPropertyBaseAction extends ViewBaseAction {
	
	protected String generateIdForProperty(String property) throws Exception { 
		String id = getView().getValueString(property);
		if (Is.emptyString(id)) {
			UUIDCalculator cal = new UUIDCalculator();  
			id = (String) cal.calculate();
			getView().setValue(property, id);
			if (!getView().isKeyEditable()) { // Modifying
				updateIdInObject(property, id);
			}
		}
		return id;
	}

	private void updateIdInObject(String property, String id) throws Exception { 
		Map values = new HashMap();
		Maps.putValueFromQualifiedName(values, property, id);
		MapFacade.setValuesNotTracking(getView().getModelName(), getView().getKeyValues(), values); 
	}


}
