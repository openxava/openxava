package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * TMR QUITAR, NO DEJAR ESTA CLASE
 * @author javi
 *
 */
public class OnChangeProductAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		/*
		System.out.println("[OnChangeProductAction.execute] getView().getModelName()=" + getView().getModelName()); // tmr
		System.out.println("[OnChangeProductAction.execute] getNewValue()=" + getNewValue()); // tmr
		System.out.println("[OnChangeProductAction.execute] getView().getValue(product.number)=" + getView().getValue("product.number")); // tmr
		View productView = getView().getSubview("product");
		Map membersNames = productView.getMembersNames();
		System.out.println("[OnChangeProductAction.execute] membersNames= " + membersNames); // tmr
		System.out.println("[OnChangeProductAction.execute] productView.getValues()> " + productView.getValues()); // tmr
		Map values = MapFacade.getValues("Product2", productView.getKeyValues(), membersNames);
		System.out.println("[OnChangeProductAction.execute] values=" + values); // tmr
		productView.setValues(values);
		// tmr getView().getSubview("product").findObject();
		
		System.out.println("[OnChangeProductAction.execute] productView.getValues()< " + productView.getValues()); // tmr
		System.out.println("[OnChangeProductAction.execute] productView.getAllValues()= " + productView.getAllValues()); // tmr
		*/
		
		
		getView().setValue("product.color.name", "Negro");
		getView().setValue("evaluation", "Mala");
		getView().getSubview("product").getSubview("color").setValue("name", "Blanco");
		getView().getRoot().setValue("evaluations.0.product.color.name", "Rojo");
		getView().getRoot().refreshCollections(); // TMR ME QUEDÉ POR AQUÍ: CON ESTO LO DE ARRIBA FUNCIONA 
		
		addMessage("Cambiado");
	}

}
