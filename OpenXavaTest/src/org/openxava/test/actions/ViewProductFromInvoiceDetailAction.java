package org.openxava.test.actions;

import java.util.*;
import javax.ejb.*;
import javax.inject.*;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class ViewProductFromInvoiceDetailAction
	extends CollectionElementViewBaseAction 
	implements INavigationAction { 

	@Inject
	private Map invoiceValues;

	public void execute() throws Exception {
		try {			
			setInvoiceValues(getView().getValues());
			Object number = getCollectionElementView().getValue("product.number");
			Map key = new HashMap();
			key.put("number", number);
			getView().setModelName("Product");			
			getParentView().setValues(key); // It's possible to use getParentView() as
			getView().findObject();			// alternative to getView()
			getView().setKeyEditable(false);
			getView().setEditable(false);
			closeDialog(); // Because getView() is the parent view, so we need to
							// close the dialog in order to see it
		}
		catch (ObjectNotFoundException ex) {
			getView().clear();
			addError("object_not_found");
		}			
		catch (Exception ex) {
			ex.printStackTrace();
			addError("system_error");			
		}		
	}

	public String[] getNextControllers() {		
		return new String [] { "ProductFromInvoice" };
	}

	public String getCustomView() {
		return SAME_VIEW;
	}

	public Map getInvoiceValues() {
		return invoiceValues;
	}

	public void setInvoiceValues(Map map) {
		invoiceValues = map;
	}

}
