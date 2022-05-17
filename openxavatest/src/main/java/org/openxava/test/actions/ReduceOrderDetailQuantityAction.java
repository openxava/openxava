package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class ReduceOrderDetailQuantityAction extends CollectionBaseAction {

	public void execute() throws Exception {
		if (getRow() < 0) return;
		OrderDetail detail = (OrderDetail) getSelectedObjects().get(0);
		detail.setQuantity(detail.getQuantity() - 1);
		addMessage("The order detail quantity is " + detail.getQuantity());		
	}

}
