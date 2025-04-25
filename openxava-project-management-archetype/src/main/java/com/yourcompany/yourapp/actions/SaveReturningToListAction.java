package com.yourcompany.yourapp.actions;

import org.openxava.actions.*;

public class SaveReturningToListAction extends SaveAction {
	
	public String getNextMode() {
		return getErrors().contains()?DETAIL:LIST;
	}

}
