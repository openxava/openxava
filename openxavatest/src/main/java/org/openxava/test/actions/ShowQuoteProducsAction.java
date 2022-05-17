package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javir Paniza
 */
public class ShowQuoteProducsAction extends ViewBaseAction {

	public void execute() throws Exception {
		Quote quote = (Quote) getView().getEntity();
		StringBuffer products = new StringBuffer();
		for (QuoteDetail detail: quote.getDetails()) {
			if (products.length() > 0) products.append(", ");
			products.append(detail.getProduct().getDescription());
		}
		addMessage(products.toString());
	}

}
