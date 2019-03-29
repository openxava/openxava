package org.openxava.view.meta;

import java.io.*;
import java.util.*;

import org.openxava.annotations.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class MetaCollectionView extends MetaMemberView implements Serializable {
	
	private String editActionName;
	private String viewActionName; 
	private String newActionName;
	private String addActionName;  
	private String saveActionName;
	private String hideActionName;
	private String removeActionName;
	private String removeSelectedActionName;
	private Collection actionsDetailNames;
	private Collection actionsListNames;
	private Collection subcontrollersListNames;
	private Collection actionsRowNames;  
	private List propertiesListNames;
	private String collectionName;
	private String mediatorClassName;
	private String viewName;
	private boolean readOnly = false;
	private boolean editOnly = false; 
	private boolean createReference = true;
	private boolean modifyReference = true; 
	private boolean asAggregate = false;
	private String propertiesListNamesAsString;
	private Collection rowStyles; 
	private String onSelectElementActionName;
	private Tree path;
	private Map<String, List<String>> totalProperties;
	
	public void addActionDetailName(String actionName) {
		if (actionsDetailNames == null) actionsDetailNames = new ArrayList();
		actionsDetailNames.add(actionName);
	}
	
	public void addActionListName(String actionName) {
		if (actionsListNames == null) actionsListNames = new ArrayList();
		actionsListNames.add(actionName);		
	}
	
	public void addSubcontrollerListName(String subcontroller){
		if (subcontrollersListNames == null) subcontrollersListNames = new ArrayList();
		subcontrollersListNames.add(subcontroller);
	}
	
	public void addActionRowName(String actionName) { 
		if (actionsRowNames == null) actionsRowNames = new ArrayList();
		actionsRowNames.add(actionName);		
	}
	
	public void addMetaRowStyle(MetaRowStyle style) {
		if (rowStyles == null) rowStyles = new ArrayList();
		rowStyles.add(style);
	}
	
	public boolean hasRowStyles() {
		return rowStyles != null;
	}
	
	public Collection getMetaRowStyles() {
		return rowStyles==null?Collections.EMPTY_LIST:rowStyles;
	}
	
	
	public String getMediatorClassName() {
		return mediatorClassName;
	}

	public void setMediatorClassName(String mediatorClassName) {
		this.mediatorClassName = mediatorClassName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
		
	public String getViewName() {		
		return viewName;
	}
		
	public void setViewName(String string) {
		viewName = string;
	}

	public boolean hasListProperties() {		
		return propertiesListNames != null && !propertiesListNames.isEmpty();
	}
	
	public List getPropertiesListNames() {
		return propertiesListNames == null?Collections.EMPTY_LIST:propertiesListNames;
	}
	
	public String getPropertiesListNamesAsString() { 
		return propertiesListNamesAsString;
	}

	public void setPropertiesList(String listProperties) {				 		
		if (!Is.emptyString(listProperties)) {
			propertiesListNamesAsString = listProperties;
			propertiesListNames = new ArrayList();
			StringTokenizer st = new StringTokenizer(listProperties, ",;"); 
			while (st.hasMoreTokens()) {
				String name = st.nextToken().trim();
				if (name.contains("[")) {
					int idx = name.indexOf('[');
					String totalProperty = name.substring(idx).trim();
					name = name.substring(0, idx).trim();				
					List<String> totalPropertiesForName = new ArrayList<String>();
					totalPropertiesForName.add(removeSquareBrackets(totalProperty));
					while (st.hasMoreTokens() && !totalProperty.endsWith("]")){
						totalProperty = st.nextToken().trim();
						totalPropertiesForName.add(removeSquareBrackets(totalProperty));
					} 	
					
					if (totalProperties == null) totalProperties = new HashMap<String, List<String>>();
					totalProperties.put(name.replace("+", ""), Collections.unmodifiableList(totalPropertiesForName)); 
				}			
				propertiesListNames.add(name); 
			}
			totalProperties = totalProperties == null?null:Collections.unmodifiableMap(totalProperties); 
		} 
	}
		
	/**
	 * 
	 * @since 4.3
	 */
	public Map<String, List<String>> getTotalProperties() { 
		return totalProperties == null?Collections.EMPTY_MAP:totalProperties;
	}

	
	private String removeSquareBrackets(String name) { 
		return name.replaceAll("[\\[\\] ]", "");
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean b) {
		readOnly = b;
	}

	public boolean isEditOnly() {
		return editOnly;
	}

	public void setEditOnly(boolean b) {
		editOnly = b;
	}

	public Collection getActionsDetailNames() {		
		return actionsDetailNames==null?Collections.EMPTY_LIST:actionsDetailNames;
	}
	
	public Collection getActionsListNames() {		
		return actionsListNames==null?Collections.EMPTY_LIST:actionsListNames;
	}
	
	public Collection getSubcontrollersListNames() {		
		return subcontrollersListNames==null?Collections.EMPTY_LIST:subcontrollersListNames;
	}
	
	public Collection getActionsRowNames() { 		
		return actionsRowNames==null?Collections.EMPTY_LIST:actionsRowNames;
	}
	
	public String getEditActionName() {
		return editActionName;
	}
	public void setEditActionName(String editActionName) {
		this.editActionName = editActionName;
	}

	public boolean isCreateReference() {
		return createReference;
	}

	public void setCreateReference(boolean createReference) {
		this.createReference = createReference;
	}

	public String getViewActionName() {
		return viewActionName;
	}

	public void setViewActionName(String viewActionName) {
		this.viewActionName = viewActionName;
	}

	public String getHideActionName() {
		return hideActionName;
	}

	public void setHideActionName(String hideActionName) {
		this.hideActionName = hideActionName;
	}

	public String getNewActionName() {
		return newActionName;
	}

	public void setNewActionName(String newActionName) {
		this.newActionName = newActionName;
	}

	public String getRemoveActionName() {
		return removeActionName;
	}

	public void setRemoveActionName(String removeActionName) {
		this.removeActionName = removeActionName;
	}

	public String getSaveActionName() {
		return saveActionName;
	}

	public void setSaveActionName(String saveActionName) {
		this.saveActionName = saveActionName;
	}

	public boolean isAsAggregate() {
		return asAggregate;
	}

	public void setAsAggregate(boolean asAggregate) {
		this.asAggregate = asAggregate;
	}

	public boolean isModifyReference() {
		return modifyReference;		
	}

	public void setModifyReference(boolean modifyReference) {
		this.modifyReference = modifyReference;
	}

	public String getRemoveSelectedActionName() {
		return removeSelectedActionName;
	}

	public void setRemoveSelectedActionName(String removeSelectedActionName) {
		this.removeSelectedActionName = removeSelectedActionName;
	}

	public String getOnSelectElementActionName() {
		return onSelectElementActionName;
	}

	public void setOnSelectElementActionName(String onSelectElementActionName) {
		this.onSelectElementActionName = onSelectElementActionName;
	}

	public void setPath(Tree path) {
		this.path = path;
	}

	public Tree getPath() {
		return path;
	}
	public String getAddActionName() {
		return addActionName;
	}

	public void setAddActionName(String addActionName) {
		this.addActionName = addActionName;
	}

}
