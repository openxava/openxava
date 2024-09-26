package org.openxava.actions;

import java.util.*;
import java.util.regex.*;

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
		System.out.println("OnChangeSearchAction");
		MetaReferenceView metaReferenceView
		= getView().getRoot().getMetaView().getMetaReferenceViewFor(getView().getMemberName());
		Tab tab = new Tab();
		tab.setRequest(getTab().getRequest());
		setTab(tab);
		getTab().setModelName(getView().getModelName());
		getTab().setTabName(metaReferenceView.getTabName());
		String baseCondition = getTab().getMetaTab().getBaseCondition().toLowerCase();
		System.out.println(baseCondition == null);
		if (baseCondition != null) {
			baseCondition = baseCondition.split("\\bwhere\\b|\\border by\\b", 2)[0].trim();
			Map<String, Object> conditionMap = new HashMap<>();
			Pattern pattern = Pattern.compile("\\$\\{(\\w+)}\\s*([=><!]+)\\s*(\\S+)");
	        Matcher matcher = pattern.matcher(baseCondition);
	        while (matcher.find()) {
	            String key = matcher.group(1); 
	            Object value = matcher.group(3);
	            System.out.println("---");
	            MetaProperty m = getView().getMetaModel().getMetaProperty(key);
	            System.out.println(m.getCMPTypeName());
	            conditionMap.put(key, Integer.parseInt(value.toString()));
	        }
	        System.out.println("conditionMap");
	        System.out.println(conditionMap);
			if (!getView().findObject(getChangedMetaProperty(), conditionMap)) {
				nextAction = getView().getSearchAction();
			}
		} else {
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
