package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * Acción para convertir a mayúsculas los elementos seleccionados en una colección.
 * 
 * @author Javier Paniza
 */
public class ToUpperCaseCollectionAction extends CollectionBaseAction implements IAvailableAction { 

	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		Map<String, Object>[] selectedOnes = getSelectedKeys();
		for (int i = 0; i < selectedOnes.length; i++) {
			System.out.println("[ToUpperCaseCollectionAction.execute] selectedOnes[" + i + "]=" + selectedOnes[i]);
			addMessage("Hi, I'm " + selectedOnes[i]);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isAvailable() {
		try {
			System.out.println("[ToUpperCaseCollectionAction.isAvailable] getRow()=" + getRow());
			if (getRow() < 0) return true;
			
			Map<String, Object> key = getSelectedKeys()[0];
			System.out.println("ToUpperCaseCollection.isAvailable: key=" + key);
			
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", key);
			System.out.println("[ToUpperCaseCollectionAction.isAvailable] carrier.getName()=" + carrier.getName());
			
			if (carrier.getNumber() == 3) return true;
			return !carrier.getName().equals(carrier.getName().toUpperCase());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
