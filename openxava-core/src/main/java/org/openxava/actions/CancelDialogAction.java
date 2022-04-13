package org.openxava.actions;

import org.openxava.controller.meta.*;

/**
 * 
 * @author Javier Paniza
 */

public class CancelDialogAction extends ViewBaseAction implements IChainAction { 
	
	private String nextAction;
		
	public void execute() throws Exception {
		for (MetaAction action: getManager().getMetaActions()) {
			if (action.getClassName().equals(getClass().getName())) continue;
			if (action.getName().equals("cancel") || 
				action.getName().equals("cancelar") ||
				action.getName().equals("return") ||
				action.getName().equals("volver") ||
				action.getName().equals("close") ||
				action.getName().equals("cerrar") ||				
				action.getQualifiedName().equals(getView().getHideCollectionElementAction()))
			{
				nextAction = action.getQualifiedName();
			}
		}				
		if (nextAction == null) closeDialog();
	}
	
	public String getNextAction() throws Exception {
		return nextAction;
	}

}
