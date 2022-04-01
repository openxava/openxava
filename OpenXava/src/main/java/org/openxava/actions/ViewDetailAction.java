package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.tab.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class ViewDetailAction extends TabBaseAction implements IChainAction, IModelAction {
	private static Log log = LogFactory.getLog(ViewDetailAction.class);
	
	@Inject  
	private int row = Integer.MIN_VALUE; 
	private boolean explicitRow = false;
	private int increment;
	private Map key;	
	private boolean goFirst = false;
	private String nextAction;
	private boolean atListBegin;
	private boolean noElementsInList;
	private String model;
	@Inject
	private Tab mainTab;
	
	private Map nextKey = null;	// si viene de deleteAction el contenido de selectedKeys varia y no podemos saber desde dónde estabamos partiendo
	private boolean deleteAllSelected = false;
	
	@Override
	protected Tab getTab() throws XavaException {
		return getMainTab() != null ? getMainTab() : super.getTab();
	}

	public void execute() throws Exception {
		getView().setModelName(model); 
		getView().setViewName(getManager().getXavaViewName()); 
		setAtListBegin(false);
		setNoElementsInList(false);				
		int previous = -1;
		
		Map [] selectedOnes = getTab().getSelectedKeys();
		
		if (!Is.empty(nextKey)) key = nextKey;
		else if (isDeleteAllSelected()) key = null;
		else if (!explicitRow && selectedOnes != null && selectedOnes.length > 0){	// hay seleccionados y no hay fila específica
			// buscamos la clave actual y la situamos en el array, después buscaremos según el increment
			Map keyActual = getView().getKeyValues();
			List l = Arrays.asList(selectedOnes);
			if (isGoFirst()) {
				key = (Map) l.get(0);
			}
			else{
				int index = 0;
				if (Is.empty(keyActual)) index = row;
				else index = l.indexOf(keyActual);
				if (increment < 0 && index == 0){
					setAtListBegin(true);
					addError("at_list_begin");			
					return;
				}
				index = index + increment;
				if (l.size() == index) key = null;	// last element
				else key = (Map)l.get(index);
				row = index; // We use row to store the last index
			}
		}
		else{	// no hay seleccionados o hay fila específica
			if (increment < 0 && row == 0) {
				setAtListBegin(true);
				addError("at_list_begin");			
				return;
			}		
			
			previous = row;
			row = goFirst?0:row + increment;
			
			if (row < 0) row = 0; 
			key = (Map) getTab().getTableModel().getObjectAt(row);
		}
		if (key == null) {
			setNoElementsInList(true);
			addError("no_list_elements");
			// row = previous;
			if (previous >= 0) row = previous;
		}		
		if (key != null) {		
			getView().setValues(key);									
		}	
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		// If row is injected twice, the second time is an explict row assignament
		//   that is, not from xava_row session object but using row=7 in action call
		if (row != Integer.MIN_VALUE) explicitRow = true;   
		row = i;		
	}
	
	public String getNextAction() throws XavaException {
		if (Is.emptyString(nextAction)) { 
			return getEnvironment().getValue("XAVA_SEARCH_ACTION");
		} 		
		return nextAction;
	}
	
	public void setNextAction(String string) {
		nextAction = string;
	}
	
	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int i) {
		increment = i;
	}

	public boolean isGoFirst() {
		return goFirst;
	}

	public void setGoFirst(boolean b) {
		goFirst = b;
	}


	public boolean isNoElementsInList() {
		return noElementsInList;
	}

	public boolean isAtListBegin() {
		return atListBegin;
	}

	private void setNoElementsInList(boolean b) {
		noElementsInList = b;
	}

	private void setAtListBegin(boolean b) {
		atListBegin = b;
	}

	public void setModel(String modelName) { 
		this.model = modelName;		
	}

	public Tab getMainTab() {		
		return mainTab;
	}

	public void setMainTab(Tab mainTab) {
		this.mainTab = mainTab;
	}

	public Map getNextKey() {
		return nextKey;
	}

	public void setNextKey(Map nextKey) {
		this.nextKey = nextKey;
	}

	public boolean isDeleteAllSelected() {
		return deleteAllSelected;
	}

	public void setDeleteAllSelected(boolean deleteAllSelected) {
		this.deleteAllSelected = deleteAllSelected;
	}	

}