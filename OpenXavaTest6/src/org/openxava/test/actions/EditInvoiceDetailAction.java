package org.openxava.test.actions;

import java.text.*;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class EditInvoiceDetailAction extends EditElementInCollectionAction {

	public void execute() throws Exception {
		super.execute();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		getCollectionElementView().setValue("remarks", "Edit at " + df.format(new java.util.Date()));
	}

}
