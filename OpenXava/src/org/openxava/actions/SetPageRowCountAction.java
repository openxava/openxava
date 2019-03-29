package org.openxava.actions;

/**
 * 
 * @author Javier Paniza 
 */

public class SetPageRowCountAction extends TabBaseAction {
	
	private int rowCount;
	
	public void execute() throws Exception {
		getTab().setPageRowCount(getRowCount());	
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getRowCount() {
		return rowCount;
	}

}
