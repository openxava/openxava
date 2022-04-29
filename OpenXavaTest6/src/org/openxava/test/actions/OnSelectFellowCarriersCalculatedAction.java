package org.openxava.test.actions;

import org.openxava.actions.OnSelectElementBaseAction;
import org.openxava.test.model.*;

/**
 * Create on 11/06/2009 (9:33:51)
 * @autor Ana Andr�s
 */
public class OnSelectFellowCarriersCalculatedAction extends OnSelectElementBaseAction {

	public void execute() throws Exception {
		int size = getView().getValueInt("fellowCarriersCalculatedSize");
		size = isSelected() ? size + 1 : size - 1;
		getView().setValue("fellowCarriersCalculatedSize", new Integer(size));
		
		StringBuffer selectedOnes = new StringBuffer();
		for (Object ocarrier: getSelectedObjects()) { // To test getSelectedObjects()
			Carrier carrier = (Carrier) ocarrier;
			selectedOnes.append(carrier.getNumber());
			selectedOnes.append(' ');
		}		
		addMessage("selected_carriers", selectedOnes); 		
	}

}
