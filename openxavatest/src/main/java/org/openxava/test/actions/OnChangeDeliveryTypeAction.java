package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class OnChangeDeliveryTypeAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		addMessage("type=" + getNewValue());
		Delivery delivery = (Delivery) getView().getEntity();
		DeliveryType type = (DeliveryType) delivery.getType(); // The not needed cast is for compile in XML version
		String typeDescription = type==null?"":type.getDescription();
		addMessage("type.description=" + typeDescription);
	}

}
