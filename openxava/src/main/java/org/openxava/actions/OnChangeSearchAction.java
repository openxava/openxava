package org.openxava.actions;

import javax.inject.*;

import org.openxava.tab.*;
import org.openxava.view.*;
import org.openxava.view.meta.*;

/**
 * The default action to execute for search a reference when the
 * user types the value. <p>
 * 
 * @author Javier Paniza
 */
public class OnChangeSearchAction extends OnChangePropertyBaseAction implements IChainActionWithArgv { 
	
	@Inject		
	private Tab tab;	
	private String tabName = "";
	private String nextAction; 
	
	public void execute() throws Exception {
		System.out.println("OnChangeSearchAction");
		MetaReferenceView metaReferenceView
		= getView().getRoot().getMetaView().getMetaReferenceViewFor(getView().getMemberName());

		Tab tab = new Tab();
		tab.setRequest(getTab().getRequest());
		setTab(tab);
		getTab().setModelName(getView().getModelName());
		getTab().setTabName(metaReferenceView.getTabName());
		System.out.println(getTab().getMetaTab().getBaseCondition());
		if (!getView().findObject(getChangedMetaProperty())) {
			nextAction = getView().getSearchAction();
		}
	}

	public String getNextAction() throws Exception {
		return nextAction;
	}

	public String getNextActionArgv() throws Exception {
		String keyProperty = getView().getMemberName() + "." + getChangedProperty();
		View parent = getView().getParent();
		if (parent != null && parent.isRepresentsElementCollection()) { 
			keyProperty = parent.getMemberName() + "." + parent.getCollectionEditingRow() + "." + keyProperty;
		}
		return "keyProperty=" + keyProperty;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	

}
