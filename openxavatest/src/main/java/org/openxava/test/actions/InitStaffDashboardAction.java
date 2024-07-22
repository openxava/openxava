package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * tmr ¿Hacer algo genérico? Podría haber incluso un controlador genérico 
 * @author Javier Paniza
 *
 */

public class InitStaffDashboardAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setModel(new StaffDashboard());		
	}

}
