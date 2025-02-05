package org.openxava.invoicedemo.actions; // tmr Borrar paqueta cuando borremos clase

import org.openxava.actions.*;

/**
 * tmr Quitar
 * @author javi
 *
 */
public class SayByeAction extends BaseAction {

	public void execute() throws Exception {
		addWarning("Bye, bye, rebya");
	}

}
