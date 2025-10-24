package org.openxava.chatvoice.actions;

import org.openxava.actions.*;
import org.openxava.controller.ModuleContext;
import org.openxava.tab.Tab;

public class ShowCustomerCountAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		// Obtener el Tab del módulo Customer usando ModuleContext
		ModuleContext context = getContext();
		Tab customerTab = (Tab) context.get("chatvoice", "Customer", "xava_tab");
		
		if (customerTab != null) {
			int totalSize = customerTab.getTotalSize();
			addMessage("El módulo Customer tiene " + totalSize + " registros");
		} else {
			addMessage("No se pudo acceder al Tab del módulo Customer");
		}
	}

}
