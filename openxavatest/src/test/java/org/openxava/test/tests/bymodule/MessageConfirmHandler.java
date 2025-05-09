package org.openxava.test.tests.bymodule;

import static org.junit.Assert.assertEquals;

import org.htmlunit.*;

class MessageConfirmHandler implements ConfirmHandler { 
	
	private boolean confirm = true;
	
	private String message;
	
	public MessageConfirmHandler() {			
	}
	
	public MessageConfirmHandler(boolean confirm) {
		this.confirm = confirm;
	}
	
	public boolean handleConfirm(Page page, String message) {
		this.message = message; 
		return confirm;
	}
	
	public void assertNoMessage() {
		assertEquals(null, message);
		message = null;
	}
	
	public void assertMessage() {
		assertEquals("You will lose all changes made since your last save. Do you want to continue?", message);
		message = null;
	}

}
