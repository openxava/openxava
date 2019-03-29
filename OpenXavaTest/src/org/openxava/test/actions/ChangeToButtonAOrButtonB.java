package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * Create on 11/04/2013 (12:39:39)
 * @author Ana Andres
 */
public class ChangeToButtonAOrButtonB extends BaseAction{

	private String button = "";
	
	public void execute() throws Exception {
		removeActions("Product5.goA");
		removeActions("Product5.goB");
		
		if ("a".equals(getButton())) addActions("Product5.goB");
		else addActions("Product5.goA");
	}

	public String getButton() {
		return button;
	}

	public void setButton(String button) {
		this.button = button;
	}

}
