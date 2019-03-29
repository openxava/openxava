package org.openxava.actions;

import org.openxava.tab.impl.*;

/**
 * @since 5.7
 * @author Javier Paniza
 */
public class LoadMoreCardsAction extends TabBaseAction {

	public void execute() throws Exception {
		IXTableModel tableModel = getTab().getTableModel();
		tableModel.getValueAt(tableModel.getRowCount(), 0); 
	}

}
