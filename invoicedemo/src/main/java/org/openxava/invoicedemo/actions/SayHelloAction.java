package org.openxava.invoicedemo.actions; // tmr Borrar paqueta cuando borremos clase

import org.openxava.actions.*;

/**
 * tmr Quitar
 * @author javi
 *
 */
public class SayHelloAction extends BaseAction {

	public void execute() throws Exception {
		addMessage("Hello");
	}

}
