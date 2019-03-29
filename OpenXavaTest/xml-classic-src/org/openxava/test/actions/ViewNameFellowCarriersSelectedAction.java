package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.ViewBaseAction;
import org.openxava.model.MapFacade;
import org.openxava.test.model.*;
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
		List carriersSelected = carriers.getCollectionSelectedValues();
		if (carriersSelected.isEmpty()) return;
		StringBuffer sb = new StringBuffer("");
		for (Iterator it = carriersSelected.iterator(); it.hasNext(); ) {
			Map carrierKey = (Map) it.next();
			ICarrier carrier = (ICarrier) MapFacade.findEntity(getView().getModelName(), carrierKey);
			sb.append(carrier.getName() + " ");
		}
		getView().setValue("fellowCarriersSelected", sb.toString().trim());
	}
	

}
