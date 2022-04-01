package org.openxava.actions;

/**
 * 
 * @author Javier Paniza 
 */

public class SumColumnAction extends TabBaseAction {
	
	private String property;

	public void execute() throws Exception {
		getTab().addSumProperty(property);
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
