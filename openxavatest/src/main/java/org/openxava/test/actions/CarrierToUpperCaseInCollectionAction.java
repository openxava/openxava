package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * Acción para convertir a mayúsculas los elementos seleccionados en una colección.
 * 
 * @author Javier Paniza
 */
public class CarrierToUpperCaseInCollectionAction extends CollectionBaseAction implements IAvailableAction { 

	private static Log log = LogFactory.getLog(CarrierToUpperCaseInCollectionAction.class);

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
		try {
			System.out.println("[CarrierToUpperCaseInCollectionAction.isAvailable] getRow()=" + getRow());
			if (getRow() < 0) return true;
			
			Map<String, Object> key = getSelectedKeys()[0];
			System.out.println("CarrierToUpperCaseInCollection.isAvailable: key=" + key);
			
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", key);
			System.out.println("[CarrierToUpperCaseInCollectionAction.isAvailable] carrier.getName()=" + carrier.getName());
			
			if (carrier.getNumber() == 3) return true;
			return !carrier.getName().equals(carrier.getName().toUpperCase());
		}
		catch (Exception ex) {
			log.error("Error checking if CarrierToUpperCaseInCollectionAction is available", ex);
			return false;
		}
	}
}
