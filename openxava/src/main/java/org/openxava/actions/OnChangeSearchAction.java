package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.model.meta.*;
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
		MetaReferenceView metaReferenceView
		= getView().getRoot().getMetaView().getMetaReferenceViewFor(getView().getMemberName());

		MetaReference metaReference = getView().getParent().getMetaReference(getView().getMemberName());
		System.out.println(metaReference.getSimpleName());


		MetaReferenceView metaReferenceView2 = getView().getMetaView().getMetaReferenceViewFor(getView().getMemberName());
		//System.out.println(metaReferenceView2.getReferenceName());

		Tab tab = new Tab();
		//tab.setRequest(getTab().getRequest());
		tab.setModelName(getView().getBaseModelName()); // for polymorphic model
		System.out.println("setModelName " + getView().getBaseModelName());
		
		setTab(tab);
		System.out.println("--");
		String tabName = metaReferenceView == null ? "" : metaReferenceView.getTabName();
		tab.setTabName(tabName);
		//String baseCondition = tab.getMetaTab().getBaseCondition();
		if (tab.getMetaTab().hasBaseCondition()) {
			System.out.println("hasBaseCondition2"); 
			System.out.println(tab.getMetaTab().getBaseCondition());
			tab.setBaseCondition("${" + getChangedMetaProperty().getName() + "} = " + getNewValue());
		    System.out.println(tab.getBaseCondition());
		    Map key = (Map) tab.getTableModel().getObjectAt(0);
			if (!getView().findObject(getChangedMetaProperty(), key)) {
				nextAction = getView().getSearchAction();
			}
		} else {
			System.out.println("original mode");
			if (!getView().findObject(getChangedMetaProperty())) {
				nextAction = getView().getSearchAction();
			}
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
