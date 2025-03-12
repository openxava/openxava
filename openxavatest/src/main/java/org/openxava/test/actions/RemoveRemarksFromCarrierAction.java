package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;
import org.openxava.test.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class RemoveRemarksFromCarrierAction extends CollectionBaseAction implements IAvailableAction { 

	private static Log log = LogFactory.getLog(RemoveRemarksFromCarrierAction.class);

	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		Map<String, Object>[] selectedOnes = getSelectedKeys();
		for (int i = 0; i < selectedOnes.length; i++) {
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", selectedOnes[i]);
			carrier.setRemarks(null);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isAvailable() {
		try {
			StaticCounter.increment();
			if (getRow() < 0) return false;			
			Map<String, Object> key = getSelectedKeys()[0];
			Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", key);
			return carrier.getRemarks() != null && !carrier.getRemarks().isEmpty();
		}
		catch (Exception ex) {
			log.error("Error checking if RemoveRemarksFromCarrierAction is available", ex);
			return false;
		}
	}
}
