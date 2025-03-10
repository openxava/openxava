package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * Acción para convertir a mayúsculas los elementos seleccionados en una lista.
 * @author Javier Paniza
 */
public class CarrierToUpperCaseInListAction extends TabBaseAction implements IAvailableAction { 

	private static Log log = LogFactory.getLog(CarrierToUpperCaseInListAction.class);

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
		// tmr Verificar las veces que se llama?
		try {
			if (getRow() < 0) return false;
			Map<String, Object> key = getSelectedKeys()[0];
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", key);
			return !carrier.getName().equals(carrier.getName().toUpperCase());
		}
		catch (Exception ex) {
			log.error("Error checking if CarrierToUpperCaseInListAction is available", ex);
			return false;
		}
	}
}
