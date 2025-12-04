package org.openxava.chatvoice.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;

@SuppressWarnings("unchecked")
public class ShowInvoiceValuesAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
        // TMR ME QUEDÉ POR AQUÍ. VIENDO COMO OBTENER LOS VALORES DE UNA ENTIDAD, QUIZAS CON UN OBJETO VIEW
        System.out.println("ShowInvoiceValuesAction.execute() getView().getMembersNames()=" + getView().getMembersNames()); // tmr
		Map<String, Object> values = MapFacade.getValues("Invoice", getView().getKeyValues(), getView().getMembersNames());
		System.out.println("Invoice values: " + values);
	}

}
