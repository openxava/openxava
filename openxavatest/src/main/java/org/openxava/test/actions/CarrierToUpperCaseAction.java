package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;
import org.openxava.test.util.*;

/**
 * Action to convert the selected items in a list to uppercase.
 * @author Javier Paniza
 */
public class CarrierToUpperCaseAction extends TabBaseAction implements IAvailableAction { 

	private static Log log = LogFactory.getLog(CarrierToUpperCaseAction.class);

	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		Map<String, Object>[] selectedOnes = getSelectedKeys();
		for (int i = 0; i < selectedOnes.length; i++) {
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", selectedOnes[i]);
			carrier.setName(carrier.getName().toUpperCase());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isAvailable() {
		StaticCounter.increment();
		try {
			if (getRow() < 0) return false;
			Map<String, Object> key = getSelectedKeys()[0];
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", key);
			return !carrier.getName().equals(carrier.getName().toUpperCase());
		}
		catch (Exception ex) {
			log.error("Error checking if CarrierToUpperCaseAction is available", ex);
			return false;
		}
	}
}
