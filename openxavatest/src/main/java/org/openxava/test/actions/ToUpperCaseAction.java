package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * tmr En collecciones? en colecciones calculadas?
 * @author Javier Paniza
 */
public class ToUpperCaseAction extends TabBaseAction implements IAvailableAction { 

	@Override
	public void execute() throws Exception {
		Map [] selectedOnes = getSelectedKeys();
		for (int i=0; i<selectedOnes.length; i++) {
			System.out.println("[ToUpperCaseAction.execute] selectedOnes[" + i + "]=" + selectedOnes[i]); // tmr
			addMessage("Hi, I'm " + selectedOnes[i]);
		}
	}

	@Override
	public boolean isAvailable() {
		try {
			System.out.println("[ToUpperCaseAction.isAvailable] getRow()=" + getRow()); // tmr
			if (getRow() < 0) return true;
			System.out.println("ToUpperCase.isAvailable 10"); // tmr
			Map key = getSelectedKeys()[0];
			System.out.println("ToUpperCase.isAvailable 20: key=" + key); // tmr
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", key);
			System.out.println("ToUpperCase.isAvailable 30"); // tmr
			System.out.println("[ToUpperCaseAction.isAvailable] carrier.getName()=" + carrier.getName()); // tmr
			if (carrier.getNumber() == 3) return true;
			System.out.println("ToUpperCase.isAvailable 40"); // tmr
			return !carrier.getName().equals(carrier.getName().toUpperCase());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
