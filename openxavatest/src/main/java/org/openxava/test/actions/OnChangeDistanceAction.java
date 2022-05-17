package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;


/**
 * @author Javier Paniza
 */
public class OnChangeDistanceAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		Delivery.Distance distance = (Delivery.Distance) getNewValue();
		if (distance == null) {
			getView().setValueNotifying("vehicle", "");
		}
		else switch (distance) {
			case LOCAL:
				getView().setValueNotifying("vehicle", "MOTORBIKE");
				break;
			case NATIONAL:
				getView().setValueNotifying("vehicle", "CAR");
				break;
			case INTERNATIONAL:
				getView().setValueNotifying("vehicle", "PLANE");
				break;	
		}
	}

}
