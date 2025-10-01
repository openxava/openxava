package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.tab.*;
import org.openxava.util.*;
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
	private String nextAction; 
	
	public void execute() throws Exception {
		if (isNewValueEmpty()) {
			getView().clear();
			return; 
		}
		MetaReferenceView metaReferenceView = getView().getRoot().getMetaView().getMetaReferenceViewFor(getView().getMemberName());
		Tab tab = new Tab();
		tab.setRequest(getTab().getRequest());
		tab.setModelName(getView().getBaseModelName());
		setTab(tab);
		String tabName = metaReferenceView == null ? "" : metaReferenceView.getTabName();
		tab.setTabName(tabName);
		if (tab.getMetaTab().hasBaseCondition()) {
			Object value = getChangedMetaProperty().isNumber() 
							|| getChangedMetaProperty().getCMPTypeName().contains("Boolean") 
							? getNewValue() 
							: "'" + getNewValue() + "'";
			tab.setBaseCondition("${" + getChangedMetaProperty().getName() + "} = " + value);
			Map keys = (Map) tab.getTableModel().getObjectAt(0);
			if (!getView().findObject(getChangedMetaProperty(), keys)) {
				nextAction = getView().getSearchAction();
			}
		} else {
			if (!getView().findObject(getChangedMetaProperty())) {
				nextAction = getView().getSearchAction();
			}
		}
		
	}

	private boolean isNewValueEmpty() {
		Object value = getNewValue();
		if (value == null) return true;
		if (value instanceof String) {
			return Is.emptyString((String)value);
		}
		return false;
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

}
