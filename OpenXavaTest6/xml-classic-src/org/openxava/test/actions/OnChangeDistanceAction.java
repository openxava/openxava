package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class OnChangeDistanceAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		Number n = (Number) getNewValue();
		int distance = n==null?0:n.intValue();
		switch (distance) {
			case 1:
				getView().setValueNotifying("vehicle", "MOTORBIKE");
				break;
			case 2:
				getView().setValueNotifying("vehicle", "CAR");
				break;
			case 3:
				getView().setValueNotifying("vehicle", "PLANE");
				break;
			default:
				getView().setValueNotifying("vehicle", "");			
		}
	}

}
