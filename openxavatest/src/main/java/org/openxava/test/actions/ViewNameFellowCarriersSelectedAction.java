package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.ViewBaseAction;
import org.openxava.model.MapFacade;
import org.openxava.test.model.Carrier;
import org.openxava.view.View;

/**
 * Create on 07/04/2008 (11:10:52)
 * 
 * @autor Ana Andrés
 */
public class ViewNameFellowCarriersSelectedAction extends ViewBaseAction{
	private static Log log = LogFactory.getLog(ViewNameFellowCarriersSelectedAction.class);

	public void execute() throws Exception {
		View carriers = getView().getSubview("fellowCarriersCalculated");
		List<Map<String, Object>> carriersSelected = carriers.getCollectionSelectedValues();
		if (carriersSelected.isEmpty()) return;
		StringBuffer sb = new StringBuffer("");
		for (Iterator<Map<String, Object>> it = carriersSelected.iterator(); it.hasNext(); ) {
			Map<String, Object> carrierKey = it.next();
			Carrier carrier = (Carrier) MapFacade.findEntity(getView().getModelName(), carrierKey);
			sb.append(carrier.getName() + " ");
		}
		getView().setValue("fellowCarriersSelected", sb.toString().trim());
	}
	

}
