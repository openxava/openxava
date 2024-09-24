package org.openxava.actions;

import org.openxava.view.*;

/**
 * The default action to execute for search a reference when the
 * user types the value. <p>
 * 
 * @author Javier Paniza
 */
public class OnChangeSearchAction extends OnChangePropertyBaseAction implements IChainActionWithArgv { 
	
	private String nextAction; 
	
	public void execute() throws Exception {
		System.out.println("ex");
		System.out.println(getChangedMetaProperty());
		System.out.println(getView().findObject(getChangedMetaProperty()));
		if (!getView().findObject(getChangedMetaProperty())) {
			nextAction = getView().getSearchAction();
		}
	}

	public String getNextAction() throws Exception {
		return nextAction;
	}

	public String getNextActionArgv() throws Exception {
		System.out.println("getNextActionArgv");
		String keyProperty = getView().getMemberName() + "." + getChangedProperty();
		System.out.println(keyProperty);
		View parent = getView().getParent();
		if (parent != null && parent.isRepresentsElementCollection()) { 
			keyProperty = parent.getMemberName() + "." + parent.getCollectionEditingRow() + "." + keyProperty;
		}
		return "keyProperty=" + keyProperty;
	}

}
