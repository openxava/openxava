package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.tab.*;

/**
 * 
 * @author Javier Paniza 
 */
public class SyncCarriersSelectionAction extends OnSelectElementBaseAction {
	private static Log log = LogFactory.getLog(SyncCarriersSelectionAction.class);
	
	public void execute() throws Exception {
		Tab targetTab = getView().getSubview("fellowCarriers").getCollectionTab();
		
		boolean oldSync = ((Boolean)getView().getValue("oldSync")).booleanValue();
		
		if (oldSync) oldImplementation(targetTab);
		else newImplementation(targetTab);
	}
	
	private void oldImplementation(Tab targetTab) throws Exception{
		int [] selected = targetTab.getSelected();		
		if (isSelected()) { 
			targetTab.setAllSelected(ArrayUtils.add(selected, getRow()));
		}
		else {
			targetTab.setSelected(ArrayUtils.removeElement(selected, getRow())); // It would be setAllSelected, but we use setSelected to test both methods
		}
	}
	
	private void newImplementation(Tab targetTab) throws Exception{
		Map [] selected = targetTab.getSelectedKeys();
		
		if (isSelected()) {
			Map newKey = (Map) targetTab.getTableModel().getObjectAt(getRow());
			targetTab.setAllSelectedKeys((Map[])ArrayUtils.add(selected, newKey));
		}
		else {
			targetTab.deselect(getRow());
		}
	}
	
}
