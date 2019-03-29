package org.openxava.web.dwr;

/**
 * 
 * @author Javier Paniza
 */

public class StrokeAction {

	private String name;
	private String confirmMessage; 
	private boolean takesLong;
	
	
	public StrokeAction(String name, String confirmMessage, boolean takesLong) {
		this.name = name;
		this.confirmMessage = confirmMessage; 
		this.takesLong = takesLong;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getConfirmMessage() {
		return confirmMessage;
	}

	public void setConfirmMessage(String confirmMessage) {
		this.confirmMessage = confirmMessage;
	}	
	public boolean isTakesLong() {
		return takesLong;
	}
	public void setTakesLong(boolean takesLong) {
		this.takesLong = takesLong;
	}
		
}
