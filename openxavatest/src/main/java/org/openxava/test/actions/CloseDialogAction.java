package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Chungyen Tsai
 */
public class CloseDialogAction extends ViewBaseAction {

	String quantity;
	
	public void execute() throws Exception {
		int q = Integer.valueOf(quantity);
		while (q > 0) {
			closeDialog();
			q--;
		}
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

}
