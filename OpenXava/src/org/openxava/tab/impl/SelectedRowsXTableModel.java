package org.openxava.tab.impl;

import java.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class SelectedRowsXTableModel extends XTableModelDecoratorBase {
	
	private int [] selectedRows;

	public SelectedRowsXTableModel(IXTableModel toDecorate, int [] selectedRows) {
		super(toDecorate);
		this.selectedRows = selectedRows == null?new int[0]:selectedRows; 
	}
		
	public int getRowCount() {
		return selectedRows.length;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return super.getValueAt(selectedRows[rowIndex], columnIndex);
	}
}
