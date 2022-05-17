package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class SearchDeliveryAction extends SearchByViewKeyAction {
			
	public void execute() throws Exception {		
		super.execute();
		if (!Is.emptyString(getView().getValueString("employee"))) {
			getView().setValue("deliveredBy", new Integer(1));
			getView().setHidden("carrier", true);
			getView().setHidden("employee", false);
		}		
		else {
			Map carrier = (Map) getView().getValue("carrier");
			if (!(carrier == null || carrier.isEmpty())) {
				getView().setValue("deliveredBy", new Integer(2));
				getView().setHidden("carrier", false);
				getView().setHidden("employee", true); 
			}
			else {
				getView().setHidden("carrier", true);
				getView().setHidden("employee", true);							
			}
		}
	}

}
