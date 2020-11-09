package org.openxava.view;

import java.math.*;
import java.util.*;
import java.util.Collections;
import java.util.prefs.*;
import java.util.stream.*;

import javax.ejb.*;
import javax.servlet.http.*;

import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.annotations.*;
import org.openxava.application.meta.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.filters.*;
import org.openxava.mapping.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.view.meta.*;
import org.openxava.web.*;
import org.openxava.web.DescriptionsLists;
import org.openxava.web.meta.*;

/**
 * Session object to manage a view based in maps,
 * hence suitable for OpenXava
 *
 * @author Javier Paniza
 */

public class View implements java.io.Serializable {
		
	private final static String COLUMN_WIDTH = "collectionColumnWidth.";
	private final static String FRAME_CLOSED = "frameClosed.";
	private final static String SUM_PROPERTIES = "sumProperties"; 
	private final static String COLLECTION_VIEW_ACTION = "Collection.view"; 
	private final static String COLLECTION_EDIT_ACTION = "Collection.edit"; 
	
	private static Log log = LogFactory.getLog(View.class);
	private static final long serialVersionUID = -7582669617830655121L;
	private static Collection defaultListActionsForTabCollections;
	private static Collection defaultRowActionsForTabCollections;	
	private static Collection defaultListActionsForCollectionsFromModel;
	private static Collection defaultRowActionsForCollectionsFromModel;	
	private static Object refiner;
	private static Object polisher; 
	
	private String viewObject;  
	private String propertyPrefix; 
	private Map objects = null; 	
	private String editCollectionElementAction;
	private String viewCollectionElementAction;
	private String addCollectionElementAction;  
	private String newCollectionElementAction;
	private String saveCollectionElementAction;
	private String hideCollectionElementAction;
	private String removeCollectionElementAction;
	private String removeSelectedCollectionElementsAction;
	private String onSelectCollectionElementAction; 
	private Collection subcontrollersNamesList; 
	private boolean focusForward;
	private String focusPropertyId;
	private String focusCurrentId; 
	private Map membersNamesWithHidden;
	private Map groupsViews;
	private Collection membersNamesInGroup;
	private Map collectionMemberNames;
	private static int nextOid = 0;	
	private int collectionEditingRow = -1;
	private boolean searchingObject;
	private Collection membersNamesWithoutSections;
	private Collection membersNamesWithoutSectionsAndCollections; 
	private View parent;
	private View realParent; 
	private List<MetaProperty> metaPropertiesList;
	private boolean knowIfDisplayDetailInCollection;
	private boolean displayDetailInCollection;
	private String lastPropertyKeyName;
	private String nameOflastPropertyMarkedAsSearchKey;
	private Map<String, View> subviews; 
	private Set hiddenMembers;		
	private int oid;
	private List metaProperties;
	private Collection metaPropertiesQualified;
	private Map calculatedPropertiesNames;
	private Map mapStereotypesProperties;
	private Map membersNames;
	private MetaModel metaModel;
	private Collection metaMembers;
	private Map values; 
	private MetaView metaView;
	private boolean keyEditable = true;
	private boolean editable = true;
	private boolean representsAggregate;
	private boolean representsEntityReference;
	private boolean representsCollection;
	private String modelName;
	private String viewName;
	private boolean subview;
	private boolean section;	
	private boolean group;
	private boolean collectionDetailVisible = false;
	private Messages messages; 
	private Messages errors;
	private Set notEditableMembersNames;
	private transient HttpServletRequest request;
	private Collection depends;
	private boolean hasToSearchOnChangeIfSubview = true;
	private Map<MetaView, View> sectionsViews;
	private int activeSection;
	private String memberName;
	private boolean collectionMembersEditables; 
	private boolean collectionEditable;
	private boolean collectionEditableFixed;
	private Collection actionsNamesDetail;
	private Collection actionsNamesList;
	private Collection actionsNamesRow; 
	private int [] listSelected;
	private boolean readOnly; // Always not editable, marked from xml
	private boolean onlyThrowsOnChange; 
	private Collection metaPropertiesIncludingSections;
	private Collection metaPropertiesIncludingGroups;
	private Collection metaMembersIncludingGroups; 
	private Collection metaMembersIncludingHiddenKey;
	private Collection<MetaMember> metaMembersIncludingCollectionTotals; 
	private Map labels;
	private Collection executedActions;	
	private boolean registeringExecutedActions = false;
	private Tab collectionTab; 	
	private String propertiesListNames;
	private Collection rowStyles; // Of type MetaRowStyle
	private Map oldValues; 
	private boolean mustRefreshCollection; 
	private Map changedPropertiesActionsAndReferencesWithNotCompositeEditor;
	private Map changedLabels; 
	private boolean sectionChanged;
	private boolean reloadNeeded;
	private boolean oldEditable; 
	private boolean oldKeyEditable;
	private String changedProperty;
	private Collection formattedProperties;
	private boolean refreshDescriptionsLists;
	private Collection oldNotEditableMembersNames;
	private boolean defaultListActionsForCollectionsIncluded = true;
	private boolean defaultRowActionsForCollectionsIncluded = true; 
	private String title; 
	private String titleId; 
	private Object [] titleArguments;
	private Collection fullOrderActionsNamesList;
	private Collection fullOrderActionsNamesRow; 
	private Map collectionTotals; 
	private int collectionTotalsCount = -1;
	private Collection<MetaProperty> recalculatedMetaProperties;
	private List<Map<String, Object>> collectionValues; 
	private Map<String, Collection<String>> changedActionsByProperty = null; 
	private Collection propertiesWithChangedActions;
	private Object model;
	private List sections;
	private boolean polished = true;
	private boolean actionsNamesListRefined = false; 
	private boolean actionsNamesRowRefined = false; 
	private boolean subcontrollersNamesListRefined = false; 
	private Map<String, String> refinedCollectionActions;
	private Map<String, Boolean> refinedReferenceActions; 
	
	// firstLevel is the root view that receives the request 
	// usually match with getRoot(), but not always. For example,
	// you can fill a dialog using a subview from the current view
	// In that case, inside the dialog this subview will be firstLevel, though
	// it continues being a subview.
	private boolean firstLevel;

	private String rootModelName;
	private Map defaultValues;
	private Map oldCollectionTotals;    
	private Map membersNameForElementCollection;
	private Map<MetaProperty, Map> validValuesByProperty;
	private Set<String> noBlankValidValuesProperties; 
	private Collection<String> sumProperties;  
	private Map<String, List<String>> totalProperties; 
	private StringBuffer defaultSumProperties;
	private int collectionSize = -1;
	private boolean dataChanged;  
	
	public static void setRefiner(Object newRefiner) {
		refiner = newRefiner;
	}
	
	public static void setPolisher(Object newPolisher) {
		polisher = newPolisher;
	}
		
	public View() {
		oid = nextOid++;
	}
	
	public Collection<MetaMember> getMetaMembers() throws XavaException { 
		if (metaMembers == null) {
			metaMembers = createMetaMembers(false);
		}		
		return metaMembers;		
	}
	
	private Collection<MetaMember> getMetaMembersIncludingCollectionTotals() throws XavaException { 
		if (metaMembersIncludingCollectionTotals == null) {
			metaMembersIncludingCollectionTotals = createMetaMembers(false, true);
		}		
		return metaMembersIncludingCollectionTotals;		
	}
	
	private Collection getMetaMembersIncludingHiddenKey() throws XavaException { 
		if (metaMembersIncludingHiddenKey == null) {
			metaMembersIncludingHiddenKey = createMetaMembers(false);
			if (!isRepresentsAggregate()) {
				boolean displayAsDescriptionsListAndReferenceView = displayAsDescriptionsListAndReferenceView(); 
				for (Iterator it=getMetaModel().getMetaPropertiesKey().iterator(); it.hasNext(); ) {
					MetaProperty p = (MetaProperty) it.next();
					if (displayAsDescriptionsListAndReferenceView || p.isHidden()) { 
						metaMembersIncludingHiddenKey.add(p);
					}
				}
			}
		}
		return metaMembersIncludingHiddenKey;		
	}
	
	private Collection createMetaMembers(boolean hiddenIncluded) throws XavaException { 
		return createMetaMembers(hiddenIncluded, hiddenIncluded);
	}
	
	private Collection createMetaMembers(boolean hiddenIncluded, boolean collectionTotalsIncluded) throws XavaException {   
		if (getModelName() == null) return Collections.EMPTY_LIST; 		
		Collection<MetaMember> metaMembers = new ArrayList<MetaMember>(getMetaView().getMetaMembers());
		if (isRepresentsElementCollection()) {
			removeNotInListProperties(metaMembers);
		}
		else if (isRepresentsCollection()) {     
			metaMembers = extractAggregateRecursiveReference(metaMembers);					
		}				
		extractRecursiveReference(metaMembers); 
		if (!hiddenIncluded && hiddenMembers != null) {
			removeHidden(metaMembers);
			removeFirstAndLastSeparator(metaMembers);
		}
		removeOverlapedProperties(metaMembers);
		if (collectionTotalsIncluded) addTotalEditablePropertiesInCollections(metaMembers);
		return metaMembers;	
	}
	
	private void removeNotInListProperties(Collection<MetaMember> metaMembers) { 
		Set<String> listMembers = getMetaPropertiesList().stream()
			.map(MetaProperty::getName)
			.map(p -> p.split("\\.", 2)[0])
			.collect(Collectors.toSet()); 
		for (Iterator<MetaMember> it = metaMembers.iterator(); it.hasNext(); ) {
			if (!listMembers.contains(it.next().getName())) it.remove(); 
		}
	}

	private void addTotalEditablePropertiesInCollections(Collection<MetaMember> metaMembers) {
		refineTotalEditablePropertiesInCollections(metaMembers, true); 
	}
	
	private void removeTotalEditablePropertiesInCollections(Collection<MetaMember> metaMembers) { 
		refineTotalEditablePropertiesInCollections(metaMembers, false); 
	}
	
	private void refineTotalEditablePropertiesInCollections(Collection<MetaMember> metaMembers, boolean add) { 
		Collection<MetaProperty> totalMetaProperties = null;
		for (MetaMember member: metaMembers) {
			if (!(member instanceof MetaCollection)) continue;
			try {
				View subview = getSubview(member.getName());
				for (List<String> totalProperties: subview.getTotalProperties().values()) {
					for (String totalProperty: totalProperties) {
						try {
							if (totalProperty.startsWith("__SUM__")) continue; 
							String propertyName = subview.removeTotalPropertyPrefix(totalProperty);
							MetaProperty metaProperty = getMetaProperty(propertyName);
							if (add) {
								MetaPropertyView metaPropertyView = getMetaView().getMetaPropertyViewFor(propertyName);
								if (metaPropertyView != null) {
									setEditable(propertyName, !(!(!metaPropertyView.isReadOnlyOnCreate() && isKeyEditable()) && metaPropertyView.isReadOnly()));
								}
							}
							if (totalMetaProperties == null) totalMetaProperties = new ArrayList<MetaProperty>();
							totalMetaProperties.add(metaProperty);
						}
						catch (ElementNotFoundException ex) { // Because of summation properties such as amount+
						}
					}
				}
			}
			catch (ElementNotFoundException ex) { // Because initializing element collections sometimes "change property" event can go to parent view, not initialized yet. 
			}
		}
		if (totalMetaProperties != null) {
			metaMembers.removeAll(totalMetaProperties); 
			if (add) metaMembers.addAll(totalMetaProperties);
		}
	} 

	private void extractRecursiveReference(Collection metaMembers) {		
		for (Iterator it=metaMembers.iterator(); it.hasNext(); ) {
			Object member = it.next();
			MetaReference ref = null;
			if (member instanceof MetaReference) ref = (MetaReference) member; 
			else continue;
			String model = ref.getMetaModel().getName();
			String view = getMetaView().getMetaView(ref).getName();			
			if (isViewInParents(model, view)) {
				it.remove();
			}
		}				
	}

	private boolean isViewInParents(String modelName, String viewName) { 
		View parent = getParent();		
		if (parent == null)	return false;
		if (isSection() || isGroup()) return parent.isViewInParents(modelName, viewName);;  
		String parentView = parent.getViewName();
		if (parentView ==null) parentView = "";		 
		if (Is.equal(parent.getModelName(), modelName) && 
			Is.equal(parentView, viewName)) return true;
			
		return parent.isViewInParents(modelName, viewName);
	}

	private Collection extractAggregateRecursiveReference(Collection metaMembers) {  		
		Set parentNames = new HashSet();
		if (isRepresentsCollection()) {
			parentNames.add(getMetaCollection().getMetaReference().getRole());
		}

		Collection filtered = new ArrayList();
		Iterator it = metaMembers.iterator();
		while (it.hasNext()) {
			MetaMember m = (MetaMember) it.next();
			if (m instanceof MetaReference) {						
				MetaReference ref = (MetaReference) m;				
				if (!parentNames.contains(ref.getName())) {
					filtered.add(m);						
				}
			}
			else {
				filtered.add(m);
			}
		}
		metaMembers = filtered;
		return metaMembers;
	}
	
	private void removeFirstAndLastSeparator(Collection metaMembers) {
		Iterator it = metaMembers.iterator();
		Object member = null;
		if (it.hasNext()) member = it.next();
		if (PropertiesSeparator.INSTANCE.equals(member)) {
			it.remove();
			member = null;
		}
		while (it.hasNext()) member = it.next();
		if (PropertiesSeparator.INSTANCE.equals(member)) it.remove();
	}

	private void removeHidden(Collection metaElements) { 
		Iterator it = metaElements.iterator();
		while (it.hasNext()) {			
			MetaElement member = (MetaElement) it.next(); 
			if (hiddenMembers.contains(member.getName())) it.remove();
		}
	}
	
	private void removeOverlapedProperties(Collection metaMembers) throws XavaException { 		
		if (!(representsEntityReference && !isRepresentsCollection())) return; 
		if (getParent().isRepresentsAggregate()) return; // At momment references to entity in aggregage can not be overlapped
		ModelMapping parentMapping = getParent().getMetaModel().getMapping();
		String referenceName = getMemberName();
		Iterator it = metaMembers.iterator();		
		while (it.hasNext()) {
			Object m = it.next();
			if (!(m instanceof MetaProperty)) continue;
			MetaProperty p = (MetaProperty) m;			
			if (!p.isKey()) continue; // In references only key is matter			
			if (parentMapping.isReferenceOverlappingWithSomeProperty(referenceName, p.getName())) {
				it.remove();
			}			
		}		
	}
	
	private void polish() { 
		if (polisher == null) return;
		if (polished) return;
		if (!isFirstLevel() && !(isGroup() || isSection())) return;

		try {
			XObjects.execute(polisher, "refine", 
				MetaModule.class, getMetaModuleForModel(), 
				View.class, this);
			polished = true;
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("refining_members_error"), ex); 
			metaMembers.clear(); 
		}		

		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.polish();
			}
		}		
					
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).polish();
			}	
		}
	}
	
	private MetaModule getMetaModuleForModel() { 
		ModuleManager moduleManager = getModuleManager(getRequest()); 
		if (getRoot() == this) return moduleManager.getMetaModule();  
		MetaApplication app = MetaApplications.getMetaApplication(getModuleManager(getRequest()).getApplicationName());
		String modelName = getModelName();
		if (modelName.contains(".")) modelName = Strings.lastToken(modelName, ".");
		return app.getMetaModule(modelName);
	}

	public void setMetaMembers(Collection metaMembers) {			
		if (Is.equal(this.metaMembers, metaMembers)) return;
		this.metaMembers = metaMembers;
		this.membersNames = null;
		this.collectionMemberNames = null;
		this.metaProperties = null;
		this.calculatedPropertiesNames = null;
		this.metaPropertiesIncludingSections = null;
		this.metaPropertiesIncludingGroups = null;
		this.metaPropertiesQualified = null;
		this.mapStereotypesProperties = null;
		this.lastPropertyKeyName = null;
		this.values = null;
		this.subviews = null;
	}
	
	/**
	 * 
	 * @since 4.8.1
	 */
	public MetaView getMetaView() throws XavaException { 
		if (metaView == null) {
			if (isRepresentsEntityReference() && !isRepresentsCollection()) {
				MetaReference ref = getParent().getMetaReference(getMemberName());
				metaView = getParent().getMetaView().getMetaView(ref, getMetaModel());
			}
			else if (Is.emptyString(getViewName())) {
				metaView = getMetaModel().getMetaViewByDefault();				
			}
			else {
				metaView = getMetaModel().getMetaView(getViewName());				
			}
		}
		return metaView;
	}
	private void setMetaView(MetaView metaView) {
		if (this.metaView == metaView) return;		
		resetMembers();
		this.metaView = metaView;
		this.viewName = metaView.getName();
	}
	
	public MetaModel getMetaModel() throws XavaException {		
		if (metaModel == null) {
			String modelName = getModelName();			
			int idx = modelName.indexOf('.');
			if (idx < 0) {
				metaModel = MetaComponent.get(modelName).getMetaEntity();
			} 
			else {
				String componentName = modelName.substring(0, idx);
				idx = modelName.lastIndexOf('.'); // We get the last one in case we have MyComponent.MyAggregate.MyNestedAggregate, thus we search MyNestedAggregate within MyComponent 
				String aggregateName = modelName.substring(idx+1);
				metaModel = MetaComponent.get(componentName).getMetaAggregate(aggregateName);
			}
		}
		return metaModel;
	}
	
	/**
	 * Copy of the values showed in view. <p>
	 * 
	 * It's a copy, if you change it the displayed data
	 * is not changed. If you wish change displayed data
	 * you have to use <code>setValues</code> or <code>setValue</code>.<br>
	 */
	public Map getValues() throws XavaException {
		return Maps.recursiveClone(getValues(false));
	}

	/**
	 * Copy of all values showed in view. <p>
	 * 
	 * It's a copy, if you change it the displayed data
	 * is not changed. If you wish change displayed data
	 * you have to use <code>setValues</code> or <code>setValue</code>.<br>
	 */	
	public Map getAllValues() throws XavaException {
		return new HashMap(getValues(true));
	}

	private Map getValues(boolean all) throws XavaException {  
		return getValues(all, false); 
	}
	
	private Map getValues(boolean all, boolean onlyKeyFromSubviews) throws XavaException {
		Map hiddenKeyAndVersion = null;
		if (values == null) {
			values = new HashMap();  
		} 
		else { 
			hiddenKeyAndVersion = getHiddenKeyAndVersion(values); 
		}  
		if (hasSubviews()) { 
			Iterator it = getSubviews().entrySet().iterator(); 
			while (it.hasNext()) { 
				Map.Entry en = (Map.Entry) it.next(); 
				View v = (View) en.getValue();
				if (v.isRepresentsCollection()) {
					if (v.getMetaCollection().isElementCollection()) {
						values.put(en.getKey(), v.getCollectionValues());  
					}
				}
				else if (!onlyKeyFromSubviews && (all || v.isRepresentsAggregate())) {					
					values.put(en.getKey(), v.getValues(all, onlyKeyFromSubviews));					
				} 
				else {  					
					values.put(en.getKey(), v.getKeyValues());					
				}  
			} 
		} 
		
		if (hasGroups()) { 
			Iterator it = getGroupsViews().values().iterator(); 
			while (it.hasNext()) { 
				View v = (View) it.next(); 
				values.putAll(v.getValues(all, onlyKeyFromSubviews)); 				
			}  
		} 
		 
		if (hasSections()) { 
			int count = getSections().size(); 
			for (int i=0; i<count; i++) {  
				values.putAll(getSectionView(i).getValues(all, onlyKeyFromSubviews));
			} 
		}  
		
		if (hiddenKeyAndVersion != null) { 
			values.putAll(hiddenKeyAndVersion);  
		} 

		return values; 
	} 	
	
	private Map getHiddenKeyAndVersion(Map keyValues) throws XavaException { 
		Map result = null;
		for (Iterator it=keyValues.keySet().iterator(); it.hasNext(); ) {
			String property = (String) it.next();
			if (getMetaModel().isHiddenKey(property) || getMetaModel().isVersion(property)) { 
				if (result == null) result = new HashMap();
				result.put(property, keyValues.get(property));
			}
		}
		return result;
	}

	private boolean hasGroups() {	
		getSubviews(); // in order to start the process that create subviews and groups 
		return groupsViews != null && !groupsViews.isEmpty();
	}
	
	public void addValues(Map values) throws XavaException { 
		addValues(values, false);
	}
 
	private void addValues(Map values, boolean setValuesForSubviews) throws XavaException {		
		values = values==null?Collections.EMPTY_MAP:values; 		
		Iterator it = values.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next(); 
			String key = (String) en.getKey();
			Object value = en.getValue();			
			int idx = key.indexOf('.');
			if (idx < 0) {				
				trySetValue(key, value, setValuesForSubviews); 
			}
			else {
				String subviewName = key.substring(0, idx);
				String member = key.substring(idx+1);								 				
				getSubview(subviewName).trySetValue(member, value, setValuesForSubviews); 
			}
		}											
	}

	public void setValues(Map values) throws XavaException {
		setValuesChangingModel(values);
		getRoot().dataChanged = !isFirstLevel(); 
	}
	
	public void setValuesChangingModel(Map values) throws XavaException {
		boolean modelChanged = false;
		if (values != null) {
			String modelName = (String) values.get(MapFacade.MODEL_NAME);
			if (modelName != null && !modelName.equals(getModelName()) && !getModelName().contains(".")) {			
				rootModelName = getModelName();
				String viewName = getViewName(); 
				setModelName(modelName);
				setViewName(viewName); 
				modelChanged = true;				
			}
		}	
		setValues(values, !isRepresentsCollection()); 
		if (modelChanged) refresh();
	}
	
	private void initDefaultValues() {
		if (!isRepresentsElementCollection()) return;
		reset();
		defaultValues = Maps.treeToPlain(getAllValues());
		clear();
	}

	/**
	 * 
	 * @since 5.1
	 */
	public Object getDefaultValueInElementCollection(String qualifiedPropertyName) { 
		return defaultValues.get(qualifiedPropertyName);
	}
	
	private void setValues(Map map, boolean closeCollections) throws XavaException { 
		clearValues(); 
		if (closeCollections) resetCollections(true);
		resetCollectionTotals();
		addValues(map, true);
	}

	private void resetCollections(boolean root) throws XavaException { 
		if (hasSubviews()) { 
			Iterator it = getSubviews().values().iterator();

			while (it.hasNext()) {
				View subview = (View) it.next();
				subview.setCollectionDetailVisible(false);
				subview.setCollectionEditingRow(-1);
				if (subview.isRepresentsCollection() && !subview.isCollectionFromModel()) {
					subview.getCollectionTab().setFilterVisible(false); 
				}
				subview.resetCollections(false);
			}
		}
				
		if (hasGroups()) {
			Iterator it = getGroupsViews().values().iterator();
			while (it.hasNext()) {
				View subview = (View) it.next();
				subview.setCollectionDetailVisible(false);
				subview.setCollectionEditingRow(-1);				
				subview.resetCollections(false); 
			}
		}
				
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				View subview = getSectionView(i); 
				subview.setCollectionDetailVisible(false);
				subview.setCollectionEditingRow(-1);
				subview.resetCollections(false); 
			}	
		}	
		listSelected = null;
		if (collectionTab != null) {
			collectionTab.setRequest(getRequest()); 
			collectionTab.goPage(1); 
			collectionTab.deselectAll();
			if (!root) collectionTab.clearCondition();
		}
	}
	
	private void resetCollectionTotals() throws XavaException {
		collectionTotals = null;
		if (hasSubviews()) { 
			Iterator it = getSubviews().values().iterator();

			while (it.hasNext()) {
				View subview = (View) it.next();
				subview.resetCollectionTotals();										
			}
		}
				
		if (hasGroups()) {
			Iterator it = getGroupsViews().values().iterator();
			while (it.hasNext()) {
				View subview = (View) it.next();
				subview.resetCollectionTotals();
			}
		}
				
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				View subview = getSectionView(i); 
				subview.resetCollectionTotals();
			}	
		}					 	
	}


	/**
	 * Set the values and execute the on-change actions associated to
	 * the assigned properties. <p>
	 */
	public void setValuesExecutingOnChangeActions(Map values) throws XavaException {		
		setOnlyThrowsOnChange(true);
		try {
			setValuesNotifying(values);
		}
		finally {
			setOnlyThrowsOnChange(false);
		}		
	}
	
	/**
	 * Set the values and throws are events associated to the changed values. 
	 */
	public void setValuesNotifying(Map values) throws XavaException {			 
		getRoot().registeringExecutedActions = true;			
		try {
			moveCollectionValuesToViewValues();
			setValues(values);
			notifying(values);
		}
		finally {
			getRoot().registeringExecutedActions = false;		
			resetExecutedActions();			
		}				
	}
	
	private void moveCollectionValuesToViewValues() {  
		if (!isRepresentsElementCollection()) {
			View parent = getParent();
			if (parent == null) return;
			parent.moveCollectionValuesToViewValues();
			return;
		}
		if (collectionValues == null) return;	
		if (collectionEditingRow < 0 || collectionEditingRow > collectionValues.size()) return; 
		if (collectionValues.size() == collectionEditingRow) {
			clearValues(); 
		}
		else setValues(collectionValues.get(collectionEditingRow)); 		
	}
	
	private void clearValues() {  
		if (values != null) values.clear();
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				if (!subview.isRepresentsCollection() || isRepresentsElementCollection()) { 
					subview.clearValues();
				}
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.clearValues();
			}
		}						
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).clearValues();
			}	
		}
	}
	
	

	private void notifying(Map values) throws XavaException {
		Iterator it = Maps.treeToPlain(values).keySet().iterator(); 
		String key = null;
		String qualifier = null;
		if (isSubview()) {
			qualifier = getMemberName() + ".";
		}
		while (it.hasNext()) {
			String property = (String) it.next();
			if (property.equals(getLastPropertyKeyName())) {
				key = property;
			}
			else if (Is.emptyString(getLastPropertyKeyName()) && getMetaModel().isKey(property)) {
				key = property;
			}
			else {
				if (qualifier == null) {
					propertyChanged(property);	
				}
				else {
					getParent().propertyChanged(qualifier + property);
				}				
			}							
		}
		if (key != null) {				
			if (qualifier == null) {
				propertyChanged(key);	
			}
			else {
				getParent().propertyChanged(qualifier + key);
			}							
		}
	}
	
	/**
	 * @param recalculatingValues If true reobtain values from views, groups and sections.
	 */
	private Object getValue(String name, boolean recalculatingValues) throws XavaException {
		int idx = name.indexOf('.');		
		if (idx < 0) { 						
			if (!getMembersNamesWithoutSections().contains(name) && (hiddenMembers == null || !hiddenMembers.contains(name)) && !getMetaModel().getKeyPropertiesNames().contains(name)) {
				return getValueInSections(name, recalculatingValues);
			}
			else {				
				if (hasSubview(name)) { 															
					View subview = getSubview(name);
					if (!subview.isRepresentsCollection()) {						
						return subview.getValues(false);
					}
					else {						
						return subview.getCollectionValues();
					}
				}
				else {															
					if (values == null && !recalculatingValues) return null;
					return recalculatingValues?getValues(false).get(name):values.get(name);
				}				 							 								
			} 			
		} 
		else {						
			String subviewName = name.substring(0, idx);			
			String member = name.substring(idx+1);
			View subview = getSubview(subviewName);
			if (subview.isRepresentsElementCollection()) { 	
				int elementIndex = Integer.parseInt(Strings.firstToken(member, "."));
				List collectionValues = subview.getCollectionValues();
				if (elementIndex < 0 || elementIndex >= collectionValues.size()) return null;
				Map element = (Map) collectionValues.get(elementIndex);
				String collectionMember = Strings.noFirstTokenWithoutFirstDelim(member, ".");
				collectionMember = collectionMember.replaceAll("^this\\.", ""); 
				return Maps.getValueFromQualifiedName(element, collectionMember);
			}
			else {
				return subview.getValue(member, recalculatingValues);
			}
		}		
	}
	

	/**
	 * A value from the property name.
	 * 
	 * The property name can be simple:
	 * <pre>
	 * getValue("name");
	 * </pre>
	 * or qualified:
	 * <pre>
	 * getValue("customer.name");
	 * </pre>
	 * Even you can obtain a single value from an element collection (since v5.0):
	 * <pre>
	 * getValue("details.3.quantity");
	 * </pre>
	 * In this case you use the index for the collection after the collection name,
	 * in the case of out of range a null is returned. 
	 * 
	 * @param name  Qualified properties are allowed
	 */	
	public Object getValue(String name) throws XavaException {
		return getValue(name, true);
	}
	
	/** @since 6.2.2 */
	public boolean isMemberFromElementCollection(String memberName) {
		if (!memberName.contains(".")) return false;
		String subviewName = memberName.split("\\.")[0];
		try {
			return getSubview(subviewName).isRepresentsElementCollection();
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
	}

	/**
	 * 
	 * @param name  Qualified properties are allowed
	 */		
	public int getValueInt(String name) throws XavaException {
		Number v = (Number) getValue(name);			
		return v==null?0:v.intValue();
	}
	
	/**
	 * 
	 * @param name  Qualified properties are allowed
	 */		
	public String getValueString(String name) throws XavaException {		
		Object v = getValue(name);
		return v == null?"":v.toString();						
	}
	
	public View getSubview(String name) throws XavaException {
		if ("__PARENT__".equals(name)) return getParent();
		int idx = name.indexOf(".");
		if (idx >= 0) {
			String firstSubview = name.substring(0, idx);			
			String restSubviews = name.substring(idx+1);
			return getSubview(firstSubview).getSubview(restSubviews);
		}
		createSubviews(); 
		View subview = (View) getSubviews().get(name);		
		if (subview == null) {			
			subview = findSubviewInSection(name);			
			if (subview == null) {			
				subview = findSubviewInGroup(name);
				if (subview == null) {					
					throw new ElementNotFoundException("subview_not_found", name, getModelName());
				}							
			}	
		}
		return subview;
	}
	
	public View getGroupView(String name) throws XavaException {		
		View subview = (View) getGroupsViews().get(name);
		if (subview == null) {			
			groupsViews = null; // to force reload the group views
			subviews = null;
			subview = (View) getGroupsViews().get(name);
			if (subview == null) {
				throw new ElementNotFoundException("subview_group_no_found", name, getModelName());
			}
		}				
		return subview;
	}
		
	private View findSubviewInSection(String name) throws XavaException {
		if (!hasSections()) return null;
		int count = getSections().size();
		for (int i=0; i<count; i++) {
			View sectionView = getSectionView(i); 
			View subview = (View) sectionView.getSubviews().get(name);
			if (subview == null) {
				subview = sectionView.findSubviewInSection(name);
				if (subview != null) return subview;
			} 
			if (subview == null) {
				subview = sectionView.findSubviewInGroup(name);
				if (subview != null) return subview;
			} 
			else {
				return subview;
			}		
		}
		return null;
	}
	
	private View findSubviewInGroup(String name) throws XavaException {
		if (!hasGroups()) return null;
		Iterator it = getGroupsViews().values().iterator();
		while (it.hasNext()) {
			View groupView = (View) it.next();			
			View subview = (View) groupView.getSubviews().get(name);
			if (subview != null) return subview;
			if (groupView.hasGroups()) {
				subview = groupView.findSubviewInGroup(name);
				if (subview != null) return subview;
			}
		}
		return null;
	}
	

	private void createAndAddSubview(MetaMember member) throws XavaException { 
		if (!(member instanceof MetaReference || member instanceof MetaCollection || member instanceof MetaGroup)) return;
		
		View newView = new View();
		newView.setSubview(true);
		newView.setParent(this);
		MetaReference ref = null;
		if (member instanceof MetaReference) {
			ref = (MetaReference) member;
		}
		else if (member instanceof MetaCollection) {
			ref = ((MetaCollection) member).getMetaReference();
			newView.setRepresentsCollection(true);					
		}
		else { // MetaGroup			
			newView.setModelName(getModelName());			 
			MetaView metaView = ((MetaGroup) member).getMetaView();
			newView.setMetaView(metaView);
			newView.setMemberName(getMemberName()); 
			newView.setGroup(true); 
			getGroupsViews().put(member.getName(), newView);			
			return;			
		}		
		if (ref.isAggregate()) {		
			newView.setModelName(getModelName() + "." + ref.getReferencedModelName());
			newView.setRepresentsAggregate(true); 
		}
		else {
			newView.setModelName(ref.getReferencedModelName());
			newView.setRepresentsEntityReference(true);
		}
		if (displayReferenceWithNotCompositeEditor(ref)) { 
			newView.setMetaView(getMetaView().getMetaViewOnlyKeys(ref));			
		}
		else {
			newView.setMetaView(getMetaView().getMetaView(ref));			
		}
		newView.setMemberName(member.getName());		
		if (newView.isRepresentsCollection()) {					
			MetaCollectionView metaCollectionView = getMetaView().getMetaCollectionView(member.getName());
			if (metaCollectionView != null) {
				if (metaCollectionView.isAsAggregate()) {
					newView.setRepresentsAggregate(true); 
				}
				Collection propertiesListNames = metaCollectionView.getPropertiesListNames();
				if (!propertiesListNames.isEmpty()) {
					newView.setPropertiesListNames(metaCollectionView.getPropertiesListNamesAsString());
					newView.setMetaPropertiesList(namesToMetaProperties(newView, propertiesListNames));
				}				
				if (metaCollectionView.hasRowStyles()) { 
					newView.setRowStyles(metaCollectionView.getMetaRowStyles());					
				}				
				Collection actionsDetailNames = metaCollectionView.getActionsDetailNames();
				if (!actionsDetailNames.isEmpty()) {
					newView.setActionsNamesDetail(new ArrayList(actionsDetailNames));
				}
				
				if (!metaCollectionView.isModifyReference()) {
					String viewAction = newView.getViewCollectionElementAction() == null?
						COLLECTION_VIEW_ACTION:newView.getViewCollectionElementAction();
					newView.setEditCollectionElementAction(viewAction);
				}
				else {
					newView.setEditCollectionElementAction(metaCollectionView.getEditActionName());
				}
				newView.setViewCollectionElementAction(metaCollectionView.getViewActionName());
				
				if (!metaCollectionView.isCreateReference()) { 
					newView.setNewCollectionElementAction("");
					newView.setAddCollectionElementAction("");
				}
				else {
					newView.setNewCollectionElementAction(metaCollectionView.getNewActionName());
					newView.setAddCollectionElementAction(metaCollectionView.getAddActionName()); 
				}
				newView.setSaveCollectionElementAction(metaCollectionView.getSaveActionName());
				newView.setHideCollectionElementAction(metaCollectionView.getHideActionName());
				newView.setRemoveCollectionElementAction(metaCollectionView.getRemoveActionName());
				newView.setRemoveSelectedCollectionElementsAction(metaCollectionView.getRemoveSelectedActionName());
				newView.setOnSelectCollectionElementAction(metaCollectionView.getOnSelectElementActionName());
				boolean editable = false;
				if (!metaCollectionView.isReadOnly()) {
					if (newView.isRepresentsAggregate()) editable = isEditable();															
				}				
				newView.setCollectionEditable(!metaCollectionView.isReadOnly() && !metaCollectionView.isEditOnly());
				if (!newView.isCollectionEditable()) {
					newView.setCollectionEditableFixed(true);
				}
				else {
					newView.setCollectionEditable(isEditable());
				}								
				newView.setCollectionMembersEditables(
					( !metaCollectionView.isReadOnly() || 
					metaCollectionView.isEditOnly() ) &&
					metaCollectionView.isModifyReference());
				newView.setViewName(metaCollectionView.getViewName());
				Collection actionsListNames = metaCollectionView.getActionsListNames();
				if (!actionsListNames.isEmpty()) {					
					Collection actions = new ArrayList(actionsListNames);
					actions.addAll(newView.getDefaultListActionsForCollections());
					newView.setActionsNamesList(actions);
				}
				Collection subcontrollerListNames = metaCollectionView.getSubcontrollersListNames();
				if (!subcontrollerListNames.isEmpty()) {					
					Collection subcontroller = new ArrayList(subcontrollerListNames);
					newView.setSubcontrollersNamesList(subcontroller);
				}
				Collection actionsRowNames = metaCollectionView.getActionsRowNames();
				if (!actionsRowNames.isEmpty()) {					
					Collection actions = new ArrayList(actionsRowNames);
					actions.addAll(newView.getDefaultRowActionsForCollections());
					newView.setActionsNamesRow(actions);
				}		
				
				if (metaCollectionView.isCollapsed()) {
					String frameId= getFrameId(member.getName());
					if (!isFrameStatusStored(frameId)) setCollapsed(member.getName(), true);
				}
			}
			else {
				newView.setCollectionEditable(isEditable()); 				
				newView.setCollectionMembersEditables(true);
			}
		}
		else {			
			MetaReferenceView metaReferenceView = getMetaView().getMetaReferenceView(ref);
			if (metaReferenceView != null) {
				newView.setReadOnly(metaReferenceView.isReadOnly());
				
				
				if (newView.isRepresentsEntityReference()) {
					newView.setRepresentsAggregate(metaReferenceView.isAsAggregate());
				}
				
				if (metaReferenceView.isCollapsed()) {
					String frameId= getFrameId(member.getName());
					if (!isFrameStatusStored(frameId)) setCollapsed(member.getName(), true);
				}
			}			
		}
		newView.initDefaultValues(); 
		subviews.put(member.getName(), newView);
	} 

	private Collection getDefaultListActionsForCollections() {
		try {
			if (!isDefaultListActionsForCollectionsIncluded()) return Collections.EMPTY_LIST; 
			if (isCollectionFromModel()) {
				if (defaultListActionsForCollectionsFromModel == null) {			
					defaultListActionsForCollectionsFromModel = createDefaultActionsForCollections("DefaultListActionsForCollections",  true); 
				}
				return defaultListActionsForCollectionsFromModel;
			}
			else {
				if (defaultListActionsForTabCollections == null) {			
					defaultListActionsForTabCollections = createDefaultActionsForCollections("DefaultListActionsForCollections", false); 
				}
				return defaultListActionsForTabCollections;
			}
		}
		catch (XavaException ex) {
			log.warn(XavaResources.getString("default_list_action_controllers_warning"), ex);
			return Collections.EMPTY_LIST;
		}
	}
	
	private Collection getDefaultRowActionsForCollections() { 
		try {
			if (!isDefaultRowActionsForCollectionsIncluded()) return Collections.EMPTY_LIST; 
			if (isCollectionFromModel()) {
				if (defaultRowActionsForCollectionsFromModel == null) {			
					defaultRowActionsForCollectionsFromModel = createDefaultActionsForCollections("DefaultRowActionsForCollections", true);
				}
				return defaultRowActionsForCollectionsFromModel;
			}
			else {
				if (defaultRowActionsForTabCollections == null) {			
					defaultRowActionsForTabCollections = createDefaultActionsForCollections("DefaultRowActionsForCollections", false);
				}
				return defaultRowActionsForTabCollections;				
			}
		}
		catch (XavaException ex) {
			log.warn(XavaResources.getString("default_row_action_controllers_warning"), ex);
			return Collections.EMPTY_LIST;
		}
	}

	private Collection createDefaultActionsForCollections(String defaultListController, boolean collectionFromModel) {
		MetaController controller = MetaControllers.getMetaController(defaultListController); 
		Collection result = new ArrayList();
		for (Iterator it = controller.getAllMetaActions().iterator(); it.hasNext();) {
			MetaAction action = (MetaAction) it.next();
			try {
				if (collectionFromModel && TabBaseAction.class.isAssignableFrom(Class.forName(action.getClassName()))) continue; 
				if (!action.isHidden()) {
					result.add(action.getQualifiedName());
				}
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("default_action_for_collection_not_added", action.getQualifiedName()), ex);
			}
		}				
		return Collections.unmodifiableCollection(result);
	}	

	private void setPropertiesListNames(String propertiesListNames) {
		this.propertiesListNames = propertiesListNames; 		
	}

	private Map getGroupsViews() throws XavaException {
		if (groupsViews == null) {
			groupsViews = new HashMap();
			getSubviews(); // in order to start the process that create subviews and groups
		}
		return groupsViews;
	}

	private List<MetaProperty> namesToMetaProperties(View view, Collection names) throws XavaException {
		List<MetaProperty> metas = new ArrayList();
		Iterator it = names.iterator();
		while (it.hasNext()) {
			String name = (String) it.next();			
			if (name.endsWith("+")) {
				name = name.substring(0, name.length() - 1); 
				if (view.isRepresentsCollection() && view.isCollectionFromModel()) {
					view.addCollectionDefaultSumProperty(name); 
				}
			}
			String qualifiedName = null;
			if (view.getMetaModel().containsMetaReference(name)) {
				MetaModel referencedModel = view.getMetaModel().getMetaReference(name).getMetaModelReferenced();
				String keyProperty = referencedModel.getAllKeyPropertiesNames().iterator().next();
				qualifiedName = name;
				name = name + "." + keyProperty;
			}
			MetaProperty metaProperty = view.getMetaModel().getMetaProperty(name);
			if (name.indexOf('.') >= 0) { 
				metaProperty = metaProperty.cloneMetaProperty();
				metaProperty.setName(name);		
				if (qualifiedName != null) {
					metaProperty.setQualifiedName(qualifiedName); // The qualifiedName is used to obtain the label in collections 
				}												// so we use the label of the reference 
			}				
			metas.add(metaProperty);		
		}
		return metas;
	}

	private boolean hasSubviews() {		
		return subviews != null && !subviews.isEmpty();		
	}
	
	private boolean hasSubview(String name) throws XavaException {
		if (!hasSubviews()) return false;		
		return getSubviews().containsKey(name); 
	}
	
	private Map<String, View> getSubviews() throws XavaException { 
		if (getModelName() == null) return Collections.EMPTY_MAP;		
		if (subviews == null) {
			if (isRepresentsCollection() && !isCollectionDetailVisible()) {
				return Collections.EMPTY_MAP;
			}			
			createSubviews(); 
		}
		return subviews;
	}

	private void createSubviews() {
		if (subviews != null) return; 
		subviews = new HashMap();		
		Iterator it = getMetaMembers().iterator();
		while (it.hasNext()) {
			MetaMember member = (MetaMember) it.next();			
			createAndAddSubview(member);
		}			
		removeTotalEditablePropertiesInCollections(getMetaMembers()); 
		setEditable(editable);
	}
	
	/**
	 * Set the value and notifies the property change, recalculating all dependent properties. 
	 * 
	 * @param name Can be qualified	 
	 */
	public void setValueNotifying(String name, Object value) throws ElementNotFoundException, XavaException {
		setValue(name, value);			
		propertyChanged(name);		
	}
	
	/**
	 * Set the value to the indicated member. <p>
	 * 
	 * If member is not of this view an exception is thrown.
	 * 
	 * @param name Can be qualified	 
	 * @exception XavaException  If name is not a displayed member of this view.
	 */
	public void setValue(String name, Object value) throws XavaException {
		if (!trySetValue(name, value)) {
			String viewName = getViewName() == null?"":"'" + getViewName() + "'";
			throw new XavaException("member_not_found_in_view", "'" + name + "'", viewName, "'" + getModelName() + "'");
		}
		try { 
			MetaMember member = getMetaModel().getMetaMember(name);
			if (!isMetaProperty(member) || !((MetaProperty)member).isTransient()) {
				getRoot().dataChanged = true;
			}
		}
		catch (ElementNotFoundException ex) {
			// It could be a view property of a XML component
		}
		// tmp fin
		moveViewValuesToCollectionValues();
	}	
	
	/**
	 * Try to set the value to the indicated member. <p>
	 * 
	 * If member does not exist in view, returns false, but it does not throw exception.<br>
	 * 
	 * @param name Can be qualified
	 * @return <code>true</code> if member exists and it's updated, <code>false</code> otherwise.	 
	 */
	public boolean trySetValue(String name, Object value) throws XavaException { 
		return trySetValue(name, value, true);
	}
					
	private boolean trySetValue(String name, Object value, boolean setValuesForSubviews) throws XavaException {
		name = Ids.undecorate(name); 
		int idx = name.indexOf('.');
		if (idx < 0) {
			if (getMembersNamesInGroup().contains(name)) {
				trySetValueInGroups(name, value);		
				return true;
			}
			if (!getMembersNamesWithoutSections().contains(name)) {
				if (!setValueInSections(name, value)){
					if (!(getMetaModel().getKeyPropertiesNames().contains(name) || getMetaModel().getKeyReferencesNames().contains(name) || getMetaModel().isVersion(name))) {
						return false;
					}															
				}
			}
			if (hasSubview(name)) {	
				View subview = getSubview(name);
				if (!subview.isRepresentsCollection()) {
					if (setValuesForSubviews) subview.setValuesChangingModel((Map)value); 
					else subview.addValues((Map)value);
				}
				else {
					subview.collectionValues = (List) value;
					subview.refreshCollection(); 
				}		
			}
			else { 					
				if (values == null) values = new HashMap();					
				value = Strings.removeXSS(value);
				values.put(name, value);
			}	
		} 
		else if (displayAsDescriptionsList()) {
			if (values == null) values = new HashMap();					
			value = Strings.removeXSS(value);
			values.put(name, value);			
		}
		else {			
			String subviewName = name.substring(0, idx);
			String member = name.substring(idx+1);
			View subview = getSubview(subviewName);
			if (subview.isRepresentsElementCollection()) { 	
				int elementIndex = Integer.parseInt(Strings.firstToken(member, "."));
				List collectionValues = subview.getCollectionValues();
				if (elementIndex < 0) return false;
				if (elementIndex >= collectionValues.size()) growCollection(collectionValues, elementIndex + 1);
				Map element = (Map) collectionValues.get(elementIndex);
				String collectionMember = Strings.noFirstTokenWithoutFirstDelim(member, ".");
				Maps.putValueFromQualifiedName(element, collectionMember, value);
				subview.setCollectionEditingRow(elementIndex);
				subview.trySetValue(collectionMember, value);
			}
			else {
				subview.trySetValue(member, value);
			}
		}
		return true;
	}
	
	/** @since 6.3 */
	public boolean isDataChanged() {  
		return dataChanged;
	}
	
	private void growCollection(Collection collection, int newSize) { 
		while (collection.size() < newSize) collection.add(new HashMap());
	}

	private Collection getMembersNamesInGroup() throws XavaException { 
		if (membersNamesInGroup == null) {
			membersNamesInGroup = new ArrayList();		
			Iterator it = getGroupsViews().values().iterator();		
			while (it.hasNext()) {
				View subview = (View) it.next();																
				membersNamesInGroup.addAll(subview.getMembersNamesWithHidden().keySet()); 
			}		
		}
		return membersNamesInGroup;
	}

	private void trySetValueInGroups(String name, Object value) throws XavaException {		
		Iterator it = getGroupsViews().values().iterator();
		while (it.hasNext()) {
			View subview = (View) it.next();
			subview.trySetValue(name, value);			
		}				
	}
	
	private boolean setValueInSections(String name, Object value) throws XavaException {
		if (!hasSections()) return false;
		int count = getSections().size();		
		for (int i = 0; i < count; i++) {	
			if (getSectionView(i).trySetValue(name, value))	return true;
			
		}		
		return false;
	}
	
	public void setSectionEditable(String sectionName, boolean editable) throws XavaException {		
		getSection(sectionName).setEditable(editable);
	}
	public boolean isSectionEditable(String sectionName) throws XavaException {		
		return getSection(sectionName).isEditable();
	} 
	
	private View getSection(String sectionName) throws XavaException {
		if (!hasSections()) {
			throw new ElementNotFoundException("no_sections_error");
		}
		int count = getSections().size();
		for (int i = 0; i < count; i++) {							
			if (getSectionView(i).getMetaView().getName().equals(sectionName)) {
				return getSectionView(i);				
			} 				
		}		
		throw new ElementNotFoundException("section_not_found", sectionName);
	}
		
	private Object getValueInSections(String name, boolean recalculatingValues) throws XavaException { 
		if (!hasSections()) return null;
		int count = getSections().size();
		for (int i = 0; i < count; i++) {			
			Object value = getSectionView(i).getValue(name, recalculatingValues);
			if (value != null) return value;
		}
		return null;
	}
	
	/**
	 * Excludes those values that are null, zero or empty string.
	 */
	public Map getKeyValuesWithValue() throws XavaException {		
		Map values = getValues(false, true); 
		Iterator it = values.keySet().iterator();
		Map result = new HashMap();
		while (it.hasNext()) {
			String name = (String) it.next();			
			if (getMetaModel().isKey(name)) {
				Object value = values.get(name);
				if (isEmptyValue(value)) continue;
				result.put(name, value);
			}			
		}						
		return result;
	}
	
	private boolean isEmptyValue(Object value) {
		if (value == null) return true;
		if (value instanceof Number && ((Number) value).intValue() == 0) return true;
		if (value instanceof String && Is.emptyString((String) value)) return true;
		return false;		
	}
	
	public Map getKeyValues() throws XavaException {		
		Map values = getValues(false, true);
		Iterator it = values.keySet().iterator();
		Map result = new HashMap();
		while (it.hasNext()) {
			String name = (String) it.next();			
			if (getMetaModel().isKey(name)) {
				result.put(name, values.get(name));
			}			
		}		

		if (getParent() != null && !getParent().isRepresentsAggregate()) {			
			// At the moment reference to entity within aggregate can not be part of key
			if (isRepresentsEntityReference() && !isRepresentsCollection()) {				
				ModelMapping mapping = getParent().getMetaModel().getMapping();
				if (mapping.isReferenceOverlappingWithSomeProperty(getMemberName())) {					
					Iterator itProperties = mapping.getOverlappingPropertiesOfReference(getMemberName()).iterator();					
					while (itProperties.hasNext()) {
						String property = (String) itProperties.next();						
						String overlappedProperty = mapping.getOverlappingPropertyForReference(getMemberName(), property);
						boolean overlappedPropertyIsInView = getMembersNames().containsKey(overlappedProperty);
						if (!overlappedPropertyIsInView) overlappedPropertyIsInView = getParent().getMembersNames().containsKey(overlappedProperty);
						if (!overlappedPropertyIsInView) continue;
						Maps.putValueFromQualifiedName(result, property, getParent().getValue(overlappedProperty, false));
					}
					
				}								
			}		
		}	
		return result; 
	}
	
	public Map getMembersNamesWithHidden() throws XavaException {
		if (membersNamesWithHidden == null) {
			membersNamesWithHidden = createMembersNames(true);
		}
		return membersNamesWithHidden;		
	}
	
	
	private Map getMembersNamesForFindObject() throws XavaException { 
		if (isRepresentsElementCollection()) {
			return getMembersNameForElementCollection();			
		}
		else if (isInsideElementCollection()) {			
			return (Map) getParent().getMembersNamesForFindObject().get(getMemberName());
		}
		return getMembersNamesWithHidden(); 
	}
	
	private Map getMembersNameForElementCollection() { 
		if (membersNameForElementCollection == null) {
			Map membersNames = new HashMap(); 
			for (MetaProperty p: getMetaPropertiesList()) {
				membersNames.put(p.getName(), null);				
			}
			membersNameForElementCollection = Maps.plainToTree(membersNames);			 
		}
		return membersNameForElementCollection;
	}
		
	private boolean isInsideElementCollection() { 
		View parent = getParent();
		if (parent == null) return false;
		if (parent.isRepresentsElementCollection()) return true; 
		return parent.isInsideElementCollection();
	}	

	public Map getMembersNames() throws XavaException {		
		if (membersNames == null) {
			membersNames = createMembersNames(false);
		}
		return membersNames;
	}	
	
	public Map getCalculatedPropertiesNames() throws XavaException {		
		if (calculatedPropertiesNames == null) { 
			calculatedPropertiesNames = createCalculatedPropertiesNames();
		}
		return calculatedPropertiesNames;
	}
	
	private Map createCalculatedPropertiesNames() throws XavaException {
		Map memberNames = new HashMap();
		Iterator it = createMetaMembers(false).iterator();
		while (it.hasNext()) {
			MetaMember m = (MetaMember) it.next();								
			if (isMetaProperty(m)) {
				if (((MetaProperty)m).isCalculated()) {
					memberNames.put(m.getName(), null);
				}
			}
			else if (m instanceof MetaReference) {
				Map names = getSubview(m.getName()).createCalculatedPropertiesNames();
				if (!names.isEmpty()) memberNames.put(m.getName(), names);				
			}
			else if (m instanceof MetaGroup) {
				Map names = getGroupView(m.getName()).createCalculatedPropertiesNames();
				if (!names.isEmpty()) memberNames.putAll(names);
			}
		}			
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				Map names = getSectionView(i).createCalculatedPropertiesNames();
				if (!names.isEmpty()) memberNames.putAll(names);
			}
		}			
		return memberNames;	
	}
	
			
	private Map createMembersNames(boolean hiddenIncluded) throws XavaException {
		Map membersNames = new HashMap();
		Collection<MetaMember> metaMembers = createMetaMembers(hiddenIncluded);
		Iterator<MetaMember> it = metaMembers.iterator();
		while (it.hasNext()) {
			MetaMember m = it.next();								
			if (isMetaProperty(m)) {										
				membersNames.put(m.getName(), null);
			}
			else if (m instanceof MetaReference) {
				membersNames.put(m.getName(), getSubview(m.getName()).createMembersNames(hiddenIncluded));
			}
			else if (m instanceof MetaCollection) { 					
				if (((MetaCollection) m).isElementCollection()) {
					membersNames.put(m.getName(), getSubview(m.getName()).createElementCollectionMembersNames());
				}
				// The entity collections are obtained from the collection view, this allows to load collections on demmand
			}				
			else if (m instanceof MetaGroup) { 
				membersNames.putAll(getGroupView(m.getName()).createMembersNames(hiddenIncluded));
			}
		}			
		if (hasSections()) {
			int quaintity = getSections().size();
			for (int i = 0; i < quaintity; i++) {
				membersNames.putAll(getSectionView(i).createMembersNames(hiddenIncluded));
			}
		}	
		return membersNames; 	
	}
	
	private Map createElementCollectionMembersNames() { 
		Map membersNames = new HashMap();
		for (MetaProperty p: getMetaPropertiesList()) {
			membersNames.put(p.getName(), null);
		}
		return Maps.plainToTree(membersNames);
	}

	/**
	 * <code>Tab</code> used for manage the data of this collection. <p>
	 *
	 * This view must represents a collection in order to call this method.<br>
	 */
	public Tab getCollectionTab() throws XavaException {
		assertRepresentsCollection("getCollectionTab()");
		if (collectionTab == null) {
			if (isCollectionFromModel()) {
				throw new IllegalStateException(
					XavaResources.getString("not_collection_from_model_for_getCollectionTab"));
			}
			collectionTab = new Tab();
			collectionTab.setCollectionView(this); 
			collectionTab.setModelName(getModelName());
			collectionTab.setTabName(Tab.COLLECTION_PREFIX + getMemberName());			
			collectionTab.setMetaRowStyles(rowStyles);
			if (propertiesListNames != null) {				
				collectionTab.setDefaultPropertiesNames(propertiesListNames);
			}
			MetaCollection metaCollection = getMetaCollection();
			if (metaCollection.hasCondition()) {
				collectionTab.setBaseCondition(metaCollection.getSQLConditionWithoutChangePropertiesByColumns());
				CollectionWithConditionInViewFilter filter = new CollectionWithConditionInViewFilter(); 
				filter.setView(getParent());
				filter.setConditionArgumentsPropertyNames(metaCollection.getConditionArgumentsPropertyNames());
				collectionTab.setFilter(filter);
			}
			else {
				collectionTab.setBaseCondition(createBaseConditionForCollectionTab());
				
				CollectionInViewFilter filter = new CollectionInViewFilter();
				filter.setView(getParent());
				collectionTab.setFilter(filter);
			}
			collectionTab.setDefaultOrder(getMetaCollection().getOrder());
			collectionTabLabels(collectionTab);
		}
		return collectionTab;
	}
	
	private void collectionTabLabels(Tab collectionTab){
		Collection properties = collectionTab.getMetaProperties();
		Iterator it = properties.iterator();
		while (it.hasNext()) {
			MetaProperty property = (MetaProperty) it.next();
			if (!Is.empty(getParent()) && !Is.empty(getParent().getModelName()) ){
				String labelId = property.getLabelId();
				if (Is.empty(labelId)) continue;
				String result = labelId.replace(
					getModelName() + ".tab.properties", 
					getParent().getModelName() + "." + getMemberName());
				if (Labels.existsExact(result, Locales.getCurrent())) property.setLabelId(result);
			}
		}	
	}
	
	private void assertRepresentsCollection(String method) {
		if (!isRepresentsCollection()) {
			throw new IllegalStateException(
				XavaResources.getString("represents_collections_required_in_view", method));
		}
	}
		
	private String createBaseConditionForCollectionTab() throws XavaException {
		String referenceToParent = getMetaCollection().getMetaReference().getRole();
		Collection keyNames = getParent().getMetaModel().getAllKeyPropertiesNames();
		StringBuffer condition = new StringBuffer();
		for (Iterator it = keyNames.iterator(); it.hasNext();) {
			condition.append("${");
			condition.append(referenceToParent);
			condition.append('.');
			condition.append(it.next());
			condition.append("} = ?");
			if (it.hasNext()) {
				condition.append(" and ");
			}			
		}				
		return condition.toString();
	}
	
	/**
	 * Meta data about the collection, only if this view represents a collection. <p>
	 * 
	 * @since 4m6
	 */	
	// Before 4m6 it was private
	public MetaCollection getMetaCollection() throws XavaException { 
		assertRepresentsCollection("getMetaCollection()");
		return getParent().getMetaModel().getMetaCollection(getMemberName());
	}


	/**
	 * A list of all collection element when each element is a map 
	 * with the values of the collection element.<p>
	 * 
	 * In order to call this method <b>this</b> view must represents a collection</b>.<p>
	 * 
	 * The values only include the displayed data in the row.<br>
	 * @return  Of type <tt>Map</tt>. Never null.
	 */	
	// 
	public List<Map<String, Object>> getCollectionValues() throws XavaException {
		if (collectionValues == null) { 			
			assertRepresentsCollection("getCollectionValues()");
			if (getMetaCollection().isElementCollection()) collectionValues = new ArrayList<>(); 
			else if (isCollectionFromModel() ||	!isDefaultListActionsForCollectionsIncluded() || !isDefaultRowActionsForCollectionsIncluded()) {				
				// If calculated we obtain the data directly from the model object
				Map mapMembersNames = new HashMap();
				mapMembersNames.put(getMemberName(), new HashMap(getCollectionMemberNames()));
				try	{
					Map mapReturnValues = null;
					Map mapKeys = getParent().getKeyValues();					
					if (null != mapKeys && !mapKeys.isEmpty() && model == null) { 
						mapReturnValues = MapFacade.getValues(getParent().getModelName(), mapKeys, mapMembersNames);	
					}
					else {
						// get transient view object model so that it might be used instead of keyValues, what is more fill
						// it with data so that accessory methods might be used on current view members values
						Object oParentObject = getParent().model;
						if (oParentObject == null) {
							oParentObject = getParent().getMetaModel().getPOJOClass().newInstance();
						}
						getParent().getMetaModel().fillPOJO(oParentObject, getParent().getValues());
						mapReturnValues = MapFacade.getValues(getParent().getModelName(), oParentObject, mapMembersNames);
					}
					collectionValues = (List) mapReturnValues.get(getMemberName());
				}
				catch (ObjectNotFoundException ex) { // New one is creating
					collectionValues = Collections.EMPTY_LIST;
				}
				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					getErrors().add("collection_error", getMemberName());
					throw new XavaException("collection_error",	getMemberName());
				}
			}
			else {
				// If not calculated we obtain the data from the Tab			
				collectionValues = getCollectionValues(getCollectionTab().getAllKeys()); 
			}
		}
		return collectionValues; 
	}
	
	/**
	 * The size of the collection. <p>
	 * 
	 * In order to call this method <b>this</b> view must represents a collection.
	 * 
	 * @since 4m5
	 */	
	public int getCollectionSize() throws XavaException {
		return getCollectionSize(true); 
	}	
	
	private int getCollectionSize(boolean fromCurrentTableModel) throws XavaException { 
		if (collectionSize < 0) { // The cache is for when the user changes to a section with just a collection (so with the counter on the tab) not to execute an SELECT COUNT(*) several times 
			assertRepresentsCollection("getCollectionSize()");
			if (isCollectionFromModel() ||	!isDefaultListActionsForCollectionsIncluded() || !isDefaultRowActionsForCollectionsIncluded()) { 
				collectionSize = getCollectionValues().size();
			}
			else {
				// If not calculated we obtain the data from the Tab
				if (getRequest().getAttribute(Tab.TAB_RESETED_PREFIX + getCollectionTab()) == null) {
					getCollectionTab().reset();
				}
				if (fromCurrentTableModel) collectionSize = getCollectionTab().getTotalSize();
				else {
					try {
						collectionSize = getCollectionTab().getAllDataTableModel().getTotalSize(); // We use getAllDataTableModel() to reduce the amount of SELECTs when change the view entity
																	// with sections with record count in tab. In this way we only add one SELECT COUNT(*) for each section with count.
					}
					catch (Exception ex) {
						log.warn(XavaResources.getString("tab_size_warning"),ex);
						collectionSize = getCollectionTab().getTotalSize();
					}
				}
			}
		}
		return collectionSize ;
	}
	
	/**
	 * 
	 * @since 5.9
	 */
	public String getLabelDecoration() { 
		if (!isSection()) return "";
		if (getMetaMembers().size() == 1) { 
			MetaMember member = getMetaMembers().iterator().next();
			if (member instanceof MetaCollection) {
				View collectionView = getSubview(member.getName());
				return "(" + collectionView.getCollectionSize(false) + ")";
			}
		}
		return "";
	}
	
	/**
	 * @since 4.3
	 */
	public Object getCollectionTotal(int row, int column) {
		return getCollectionTotal(getMetaPropertiesList().get(column).getName(), row);
	}	
		
	/**
	 * @since 4.3
	 */	
	public Object getCollectionTotal(String qualifiedPropertyName, int index) {
		assertRepresentsCollection("getCollectionTotal()");
		return getCollectionTotalImpl(getCollectionTotals(), qualifiedPropertyName, index);
	}
	
	private Object getCollectionTotalOldValue(int row, int column) { 
		return getCollectionTotalOldValue(getMetaPropertiesList().get(column).getName(), row);
	}	
	
	
	private Object getCollectionTotalOldValue(String qualifiedPropertyName, int index) { 
		return getCollectionTotalImpl(oldCollectionTotals, qualifiedPropertyName, index);
	}
	
	private Object getCollectionTotalImpl(Map totalValues, String qualifiedPropertyName, int index) {      
		List<String> totalProperties = getTotalProperties().get(qualifiedPropertyName); 
		if (totalProperties != null && !totalProperties.isEmpty()) {			
			String totalProperty = totalProperties.get(index);
			if (totalValues == null) return null;
			return totalValues.get(removeTotalPropertyPrefix(totalProperty));
		}
		throw new XavaException("total_properties_not_found", qualifiedPropertyName, getMemberName());
	}

	private Map getCollectionTotals() {
		if (collectionTotals == null) {
			try { 
				Map memberNames = new HashMap();
				for (List<String> propertyList: getTotalProperties().values()) {
					for (String property: propertyList) {
						if (!property.startsWith("__SUM__")) { 
							memberNames.put(removeTotalPropertyPrefix(property), null);
						}
					}				
				}						
				if (isRepresentsElementCollection()) {
					collectionTotals = MapFacade.getValues(getParent().getModelName(), getParent().getTransientPOJO(), memberNames);
				}
				else {
					Map key = getParent().getKeyValues();
					if (hasNull(key)) {
						collectionTotals = Collections.EMPTY_MAP;
					}
					else {
						try {
							collectionTotals = MapFacade.getValues(getParent().getModelName(), key, memberNames);
						}
						catch (javax.ejb.ObjectNotFoundException ex) {
							collectionTotals = Collections.EMPTY_MAP;
						}				
					}
				}
			}
			catch (Throwable ex) {
				log.warn(XavaResources.getString("total_problem"),ex); 
				return null; 
			} 

			for (List<String> propertyList: getTotalProperties().values()) {
				for (String property: propertyList) {
					if (property.startsWith("__SUM__")) {
						if (collectionTotals == null || collectionTotals == Collections.EMPTY_MAP) collectionTotals = new HashMap();
						String propertyName = removeTotalPropertyPrefix(property);
						collectionTotals.put(propertyName, calculateCollectionSum(propertyName)); 
					}
				}				
			}
		}
		return collectionTotals;
	}	
	
	private Object calculateCollectionSum(String property) {
		BigDecimal sum = new BigDecimal("0");  
		List<Map<String, Object>> values = getCollectionValues(); 
		for (Map entry: values) {
			Object ovalue = Maps.getValueFromQualifiedName(entry, property);
			if (ovalue != null) { 
				BigDecimal value = ovalue instanceof BigDecimal?(BigDecimal) ovalue:new BigDecimal(ovalue.toString());
				sum = sum.add(value); 
			}
		}
		return sum;
	}

	private boolean hasNull(Map key) {
		for (Object value: key.values()) {
			if (value == null) return true;
		}
		return false;
	}

	private String removeTotalPropertyPrefix(String totalProperty) {
		if (totalProperty.startsWith("__SUM__")) return totalProperty.substring("__SUM__".length()); 
		return getMetaCollection().removeTotalPropertyPrefix(totalProperty);
	}
	
	
	/**
	 * @since 4.3
	 */
	public boolean hasCollectionTotal(int row, int column) { 
		assertRepresentsCollection("hasCollectionTotal()");
		if (column >= getMetaPropertiesList().size()) return false;
		MetaProperty p = getMetaPropertiesList().get(column);
		if (getTotalProperties().containsKey(p.getName())) {		
			return row < getTotalProperties().get(p.getName()).size(); 
		}		
		return false;
	}
	
	/**
	 * @since 5.9
	 */
	public boolean hasCollectionTotals() { 
		assertRepresentsCollection("hasCollectionTotal()");
		if (getCollectionTotalsCount() == 0) return false;
		if (getCollectionTotalsCount() > 1) return true;
		return !getTotalProperties().isEmpty(); 
	}
	
	/**
	 * @since 5.9
	 */
	public boolean hasCollectionSum(int column) { 
		assertRepresentsCollection("hasCollectionSum()");
		if (column >= getMetaPropertiesList().size()) return false;
		MetaProperty p = getMetaPropertiesList().get(column);
		return getSumProperties().contains(p.getName());
	}
	
	/**
	 * 
	 * @since 4.3
	 */
	public int getCollectionTotalsCount() { 
		assertRepresentsCollection("getCollectionTotalsCount()");
		if (collectionTotalsCount < 0) {
			collectionTotalsCount = 0;
			for (List list: getTotalProperties().values()) {
				collectionTotalsCount = Math.max(collectionTotalsCount, list.size()); 
			}
			if (collectionTotalsCount == 0) collectionTotalsCount = isAnyCollectionPropertyTotalCapable()?1:0; 
		}
		return collectionTotalsCount;
	}
	
	
	/**
	 * @since 5.9
	 */
	public Map<String, List<String>> getTotalProperties() {
		if (totalProperties == null) { 
			MetaCollectionView metaCollectionView = getParent().getMetaView().getMetaCollectionView(getMemberName());
			totalProperties = metaCollectionView==null?Collections.EMPTY_MAP:metaCollectionView.getTotalProperties();
			boolean collectionFromModel = isCollectionFromModel();
			Collection<String> sumProperties = collectionFromModel?getSumProperties():getCollectionTab().getSumPropertiesNames();
			if (!sumProperties.isEmpty()) {
				totalProperties = Maps.recursiveCloneWithCollections(totalProperties);
				for (MetaProperty p: getMetaPropertiesList()) {
					if (sumProperties.contains(p.getName())) {
						List properties = totalProperties.get(p.getName());
						if (properties != null) properties.add(0, "__SUM__" + p.getName());
						else if (collectionFromModel) totalProperties.put(p.getName(), Strings.toList("__SUM__" + p.getName())); 
					}
				}
			}
		}
		return totalProperties;
	}
	
	/**
	 * @since 5.9
	 */
	public void addCollectionSumProperty(String property) { 
		assertRepresentsCollection("addCollectionSumProperty()");
		getSumProperties().add(property);
		collectionTotals = null;
		totalProperties = null;
		saveSumProperties();
		refreshCollection();
	}	
	
	/**
	 * @since 5.9
	 */
	public void removeCollectionSumProperty(String property) { 
		assertRepresentsCollection("removeCollectionSumProperty()");
		getSumProperties().remove(property);
		collectionTotals = null;
		totalProperties = null;
		saveSumProperties();
		refreshCollection();		
	}
	
	private void addCollectionDefaultSumProperty(String property) { 
		if (defaultSumProperties == null) {
			defaultSumProperties = new StringBuffer();
			defaultSumProperties.append(property);
		}
		else {
			defaultSumProperties.append(',');
			defaultSumProperties.append(property);
		}
		
	}
	
	private Collection<String> getSumProperties() { 
		if (sumProperties == null) sumProperties = loadSumProperties();
		return sumProperties;
	}
	
	private Collection<String> loadSumProperties() { 
		try {
			String properties = getPreferences().get(SUM_PROPERTIES, defaultSumProperties==null?"":defaultSumProperties.toString());
			return Strings.toSet(properties);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_load_sum_properties"),ex);  
			return new HashSet();
		}
	}
	
	private void saveSumProperties() { 
		try {
			Preferences pref = getPreferences();  
			pref.put(SUM_PROPERTIES, Strings.toString(sumProperties));
			pref.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_save_sum_properties"),ex); 
		}
	}
	
	private boolean isAnyCollectionPropertyTotalCapable() { 
		int count = getMetaPropertiesList().size();
		for (int c=0; c<count; c++) {
			if (isCollectionTotalCapable(c)) return true;
		}
		return false;
	}
	
	public boolean isCollectionTotalCapable(int column) {  
		assertRepresentsCollection("isCollectionTotalCapable()");
		return getMetaPropertiesList().get(column).isNumber();
	}
	
	public boolean isCollectionFixedTotal(int column) {
		assertRepresentsCollection("isCollectionFixedTotal()"); 
		return !getSumProperties().contains(getMetaPropertiesList().get(column).getName());
	}

	
	/**
	 * @since 4.3
	 */
	public String getCollectionTotalLabel(int row, int column) { 
		assertRepresentsCollection("getCollectionTotalLabel()"); 
		try {
			MetaProperty columnProperty = getMetaPropertiesList().get(column);
			String rawTotalProperty = getTotalProperties().get(columnProperty.getName()).get(row);
			if (rawTotalProperty.startsWith("__SUM__")) {
				return XavaResources.getString("sum_of", columnProperty.getLabel());
			}
			else {
				String totalPropertyName = removeTotalPropertyPrefix(rawTotalProperty);
				return getParent().getMetaModel().getMetaProperty(totalPropertyName).getLabel();
			}
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("total_label_warning"), ex);
			return "";
		}
	}		
	
	public String getCollectionTotalPropertyName(int row, int column) {    
		assertRepresentsCollection("getCollectionTotalProperty()"); 
		MetaProperty columnProperty = getMetaPropertiesList().get(column);
		List<String> totalPropertiesForColumn = getTotalProperties().get(columnProperty.getName());
		if (totalPropertiesForColumn == null) return ""; 
		String rawTotalProperty = totalPropertiesForColumn.get(row);
		if (rawTotalProperty.startsWith("__SUM__")) rawTotalProperty += "_SUM_"; 
		return removeTotalPropertyPrefix(rawTotalProperty);
	}
	
	public boolean isCollectionTotalEditable(int row, int column) { 
		assertRepresentsCollection("isCollectionTotalEditable()");
		String columnProperty = getMetaPropertiesList().get(column).getName();
		List<String> totalProperties = getTotalProperties().get(columnProperty);
		if (totalProperties == null) return false;
		String totalProperty = totalProperties.get(row);
		if (totalProperty.startsWith("__SUM__")) return false; 
		try {
			return !getTotalMetaProperty(totalProperty).isCalculated();
		}
		catch (ElementNotFoundException ex) { // For summation properties, such as amount+
			return false;
		}
	}
	
	private MetaProperty getTotalMetaProperty(String qualifiedPropertyName) {  
		String totalProperty = removeTotalPropertyPrefix(qualifiedPropertyName); 
		return getParent().getMetaModel().getMetaProperty(totalProperty);
	}
		
	/**
	 * A list of selected collection element when each element is a map 
	 * with the values of the collection element.<p>
	 * 
	 * In order to call this method <b>this view must represents a collection</b>.<p>
	 * 
	 * The values only include the displayed data in the row.<br>
	 * @return  Of type <tt>Map</tt>. Never null.
	 */
	public List getCollectionSelectedValues() throws XavaException {
		assertRepresentsCollection("getCollectionSelectedValues()");
		if (isCollectionFromModel()) {
			// If calculated we obtain the data directly from the model object
			if (listSelected == null) return Collections.EMPTY_LIST;
			List result = new ArrayList();
			List all = getCollectionValues();
			for (int i=0; i<listSelected.length; i++) {
				result.add(all.get(listSelected[i]));
			}
			return result;
		}
		else { 
			// If not calculated we obtain the data from the Tab
			Map[] selectedKeys = getCollectionTab().getSelectedKeys();
			return selectedKeys == null ? Collections.EMPTY_LIST : getCollectionValues(selectedKeys);
		}
	}

	private List getCollectionValues(Map [] keys) throws XavaException { 
		List result = new ArrayList();
		Map memberNames = new HashMap(getCollectionMemberNames());
		for (int i = 0; i < keys.length; i++) {			
			try {
				Map values = MapFacade.getValues(getModelName(), keys[i], memberNames);
				result.add(values);				
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				getErrors().add("collection_error", getMemberName());
				throw new XavaException("collection_error", getMemberName());	
			}				
		}
		return result;
	}
	
	/**
	 * A list of all objects (POJOs or EntityBeans) in the collection.<p>
	 * 
	 * In order to call this method <b>this view must represents a collection</b>.<p>
	 * 
	 * Generally the objects are POJOs, although if you use EJBPersistenceProvider
	 * the they will be EntityBeans (of EJB2).<br> 
	 *  
	 * @return  Never null.
	 */		
	public List getCollectionObjects() throws XavaException {   		
		assertRepresentsCollection("getCollectionObjects()");		
		Map [] keys = null;
		if (isCollectionFromModel()) { 
			try {
				Object model = getParent().getModel();
				if (model == null) {
					model = getParent().getEntity();
					PersistenceFacade.refreshIfManaged(model);
				}
				PropertiesManager modelProperties = new PropertiesManager(model);
				return new ArrayList((Collection) modelProperties.executeGet(getMemberName()));
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("collection_error", getMemberName()), ex);				
				throw new XavaException("collection_error", getMemberName());
			}				
		}
		else {				
			keys = getCollectionTab().getAllKeys(); 
		}
		return getCollectionObjects(keys);						
	}
	
	/**
	 * A list of selected objects (POJOs or EntityBeans) in the collection.<p>
	 * 
	 * In order to call this method <b>this view must represents a collection</b>.<p>
	 * 
	 * Generally the objects are POJOs, although if you use EJBPersistenceProvider
	 * the they will be EntityBeans (of EJB2).<br> 
	 *  
	 * @return  Never null.
	 */
	public List getCollectionSelectedObjects() throws XavaException {  
		assertRepresentsCollection("getCollectionSelectedObjects()");		
		Map [] selectedKeys = null;
		if (isCollectionFromModel()) {
			if (listSelected == null) return Collections.EMPTY_LIST;
			List selectedObjects = new ArrayList();
			List objects = getCollectionObjects();
			for (int i=0; i<listSelected.length; i++) {
				selectedObjects.add(objects.get(listSelected[i]));
			}
			return selectedObjects;
		}
		else {				
			return getCollectionObjects(getCollectionTab().getSelectedKeys());
		}						
	}

	private List getCollectionObjects(Map[] keys) throws XavaException {
		List result = new ArrayList();		
		for (int i = 0; i < keys.length; i++) {			
			try {
				Object object = MapFacade.findEntity(getModelName(), keys[i]);
				result.add(object);				
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				getErrors().add("collection_error", getMemberName()); 
				throw new XavaException("collection_error", getMemberName()); 									
			}			
		}
		return result;
	}
	
	private List getCollectionObjects(Collection<Map> keys) throws XavaException { 
		List result = new ArrayList();
		if (keys == null || keys.isEmpty()) return result;
		for (Map key : keys) {			
			try {
				Object object = MapFacade.findEntity(getModelName(), key);
				result.add(object);				
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				getErrors().add("collection_error", getMemberName()); 
				throw new XavaException("collection_error", getMemberName()); 									
			}			
		}
		return result;
	}
	
	/**
	 * If the collection data is obtained from the model collection directly. <p>
	 * 
	 * If not is obtained from model usually a Tab is used to get the data.<br>
	 * 
	 * In order to call this method <b>this view must represents a collection</b>.<p>
	 * 
	 * @since 5.3
	 */
	public boolean isCollectionFromModel() throws XavaException {  
		assertRepresentsCollection("isCollectionFromModel()");
		if (!getMetaCollection().getMetaModel().isPOJOAvailable()) return false;
		return getMetaCollection().hasCalculator() || getMetaCollection().isSortable(); 
	}
	
	/**
	 * If the collection represents by this view is calculated. <p>
	 * 
	 * In order to call this method <b>this view must represents a collection</b>.<p>
	 * 
	 * @deprecated  Since 5.3, use isCollectionFromModel() instead. 
	 */
	public boolean isCollectionCalculated() throws XavaException {
		assertRepresentsCollection("isCollectionCalculated()");
		return isCollectionFromModel(); // Not the most exact but the most practical, 
										// because this method was used to choose between using tab or not
	}

	
	private Map getCollectionMemberNames() throws XavaException {
		if (collectionMemberNames == null) {   		
			Map result = new HashMap();			
			Iterator it = getMetaPropertiesList().iterator();
			while (it.hasNext()) {
				MetaProperty pr = (MetaProperty) it.next();
				String propertyName = pr.getName();				
				addQualifiedMemberToMap(propertyName, result);
			}	
			collectionMemberNames = Collections.unmodifiableMap(result); 
		}	
		return collectionMemberNames;
	}
	
	private void addQualifiedMemberToMap(String propertyName, Map collectionMemberNames) { 
		int idx = propertyName.indexOf('.');
		if (idx < 0) {
			collectionMemberNames.put(propertyName, null);				
		}
		else {
			String referenceName = propertyName.substring(0, idx);
			String referencePropertyName = propertyName.substring(idx+1);			
			Map ref = (Map) collectionMemberNames.get(referenceName);
			if (ref == null) {
				ref = new HashMap();
				collectionMemberNames.put(referenceName, ref);
			}
			if (referencePropertyName.indexOf('.') < 0) {
				ref.put(referencePropertyName, null);				
			}
			else {
				addQualifiedMemberToMap(referencePropertyName, ref);
			}												
		}			
	}
	
	public Collection getMembersNamesWithoutSectionsAndCollections() throws XavaException {   
		if (membersNamesWithoutSectionsAndCollections == null) {
			Iterator it = createMetaMembers(true).iterator();						
			membersNamesWithoutSectionsAndCollections = new ArrayList();
			while (it.hasNext()) {
				MetaMember m = (MetaMember) it.next();
				if (isMetaProperty(m)) {					
					membersNamesWithoutSectionsAndCollections.add(m.getName());
				}
				else if (m instanceof MetaReference) {										
					membersNamesWithoutSectionsAndCollections.add(m.getName());					
				}
				else if (m instanceof MetaGroup && !isHidden(m.getName())) {  
					membersNamesWithoutSectionsAndCollections.addAll(getGroupView(((MetaGroup) m).getName()).getMembersNamesWithoutSectionsAndCollections());					
				}
			}					
		}
		return membersNamesWithoutSectionsAndCollections;
	}
	

	private Collection getMembersNamesWithoutSections() throws XavaException {   
		if (membersNamesWithoutSections==null) { 	
			Iterator it = createMetaMembers(true).iterator();						
			membersNamesWithoutSections = new ArrayList();
			while (it.hasNext()) {
				MetaMember m = (MetaMember) it.next();
				if (isMetaProperty(m)) {		
					membersNamesWithoutSections.add(m.getName());
				}
				else if (m instanceof MetaReference) {				
					membersNamesWithoutSections.add(m.getName());					
				}
				else if (m instanceof MetaCollection) {				
					membersNamesWithoutSections.add(m.getName());
				}				
				else if (m instanceof MetaGroup && !isHidden(m.getName())) {
					membersNamesWithoutSections.addAll(getGroupView(((MetaGroup) m).getName()).getMembersNamesWithoutSections());					
				}
			}					
		}

		return membersNamesWithoutSections;
	}

	/**
	 * Clear all data and set the default values.
	 */
	public void reset() throws XavaException {
		createSubviews();
		clear();		
		resetCollections(true); 	
		calculateDefaultValues(true);
		getRoot().dataChanged = false;
		 
	}
	
	/**
	 * If this view represent a polymorphic model, this method return the base class
	 * of the hierarchy. <p>
	 * 
	 * It can return just modelName.
	 * 
	 * @since 4.5.1
	 */
	public String getBaseModelName() {
		return rootModelName==null?getModelName():rootModelName; 
	}
		
	/**
	 * Clear all displayed data.  
	 */
	public void clear() throws XavaException {
		if (rootModelName != null) {
			String viewName = getViewName(); 
			setModelName(rootModelName); 
			setViewName(viewName);  
			rootModelName = null;
		}

		if (isRepresentsElementCollection() && (collectionValues != null && !collectionValues.isEmpty())) refreshCollection();
		collectionValues = null;
		collectionTotals = null;
		collectionSize = -1; 
		setIdFocusProperty(null);
		setFocusCurrentId(null); 
		setCollectionDetailVisible(false);
		resetRecalculatedProperties();
		if (values == null) return;
		Iterator it = values.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			// The next if is for a bug with Java 8
			if (getMembersNames().containsKey(e.getKey())) values.put(e.getKey(), null); 
			else it.remove();
		}
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.clear();
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.clear();
			}
		}						
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).clear();
			}	
		}		
	}
	
	/**
	 * Set the default values in the empty fields.  
	 */
	private void calculateDefaultValues(boolean firstLevel) throws XavaException {
		if (firstLevel) { 
			getRoot().registeringExecutedActions = true;
		}
		// Properties
		try {					
			Collection properties = new ArrayList(getMetaModel().getMetaPropertiesWithDefaultValueCalculator());			
			properties.addAll(getMetaModel().getMetaPropertiesViewWithDefaultCalculator());			
			if (!properties.isEmpty()) {
				Map membersNames = getMembersNames(); 
				Iterator it = properties.iterator();
				Collection alreadyPut = new ArrayList();				
				while (it.hasNext()) {
					MetaProperty p = (MetaProperty) it.next();
					if (p.hasCalculatorDefaultValueOnCreate()) continue;  
					if (membersNames.containsKey(p.getName()) || isTotalPropertyInAnyCollection(p.getName())) { 
						try {
							if (!p.getMetaCalculatorDefaultValue().containsMetaSetsWithoutValue()) { // This way to avoid calculate the dependent ones
								ICalculator calculator = p.createDefaultValueCalculator();
								if (calculator instanceof IJDBCCalculator) {
									((IJDBCCalculator) calculator).setConnectionProvider(DataSourceConnectionProvider.getByComponent(getModelName()));
								}		
								trySetValue(p.getName(), calculator.calculate());
								alreadyPut.add(p.getName());
							}					
						}
						catch (Exception ex) {
							log.error(ex.getMessage(), ex);
							getErrors().add("calculate_default_value_error", p.getName());
						}				 
					}
				}			
				
				if (!alreadyPut.isEmpty()) {
					Iterator itAlreadyPut = alreadyPut.iterator();					
					boolean hasNext = itAlreadyPut.hasNext(); 
					while (hasNext) {												 
						String propertyName = (String) itAlreadyPut.next();
						try {
							hasToSearchOnChangeIfSubview = false;
							propertyChanged(propertyName);							
						}
						finally {
							hasToSearchOnChangeIfSubview = true;						
						}						
						hasNext = itAlreadyPut.hasNext(); // Loop in this way to bypass a bug in Websphere 5.0.2.9 JDK						
					}					
				}								
			}
				
			// On change events					
			Iterator itOnChangeProperties = getMetaView().getPropertiesNamesThrowOnChange().iterator();			
			while (itOnChangeProperties.hasNext()) {
				String propertyName = (String) itOnChangeProperties.next();
				propertyChanged(propertyName);
			}			
					
			// Subviews		
			Iterator itSubviews = getSubviews().values().iterator();			
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				if (subview.isRepresentsElementCollection()) subview.calculateDefaultValues(false);
				else if (subview.isRepresentsCollection()) continue;
				else if (subview.isRepresentsAggregate()) { 
					subview.calculateDefaultValues(false);
				}
				else { // Reference to entity
					subview.clear();
				}
			}			
					
			// Groups		
			Iterator itGroups = getGroupsViews().values().iterator();			
			while (itGroups.hasNext()) {
				View group = (View) itGroups.next(); 
				group.calculateDefaultValues(false);
			}			
					
			// Sections		
			if (hasSections()) {
				int count = getSections().size();
				for (int i = 0; i < count; i++) {
					getSectionView(i).calculateDefaultValues(false);
				}	
			}
			
			// References			
			Collection references = getMetaModel().getMetaReferencesWithDefaultValueCalculator();				
			if (!references.isEmpty()) {		
				Map membersNames = getMembersNames();
				Iterator it = references.iterator();
				Collection alreadyPut = new ArrayList();						
				while (it.hasNext()) {
					MetaReference ref = (MetaReference) it.next();
					if (membersNames.containsKey(ref.getName())) {
						try {							
							if (!ref.getMetaCalculatorDefaultValue().containsMetaSetsWithoutValue()) { // This way to avoid calculated dependend ones
								Object value = ref.getDefaultValueCalculator().calculate();
								MetaModel referencedModel = ref.getMetaModelReferenced();								
								if (referencedModel.getPOJOClass().isInstance(value)) { 																								
									Map values = referencedModel.toMap(value);
									trySetValue(ref.getName(), values); 
									alreadyPut.addAll(referencedModel.getAllKeyPropertiesNames());									
								}
								else {
									Collection keys = referencedModel.getAllKeyPropertiesNames();
									if (keys.size() != 1) {
										throw new XavaException("reference_calculator_with_multiple_key_requires_key_class", ref.getName(), referencedModel.getPOJOClass());
									}
									String propertyKeyName = ref.getName() + "." + (String) keys.iterator().next();
									trySetValue(propertyKeyName, value); 
									alreadyPut.add(propertyKeyName);
								}
							}					
						}
						catch (Exception ex) {
							log.error(ex.getMessage(), ex);
							getErrors().add("calculate_default_value_error", ref.getName());
						}				 
					}
				}				
				if (!alreadyPut.isEmpty()) { 
					Iterator itAlreadyPut = alreadyPut.iterator();
					boolean hasNext = itAlreadyPut.hasNext(); 
					while (hasNext) {
						String propertyName = (String) itAlreadyPut.next();										
						try {
							hasToSearchOnChangeIfSubview = false;
							propertyChanged(propertyName);
							
						}
						finally {
							hasToSearchOnChangeIfSubview = true;						
						}
						hasNext = itAlreadyPut.hasNext(); // Loop in this way to bypass a bug in Websphere 5.0.2.9 JDK
					}
				}
			}
		}
		finally {						
			if (firstLevel) { 
				getRoot().registeringExecutedActions = false;		
				resetExecutedActions();
			}			
		}
	}

	private boolean isTotalPropertyInAnyCollection(String name) { 
		for (View subview: getSubviews().values()) {
			if (subview.isRepresentsCollection()) {
				for (List<String> properties: subview.getTotalProperties().values()) {
					for (String property: properties) {
						if (!property.startsWith("__SUM__")) {
							String pureProperty = subview.getMetaCollection().removeTotalPropertyPrefix(property);
							if (pureProperty.equals(name)) return true;
						}
					}
				}
			}
		}
		return false;		
	}

	private void resetExecutedActions() {		
		if (getRoot().executedActions != null) getRoot().executedActions.clear();		
	}
	
	private void registerExecutedAction(String name, Object action) {
		if (!getRoot().registeringExecutedActions) return;		
		if (getRoot().executedActions == null) getRoot().executedActions = new HashSet();
		getRoot().executedActions.add(getModelName() + "::" + name + "::" + action.getClass());
	}
	
	private boolean actionRegisteredAsExecuted(String name, Object action) {
		if (!getRoot().registeringExecutedActions) return false;
		if (getRoot().executedActions == null) return false;
		return getRoot().executedActions.contains(getModelName() + "::" + name + "::" + action.getClass());
	}

	public boolean isKeyEditable() {
		if (insideAViewDisplayedAsDescriptionsListAndReferenceView()) return false;
		return !isReadOnly() && keyEditable;
	}
	
	

	private boolean insideAViewDisplayedAsDescriptionsListAndReferenceView() {
		if (displayAsDescriptionsListAndReferenceView()) return true;
		if (getParent() == null) return false;
		return getParent().insideAViewDisplayedAsDescriptionsListAndReferenceView();
	}

	public void setKeyEditable(boolean b) throws XavaException {
		keyEditable = b;						
		Collection metaReferencesKey = isRepresentsEntityReference()?getMetaModel().getMetaReferencesKeyAndSearchKey():getMetaModel().getMetaReferencesKey();
		Iterator it = metaReferencesKey.iterator();
		while (it.hasNext()) {
			MetaReference ref = (MetaReference) it.next();
			if (hasSubview(ref.getName())) {
				getSubview(ref.getName()).setKeyEditable(b);
				getSubview(ref.getName()).setEditable(false);
			}
		}

		if (hasGroups()) { 
			it = getGroupsViews().values().iterator();
			while (it.hasNext()) {				
				View subview = (View) it.next();
				subview.setKeyEditable(b);
			}
		}
		
		
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).setKeyEditable(b);
			}	
		}	
						
	}
	

	/**
	 * If at this moment is editable.
	 */
	public boolean isEditable(MetaProperty metaProperty) { 
		if (isEditableImpl(metaProperty)) return true;
		if (isLastSearchKey(metaProperty)) return isKeyEditable();
		return false;
	}
	
	/**
	 * If at this moment is editable.
	 */
	private boolean isEditableImpl(MetaProperty metaProperty) { 		
		try {
			MetaPropertyView metaPropertyView = getMetaView().getMetaPropertyViewFor(metaProperty.getName());
			if (metaPropertyView != null) {
				if (isKeyEditable() && metaPropertyView.isReadOnly() && !metaPropertyView.isReadOnlyOnCreate()) return true;
			}
			if (metaProperty.isReadOnly()) return false;			
			if (metaProperty.isKey() || 
				(metaProperty.isSearchKey() && isRepresentsEntityReference())) 
			{
				return isKeyEditable();
			}
			if (!isEditable()) return false;			
			return isMarkedAsEditable(metaProperty.getName());
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("readonly_not_know_warning", metaProperty),ex);
			return false;
		}		
	}

	
	/**
	 * If at this moment is editable.
	 */
	public boolean isEditable(MetaReference metaReference) {
		try {
			MetaReferenceView metaReferenceView = getMetaView().getMetaReferenceView(metaReference);
			if (metaReferenceView != null && isKeyEditable() && metaReferenceView.isReadOnly() && !metaReferenceView.isReadOnlyOnCreate()) {
				setEditable(metaReference.getName(), true); 
				return true;
			}
			if (metaReferenceView != null && metaReferenceView.isReadOnly()) return false;
			if (metaReference.isKey() || 
				(metaReference.isSearchKey() && isRepresentsEntityReference())) 
			{
				return isKeyEditable(); 				
			}
			if (!isEditable()) return false;				
			return isMarkedAsEditable(metaReference.getName());
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("readonly_not_know_warning", metaReference),ex);
			return false;
		}		
	}
	
	public boolean isEditable(String member) throws XavaException {
		int idx = member.indexOf('.'); 
		if (idx >= 0) {
			String compoundMember = member.substring(0, idx); 
			String submember = member.substring(idx + 1);
			try {
				View subview = getSubview(compoundMember);
				if (subview.isRepresentsElementCollection()) submember = Strings.noFirstTokenWithoutFirstDelim(submember, ".");
				return subview.isEditable(submember);
			}
			catch (ElementNotFoundException ex) {
				// Maybe a custom JSP view wants access to a property not showed in default view
				MetaModel referencedModel = getMetaModel().getMetaReference(compoundMember).getMetaModelReferenced(); 				
				return (referencedModel instanceof MetaAggregate) ||
					referencedModel.isKey(submember);
			}		
		}		
		try {
			return isEditable(getMetaProperty(member));
		} 
		catch (ElementNotFoundException ex) {
			try {
				return isEditable(getMetaView().getMetaModel().getMetaReference(member));
			}
			catch (ElementNotFoundException ex2) {
				throw new ElementNotFoundException("member_not_found_in_view", member, getViewName(), getModelName());
			}
		}
	}
	
	private boolean isMarkedAsEditable(String name) {
		if (notEditableMembersNames == null) return true;
		return !getNotEditableMembersNames().contains(name);
	}
	
	/**
	 * To make an element of the user interface editable or not editable. 
	 * <br/>
	 * You can use it for properties, references (since v5.5) and collections (since v5.5). 
	 * The name of the member can be qualified (since v6.2).
	 * <br/>
	 * 
	 * @param name  Name (can be qualified) of the property, reference or section
	 * @param editable  true to make it editable, false to make it read only
	 */
	public void setEditable(String name, boolean editable) throws XavaException {
		int idx = name.indexOf('.');
		if (idx >= 0) {
			String ref = name.substring(0, idx);
			String member = name.substring(idx + 1);
			getSubview(ref).setEditable(member, editable);
			return;
		}

		if (editable) getNotEditableMembersNames().remove(name);
		else getNotEditableMembersNames().add(name);
		
		if (hasSubview(name)) {
			View subview = getSubview(name);
			if (subview.isRepresentsEntityReference()) subview.setKeyEditable(editable);
		}
		
		if (hasGroups()) {
			Iterator it = getGroupsViews().values().iterator();
			while (it.hasNext()) {
				View v = (View) it.next();
				v.setEditable(name, editable);
			}
		}
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).setEditable(name, editable);
			}	
		}						
	}
	
	public boolean isEditable() { 
		if (isReadOnly()) return false;
		if (isRepresentsAggregate()) {
			Set parentNotEditableMembersNames = getParentIfSectionOrGroup().getParent().notEditableMembersNames; 
			if (parentNotEditableMembersNames != null) {
				if (parentNotEditableMembersNames.contains(getMemberName())) return false;
			}
		}
		return editable;
	}
	
	public void setEditable(boolean b) throws XavaException {
		editable = b;
			
		for (Iterator it = getSubviews().values().iterator(); it.hasNext();) {				
			View subview = (View) it.next();
			if (subview.isRepresentsCollection()) {
				subview.setCollectionEditable(isMarkedAsEditable(subview.getMemberName())?b:false); 
				if (!subview.collectionMembersEditables || !isMarkedAsEditable(subview.getMemberName())) {
					subview.setKeyEditable(false); 
					subview.setEditable(false); 
				}	
				else {
					subview.setKeyEditable(!subview.isRepresentsEntityReference());
					subview.setEditable(b);					
				}
			}
			else if (subview.isRepresentsEntityReference()) {
				subview.setEditable(false);
				subview.setKeyEditable(isMarkedAsEditable(subview.getMemberName())?b:false);  
			}
			else {
				subview.setEditable(b);
			}						
		}			
			
		for (Iterator it = getGroupsViews().values().iterator(); it.hasNext();) {				
			View subview = (View) it.next();
			subview.setEditable(b); 
		}		
		
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).setEditable(b);
			}	
		}				
	}

	public String getModelName() {		
		return modelName;
	}
	
	public void setModelName(String newModel) {
		if (Is.equal(modelName, newModel)) return;		
		modelName = newModel;
		getFirstLevelView().reloadNeeded = true; // If the model of the view of a reference changes, the main view must be reloaded.
		resetMembers();		
		if (model != null && !model.getClass().getSimpleName().equals(modelName)) model = null; 
	}
	
	private void resetMembers() {
		viewName = null;
		membersNames = null;
		collectionMemberNames = null;
		membersNamesWithoutSections = null;
		membersNamesWithoutSectionsAndCollections = null;
		membersNamesWithHidden = null;
		membersNamesInGroup = null;
		metaModel = null;
		metaMembers = null;
		metaMembersIncludingHiddenKey = null;
		metaMembersIncludingCollectionTotals = null;
		values = null;		
		metaView = null;
		mapStereotypesProperties = null;
		metaProperties = null;
		metaPropertiesIncludingSections = null;
		metaPropertiesIncludingGroups = null;
		metaPropertiesQualified = null;
		calculatedPropertiesNames = null;
		lastPropertyKeyName = null;		
		depends = null;
		subviews = null;
		sectionsViews = null;
		sections = null; 
		hiddenMembers = null; 
		groupsViews = null;
		polished = false; 
	}
	
	public void assignValuesToWebView() {
		assignValuesToWebView("", true);
	}
		
	private void assignValuesToWebView(String qualifier, boolean firstLevel) {
		try {
			this.firstLevel = firstLevel; 
			formattedProperties = null; 
			focusForward = "true".equalsIgnoreCase(getRequest().getParameter("xava_focus_forward"));
			setIdFocusProperty(getRequest().getParameter("xava_previous_focus")); 
			setFocusCurrentId(getRequest().getParameter("xava_current_focus")); 
			if (isRepresentsCollection()) fillCollectionInfo(qualifier);			
			if (firstLevel) changedProperty = Ids.undecorate(getRequest().getParameter("xava_changed_property"));
			if (firstLevel || !isRepresentsCollection()) { 
				assignValuesToMembers(qualifier, isSubview()?getMetaMembersIncludingHiddenKey():getMetaMembersIncludingCollectionTotals());  
			}
			oldValues = values==null?null:new HashMap(values);
			mustRefreshCollection = false;
			reloadNeeded = false;			
			changedPropertiesActionsAndReferencesWithNotCompositeEditor = null;
			propertiesWithChangedActions = null; 
			sectionChanged = false; 
			oldKeyEditable = keyEditable; 
			oldEditable = editable;
			changedLabels = null;
			refreshDescriptionsLists = false;
			oldNotEditableMembersNames = notEditableMembersNames==null?null:new HashSet(notEditableMembersNames);
			
			throwElementCollectionTotalsChanged(); 
						
			if (hasSections()) { 								
				View section = getSectionView(getActiveSection());
				section.assignValuesToWebView(qualifier, false);
			}						
						
			if (firstLevel) {
				if (!Is.emptyString(changedProperty)) {
					getRoot().registeringExecutedActions = true;
					try {		
						propertyChanged(changedProperty);
					}
					finally {
						getRoot().registeringExecutedActions = false;		
						resetExecutedActions();						
					}						
				}			
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			getErrors().add("system_error");
		}
	}
	
	
	private void throwElementCollectionTotalsChanged() { 
		for (View subview: getSubviews().values()) {				
			if (subview.isRepresentsElementCollection()) {
				if (subview.collectionSize < 0 && !subview.getTotalProperties().isEmpty()) {
					for (Collection<String> totalProperties: subview.getTotalProperties().values()) {
						for (String totalProperty: totalProperties) {
							String property = subview.removeTotalPropertyPrefix(totalProperty);
							propertyChanged(property);
						}
					}
				}
			}						
		}			
			
		for (Iterator it = getGroupsViews().values().iterator(); it.hasNext();) {				
			View subview = (View) it.next();
			subview.throwElementCollectionTotalsChanged(); 
		}
	}

	private void assignValuesToMembers(String qualifier, Collection members) {
		for (Object m: members) { 		
			if (isMetaProperty(m)) {
				MetaProperty p = (MetaProperty) m;
				boolean mustToFormat = WebEditors.mustToFormat(p, getViewName()); 
				if (!mustToFormat) continue;
				String propertyKey= qualifier + p.getName();
				String valueKey = propertyKey + ".value";
				String [] results = getRequest().getParameterValues(propertyKey);				
				Object value = WebEditors.parse(getRequest(), p, results, getErrors(), getViewName());
				boolean isHiddenKeyWithoutValue = p.isHidden() && (results == null); // for not reset hidden values					
				if (!isHiddenKeyWithoutValue && mustToFormat) { 
					getRequest().setAttribute(valueKey, value);
					trySetValue(p.getName(), value);
					if (isNeededToVerifyHasBeenFormatted(p)) {
						String formattedValue = WebEditors.format(getRequest(), p, value, getErrors(), getViewName());
						if (results != null && !equals(formattedValue, results[0])) {
							if (formattedProperties == null) formattedProperties = new HashSet();
							formattedProperties.add(p.getName()); 
						}
					}
				}						
			}
			else if (m instanceof MetaReference) {
				MetaReference ref = (MetaReference) m;
				String key = qualifier + ref.getName() + DescriptionsLists.COMPOSITE_KEY_SUFFIX;
				String value = getRequest().getParameter(key);
				if (value == null) {							
					View subview = getSubview(ref.getName());						
					subview.assignValuesToWebView(qualifier + ref.getName() + ".", false);
				}
				else { // References as combo (descriptions-list) and composite key
					if (displayAsDescriptionsListAndReferenceView(ref)) {
						View subview = getSubview(ref.getName());					
						subview.assignValuesToWebView(qualifier + ref.getName() + ".", false);
						assignReferenceValue(qualifier, ref, value, false);
					}
					else {
						assignReferenceValue(qualifier, ref, value, true);
					}			
				}		
			}
			
			else if (m instanceof MetaCollection) {
				MetaCollection collec = (MetaCollection) m;
				View subview = getSubview(collec.getName());
				String collectionQualifier = qualifier + collec.getName() + ".";
				if (((MetaCollection) m).isElementCollection()) {
					subview.assignValuesToElementCollection(collectionQualifier);
				}
				else {
					subview.assignValuesToWebView(collectionQualifier, false);
				}
			}
			else if (m instanceof MetaGroup) {
				MetaGroup group = (MetaGroup) m;
				View subview = getGroupView(group.getName());
				subview.assignValuesToWebView(qualifier, false);
			}
		}
	}
	
	private void assignValuesToElementCollection(String qualifier) {
		if (!isCollectionMembersEditables()) return;
		int oldCount = collectionValues == null?0:collectionValues.size(); 
		List<Map<String, Object>> oldCollectionValues = collectionValues;
		collectionValues = new ArrayList();		
		mustRefreshCollection = false;
		String lastLineScannedProperty = getMetaPropertiesList().get(0).getName();
		boolean lastLineScannedPropertyisDescriptionsList = false;
		String lastLineScannedPropertyBase = lastLineScannedProperty.split("\\.", 2)[0];
		if (getMetaModel().containsMetaReference(lastLineScannedPropertyBase)) {
			MetaReference ref = getMetaModel().getMetaReference(lastLineScannedPropertyBase);
			lastLineScannedPropertyisDescriptionsList = displayAsDescriptionsList(ref);
		}
		if (lastLineScannedPropertyisDescriptionsList || 
			getMetaPropertiesList().get(0).isKey() && 
			getMetaPropertiesList().get(0).getMetaModel().getAllKeyPropertiesNames().size() > 1) 
		{
			lastLineScannedProperty = lastLineScannedPropertyBase;
		}		
		String lastLineKeySuffix = "." + lastLineScannedProperty + "_EDITABLE_";
		int lastLineKeyIncrement = isCollectionEditable()?1:0;
		for (int i=0; ;i++) {
			String lastLineKey = qualifier + (i + lastLineKeyIncrement) + lastLineKeySuffix;
			if (getRequest().getParameterValues(lastLineKey) == null) break;			
			boolean containsReferences = false;
			Set<String> falseBooleans = null; 
			Map element = new HashMap();
			Map originalValues = null; 
			for (MetaProperty p: getMetaPropertiesList()) {
				String propertyKey= qualifier + i + "." + p.getName();
				String [] results = getRequest().getParameterValues(propertyKey);
				if (results == null && p.getName().contains(".")) {
					String refName = Strings.noLastTokenWithoutLastDelim(p.getName(), ".");
					if (p.getMetaModel().getAllKeyPropertiesNames().size() > 1) {						
						propertyKey= qualifier + i + "." + refName + DescriptionsLists.COMPOSITE_KEY_SUFFIX; 
						results = getRequest().getParameterValues(propertyKey);
						if (results != null) {
							MetaReference ref = getMetaModel().getMetaReference(refName); 
							fillReferenceValues(element, ref, results[0], null, ref.getName() + ".");
							containsReferences = true;
							continue;
						}
					}
					else {
						String firstKeyProperty = p.getMetaModel().getAllKeyPropertiesNames().iterator().next();
						propertyKey= qualifier + i + "." + refName + "." + firstKeyProperty;
						results = getRequest().getParameterValues(propertyKey);
						p = p.getMetaModel().getMetaProperty(firstKeyProperty).cloneMetaProperty();
						p.setName(refName + "." + p.getName());
					}
				}		
				if (results == null && (p.getType().equals(boolean.class) || p.getType().equals(Boolean.class))) {
					if (falseBooleans == null) falseBooleans = new HashSet();
					falseBooleans.add(p.getName());
				}
				if (results == null || !WebEditors.mustToFormat(p, getViewName())) { 
					if (oldCollectionValues != null && i < oldCollectionValues.size()) {
						Object originalValue = oldCollectionValues.get(i).get(p.getName());
						if (originalValues == null) originalValues = new HashMap();
						originalValues.put(p.getName(), originalValue);
					}
					continue;
				}
				Object value = WebEditors.parse(getRequest(), p, results, getErrors(), getViewName());
				element.put(p.getName(), value);
				if (p.getName().contains(".")) containsReferences = true;
			}
			
			if (originalValues != null) element.putAll(originalValues);
			if (Maps.isEmpty(element)) continue;
			if (falseBooleans != null) {
				for (String property: falseBooleans) {
					element.put(property, false); 
				}
			}
			if (containsReferences) element = Maps.plainToTree(element);
			collectionValues.add(element);
		}			
		setCollectionEditionRowFromChangedProperty();
		oldCollectionTotals = collectionTotals; 
		moveCollectionValuesToViewValues();
		collectionSize = collectionValues.size(); 
		if (collectionValues.size() != oldCount) {
			collectionTotals = null;
			collectionSize = -1; 
		}
		setOldStateInElementCollection();		
	}

	private void setCollectionEditionRowFromChangedProperty() {  
		String changedProperty = getChangedProperty();
		if (!changedProperty.startsWith(getMemberName() + ".")) return;
		if (!Is.emptyString(changedProperty)) {
			for (String token: changedProperty.split("\\.")) {
				if (StringUtils.isNumeric(token)) {
					collectionEditingRow = Integer.parseInt(token);
					break;
				}
			}
		}		
	}

	private String getChangedProperty() { 
		if (firstLevel) {
			return changedProperty;
		}
		return getParent().getChangedProperty();
	}

	private void setOldStateInElementCollection() {  		
		oldValues = values==null?null:new HashMap(values);
		oldEditable = editable; 
		oldKeyEditable = keyEditable;
		for (View subview: getSubviews().values()) {
			subview.setOldStateInElementCollection();
		}		
	}

	private boolean isNeededToVerifyHasBeenFormatted(MetaProperty p) { 
		// This code can be improved using a property in editors.xml for mark
		// if it's needed to verify the formatted		
		return !p.hasValidValues() && !boolean.class.equals(p.getType()) &&
			!Boolean.class.equals(p.getType());
	}

	private String getQualifiedCollectionName() {
		if (isFirstLevel()) return ""; 
		if (!isRepresentsCollection() && !isRepresentsEntityReference() && !isRepresentsAggregate()) return ""; 
		return getParent().getQualifiedCollectionName() + getMemberName() + "_"; 
	}
	
	private void fillCollectionInfo(String qualifier) throws XavaException { 
		String id = null;
		if (!isCollectionFromModel()) { 
			id = Tab.COLLECTION_PREFIX + getQualifiedCollectionName() + "selected";
			getCollectionTab().setSelected(getRequest().getParameterValues(id));
		}
		else {
			id = qualifier + "__SELECTED__";
		}		
		// Fill selected
		String [] sel = getRequest().getParameterValues(id);
		if (sel == null || sel.length == 0) {
			listSelected = null;
			return;
		}
		listSelected = new int[sel.length];
		for (int i=0; i<sel.length; i++) {
			listSelected[i] = Integer.parseInt(sel[i]);
		}		
	}
	
	private void assignReferenceValue(String qualifier, MetaReference ref, String value, boolean displayAsDescriptionsListAndReferenceView) throws XavaException {  
		Map referenceValues = new HashMap();  
		fillReferenceValues(referenceValues, ref, value, qualifier, null); 
		View subview = getSubview(ref.getName());
		subview.addValues(Maps.plainToTree(referenceValues)); 
		if (displayAsDescriptionsListAndReferenceView) { 
			subview.oldValues = subview.values==null?null:new HashMap(subview.values);
			subview.oldKeyEditable = subview.keyEditable; 
			subview.oldEditable = subview.editable;
			subview.refreshDescriptionsLists = false;
		}
	}

	private void fillReferenceValues(Map referenceValues, MetaReference ref, String value, String qualifier, String propertyPrefix) {
		MetaModel metaModel = ref.getMetaModelReferenced();
		if (!value.startsWith("[")) value = "";
		StringTokenizer st = new StringTokenizer(Strings.change(value, "..", ". ."), "[.]");
		for (String propertyName: metaModel.getAllKeyPropertiesNames()) {
			MetaProperty p = metaModel.getMetaProperty(propertyName);			 													
			Object propertyValue = null;
			if (st.hasMoreTokens()) { // if not then null is assumed. This is a case of empty value
				String stringPropertyValue = st.nextToken();
				propertyValue = WebEditors.parse(getRequest(), p, stringPropertyValue, getErrors(), getViewName());									
			}			
			if (WebEditors.mustToFormat(p, getViewName())) {				
				if (qualifier != null) { 
					String valueKey = qualifier + "." + ref.getName() + "." + propertyName + ".value"; 
					getRequest().setAttribute(valueKey, propertyValue);
				}
				referenceValues.put(propertyPrefix==null?propertyName:propertyPrefix + propertyName, propertyValue);
			}									
		}
	}
	
	public boolean throwsPropertyChanged(MetaProperty p) {
		try {									
			if (hasDependentsProperties(p) && 
				!(isSubview() && isRepresentsEntityReference() && !displayAsDescriptionsList())) 
			{
				return true; 						
			}
			if (getMetaView().hasOnChangeAction(p.getName())) return true;			
			if (isLastSearchKey(p)) return true; 			
			if (!isSubview()) return false;							
			return isRepresentsEntityReference() && !isRepresentsCollection() && getLastPropertyKeyName().equals(p.getName()); 
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("property_changed_not_know_warning", p.getName()), ex);
			return false;
		}		 		 				
	}
	
	public boolean throwsPropertyChanged(String propertyName) throws XavaException {
		int idx = propertyName.indexOf('.'); 
		if (idx >= 0) {
			String reference = propertyName.substring(0, idx);			
			String member = propertyName.substring(idx + 1); 
			try {
				View subview = getSubview(reference);
				if (subview.isRepresentsElementCollection()) member = Strings.noFirstTokenWithoutFirstDelim(member, "."); 
				return subview.throwsPropertyChanged(member);
			}
			catch (ElementNotFoundException ex) {
				// Maybe a custom JSP view wants access to a property not showed in default view
				MetaModel referencedModel = getMetaModel().getMetaReference(reference).getMetaModelReferenced(); 				
				return (referencedModel instanceof MetaEntity) &&
					referencedModel.isKey(member);
			}			
		}				
		return throwsPropertyChanged(getMetaProperty(propertyName)); 
	}	
	
	private boolean isLastKeyProperty(MetaProperty p) throws XavaException {		
		return p.isKey() && p.getName().equals(getLastPropertyKeyName());
	}
	
	private boolean isLastPropertyMarkedAsSearchKey(MetaProperty p) throws XavaException {
		return p.isSearchKey() && p.getName().equals(getNameOfLastPropertyMarkedAsSearchKey());		
	}
	
	private boolean isLastPropertyMarkedAsSearch(String qualifiedProperty) throws XavaException {
		return qualifiedProperty.equals(getNameOfLastPropertyMarkedAsSearchKey());
	}	
	
	public boolean isFirstPropertyAndViewHasNoKeys(MetaProperty pr) throws XavaException {		
		if (pr == null) return false; 
		if (isGroup()) return getParent().isFirstPropertyAndViewHasNoKeys(pr); 
		if (!pr.hasMetaModel()) return false; // maybe a view property
		if (getMetaProperties().isEmpty()) return false;
		MetaProperty first = (MetaProperty) getMetaProperties().get(0);
		return first.equals(pr) && !hasKeyProperties(); 
	}
	
	public boolean hasKeyProperties() throws XavaException {		
		for (Iterator it=getMetaProperties().iterator(); it.hasNext();) {
			MetaProperty pr = (MetaProperty) it.next();
			if (pr.isKey()) {
				return true;
			}
		}

		if (hasGroups()) {
			Iterator it = getGroupsViews().values().iterator();
			while (it.hasNext()) {
				View v = (View) it.next();
				if (v.hasKeyProperties()) return true;				
			}			
		}

		return false;
	}
		
	private String getLastPropertyKeyName() throws XavaException {
		if (lastPropertyKeyName == null) {
			Iterator it = getMetaPropertiesIncludingGroups().iterator(); 
			lastPropertyKeyName = "";
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();
				if (p.isKey()) {
					lastPropertyKeyName = p.getName();
				}
			}
		}
		return lastPropertyKeyName;		
	}
	
	private String getNameOfLastPropertyMarkedAsSearchKey() throws XavaException { 
		if (nameOflastPropertyMarkedAsSearchKey == null) {
			Iterator it = getMetaMembersIncludingGroups().iterator();  
			nameOflastPropertyMarkedAsSearchKey = "";
			while (it.hasNext()) {
				MetaMember member = (MetaMember) it.next();
				if (member instanceof MetaProperty) {
					if (((MetaProperty) member).isSearchKey()) {
						nameOflastPropertyMarkedAsSearchKey = member.getName();
					}
				}
				else if (member instanceof MetaReference) {
					if (((MetaReference) member).isSearchKey()) {
						nameOflastPropertyMarkedAsSearchKey = member.getName() + "." +
							getSubview(member.getName()).getLastSearchKeyName();
						
					}
				}
			}
		}		
		return nameOflastPropertyMarkedAsSearchKey;		
	}
	
	private boolean hasSearchMemberKeys() throws XavaException { 
		return !Is.emptyString(getNameOfLastPropertyMarkedAsSearchKey());
	}
	

	private void propertyChanged(String propertyId) {
		try {		
			String name = Ids.undecorate(propertyId);
			if (isRepresentsElementCollection()) {
				if (StringUtils.isNumeric(Strings.firstToken(name, "."))) {
					name = Strings.noFirstTokenWithoutFirstDelim(name, ".");
				}
			}
			if (name.endsWith(DescriptionsLists.COMPOSITE_KEY_SUFFIX)) {
				String refName = name.substring(0, name.length() - 7);
				MetaModel referencedModel = null;
				try {
					referencedModel = getMetaModel().getMetaReference(refName).getMetaModelReferenced();
				}
				catch (ElementNotFoundException ex) {
					// try if is a collection					
					int idx = refName.indexOf('.');
					String collectionName = refName.substring(0, idx);
					String refName2 = refName.substring(idx+1);
					referencedModel = getMetaModel().getMetaCollection(collectionName).getMetaReference().getMetaModelReferenced().getMetaReference(refName2).getMetaModelReferenced();
				}
				
				Iterator itKeyProperties = referencedModel.getKeyPropertiesNames().iterator();
				while (itKeyProperties.hasNext()) {
					propertyChanged(refName + "." + itKeyProperties.next());								
				}
				return;								
			}
			int idxDot = name.indexOf('.');			
			if (idxDot >= 0) { // it's qualified
				String subviewName = name.substring(0, idxDot);	
				String propertyName = name.substring(idxDot + 1);
				View subview = getSubview(subviewName);				
				subview.propertyChanged(propertyName);
			}
			else {
				MetaProperty changedProperty = getMetaProperty(name); 
				propertyChanged(changedProperty, name);
				if (getParent() != null) {					
					String qualifiedName = Is.emptyString(getMemberName())?name:(getMemberName() + "." + name);
					getParent().propertyChanged(changedProperty, qualifiedName); 
				}
			}

			if (isRepresentsElementCollection()) {
				Collection<String> totalProperties = getTotalProperties().get(name);
				if (totalProperties != null) {
					for (String totalProperty: totalProperties) {
						String property = Strings.noFirstTokenWithoutFirstDelim(totalProperty, ".");
						getParent().propertyChanged(property);
					}
				}
			}
		}
		catch (ElementNotFoundException ex) {
			// So that sections that do not have all the properties do not throw exceptions
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("property_changed_warning", propertyId),ex);
			getErrors().add("change_property_error");
		}			
	}
	
	private void propertyChanged(MetaProperty changedProperty, String changedPropertyQualifiedName) { 
		try {			
			tryPropertyChanged(changedProperty, changedPropertyQualifiedName);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("property_changed_warning", changedProperty),ex);
			getErrors().add("change_property_error");			
		}		 		 		
	}
	
	private void tryPropertyChanged(MetaProperty changedProperty, String changedPropertyQualifiedName) throws Exception {
		if (!isOnlyThrowsOnChange()) {
			boolean calculationDone = false; 
			Iterator it = getMetaPropertiesIncludingGroups().iterator();	
			while (it.hasNext()) {
				MetaProperty pr = (MetaProperty) it.next();				
				if (dependsOn(pr, changedProperty, changedPropertyQualifiedName)) {
					if (pr.hasCalculator()) {						
						calculateValue(pr, pr.getMetaCalculator(), pr.getCalculator(), errors, messages);
						calculationDone = true;
					}
					if (pr.hasDefaultValueCalculator()) {	
						calculateValue(pr, pr.getMetaCalculatorDefaultValue(), pr.createDefaultValueCalculator(), errors, messages);
						calculationDone = true;
					}				
				}
			}
			
			for (MetaReference ref: getMetaModel().getMetaReferencesWithDefaultValueCalculator()) {
				if (hasSubview(ref.getName()) && ref.getMetaCalculatorDefaultValue().containsMetaSetsWithoutValue()) {
					MetaProperty pr = ref.getMetaModelReferenced().getAllMetaPropertiesKey().get(0).cloneMetaProperty();
					pr.setName(ref.getName() + "." + pr.getName());
					calculateValue(pr, ref.getMetaCalculatorDefaultValue(), ref.createDefaultValueCalculator(), errors, messages);
					calculationDone = true;					
				}
			}
			
			if (calculationDone && isRepresentsElementCollection()) {
				moveViewValuesToCollectionValues();
			}

			if (hasToSearchOnChangeIfSubview && isSubview() && isRepresentsEntityReference() && !isGroup() && !displayAsDescriptionsList() && 
					( 	
					(getLastPropertyKeyName().equals(changedProperty.getName()) && getMetaPropertiesIncludingGroups().contains(changedProperty)) || // Visible keys
					(!hasKeyProperties() && changedProperty.isKey() && changedProperty.getMetaModel() == getMetaModel()) || // hidden keys or key inside a @DescriptionsList with showReferenceViw=true 
					(isFirstPropertyAndViewHasNoKeys(changedProperty) && isKeyEditable()) || // A searching value that is not key 
					(hasSearchMemberKeys() && isLastPropertyMarkedAsSearch(changedPropertyQualifiedName))  // Explicit search key
					)
				) {
				if (!searchingObject) { // To avoid recursive infinite loops				
					try {
						searchingObject = true;												
						IOnChangePropertyAction action = getParent().getMetaView().createOnChangeSearchAction(getMemberName());
						executeOnChangeAction(changedPropertyQualifiedName, action);
						// If the changed property is not the key, for example, if we have a hidden
						//   key and we are using a search key (or simply the first displayed property),
						// then we throw the change of the id, because the id changed too. And maybe
						//   there are events attached to it.
						if (!getMetaModel().isKey(changedPropertyQualifiedName)) {
							String id = (String) getMetaModel().getKeyPropertiesNames().iterator().next();
							propertyChanged(id);
						}	
						refreshCollections();
						moveViewValuesToCollectionValues();
					}
					finally {
						searchingObject = false;				 
					}				
				}			
			}
		} // of if (!isOnlyThrowsOnChange())
		if (!isSection() && getMetaView().hasOnChangeAction(changedPropertyQualifiedName)) {
			IOnChangePropertyAction action = getMetaView().createOnChangeAction(changedPropertyQualifiedName);
			executeOnChangeAction(changedPropertyQualifiedName, action);
		}
		if (hasGroups()) {
			Iterator itGroups = getGroupsViews().values().iterator();
			while (itGroups.hasNext()) {
				View v = (View) itGroups.next();
				try {
					v.tryPropertyChanged(changedProperty, changedPropertyQualifiedName); 					
				}
				catch (ElementNotFoundException ex) {
					// The common case of a qualified property  whose
					// her subview is not in this group (maybe in another group
					// or main view)
				}
			}		
		}
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				getSectionView(i).propertyChanged(changedProperty, changedPropertyQualifiedName); 				
			}			
		}		
	}

	private void moveViewValuesToCollectionValues() { 
		if (!isRepresentsElementCollection()) {
			View parent = getParent();
			if (parent == null) return;
			parent.moveViewValuesToCollectionValues();
			return;
		}
		if (collectionValues == null) return;
		if (collectionEditingRow == collectionValues.size()) collectionValues.add(collectionEditingRow, getAllValues());
		else collectionValues.set(collectionEditingRow, getAllValues());		
	}

	private void executeOnChangeAction(String changedPropertyQualifiedName, IOnChangePropertyAction action) 
		throws XavaException 
	{
		if (!actionRegisteredAsExecuted(changedPropertyQualifiedName, action)) {
			View viewOfAction = this;
			while (viewOfAction.isGroup()) viewOfAction = viewOfAction.getParent();
			action.setView(viewOfAction);
			action.setChangedProperty(changedPropertyQualifiedName); 
			action.setNewValue(getValue(changedPropertyQualifiedName));
			getModuleManager(getRequest()).executeAction(action, getErrors(), getMessages(), getRequest());
			registerExecutedAction(changedPropertyQualifiedName, action);
		}
	}	
	
	
	private boolean dependsOn(MetaProperty pr, MetaProperty changedProperty, String changedPropertyQualifiedName) throws XavaException {		
		if (pr.depends(changedProperty)) return true;
		if (pr.getPropertyNamesThatIDepend().contains(changedPropertyQualifiedName) ) {
			return true;
		}		 					
		return false;
	}

	private ModuleManager getModuleManager(HttpServletRequest request) throws XavaException {		
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");		
		return (ModuleManager) context.get(request, "manager");		
	}

	/**
	 * Using the key values loaded in the view search the rest of the data
	 * from persistent storage and fill the view. 
	 */
	public void findObject() throws Exception {		
		findObject(null);
	}

	/**
	 * Using the key values loaded in the view search the rest of the data
	 * from persistent storage and fill the view.
	 * 
	 * @param changedProperty  Property which change produces the search. 
	 * @return true  if the object is found
	 */	
	public boolean findObject(MetaProperty changedProperty) throws Exception { 
		Map key = getKeyValues();
		try {			
			if (isRepresentsEntityReference() && isFirstPropertyAndViewHasNoKeys(changedProperty) && isKeyEditable()) {
				// Searching by the first visible property: Useful for searching from a reference with hidden key		
				Map alternateKey = new HashMap();
				alternateKey.put(changedProperty.getName(), getValue(changedProperty.getName()));
				clear();
				if (!Maps.isEmptyOrZero(alternateKey)) {
					setValues(MapFacade.getValuesByAnyProperty(getModelName(), alternateKey, getMembersNamesForFindObject())); 
				}
			}
			else if (isRepresentsEntityReference() && changedProperty != null && changedProperty.isHidden() && changedProperty.isKey()) {
				// If changed property is hidden key, although there are search member we search by key
				clear();
				if (!Maps.isEmptyOrZero(key)) {				
					setValues(MapFacade.getValues(getModelName(), key, getMembersNamesForFindObject()));					
				}
			}
			else if (isRepresentsEntityReference() && hasSearchMemberKeys()) {
				Map alternateKey = getSearchKeyValues();
				clear();
				if (!Maps.isEmptyOrZero(alternateKey)) {				
					setValues(MapFacade.getValuesByAnyProperty(getModelName(), alternateKey, getMembersNamesForFindObject())); 
				}				
			}						
			else {							
				// Searching by key, the normal case
				clear();
				if (!Maps.isEmptyOrZero(key)) {				
					setValues(MapFacade.getValues(getModelName(), key, getMembersNamesForFindObject())); 
				}
			}
			// To force AJAX reload of keys in a @DescriptionsList with showReferenceView=true
			if (oldValues != null && displayAsDescriptionsListAndReferenceView()) {
				for (Object keyName: key.keySet()) {
					oldValues.remove(keyName);
				}
			}
			return true; 
		}
		catch (ObjectNotFoundException ex) {
			return false;  
		}				
	}		
	
	/**
	 * Refresh the displayed data from database. <p> 
	 */
	public void refresh() {
		Map key = getKeyValues();
		try {			
			if (Maps.isEmptyOrZero(key)) clear();				
			else setValues(MapFacade.getValues(getModelName(), key, getMembersNamesWithHidden()), false);				
			refreshCollections(); 
		}
		catch (FinderException ex) {						
			getErrors().add("object_with_key_not_found", getModelName(), key);
			clear(); 								
		}
	}
	
	private Map getSearchKeyValues() { 
		Map values = new HashMap();
		for (Iterator it = getMetaMembers().iterator(); it.hasNext();) {
			MetaMember member = (MetaMember) it.next();
			if (member instanceof MetaProperty && isSearchKey((MetaProperty) member)) {
				values.put(member.getName(), getValue(member.getName()));
			}
			else if (member instanceof MetaReference && ((MetaReference) member).isSearchKey()) {
				values.put(member.getName(), getSubview(member.getName()).getSearchKeyValues());				
			}
		}
		return values;
	}

	private boolean isSearchKey(MetaProperty property) {		
		return property.isKey() || property.isSearchKey() || isLastSearchKey(property);
	}

	private View getParentIfSectionOrGroup() { 
		if (isSection() || isGroup()) return getParent().getParentIfSectionOrGroup();
		return this;
	}
	
	private void calculateValue(MetaProperty metaProperty, MetaCalculator metaCalculator, ICalculator calculator, Messages errors, Messages messages) {		
		try {	
			PropertiesManager mp = new PropertiesManager(calculator);
			Iterator it = metaCalculator.getMetaSets().iterator();			
			while (it.hasNext()) {
				MetaSet set = (MetaSet) it.next();								
				if (!set.hasValue()) {					
					Object value = getParentIfSectionOrGroup().getValue(set.getPropertyNameFrom());
					if (value == null) value = getValueInTotals(set.getPropertyNameFrom()); 
					mp.executeSet(set.getPropertyName(), value); 
				}											
			}		
						
			Object pojo = null; 
			if (calculator instanceof IModelCalculator) { 				
				pojo = getPOJO();
				((IModelCalculator) calculator).setModel(pojo);
			}
			if (calculator instanceof IEntityCalculator) {
				if (pojo == null) pojo = getPOJO();
				((IEntityCalculator) calculator).setEntity(pojo); 
			}
			if (calculator instanceof IJDBCCalculator) {
				((IJDBCCalculator) calculator).setConnectionProvider(DataSourceConnectionProvider.getByComponent(getModelName()));
			}					
			
			Object newValue = calculator.calculate();
			PersistenceFacade.refreshIfManaged(pojo);
			
			if (calculator instanceof IOptionalCalculator) {
				if (!((IOptionalCalculator) calculator).isCalculate()) {
					return;
				}
			}
			Object old = getValue(metaProperty.getName()); 
			if (!setValueNotifyingInTotals(metaProperty.getName(), newValue, old)) {
				if (!Is.equal(old, newValue)) {				
					setValueNotifying(metaProperty.getName(), newValue);
				}
			}  
			addRecalculatedProperty(metaProperty); 
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("value_calculate_warning", metaProperty),ex);
		}		
	}

	private boolean setValueNotifyingInTotals(String propertyName, Object newValue, Object oldValue) { 
		boolean result = false;
		for (MetaCollection col: getMetaModel().getMetaCollections()) {
			try {
				View subview = getSubview(col.getName());
				if (!subview.getCollectionTotals().containsKey(propertyName)) continue;
				if (subview.isRepresentsElementCollection() || subview.isCollectionFromModel()) {
					if (subview.oldCollectionTotals == null) subview.oldCollectionTotals = new HashMap(subview.getCollectionTotals());
					subview.oldCollectionTotals.put(propertyName, oldValue);
				}
				else subview.refreshCollection();
				subview.getCollectionTotals().put(propertyName, newValue);
				propertyChanged(propertyName);
				result = true;
			}
			catch (ElementNotFoundException ex) {
			}
		}
		return result;
	}
	
	private Object getValueInTotals(String propertyName) { 
		boolean result = false;
		for (MetaCollection col: getMetaModel().getMetaCollections()) {
			try {
				View subview = getSubview(col.getName());
				if (!subview.getCollectionTotals().containsKey(propertyName)) continue;
				return subview.getCollectionTotals().get(propertyName);
			}
			catch (ElementNotFoundException ex) {
			}
		}
		return null;
	}
	
	private void addRecalculatedProperty(MetaProperty metaProperty) { 
		if (recalculatedMetaProperties == null) recalculatedMetaProperties = new HashSet<MetaProperty>();
		else if (recalculatedMetaProperties.contains(metaProperty)) return;
		recalculatedMetaProperties.add(metaProperty);
	}
	
	private void resetRecalculatedProperties() { 
		if (recalculatedMetaProperties != null) recalculatedMetaProperties.clear();
	}
	
	private void recalculateRecalculatedProperties() { 
		if (recalculatedMetaProperties == null) return;
		for (MetaProperty pr: recalculatedMetaProperties) {
			calculateValue(pr, pr.getMetaCalculator(), pr.getCalculator(), errors, messages);
		}
	}
	
	/**
	 * POJO associated to the current view. <p>
	 */
	private Object getPOJO() throws Exception {
		if (isKeyEditable()) {
			return getTransientPOJO();
		}
		else {
			Object pojo = MapFacade.findEntity(getModelName(), getParentIfSectionOrGroup().getKeyValues());
			getMetaModel().fillPOJO(pojo, getParentIfSectionOrGroup().getAllValues()); // fillPOJO does not get the references from DB, if we change that maybe we can remove the refreshIfManaged from getCollectionObjects()   			
			return pojo;			
		}
	}
	
	private Object getTransientPOJO() throws Exception {
		Map values = getParentIfSectionOrGroup().getValues(); 
		Object pojo = getMetaModel().toPOJO(values);
		loadPOJOReferences(pojo, values);
		return pojo;
	}

	private void loadPOJOReferences(Object pojo, Map values) throws Exception	{ 
		PropertiesManager pm = new PropertiesManager(pojo);
		for (Object oen: values.entrySet()) {
			Map.Entry en = (Map.Entry) oen;
			if (en.getValue() instanceof Map) {
				String name = (String) en.getKey();
				Map key = (Map) en.getValue();
				if (getMetaModel().containsMetaReference(name) && !Maps.isEmpty(key)) {
					MetaReference ref = getMetaModel().getMetaReference(name);
					if (!ref.isAggregate()) {
						try {
							Object value = MapFacade.findEntity(ref.getMetaModelReferenced().getName(), key);
							pm.executeSet(name, value);
						}
						catch (ObjectNotFoundException ex) {							
						}
					}
				}
			}
		}
	}
	
	
		
	/** 
	 * The model object attached to this view using {@link #setModel(Object model)}. <p>
	 * 
	 * If there is no model attached, that is the setModel() method has not been called on this View,
	 * it returns null. If you want to get the view data as an entity even if you have not attached
	 * a model to it before, use the {@link getEntity()} method instead.
	 * 
	 * The model is not updated automatically from view data you have to call updateModelFromView() for that.
	 * 
	 *  @since 4.6
	 */
	public Object getModel() throws XavaException {
		return model; 
	}
	
	/**
	 * Update the model object set with setModel() from data in the view. <p>
	 * 
	 * If model is null does nothing.
	 * 
	 * @since 5.8
	 */
	public void updateModelFromView() { 
		if (model != null) {
			Map values = getParentIfSectionOrGroup().getValues(); 
			getMetaModel().fillPOJO(model, values);
			try {
				loadPOJOReferences(model, values);
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("fill_model_from_view_error"), ex);				
				throw new XavaException("fill_model_from_view_error");
			}
		}
	}
	
	/**
	 * The view will be populate with data from the model object, and the model object is attached to the view. <p>
	 * 
	 * You can even assign a model of different type of the current one and the view will change its shape.<br>
	 * 
	 * @since 4.6
	 */
	public void setModel(Object model) { 
		this.model = model;
		if (model == null) {
			clear();
			return;
		}
		refreshCollections();  
		setModelName(model.getClass().getSimpleName());
		setValues(MapFacade.getValues(getModelName(), model, getMembersNamesWithHidden()));
	}
	
	/**
	 * Entity associated to the current view. <p>
	 * 
	 */
	public Object getEntity() throws Exception {		
		return getPOJO();		
	}

	private boolean hasDependentsProperties(MetaProperty p) {		
		try {			
			// In this view								
			for (Iterator it = getMetaPropertiesQualified().iterator(); it.hasNext();) {
				Object element = it.next();				
				if (isMetaProperty(element)) {
					MetaProperty pro = (MetaProperty) element;					
					if (WebEditors.depends(pro, p, getViewName())) {
						return true;
					}
				}
			}
			
			if (isRepresentsAggregate() || isRepresentsEntityReference()) {
				p = p.cloneMetaProperty();
				p.setName(getMemberName() + "." + p.getName());
			}
						
			// From the root			
			for (Iterator it = getRoot().getMetaPropertiesQualified().iterator(); it.hasNext();) {
				Object element = (Object) it.next();
				if (isMetaProperty(element)) {
					MetaProperty pro = (MetaProperty) element;					
					if (pro.getPropertyNamesThatIDepend().contains(p.getName())) {						
						return true;
					}										
					if (WebEditors.depends(pro, p, getViewName())) {
						return true;
					}
				}
			}			
			
			// In descriptionList of reference			
			for (Iterator it=getMetaView().getMetaDescriptionsLists().iterator(); it.hasNext();) {
				MetaDescriptionsList descriptionList = (MetaDescriptionsList) it.next();				
				if (descriptionList.dependsOn(p)) {
					return true;
				}
			}	
			
			return false;
		}	
		catch (Exception ex) {
			log.warn(XavaResources.getString("dependents_properties_warning", p),ex);
			return false;
		}	
	}
	
	private Collection<MetaProperty> getMetaPropertiesQualified() throws XavaException { 		
		if (metaPropertiesQualified == null) {
			metaPropertiesQualified = new ArrayList();
			fillMetaPropertiesQualified(this, metaPropertiesQualified, null); 
			if (hasSections()) {
				int count = getSections().size();
				for (int i=0; i<count; i++) {				
					fillMetaPropertiesQualified(getSectionView(i), metaPropertiesQualified, null);								
				}			
			}			
		}
		return metaPropertiesQualified;
	}
	
	private void fillMetaPropertiesQualified(View view, Collection properties, String prefix) throws XavaException {		
		Iterator it = view.getMetaMembersIncludingCollectionTotals().iterator(); 
		while (it.hasNext()) {
			Object element = (Object) it.next();
			if (isMetaProperty(element)) {
				MetaProperty pro = (MetaProperty) element;
				if (prefix == null) properties.add(pro);
				else {
					MetaProperty p = pro.cloneMetaProperty();
					p.setName(prefix + p.getName());
					properties.add(p);
				}
			}
			else if (element instanceof MetaReference) {
				MetaReference ref = (MetaReference) element;
				View subview = view.getSubview(ref.getName());
				fillMetaPropertiesQualified(subview, properties, ref.getName() + ".");
			}
			else if (element instanceof MetaGroup) {				
				MetaGroup group = (MetaGroup) element;
				View subview = view.getGroupView(group.getName());
				fillMetaPropertiesQualified(subview, properties, prefix);				
			}
		}		
	}

	
	/**	  
	 * @param stereotypesList Comma separated
	 */
	public Collection getPropertiesNamesFromStereotypesList(String stereotypesList) throws XavaException {		
		if (Is.emptyString(stereotypesList)) return Collections.EMPTY_LIST;
		StringTokenizer st = new StringTokenizer(stereotypesList, ", ");
		Collection r = new ArrayList();
		while (st.hasMoreTokens()) {
			String stereotype = st.nextToken().trim();
			String property = getPropertyNameFromStereotype(stereotype);
			if (property != null) {
				r.add(property);
			}
			else {
				log.warn(XavaResources.getString("property_for_stereotype_warning", stereotype));
				r.add(null);
			}
		} 				
		return r;				
	}
	
	private String getPropertyNameFromStereotype(String stereotype) {		
		if (getParent() != null) return getParent().getPropertyNameFromStereotype(stereotype);
		String property = (String) getMapStereotypesProperties().get(stereotype);
		if (property != null) return property;
		try {			
			Iterator it = getMetaPropertiesIncludingSections().iterator();
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();				
				if (stereotype.equals(p.getStereotype())) {
					getMapStereotypesProperties().put(stereotype, p.getName());
					return p.getName();					
				}
			}
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("property_for_stereotype_warning", stereotype),ex); 
		}
		return null;
	}
	
	public List<MetaProperty> getMetaProperties() throws XavaException { 		
		if (metaProperties == null) {
			Iterator it = getMetaMembersIncludingCollectionTotals().iterator();  
			metaProperties = new ArrayList();
			while (it.hasNext()) {
				Object element = (Object) it.next();
				if (isMetaProperty(element)) {
					metaProperties.add(element);					
				}
			}			
		}
		return metaProperties;		
	}
	
	public MetaProperty getMetaProperty(String name) throws XavaException {		
		int idx = name.indexOf('.');
		if (idx >= 0) {
			String reference = name.substring(0, idx);
			String member = name.substring(idx + 1);
			if (reference.equals("__PARENT__")) {
				return getParent().getMetaModel().getMetaProperty(member);
			}
			try {
				return getSubview(reference).getMetaProperty(member);
			}
			catch (ElementNotFoundException ex) {
				// Maybe a custom JSP view wants access to a property not showed in default view
				if (getMetaModel().containsMetaCollection(reference)) { 
					// From element collection
					String collectionMember = Strings.noFirstTokenWithoutFirstDelim(member, ".");
					collectionMember = collectionMember.replaceAll("^this\\.", ""); 
					return getMetaModel().getMetaCollection(reference).getMetaReference().getMetaModelReferenced().getMetaProperty(collectionMember);
				}
				if (getMetaModel().containsMetaReference(reference)) { 
					return getMetaModel().getMetaReference(reference).getMetaModelReferenced().getMetaProperty(member);
				}				
				throw ex;
			}
		}
		try {
			return getMetaView().getMetaProperty(name);			
		}
		catch (ElementNotFoundException ex) { 
			// try if is hidden
			return getMetaModel().getMetaProperty(name);  
		}
	}
	
	public MetaReference getMetaReference(String name) throws XavaException { 			
		int idx = name.indexOf('.');
		if (idx >= 0) {
			String reference = name.substring(0, idx);
			String member = name.substring(idx + 1);
			try { 
				return getSubview(reference).getMetaReference(member);
			}
			catch (ElementNotFoundException ex) {
				if (!getMetaModel().containsMetaCollection(reference)) throw ex;
				// From element collection
				String collectionMember = Strings.noFirstTokenWithoutFirstDelim(member, ".");
				return getMetaModel().getMetaCollection(reference).getMetaReference().getMetaModelReferenced().getMetaReference(collectionMember);
			}
		}				
		return getMetaModel().getMetaReference(name);
	}
		
	private Collection getMetaPropertiesIncludingSections() throws XavaException {
		if (!hasSections()) return getMetaProperties();
		if (metaPropertiesIncludingSections == null) { 
			metaPropertiesIncludingSections = new ArrayList(getMetaProperties());
			int count = getSections().size();
			for (int i = 0; i < count; i++) {
				metaPropertiesIncludingSections.addAll(getSectionView(i).getMetaProperties());
			}	
		}
		return metaPropertiesIncludingSections;
	}
	
	private Collection getMetaPropertiesIncludingGroups() throws XavaException {
		if (!hasGroups()) return getMetaProperties();
		if (metaPropertiesIncludingGroups == null) {
			metaPropertiesIncludingGroups = new ArrayList(getMetaProperties());	
			for (Iterator it = getGroupsViews().values().iterator(); it.hasNext();) {
				View group = (View) it.next();
				metaPropertiesIncludingGroups.addAll(group.getMetaPropertiesIncludingGroups()); 
			}			
		}
		return metaPropertiesIncludingGroups;
	}
		
	private Collection getMetaMembersIncludingGroups() throws XavaException { 
		if (!hasGroups()) return getMetaMembers();
		if (metaMembersIncludingGroups == null) {
			metaMembersIncludingGroups = new ArrayList(getMetaMembers());	
			for (Iterator it = getGroupsViews().values().iterator(); it.hasNext();) {
				View group = (View) it.next();
				metaMembersIncludingGroups.addAll(group.getMetaMembersIncludingGroups());
			}			
		}
		return metaMembersIncludingGroups;
	}
			
	public List<MetaProperty> getMetaPropertiesList() throws XavaException {		
		if (metaPropertiesList == null) {
			metaPropertiesList = new ArrayList<MetaProperty>();
			Iterator it = getMetaModel().getPropertiesNames().iterator();
			while (it.hasNext()) {
				MetaProperty pr= getMetaModel().getMetaProperty((String) it.next());
				if (!pr.isHidden()) {
					MetaProperty prList = pr.cloneMetaProperty();					
					metaPropertiesList.add(prList);
				}
			}
			setLabelsIdForMetaPropertiesList();
		} 
		return metaPropertiesList;
	}
	
	
	private void setLabelsIdForMetaPropertiesList() throws XavaException {
		if (getMemberName() == null || metaPropertiesList == null) return;
		
		List<MetaProperty> newList = new ArrayList();
		Iterator it = metaPropertiesList.iterator();
		while (it.hasNext()) {
			MetaProperty p = ((MetaProperty) it.next()).cloneMetaProperty();
			String prefix = Is.empty(getParent().getMetaModel().getName()) ? 
				getMetaModel().getMetaComponent().getName() :
				getParent().getMetaModel().getName();				
			p.setLabelId(prefix + "." + getMemberName() + "." + p.getName());
			newList.add(p);
		}
		metaPropertiesList = newList;
	}
	
		
	private void setMetaPropertiesList(List<MetaProperty> metaProperties) throws XavaException {  
		this.metaPropertiesList = metaProperties;
		setLabelsIdForMetaPropertiesList();
	}

	private Map getMapStereotypesProperties() {
		if (mapStereotypesProperties == null) {
			mapStereotypesProperties = new HashMap();
		}
		return mapStereotypesProperties;		
	}

	/**	  
	 * @param propertiesList Properties names comma separated
	 */
	public Collection getPropertiesNamesFromPropertiesList(String propertiesList) throws XavaException {
		if (Is.emptyString(propertiesList)) return Collections.EMPTY_LIST;
		StringTokenizer st = new StringTokenizer(propertiesList, ", ");
		Collection r = new ArrayList();
		while (st.hasMoreTokens()) {
			String property = st.nextToken().trim();
			r.add(property);
		}
		return r;
	}
	
	public String getViewName() {		
		if (isGroup() || isSection()) return getParent().getViewName();
		return viewName;
	}

	public void setViewName(String newView) {
		if (Is.equal(viewName, newView)) return;
		resetMembers();		
		viewName = newView;
		getFirstLevelView().reloadNeeded = true; // We reload the root view when a subview
										// is changed. Obviously this can be optimized		
		reloadNeeded = true;
	}
	
	public String toString() {
		return "View:" + oid;
	}


	public boolean isSubview() {		
		return subview;
	}

	public void setSubview(boolean b) {		
		subview = b;
	}

	public boolean isRepresentsAggregate() {		
		if (isGroup()) return getParent().isRepresentsAggregate();
		return representsAggregate;
	}

	public void setRepresentsAggregate(boolean b) {		
		representsAggregate = b;
		representsEntityReference = !representsAggregate;
	}
			
	public String getSearchAction() throws XavaException {
		if (getMetaView().hasMetaSearchAction()) {
			String action = getMetaView().getMetaSearchAction().getActionName();
			if (!Is.emptyString(action)) return action; 
		}		
		return "Reference.search";					
	}

	public boolean isRepresentsCollection() {		
		return representsCollection;
	}
	
	/** @since 5.6.1 */
	public boolean isRepresentsSortableCollection() { 
		return isRepresentsCollection() && getMetaCollection().isSortable();
	}

	public void setRepresentsCollection(boolean b) {		
		representsCollection = b;
	}
	
	public boolean displayDetailInCollection(String collectionName) throws XavaException {
		if (knowIfDisplayDetailInCollection) return displayDetailInCollection;
		Iterator it = getMetaMembers().iterator();
		while (it.hasNext()) {
			Object m = it.next();
			if (m instanceof MetaReference || m instanceof MetaCollection) {
				displayDetailInCollection = true;
				knowIfDisplayDetailInCollection = true;
				return displayDetailInCollection;
			}
		}
		displayDetailInCollection = false;
		knowIfDisplayDetailInCollection = true;
		return displayDetailInCollection;				
	}
	
	public boolean isCollectionDetailVisible() {		
		return collectionDetailVisible;
	}
	
	public void setCollectionDetailVisible(boolean b) {		
		if (b != collectionDetailVisible) {
			refreshCollection(); 
		}
		collectionDetailVisible = b;	
		firstLevel = b; 
	}

	public Messages getErrors() {		
		if (getParent() != null) return getParent().getErrors();				
		return errors;
	}

	public Messages getMessages() {		
		if (getParent() != null) return getParent().getMessages();
		return messages;
	}

	public void setErrors(Messages messages) {				
		errors = messages;
	}

	public void setMessages(Messages messages) throws XavaException {		
		this.messages = messages;		
	}
	
	private Set getNotEditableMembersNames() {
		if (notEditableMembersNames == null) {
			notEditableMembersNames = new HashSet();
		}
		return notEditableMembersNames;
	}
	
	public HttpServletRequest getRequest() {	
		if (request == null) return parent.getRequest();
		return request;
	}

	public void setRequest(HttpServletRequest request) throws XavaException {			
		getRoot().request = request;
		polish(); 
	}

	public boolean displayAsDescriptionsList(MetaReference ref) throws XavaException {		
		MetaDescriptionsList descriptionsList = getMetaDescriptionsList(ref);
		if (descriptionsList == null) return false;
		return !descriptionsList.isShowReferenceView(); 
	}
	
	public boolean displayAsDescriptionsListAndReferenceView(MetaReference ref) throws XavaException { 
		if (isRepresentsElementCollection()) return false; 
		MetaDescriptionsList descriptionsList = getMetaDescriptionsList(ref);
		if (descriptionsList == null) return false;
		return descriptionsList.isShowReferenceView();
	}
	
	public String getDescriptionPropertyInDescriptionsList(MetaReference ref) throws XavaException {
		MetaDescriptionsList metaDescriptionList = getMetaDescriptionsList(ref);
		if (metaDescriptionList != null) return metaDescriptionList.getDescriptionPropertyName();		
		return getMetaView().createMetaDescriptionList(ref).getDescriptionPropertyName();
	}
	
	public String getDescriptionPropertiesInDescriptionsList(MetaReference ref) throws XavaException { 		
		MetaDescriptionsList metaDescriptionList = getMetaDescriptionsList(ref); 
		if (metaDescriptionList != null) return metaDescriptionList.getDescriptionPropertiesNames();
		return getMetaView().createMetaDescriptionList(ref).getDescriptionPropertiesNames();			
	}
		
	private MetaDescriptionsList getMetaDescriptionsList(MetaReference ref) {
		MetaView view = getMetaView(ref);
		
		if (ref.getName().contains(".")) {
			ref = ref.cloneMetaReference();
			ref.setName(Strings.lastToken(ref.getName(), "."));
		}
		return view.getMetaDescriptionList(ref);
	}
	
	private MetaView getMetaView(MetaReference ref) {   
		String refName = ref.getName();
		int idx = refName.indexOf('.');
		if (idx < 0) return getMetaView();
		String subviewName = refName.substring(0, idx);
		String member = refName.substring(idx+1);		
		View subview = getSubview(subviewName);
		if (subview.isRepresentsElementCollection()) member = Strings.noFirstTokenWithoutFirstDelim(member, ".");
		return subview.getMetaView(subview.getMetaReference(member));
	}
	
	public boolean throwsReferenceChanged(MetaReference ref) throws XavaException {
		String refName = ref.getName(); 
		int idx = refName.indexOf('.');
		if (idx >= 0) {
			if (isRepresentsElementCollection()) {
				refName = Strings.noFirstTokenWithoutFirstDelim(refName, ".");
			}
			else {
				String subviewName = refName.substring(0, idx);
				String memberName = refName.substring(idx+1);
				View subview = getSubview(subviewName);
				MetaReference member = ref.cloneMetaReference();
				member.setName(memberName);
				return subview.throwsReferenceChanged(member);
			}
		}
		
		if (getDepends().contains(refName)) return true;
		Iterator itKeys = ref.getMetaModelReferenced().getKeyPropertiesNames().iterator();
		while (itKeys.hasNext()) {			
			String propertyKey = (String) itKeys.next();
			String propertyName = refName + "." + propertyKey;
			if (getMetaView().hasOnChangeAction(propertyName)) {
				return true;
			}
			
			Iterator itProperties = getRoot().getMetaPropertiesQualified().iterator();
			while (itProperties.hasNext()) {
				MetaProperty p = (MetaProperty) itProperties.next();
				if (p.getPropertyNamesThatIDepend().contains(propertyName)) return true;
			}
			
			for (MetaReference r: getRoot().getMetaModel().getMetaReferencesWithDefaultValueCalculator()) {
				if (hasSubview(r.getName()) && r.getMetaCalculatorDefaultValue().containsMetaSetsWithoutValue()) {
					if (r.getPropertyNamesThatIDepend().contains(propertyName)) return true;
				}
			}
			
			MetaProperty p = ref.getMetaModelReferenced().getMetaProperty(propertyKey).cloneMetaProperty();
			p.setName(propertyName);
			if (hasDependentsProperties(p)) return true;			
		}
		return displayAsDescriptionsListAndReferenceView(ref); 
	}
		
	private Collection getDepends() throws XavaException {
		if (depends == null) {
			depends = new ArrayList();
			Iterator it = getMetaView().getMetaDescriptionsLists().iterator();
			while (it.hasNext()) {
				MetaDescriptionsList metaDescriptionsList = (MetaDescriptionsList) it.next();					
				StringTokenizer st = new StringTokenizer(metaDescriptionsList.getDepends(), ",");
				while (st.hasMoreTokens()) {
					String token = st.nextToken().trim();
					if (token.startsWith("this.")) {
						depends.add(token.substring(5));
					}
					else {
						depends.add(token);
					}
				}					
			}
		}		
		return depends;
	}
	
	public String getParameterValuesPropertiesInDescriptionsList(MetaReference ref) throws XavaException {
		if (ref.getName().contains(".")) {
			MetaReference unqualifiedRef = ref.cloneMetaReference();
			unqualifiedRef.setName(Strings.lastToken(ref.getName(), "."));
			String prefix = Strings.noLastToken(ref.getName(), ".");
			StringBuffer sb = new StringBuffer();
			StringTokenizer st = new StringTokenizer(unqualifiedRef.getParameterValuesPropertiesInDescriptionsList(getMetaView(ref)), ", ");
			while (st.hasMoreTokens()) {
				String property = st.nextToken();
				if (sb.length() > 0) sb.append(",");
				sb.append(prefix + property);
			}
			return sb.toString();
		}	
		else {
			return ref.getParameterValuesPropertiesInDescriptionsList(getMetaView());
		} 
	}
	
	/** @since 6.4 */
	public String getFilterInDescriptionsList(MetaReference ref) throws XavaException { 
		return ref.getFilterInDescriptionsList(getMetaView());
	}	

	
	

	public String getConditionInDescriptionsList(MetaReference ref) throws XavaException {
		MetaDescriptionsList descriptionsList = getMetaDescriptionsList(ref); 
		if (descriptionsList == null) return "";
		return descriptionsList.getCondition();
	}
	
	public String getOrderInDescriptionsList(MetaReference ref) throws XavaException {		
		MetaDescriptionsList descriptionsList = getMetaDescriptionsList(ref);  
		if (descriptionsList == null) return "";
		return descriptionsList.getOrder();				
	}		
		
	public boolean isOrderByKeyInDescriptionsList(MetaReference ref) throws XavaException {
		MetaDescriptionsList descriptionsList = getMetaDescriptionsList(ref);  
		if (descriptionsList == null) return false;
		return descriptionsList.isOrderByKey();
	}
	
	public boolean isCreateNewForReference(MetaReference ref) throws XavaException {
		return isActionForReference(ref, true); 
	}
	
	public boolean isModifyForReference(MetaReference ref) throws XavaException {
		return isActionForReference(ref, false); 
	}
		
	private boolean isActionForReference(MetaReference ref, boolean create) throws XavaException {
		MetaReferenceView viewRef = getMetaView().getMetaReferenceView(ref);		
		if (viewRef == null) return true;
		if (create && !viewRef.isCreate()) return false; 
		if (!create && !viewRef.isModify()) return false; 
		if (refiner == null) return true;
		String key = ref.getName() + ":" + create;
		Boolean result = getRefinedReferenceActions().get(key);
		if (result == null) {
			try {
				result = (Boolean) XObjects.execute(refiner, "accept", 
					String.class, ref.getReferencedModelName(), 
					String.class, create?"new":"save");
				getRefinedReferenceActions().put(key, result);
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("action_for_reference_error", ref.getName(), ref.getMetaModel().getName()), ex); 
				return false; 
			}				
		}
		return result;
	}
	
	private Map<String, Boolean> getRefinedReferenceActions() {
		if (refinedReferenceActions == null) {
			if (isGroup()) return getParent().getRefinedReferenceActions();
			refinedReferenceActions = new HashMap<>();
		}
		return refinedReferenceActions;
	}
	
	public boolean isSearchForReference(MetaReference ref) throws XavaException {
		MetaReferenceView viewRef = getMetaView().getMetaReferenceView(ref);		
		if (viewRef == null) return true;
		return viewRef.isSearch();
	}
		
	public boolean isCreateNew() throws XavaException {		
		if (isGroup()) return getParent().isCreateNew();
		try {			
			MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());			
			return getParent().isCreateNewForReference(ref);
		}
		catch (ElementNotFoundException ex) {			
			if (!getParent().getMetaModel().containsMetaCollection(getMemberName())) return false;
			MetaCollectionView collectionView = getParent().getMetaView().getMetaCollectionView(getMemberName());
			if (collectionView == null) return true;
			return collectionView.isCreateReference(); 
		}
	}
	
	public boolean isModify() throws XavaException {
		if (isGroup()) return getParent().isModify();
		try {			
			MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());			
			return getParent().isModifyForReference(ref);
		}
		catch (ElementNotFoundException ex) {			
			if (!getParent().getMetaModel().containsMetaCollection(getMemberName())) return false;
			MetaCollectionView collectionView = getParent().getMetaView().getMetaCollectionView(getMemberName());
			if (collectionView == null) return true;
			return collectionView.isModifyReference(); 
		}
	}
		
	public boolean isSearch() throws XavaException {		
		if (isGroup()) return getParent().isSearch(); 
		try {			
			MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());			
			return getParent().isSearchForReference(ref);
		}
		catch (ElementNotFoundException ex) {
			return getParent().getMetaModel().containsMetaCollection(getMemberName());
		}
	}
	
	public boolean isLastSearchKey(String propertyName) throws XavaException {
		try {
			int idx = propertyName.indexOf('.'); 
			if (idx >= 0) {
				String reference = propertyName.substring(0, idx);
				String member = propertyName.substring(idx + 1);
				return getSubview(reference).isLastSearchKey(member);
			}				
			return isLastSearchKey(getMetaView().getMetaProperty(propertyName));
		}
		catch (ElementNotFoundException ex) {
			// Maybe a view property
			return false;
		}			
	}
	
	public boolean isLastSearchKey(MetaProperty p) throws XavaException {
		return isLastSearchKey(p, isEditableImpl(p), isKeyEditable());
	}
	
	private boolean isLastSearchKey(MetaProperty p, boolean editable, boolean keyEditable) throws XavaException {		
		if (!isRepresentsEntityReference()) return false;
		if (isRepresentsCollection()) return false; 
		if (displayAsDescriptionsList()) return false; 
		if (hasSearchMemberKeys())	return isLastPropertyMarkedAsSearchKey(p); // explicit search key
		return 
			(editable && isLastKeyProperty(p)) || // with key visible
			(isFirstPropertyAndViewHasNoKeys(p) && keyEditable); // with key hidden
	}
	
	public boolean isRepresentsElementCollection() {   
		return isRepresentsCollection() && getMetaCollection().isElementCollection();
	}
	
	
		
	private String getLastSearchKeyName() {
		for (Iterator it = getMetaPropertiesIncludingGroups().iterator(); it.hasNext();) {
			MetaProperty p = (MetaProperty) it.next();
			if (isLastSearchKey(p)) return p.getName();
		}
		return "";
	}
	
	/** @since 5.7.1 */
	public String getSearchKeyName() { 
		String searchKeyName = getLastSearchKeyName();
		return Is.emptyString(searchKeyName)?getMetaModel().getAllMetaPropertiesKey().get(0).getName():searchKeyName;
	}
		
	public boolean isHidden(String name) {				
		return hiddenMembers != null && hiddenMembers.contains(name);
	}

	/**
	 * To hide/show an element of the user interface. 
	 * <br/>
	 * You can hide properties, references, collections, groups and sections. 
	 * <br/>
	 * If the name of a member and its container section or group is the same, the container section or
	 * group will be hidden. If you want to hide the member inside you should rename the group or section.
	 * The name of the member can be qualified (since v6.2).
	 * 
	 * @param name  Name (can be qualified) of the member, group or section
	 * @param hidden  true to hide, false to show
	 */
	public void setHidden(String name, boolean hidden) throws XavaException {
		int idx = name.indexOf('.');
		if (idx >= 0) {
			String ref = name.substring(0, idx);
			String member = name.substring(idx + 1);
			getSubview(ref).setHidden(member, hidden);
			return;
		}
		if (hiddenMembers == null) {
			if (!hidden) return;		
			hiddenMembers = new HashSet();
		}
		// getSubview() is for starting the process that creates subviews and groups
		// before to hide any member
		getSubviews(); 
		boolean modified = false;
		if (hidden) modified = hiddenMembers.add(name);
		else modified = hiddenMembers.remove(name);
		if (modified) { 
			metaMembers = null;
			metaMembersIncludingHiddenKey = null;
			metaMembersIncludingCollectionTotals = null; 
			membersNames = null;		
			membersNamesWithoutSections = null;
			membersNamesWithoutSectionsAndCollections = null;
			membersNamesWithHidden = null;
			membersNamesInGroup = null;
			metaPropertiesQualified = null;
			reloadNeeded = true;			
			updateHiddenSections(); 
			getFirstLevelView().reloadNeeded = true; 
			refreshCollection();
		}
		
		// The hidden ones are sent to all sections and groups,
		// thus if a property is shown in heading and in some
		// sections, is hidden/show in all places.
		if (hasGroups()) {
			Iterator it = getGroupsViews().values().iterator();
			while (it.hasNext()) {
				View v = (View) it.next();
				v.setHidden(name, hidden);
			}
		}
		
		if (hasSections()) {
			int count = getSections().size();
			for (int i = 0; i < count; i++) {				
				getSectionView(i).setHidden(name, hidden);				
			}	
		}
		
	}
		
	public View getParent() { 	
		if (parent != null && (parent.isSection() || parent.isGroup())) { 
			View result = parent.getParent();
			realParent = parent;
			parent = result;
		}
		return parent;
	}
	
	private View getRealParent() {  
		return realParent == null?parent:realParent;
	}
	
	public View getRoot() { 		
		View parent = getParent();
		if (parent == null) return this;
		return parent.getRoot();
	}
	
	private View getFirstLevelView() { 
		if (isFirstLevel()) return this;
		return getParent().getFirstLevelView();
	}
	
	/**
	 * 
	 * @since 5.8.1
	 */
	public View getCollectionRootOrRoot() { 
		if (isRepresentsCollection()) return this;
		View parent = getParent();
		if (parent == null) return this;
		return parent.getCollectionRootOrRoot();
	}

	private void setParent(View view) {
		parent = view;
	}
	
	
	public boolean hasSections() throws XavaException {			
		if (getModelName() == null) return false;
		if (displayAsDescriptionsList()) return false; 						
		return getMetaView().hasSections() && !getSections().isEmpty();
	}
	
	private boolean displayAsDescriptionsList() {
		if (getMemberName() == null) return false;
		if (!isRepresentsEntityReference()) return false;
		try {
			MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());
			return getParent().displayAsDescriptionsList(ref);
		}
		catch (XavaException ex) {
			// log.warn(XavaResources.getString("display_as_description_warning", getMemberName()));  
			return false;
		}				
	}
	
	public boolean displayAsDescriptionsListAndReferenceView() { 
		if (getMemberName() == null) return false;
		if (!isRepresentsEntityReference()) return false;
		try {
			MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());
			return getParent().displayAsDescriptionsListAndReferenceView(ref);
		}
		catch (XavaException ex) {
			return false;
		}				
	}
	
	public boolean displayReferenceWithNoFrameEditor(MetaReference ref) { 
		if (displayAsDescriptionsList(ref)) return true;
		try {
			return !WebEditors.getMetaEditorFor(ref, getViewName()).isFrame(); 
		}
		catch (ElementNotFoundException ex) {
			return false;  
		}
		
	}
	
	public boolean displayWithFrame() { 
		View parent = getRealParent();
		if (parent == null) return false;
		if (isGroup()) return parent.displayWithFrame();
		return 	isFrame() && 
				(!parent.isSection() || parent.getMetaMembers().size() > 1);
	}
	
	private boolean displayReferenceWithNotCompositeEditor() {  
		if (getMemberName() == null) return false;
		if (!(isRepresentsEntityReference() || isRepresentsAggregate())) return false;
		try {
			MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());
			return getParent().displayReferenceWithNotCompositeEditor(ref);
		}
		catch (XavaException ex) {  
			return false;
		}				
	}	

	
	public boolean displayReferenceWithNotCompositeEditor(MetaReference ref) { 
		if (displayAsDescriptionsList(ref)) return true;
		try {
			return !WebEditors.getMetaEditorFor(ref, getViewName()).isComposite(); 
		}
		catch (ElementNotFoundException ex) {
			return false;  
		}		
	}

	public List getSections() throws XavaException {	
		if (sections == null) {
			sections = getMetaView().getSections();
		}
		return sections;
	}
	
	private void updateHiddenSections() { 
		if (!getMetaView().hasSections()) return;
		Object preSection = sections==null || sections.isEmpty()?null:sections.get(activeSection); 
		if (hiddenMembers != null && !hiddenMembers.isEmpty()) {
			List newSections = new ArrayList(getMetaView().getSections());			
			removeHidden(newSections);			
			sections = newSections;
		}
		else {
			sections = getMetaView().getSections();
		}
		Object postSection = activeSection < sections.size()?sections.get(activeSection):null;
		if (preSection != postSection) {
			int idx = sections.indexOf(preSection);
			if (idx >= 0) activeSection = idx;
		}
	}
	
	public View getSectionView(int index) throws XavaException {
		View section = getSectionView((MetaView) getSections().get(index));
		if (Is.emptyString(section.getViewObject()) && !Is.emptyString(getViewObject())) {
			section.setViewObject(getViewObject() + "_section" + index); 
		}
		return section;
	}
			
	private View getSectionView(MetaView metaView) { 
		if (sectionsViews == null) {
			sectionsViews = new HashMap<MetaView, View>();			
		}
		View v = sectionsViews.get(metaView); 
		
		if (v == null) {
			v = new View();
			v.setSection(true);
			v.setParent(this);			
			v.setModelName(getModelName());			
			v.setMetaView(metaView);
			sectionsViews.put(metaView, v);
		}		
		
		return v;
	}

	public boolean isSection() {		
		return section;
	}

	public void setSection(boolean b) {		
		section = b;
	}

	public int getActiveSection() throws XavaException {		
		if (activeSection >= getSections().size()) {
			activeSection = 0;
		}
		return activeSection;
	}

	public void setActiveSection(int i) {
		if (activeSection != i) sectionChanged = true; 
		activeSection = i;		
	}

	public int getCollectionEditingRow() {		
		return collectionEditingRow;
	}

	public void setCollectionEditingRow(int i) { 		
		collectionEditingRow = i;
	}

	public String getMemberName() {		
		return memberName;
	}
	
	public void setMemberName(String string) throws XavaException {		
		memberName = string;		
		setLabelsIdForMetaPropertiesList();
	}

	public boolean isCollectionMembersEditables() {				
		return isEditable() && collectionMembersEditables; 
	}

	public void setCollectionMembersEditables(boolean b) {		
		collectionMembersEditables = b;
	}
	
	public boolean isCollectionSortable() {  
		assertRepresentsCollection("isCollectionSortable()");
		return getMetaCollection().isSortable() && isCollectionEditable(); 
	}

	public boolean isCollectionEditable() {		
		if (isCollectionEditableFixed()) return false;
		return collectionEditable;
	}

	public void setCollectionEditable(boolean b) {		
		collectionEditable = b;
	}

	public boolean isRepresentsEntityReference() {
		if (isGroup()) return getParent().isRepresentsEntityReference(); 
		return representsEntityReference;
	}

	public void setRepresentsEntityReference(boolean b) {		
		representsEntityReference = b;
		representsAggregate = !representsEntityReference;
	}

	/**
	 * Has sense if the subview represents a collection, although always works.	 
	 */
	public Collection getActionsNamesDetail() {		
		return actionsNamesDetail==null?Collections.EMPTY_LIST:actionsNamesDetail;
	}

	/**
	 * Has sense if the subview represents a collection, although always works.	 
	 */
	public void setActionsNamesDetail(Collection collection) {		
		actionsNamesDetail = collection;
	}
	
	/**
	 * Has sense if the subview represents a collection, although always works.
	 * 
	 * @param qualifiedActionName  Qualified name (controller.action) as in controllers.xml 	 
	 */	
	public void addDetailAction(String qualifiedActionName) {		
		if (actionsNamesDetail == null) actionsNamesDetail = new ArrayList();
		actionsNamesDetail.add(qualifiedActionName);
	}

	/**
	 * Has sense if the subview represents a collection, although always works.
	 * 
	 * @param qualifiedActionName  Qualified name (controller.action) as in controllers.xml 	 
	 */	
	public void removeDetailAction(String qualifiedActionName) {		
		if (actionsNamesDetail == null) return;
		actionsNamesDetail.remove(qualifiedActionName);		
	}
	
	/**
	 * Has sense if the subview represents a collection, although always works.
	 * 
	 * @param qualifiedActionName  Qualified name (controller.action) as in controllers.xml 	 
	 */	
	public void addListAction(String qualifiedActionName) {		
		if (actionsNamesList == null) actionsNamesList = new ArrayList(getDefaultListActionsForCollections()); 
		if (actionsNamesList.contains(qualifiedActionName))	return;
		refreshCollection(); 
		if (getFullOrderActionsNamesList().contains(qualifiedActionName)) {
			// If already in order we insert it in the correct position
			ArrayList list = (ArrayList) actionsNamesList;
			int position = 0;
			for (Object o : getFullOrderActionsNamesList()) {
				if (actionsNamesList.contains(o))
					position++;
				else if (o.equals(qualifiedActionName)) {
					((ArrayList) actionsNamesList).add(position, o);
					return;
				}
			}
		} else {
			// If it does not exist, we add it at the end of both list 
			actionsNamesList.add(qualifiedActionName);
			getFullOrderActionsNamesList().add(qualifiedActionName);
		}		
		actionsNamesListRefined = false;  
		subcontrollersNamesListRefined = false; 
	}
	
	public void addRowAction(String qualifiedActionName) {	
		if (actionsNamesRow == null) actionsNamesRow = new ArrayList(getDefaultRowActionsForCollections()); 
		if (actionsNamesRow.contains(qualifiedActionName))	return;
		refreshCollection(); 
		if (getFullOrderActionsNamesRow().contains(qualifiedActionName)) {
			// If already in order we insert it in the correct position
			ArrayList row = (ArrayList) actionsNamesRow;	// no se si row seria el nombre apropiado, para que se usa row?
			int position = 0;
			for (Object o : getFullOrderActionsNamesRow()) {
				if (actionsNamesRow.contains(o))
					position++;
				else if (o.equals(qualifiedActionName)) {
					((ArrayList) actionsNamesRow).add(position, o);
					return;
				}
			}
		} else {
			// If it does not exist, we add it at the end of both list 
			actionsNamesRow.add(qualifiedActionName);
			getFullOrderActionsNamesRow().add(qualifiedActionName);
		}
		actionsNamesRowRefined = false;
	}	
	
	public void removeRowAction(String qualifiedActionName) { 		
		if (actionsNamesRow == null) actionsNamesRow = new ArrayList(getDefaultRowActionsForCollections()); 
		actionsNamesRow.remove(qualifiedActionName);
		actionsNamesRowRefined = false;
		refreshCollection(); 
	}	


	/**
	 * Has sense if the subview represents a collection, although always works.
	 * 
	 * @param qualifiedActionName  Qualified name (controller.action) as in controllers.xml 	 
	 */	
	public void removeListAction(String qualifiedActionName) {		
		if (actionsNamesList == null) actionsNamesList = new ArrayList(getDefaultListActionsForCollections()); 
		actionsNamesList.remove(qualifiedActionName);
		actionsNamesListRefined = false; 
		subcontrollersNamesListRefined = false; 
		refreshCollection(); 
	}
	
	public Collection getRowActionsNames() { 
		Collection rowActionsNames = new ArrayList();
		if (isCollectionEditable() && isRowAction(getRemoveSelectedCollectionElementsAction())) {
			rowActionsNames.add(getRemoveSelectedCollectionElementsAction());
		}		
		rowActionsNames.addAll(getActionsNamesRow());
		for (Object action: getActionsNamesList()) {
			if (isRowAction(action)) {
				rowActionsNames.add(action);
			}			
		}
		return rowActionsNames;
	}
	
	private boolean isRowAction(Object qualifiedAction) {
		if (Is.emptyString((String) qualifiedAction)) return false;
		return MetaControllers.getMetaAction((String) qualifiedAction).isInEachRow();
	}
		
	public Collection getActionsNamesList() {
		if (actionsNamesList == null) actionsNamesList = new ArrayList(getDefaultListActionsForCollections());
		if (!actionsNamesListRefined) {
			refine(actionsNamesList);
			actionsNamesListRefined = true;
		}
		return actionsNamesList;
	}
	
	public Collection<String> getSubcontrollersNamesList(){
		if (subcontrollersNamesList == null) return Collections.EMPTY_LIST;
		if (!subcontrollersNamesListRefined) {
			refine(subcontrollersNamesList);
			subcontrollersNamesListRefined = true;
		}
		return subcontrollersNamesList;		
	}
	
	public boolean hasListActions() {				
		// return !getActionsNamesList().isEmpty();
		return true;	// because RemoveSelectedInCollectionAction is always present (at the moment)
	}
	
	public void setActionsNamesList(Collection collection) {		
		actionsNamesList = collection;
		actionsNamesListRefined = false; 
		subcontrollersNamesListRefined = false; 
		if (fullOrderActionsNamesList == null) {
			fullOrderActionsNamesList = new ArrayList(collection);
		}
		refreshCollection();
	}
	
	public void setSubcontrollersNamesList(Collection collection) {		
		subcontrollersNamesList = collection;
		refreshCollection();
	}
	
	public void setActionsNamesRow(Collection collection) { 		
		actionsNamesRow = collection;
		actionsNamesRowRefined = false;
		if (fullOrderActionsNamesRow == null) {
			fullOrderActionsNamesRow = new ArrayList(collection);
		}
		refreshCollection();
	}	
	
	public Collection getActionsNamesRow() {
		if (actionsNamesRow == null) actionsNamesRow = new ArrayList(getDefaultRowActionsForCollections());
		if (!actionsNamesRowRefined) {
			refine(actionsNamesRow);
			actionsNamesRowRefined = true;
		}
		return actionsNamesRow;
	}
			                                                                                                                                                                                                                                                                                                                                                                                                                   
	public Collection getActionsNamesForProperty(MetaProperty p, boolean editable) throws XavaException {		
		if (getParentIfSectionOrGroup().changedActionsByProperty == null) return getMetaView().getActionsNamesForProperty(p, editable);
		initializeActionsForProperty(p.getName());
		if (!getChangedActionsByProperty().containsKey(p.getName())) return getMetaView().getActionsNamesForProperty(p, editable);
		Collection addedActions = getChangedActionsByProperty().get(p.getName()); 
		if (addedActions.isEmpty()) return getMetaView().getActionsNamesForProperty(p, editable);
		Collection result = new ArrayList(getMetaView().getActionsNamesForProperty(p, editable));
		result.addAll(addedActions);
		return result;
	}
	
	public Collection getActionsNamesForReference(MetaReference ref, boolean editable) throws XavaException {		
		return getMetaView().getActionsNamesForReference(ref, editable);
	}
	
	/**
	 * If this is a view that represents a reference
	 */
	public Collection getActionsNamesForReference(boolean editable) throws XavaException {		
		if (!isRepresentsEntityReference()) return Collections.EMPTY_LIST;
		if (isGroup()) return getParent().getActionsNamesForReference(editable); 
		View parent = getParent();
		if (parent == null) return Collections.EMPTY_LIST; // Impossible?
		MetaReference ref = null;
		try {
			ref = parent.getMetaModel().getMetaReference(getMemberName());
		}
		catch (ElementNotFoundException ex) {
			ref = parent.getMetaModel().getMetaCollection(getMemberName()).getMetaReference();
		}
		return parent.getActionsNamesForReference(ref, editable);
	}	
	
	
	public int getLabelFormatForProperty(MetaProperty p) throws XavaException {
		if (isFlowLayout()) return LabelFormatType.SMALL.ordinal(); 
		return getMetaView().getLabelFormatForProperty(p);
	}
	
	public int getLabelFormatForReference(MetaReference ref) throws XavaException {
		if (isFlowLayout()) return LabelFormatType.SMALL.ordinal(); 
		return getMetaView().getLabelFormatForReference(ref);
	}
	
	/**
	 * To determine what type of layout to do with flowLayout activated.
	 * 
	 * @since 5.7
	 */
	public boolean isSimple() {  
		if (hasSections()) return false;
		int c = 0; 
		for (MetaMember member: getMetaMembers()) {
			if (member instanceof PropertiesSeparator) continue;
			if (member.isHidden()) continue;
			if (member instanceof MetaProperty) {
				MetaEditor editor = WebEditors.getMetaEditorFor(member, getViewName());
				if (editor.isFrame() || editor.isComposite()) return false;
				c++;
			}
			else if (member instanceof MetaReference) {
				if (!displayAsDescriptionsList((MetaReference) member)) {
					MetaEditor editor = WebEditors.getMetaEditorFor(member, getViewName());
					if (editor.isFrame() || editor.isComposite()) return false;
				}
				c++;
			}
			else return false;
			if (c > 8) return false;
		}
		return true;
	}
	
	public boolean isFlowLayout() { 
		if ("SignIn".equals(getModelName())) return false; // A little ad hoc, but was the simplest way at the moment
		if (Chart.class.getSimpleName().equals(getModelName())) return false; // A little ad hoc, but was the simplest way at the moment
		return XavaPreferences.getInstance().isFlowLayout(); 
	}
	
	// @Trifon
	public int getDisplaySizeForProperty(MetaProperty p) throws XavaException {		
		return getMetaView().getDisplaySizeForProperty(p);
	}
	
	public int getDisplaySizeForProperty(String propertyName) throws XavaException { 		
		int idx = propertyName.indexOf('.');		
		if (idx < 0) {
			return getDisplaySizeForProperty(getMetaProperty(propertyName)); 
		} 
		else {						
			String subviewName = propertyName.substring(0, idx);			
			String member = propertyName.substring(idx+1);
			View subview = getSubview(subviewName);
			if (subview.isRepresentsElementCollection()) { 
				member = Strings.noFirstTokenWithoutFirstDelim(member, ".");
			}
			return subview.getDisplaySizeForProperty(member);
		}				
	}
	
	public boolean isFrame() throws XavaException {
		if (displayAsDescriptionsListAndReferenceView()) return true; 	
		return getMetaView().isFrame();
	}
	
	public String getFocusPropertyId() {
		try {			
			if (!Is.emptyString(focusCurrentId)) {
				return focusCurrentId;
			}
			if (!Is.emptyString(focusPropertyId) && !focusForward) {
				return focusPropertyId;
			}
			return calculateFocusPropertyId();
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("focus_warning"),ex);
			return "";			
		}
	}
	
	private boolean isFirstLevel() { 
		return firstLevel || getParent() == null; 
	}
	
	private String calculateFocusPropertyId() throws XavaException { 
		String prefix = getPropertyPrefix(); 
		if (prefix == null) prefix = "";
		
		if (Is.emptyString(focusPropertyId)) {			
			return getFirsEditablePropertyId(prefix);
		}
		else {		
			String focusPropertyName = focusPropertyId.startsWith(prefix)?focusPropertyId.substring(prefix.length()):focusPropertyId; 
			int idx = focusPropertyName.indexOf('.'); 
			if (idx < 0) {							
				String name = getNextFocusPropertyName(focusPropertyName);				
				return name==null?getFirsEditablePropertyId(prefix):prefix + name;
			}
			else {				
				String subviewName = focusPropertyName.substring(0, idx);
				String member = focusPropertyName.substring(idx + 1);
				View subview = getSubview(subviewName);				
				String name = subview.getNextFocusPropertyName(member);				
				if (name != null) return prefix + subviewName + "." + name;				
				name = getNextFocusPropertyName(subviewName);				
				return name==null?getFirsEditablePropertyId(prefix):prefix + name;				  
			}
		}
	}
	
	private String getFirsEditablePropertyId(String prefix) throws XavaException {
		Iterator it = getMetaMembers().iterator();
		while (it.hasNext()) {
			MetaMember m = (MetaMember) it.next();			
			if (m instanceof MetaProperty) {			
				if (PropertiesSeparator.INSTANCE.equals(m)) continue; 
				if (isEditableImpl((MetaProperty) m) || isLastSearchKey((MetaProperty) m)) { 
					return prefix + m.getName();
				}
			}
			else if (m instanceof MetaGroup) {
				String result = getGroupView(m.getName()).getFirsEditablePropertyId(prefix);
				if (result != null) return result;
			}
			else if (m instanceof MetaReference) {				
				String result = getSubview(m.getName()).getFirsEditablePropertyId(prefix + m.getName() + ".");
				if (result != null) return result;
			}
			
		}		
		if (hasSections()) {
			return getSectionView(getActiveSection()).getFirsEditablePropertyId(prefix);
		}
		return null;
	}
	
	private String getNextFocusPropertyName(String memberName) throws XavaException {
		memberName = Strings.firstToken(memberName, "."); 
		Iterator it = getMetaMembers().iterator();
		boolean found = false;		
		while (it.hasNext()) {
			MetaMember m = (MetaMember) it.next();						
			if (m instanceof MetaGroup) {				
				String name = getGroupView(m.getName()).getNextFocusPropertyName(memberName);
				if ("__FOUND__".equals(name)) {
					found = true; 
					continue;
				}
				else if (name != null) return name;
			}
						
			if (m.getName().equals(memberName)) {								
				found = true;
				continue;
			}
			if (!found) continue;
			if (m instanceof MetaGroup) {												
				String name = getGroupView(m.getName()).getFirsEditablePropertyId("");				
				if (name != null) return name;
			}			
			if (m instanceof MetaProperty &&
				!(m instanceof PropertiesSeparator) && 
				isEditableImpl((MetaProperty) m)) {					
				return m.getName();
			}			
			if (m instanceof MetaReference &&				 
				isEditable((MetaReference) m)) {
				if (displayAsDescriptionsList((MetaReference) m)) {
					MetaReference ref = (MetaReference) m;
					Collection keys = ref.getMetaModelReferenced().getKeyPropertiesNames();
					if (keys.size() == 1) {
						String key = (String) keys.iterator().next();
						return m.getName() + "." + key;
					}
					else {
						return m.getName();
					}
				}
				else {				
					return getSubview(m.getName()).getFirsEditablePropertyId(m.getName() + ".");
				}
			}			
		}
		if (hasSections()) {
			return getSectionView(getActiveSection()).getNextFocusPropertyName(memberName);
		}	
		if (isGroup() && found) return "__FOUND__";		
		return null;
	}

	private void setIdFocusProperty(String string) {
		focusPropertyId = Ids.undecorate(string); 
	}
	
	private void setFocusCurrentId(String string) {
		focusCurrentId = Ids.undecorate(string); 
	}
	

    /**
     * Sets the focus in the provided property
     */
    public void setFocus(String newFocusProperty) {  
        focusPropertyId = newFocusProperty; 
        focusCurrentId = null; 
        focusForward = false; 
    }

	public String getEditCollectionElementAction() {
		String action = getCollectionAction(editCollectionElementAction, COLLECTION_EDIT_ACTION);
		return action.equals("") ? getViewCollectionElementAction() : action;  
	}
	
	public String getViewCollectionElementAction() {		
		return viewCollectionElementAction == null?COLLECTION_VIEW_ACTION:viewCollectionElementAction;
	}
		
	public void setEditCollectionElementAction(String editCollectionElementAction) {		
		this.editCollectionElementAction = editCollectionElementAction;
	}
	
	public void setViewCollectionElementAction(String viewCollectionElementAction) {		
		this.viewCollectionElementAction = viewCollectionElementAction;
	}	

	public void recalculateProperties() {
		try {												
			Map names = getCalculatedPropertiesNames();
			if (!names.isEmpty()) {
				Map values = MapFacade.getValues(getModelName(), getKeyValues(), names);
				addValues(values);
				recalculateRecalculatedProperties();
			}			
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("recalculate_view_error"), ex);
			getErrors().add("recalculate_view_error", getModelName());	 								
		}				
	}
	
	private boolean isReadOnly() {
		return readOnly;
	}
	private void setReadOnly(boolean onlyRead) {
		this.readOnly = onlyRead;
	}
	
	public String getLabelFor(MetaMember p) throws XavaException {
		if (getMetaView().getLabelFormatFor(p) == LabelFormatType.NO_LABEL.ordinal()) return "";
		if (getLabels() != null) {
			String idLabel = (String) getLabels().get(p.getName());
			if (idLabel != null) { 
				return Labels.get(idLabel, Locales.getCurrent());
			}
		}		
		if (!Is.emptyString(getMemberName())) {
			int idx = p.getId().lastIndexOf('.');
			if (idx < 0) idx = 0; 
			String id = p.getId().substring(0, idx) + "." + getMemberName() + p.getId().substring(idx);
			if (Labels.existsExact(id, Locales.getCurrent())) {
				return Labels.get(id, Locales.getCurrent());
			}
			id = getMemberName() + p.getId().substring(idx);
			if (Labels.existsExact(id, Locales.getCurrent())) {				
				return Labels.get(id, Locales.getCurrent());
			}			
		}
		return p.getLabel(getRequest());
	}	

	/**
	 * 
	 * @param property  Since v4.2 can qualified. Since v6.4 can be a group or section name
	 * @param id  Id of the label from i18n files
	 */
	public void setLabelId(String property, String id) {
		if (property == null) return; 
		int idx = property.indexOf('.');
		if (idx >= 0) {
			String subviewName = property.substring(0, idx);
			String member = property.substring(idx+1);								 				
			getSubview(subviewName).setLabelId(member, id);
			return;
		}
		if (getLabels() == null) setLabels(new HashMap());
		String old = (String) getLabels().put(property, id);		
		if (!Is.equal(old, id)) {
			if (getRoot().changedLabels == null) getRoot().changedLabels = new HashMap();
			int sectionIndex = getIndexOfSection(property);
			if (sectionIndex >= 0) {
				String sectionId = getViewObject() + "_section" + sectionIndex + "_sectionName";
				String label = Labels.get(id);
				getSectionView(sectionIndex).setTitle(label);
				getRoot().changedLabels.put(sectionId, label);
			}
			else if (getGroupsViews().containsKey(property)) {
				String label = Labels.get(id);
				getGroupView(property).setTitle(label);
				getRoot().changedLabels.put(property, label);
			}
			else {
				getRoot().changedLabels.put(getPropertyPrefix() + property,
					getLabelFor(getMetaModel().getMetaMember(property)));
			}
		}
	}
	
	/**
	 * @return -1 if name is not a section
	 * @since 6.4
	 */
	private int getIndexOfSection(String name) {
		try {
			View view = getSection(name);	
			for (int i = 0; i < getSections().size(); i++) {
				MetaView mv = (MetaView)getSections().get(i);
				if (mv.equals(view.getMetaView())) return i;
			}
			return -1;
		}
		catch(ElementNotFoundException e) {
			return -1;	// section does not exist
		}
	}
	
	private Map getLabels() {
		View root = getRoot();
		if (this == root) return labels;
		return root.getLabels();
	}
	
	private void setLabels(Map labels) {
		View root = getRoot();
		if (this == root) {
			this.labels = labels;
		}
		else {
			root.setLabels(labels);
		}
	}
	
	private boolean isMetaProperty(Object member) { 
		return member instanceof MetaProperty && !member.equals(PropertiesSeparator.INSTANCE) && !(member instanceof MetaViewAction);
	}
	
	private boolean isGroup() {  
		return group;
	}
	private void setGroup(boolean group) {
		this.group = group;
	}
	public boolean isOnlyThrowsOnChange() {		
		if (getParent() == null) return onlyThrowsOnChange;
		return getParent().isOnlyThrowsOnChange();
	}
	public void setOnlyThrowsOnChange(boolean onlyThrowsOnChange) {		
		if (getParent() == null) this.onlyThrowsOnChange = onlyThrowsOnChange;
		else getParent().setOnlyThrowsOnChange(onlyThrowsOnChange);
	}

	private boolean isCollectionEditableFixed() {
		return collectionEditableFixed;
	}

	private void setCollectionEditableFixed(boolean collectionEditableFixed) {
		this.collectionEditableFixed = collectionEditableFixed;
	}
	
	public void setCollectionColumnWidth(int columnIndex, int width) { 
		try {
			Preferences pref = getPreferences();
			pref.putInt(
				COLUMN_WIDTH + getMetaPropertiesList().get(columnIndex).getQualifiedName(), 
				width
			);
			pref.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_store_column_width"),ex);
		}
	}
	
	public int getCollectionColumnWidth(int columnIndex) {
		MetaProperty p = getMetaPropertiesList().get(columnIndex); 
		try {
			return getPreferences().getInt( 				
				COLUMN_WIDTH + p.getQualifiedName(), defaultColumnWidth(p, columnIndex) 
			);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_load_column_width"),ex);
			return defaultColumnWidth(p, columnIndex); 
		}
	}	
	
	private int defaultColumnWidth(MetaProperty p, int columnIndex) { 
		if (getSumPropertiesSize() < 100) return -1;
		if (hasCollectionTotal(1, columnIndex) || hasCollectionTotal(1, columnIndex + 1)) return -1; 
		return Tab.friendViewGetDefaultColumnWidth(p);
	} 
	
	private int getSumPropertiesSize() { 
		int result = 0;
		for (MetaProperty eachProperty: getMetaPropertiesList()) {
			result += eachProperty.getSize();
		}
		return result;
	}
	
	private Preferences getPreferences() throws BackingStoreException { 		
		String viewName = getViewName();
		if (viewName == null) viewName = ""; 
		return Users.getCurrentPreferences().node("view." + getMetaModel().getName() + "." + viewName + ".");
	}


	public String getHideCollectionElementAction() {
		return getCollectionAction(hideCollectionElementAction, "Collection.hideDetail");
	}

	public void setHideCollectionElementAction(String hideCollectionElementAction) {		
		this.hideCollectionElementAction = hideCollectionElementAction;
	}
	
	public String getAddCollectionElementAction() {
		return getCollectionAction(addCollectionElementAction, "Collection.add");		
	}

	public String getNewCollectionElementAction() {
		return getCollectionAction(newCollectionElementAction, getMetaCollection().getInverseCollection() == null?"Collection.new":"ManyToMany.new"); 
	}

	public void setNewCollectionElementAction(String newCollectionElementAction) {		
		this.newCollectionElementAction = newCollectionElementAction;
	}

	public String getRemoveCollectionElementAction() {
		return getCollectionAction(removeCollectionElementAction, "Collection.remove");
	}

	public void setRemoveCollectionElementAction(
			String removeCollectionElementAction) {		
		this.removeCollectionElementAction = removeCollectionElementAction;
	}
	
	public String getRemoveSelectedCollectionElementsAction() {	
		return getCollectionAction(removeSelectedCollectionElementsAction, 
				                   isRepresentsElementCollection()?removeSelectedCollectionElementsAction:"Collection.removeSelected");
	}

	public void setRemoveSelectedCollectionElementsAction(
			String removeSelectedCollectionElementAction) {		
		this.removeSelectedCollectionElementsAction = removeSelectedCollectionElementAction;
	}
	
	public String getSaveCollectionElementAction() {
		return getCollectionAction(saveCollectionElementAction, "Collection.save");
	}

	public void setSaveCollectionElementAction(String saveCollectionElementAction) {
		this.saveCollectionElementAction = saveCollectionElementAction;
	}
	
	/** @since 5.7 */
	public boolean isAlignedByColumns() throws XavaException {
		if (isFlowLayout()) return false; 
		return getMetaView().isAlignedByColumns();
	}
	
	/**
	 * Associates an arbitrary object to this view. <p>
	 * 
	 * This object is not used by the view for any purpose,
	 * this is only a convenience method for developer.<br>
	 * 
	 * @param name The id to retrieve the object later.
	 * @param object The object to associate.
	 */
	public void putObject(String name, Object object) { 
		if (objects == null) objects = new HashMap();
		objects.put(name, object);
	}
	
	/**
	 * Retrieve an object associated to this view. <p>
	 * 
	 * The object have to be associated to this view using @link #putObject(String, Object)
	 * 
	 * @param name Name of the object to retrieve.
	 * @return The object, or null if it is not found.
	 */
	public Object getObject(String name) { 
		if (objects == null) return null;
		return objects.get(name);
	}
	
	/**
	 * Remove from this view an object previously associated with @link #putObject(String, Object)
	 * @param name Name of the object to remove.
	 */
	public void removeObject(String name) {
		if (objects == null) return;
		objects.remove(name);
	}

	private Collection getRowStyles() {
		return rowStyles;
	}

	private void setRowStyles(Collection rowStyles) {
		this.rowStyles = rowStyles;
	}
	
	/**
	 * Qualified ids of the properties and references with not composite editor (that includes
	 * descriptions lists) changed in this request. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 * 
	 * @return In each entry the key is the qualified id and value the container view 
	 */
	public Map getChangedPropertiesActionsAndReferencesWithNotCompositeEditor() {  		
		if (changedPropertiesActionsAndReferencesWithNotCompositeEditor == null) {
			changedPropertiesActionsAndReferencesWithNotCompositeEditor = new HashMap();
			fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(changedPropertiesActionsAndReferencesWithNotCompositeEditor);
		}		
		return changedPropertiesActionsAndReferencesWithNotCompositeEditor;
	}
	
	private void propertiesAndReferencesWithReadOnlywithOnCreateFalse(Map result) {
		for (MetaMember m : getMetaMembers()) {
			if (m instanceof MetaProperty) {
				MetaPropertyView metaPropertyView = getMetaView().getMetaPropertyViewFor(m.getName());
				if (metaPropertyView != null && !metaPropertyView.isReadOnlyOnCreate() && metaPropertyView.isReadOnly()) {
					result.put(m.getName(), this);
				}
			}
			else if (m instanceof MetaReference) {
				MetaReference t = getMetaReference(m.getName());
				MetaReferenceView metaReferenceView = getMetaView().getMetaReferenceView(t);
				if (metaReferenceView != null && isKeyEditable() && metaReferenceView.isReadOnly() && !metaReferenceView.isReadOnlyOnCreate()) {
					result.put(m.getName(), this);
				}
			}
		}
	}
	
	private void fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(Map result) {
		propertiesAndReferencesWithReadOnlywithOnCreateFalse(result);
		
		if (displayAsDescriptionsList() && 
				(
					refreshDescriptionsLists ||	
					getParent().hasEditableMemberChanged(getMemberName()) || 
					(
					!Is.emptyString(getMetaDescriptionsList().getDepends()) &&
					!getParent().isHidden(getMemberName())
					)
				)
			)
		{
			result.put(getPropertyPrefix(), getParent().getViewForChangedProperty());
			return;
		}		 				
		
		if (displayReferenceWithNotCompositeEditor() && 
			getParent().hasEditableMemberChanged(getMemberName()))
		{	
			result.put(getPropertyPrefix(), getParent().getViewForChangedProperty());
			return;
		}		
		
		if (displayAsDescriptionsListAndReferenceView() && 
			(
				refreshDescriptionsLists ||	
				getParent().hasEditableMemberChanged(getMemberName()) || 
				(
				!Is.emptyString(getMetaDescriptionsList().getDepends()) &&
				!getParent().isHidden(getMemberName())
				)
			)
		)
		{
			result.put(getPropertyPrefix().replace(".", ""), getViewForChangedProperty()); 
		}
				
		if (oldValues == null) oldValues = Collections.EMPTY_MAP;
		if (values == null) values = new HashMap();
		for (Iterator it=values.entrySet().iterator(); it.hasNext(); ) { 
			Map.Entry en = (Map.Entry) it.next();
			boolean isKey = getMetaModel().isKeyOrSearchKey((String) en.getKey());
			if (!oldValues.containsKey(en.getKey()) && en.getValue() != null || 
				!equals(en.getValue(), oldValues.get(en.getKey())) || 					
				isKey && hasKeyEditableChanged() || 
				hasEditableChanged() ||
				hasEditableMemberChanged((String) en.getKey()) ||
				propertyHasChangedActions((String) en.getKey()) || 
				formattedProperties != null && formattedProperties.contains(en.getKey()) ||
				editorMustBeReloaded((String) en.getKey())) 
			{				
				addChangedPropertyOrReferenceWithSingleEditor(result, (String) en.getKey());
			}
			oldValues.remove(en.getKey());			
		}
		for (Iterator it=oldValues.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry en = (Map.Entry) it.next();
			if (!equals(en.getValue(), values.get(en.getKey())) ||
				editorMustBeReloaded((String) en.getKey()) ||
				hasKeyEditableChanged())  
			{					
				addChangedPropertyOrReferenceWithSingleEditor(result, (String) en.getKey());
			}
		}	
		
		if (hasEditableChanged()) {
			for (Iterator it = getMetaView().getNotAlwaysEnabledViewActionsNames().iterator(); it.hasNext(); ) {
				String action = (String) it.next();
				result.put(getPropertyPrefix() + action, this);
			}
		}		
			
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				if (isHidden(subview.getMemberName())) continue; 
				if (subview.isRepresentsElementCollection()) {
					subview.fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(result);
				}
				else if (subview.isRepresentsCollection()) {
					if (subview.isCollectionDetailVisible()) {
						if (subview.getViewObject() == null) { // First time
							subview.refreshCollection();
						}
						else if (!subview.mustRefreshCollection) { 
							subview.fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(result);
						}
					}
				}
				else { 				 					
					if (subview.displayReferenceWithNotCompositeEditor()) {
						subview.setPropertyPrefix(getPropertyPrefix() + subview.getMemberName());						
					}
					subview.fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(result);					
				}
			}
		}

		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().entrySet().iterator();
			while (itSubviews.hasNext()) {
				Map.Entry en = (Map.Entry) itSubviews.next(); 
				String name = (String) en.getKey();
				View subview = (View) en.getValue();
				if (!isHidden(name)) { 
					subview.fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(result);
				}
			}
		}

		if (!sectionChanged && hasSections()) {
			// Only the displayed data matters here
			getSectionView(getActiveSection()).fillChangedPropertiesActionsAndReferencesWithNotCompositeEditor(result);	
		}
	}

	private boolean hasEditableMemberChanged(String member) { 
		if (oldNotEditableMembersNames == notEditableMembersNames) return false;
		if (Is.equal(oldNotEditableMembersNames, notEditableMembersNames)) return false;
		boolean existsOld = oldNotEditableMembersNames == null?false:oldNotEditableMembersNames.contains(member);
		boolean existsCurrent = notEditableMembersNames == null?false:notEditableMembersNames.contains(member);
		return existsOld != existsCurrent;
	}
	
	private boolean editorMustBeReloaded(String memberName) {
		MetaProperty p = null;
		try {
			p = getMetaProperty(memberName);
		}
		catch (ElementNotFoundException ex) {			
			return false;
		}
		if (WebEditors.dependsOnSomeOther(p, getViewName())) {		
			return true;
		}
		MetaEditor editor = WebEditors.getMetaEditorFor(p, getViewName());		
		if (editor.isAlwaysReload()) return true;		
		// The next is a little 'ad hoc'. It's to avoid to put always-reload="true" in a lot
		// of already existing editors in existing applications.  
		if (editor.getUrl().startsWith("descriptionsEditor.jsp") && editor.hasProperty("filter")) {
			return true;
		}		
		return false;
	}

	/**
	 * If the state of the <code>editable</property> has changed in the last request. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 */
	public boolean hasEditableChanged() {
		return oldEditable != editable;
	}

	/**
	 * If the state of the <code>keyEditable</property> has changed in the last request. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 */
	public boolean hasKeyEditableChanged() {
		return oldKeyEditable != keyEditable;
	}

	private void addChangedPropertyOrReferenceWithSingleEditor(Map result, String name) { 
		if (!isHidden(name)) {
			if (displayReferenceWithNotCompositeEditor()) { 				
				result.put(getPropertyPrefix(), getParent().getViewForChangedProperty());
			}
			else if ((
					getMetaModel().containsMetaProperty(name) || 
					getMetaView().containsViewProperty(name)
				) &&				
				getMembersNamesWithoutSections().contains(name) && 
				!getMembersNamesInGroup().contains(name)) 
			{				
				result.put(getPropertyPrefix() + name, getViewForChangedProperty());
			}
			if (hasKeyEditableChanged() && displayAsDescriptionsListAndReferenceView()) {
				String propertyPrefix = getPropertyPrefix(); 
				result.put(propertyPrefix.substring(0, propertyPrefix.length() - 1), getViewForChangedProperty());
			}
		}
		
		if (getMetaModel().isKeyOrSearchKey(name)) { 
			if (!getRoot().isReloadNeeded()) refreshCollections(); 
		}
	}

	private View getViewForChangedProperty() {
		View result = getViewForChangedPropertyIfElementCollection();
		return result==null?this:result;
	}
	
	private View getViewForChangedPropertyIfElementCollection() {
		View parent = getParent();
		if (parent == null) return null; 
		if (isRepresentsElementCollection()) return parent;
		return parent.getViewForChangedPropertyIfElementCollection();
	}

	private MetaDescriptionsList getMetaDescriptionsList() { 
		MetaReference ref = getParent().getMetaModel().getMetaReference(getMemberName());
		MetaDescriptionsList descriptionsList = getParent().getMetaDescriptionsList(ref);  
		return descriptionsList;
	}
	
	public boolean descriptionsListsRefreshed() {   
		return refreshDescriptionsLists;
	}

	/**
	 * Refresh the displayed data of all the references as descriptions list of this
	 * view from database. <p>
	 */	
	public void refreshDescriptionsLists() { 
		refreshDescriptionsLists = true;
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.refreshDescriptionsLists();
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.refreshDescriptionsLists();
			}
		}						
		if (!sectionChanged && hasSections()) {
			// Only the displayed data matters here
			getSectionView(getActiveSection()).refreshDescriptionsLists();	
		}						
	}
	
	/**
	 * Refreshs the displayed data of all the collections of this
	 * view from database. <p>
	 */
	public void refreshCollections() {  
		if (isRepresentsCollection()) {
			refreshCollection();
			collectionTotals = null;
			collectionSize = -1;
			return;
		}
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.refreshCollections();
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.refreshCollections();
			}
		}						
		if (!sectionChanged && hasSections()) {
			// Only the displayed data matters here
			getSectionView(getActiveSection()).refreshCollections();	
		}						
	}
	
	/**
	 * Qualified ids of the member whose labels have changed in this request. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 * 
	 * @return In each entry the key is the qualified id and value of the new label
	 */  
	public Map getChangedLabels() {
		return changedLabels == null?Collections.EMPTY_MAP:changedLabels; 
	}

	/**
	 * Qualified ids of the collections changed in this request. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 * 
	 * @return In each entry the key is the qualified id and value the container view 
	 */	
	public Map<String, View> getChangedCollections() { 
		Map result = new HashMap();
		fillChangedCollections(result);
		return result;
	}
	
	private void fillChangedCollections(Map result) { 
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				if (subview.isRepresentsCollection() && 
					subview.mustRefreshCollection() && isShown() &&
					!isHidden(subview.getMemberName()))  
				{
					result.put(getPropertyPrefix() + subview.getMemberName(), this);
				}		
				subview.fillChangedCollections(result);				
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();				
				subview.fillChangedCollections(result);
			}
		}						
		if (!sectionChanged && hasSections()) {
			// Only the displayed data matters here
			getSectionView(getActiveSection()).fillChangedCollections(result);	
		}						
	}
	
	/**
	 * Views of sections that have collection size in the label, with the collection size. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 * 
	 * @since 5.9
	 */	
	public Map<View, Integer> getChangedCollectionSizesInSections() {  
		if (!hasSections()) return Collections.EMPTY_MAP;
		Map<View, Integer> result = new HashMap();
		fillCollectionSizesInSections(result);
		return result;
	}
	
	private void fillCollectionSizesInSections(Map<View, Integer> result) {
		if (hasSections()) {
			int count = getSections().size(); 
			for (int i=0; i<count; i++) {
				View section = getSectionView(i);
				if (section.getMetaMembers().size() == 1) {
					MetaMember member = section.getMetaMembers().iterator().next();
					if (member instanceof MetaCollection) {
						View collectionView = getSubview(member.getName());
						if (collectionView.collectionSize < 0) {
							result.put(section, collectionView.getCollectionSize(activeSection == i));
						}
					}
				}
				if (i == activeSection)	section.fillCollectionSizesInSections(result); 				
			} 
		}						
	}
	
	/**
	 * Qualified ids of the collections totals changed in this request. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 * 
	 * @return In each entry the key is the qualified id and value the container view
	 * @since 5.1 
	 */	
	public Map getChangedCollectionsTotals() { 		
		Map result = new HashMap();
		fillChangedCollectionsTotals(result);
		return result;
	}
	
	private void fillChangedCollectionsTotals(Map result) {  
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				if ((subview.isRepresentsElementCollection() && !subview.mustRefreshCollection) || 
					(subview.isRepresentsCollection() && subview.isCollectionFromModel() && !subview.isRepresentsElementCollection())) 
				{
					int rowCount = subview.getCollectionTotalsCount();
					int columnCount = subview.getMetaPropertiesList().size();					
					for (int row=0; row<rowCount; row++) {
						for (int column=0; column<columnCount; column++) {
							if (subview.hasCollectionTotal(row, column)) {
								if (!Is.equal(subview.getCollectionTotal(row, column), subview.getCollectionTotalOldValue(row, column))) {
									result.put(getPropertyPrefix() + subview.getMemberName() + ":" + row + ":" + column, this);
								}
							}
						}
					}
				}
				else subview.fillChangedCollectionsTotals(result);				
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();				
				subview.fillChangedCollectionsTotals(result);
			}
		}						
		if (!sectionChanged && hasSections()) {
			// Only the displayed data matters here
			getSectionView(getActiveSection()).fillChangedCollectionsTotals(result);	
		}						
	}	

	
	/** 
	 * Indices of selected rows of collections that has changed their selected rows but not their content. <p>
	 * 
	 * This does not have a valid value until the end of the request, and it's intended
	 * to be used from the AJAX code in order to determine what to refresh.
	 * 
	 * @since 4.5
	 */	
	public Map<String, int []> getChangedCollectionsSelectedRows() { 		
		Map result = new HashMap();
		fillChangedCollectionsSelectedRows(result);
		return result;
	}
	
	private void fillChangedCollectionsSelectedRows(Map<String, int []> result) {  
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();				
				if (subview.isRepresentsCollection() && 
					!subview.mustRefreshCollection() && isShown() &&
					!isHidden(subview.getMemberName()))  
				{
					if (subview.collectionTab != null) {						
						int [] selected = subview.collectionTab.getSelected();	// only we need the displayed data so we don't use getSelectedKeys
						if (!XArrays.haveSameElements(selected, subview.listSelected)) {
							result.put(getPropertyPrefix() + subview.getMemberName(), selected);				
						}
					}
				}				
				subview.fillChangedCollectionsSelectedRows(result);				
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();				
				subview.fillChangedCollectionsSelectedRows(result);
			}
		}						
		if (!sectionChanged && hasSections()) {
			// Only the displayed data matters here
			getSectionView(getActiveSection()).fillChangedCollectionsSelectedRows(result);	
		}						
	}		

	private boolean isShown() { 
		if (viewObject == null) return false;
		View v = this;		
		while (v != null) {
			if (v.isRepresentsCollection() && !v.isCollectionDetailVisible()) return false;
			v = v.parent;
		}
		return true;
	}

	private boolean mustRefreshCollection() { 
		if (mustRefreshCollection) return true;
		if (getMetaCollection().hasCondition() && 
			getMetaCollection().getCondition().indexOf("${this.") >= 0) 
		{
			Collection conditionArgumentsPropertyNames = getMetaCollection().getConditionArgumentsPropertyNames(); 			
			if (conditionArgumentsPropertyNames.contains(Ids.undecorate(getRoot().changedProperty))) return true; 			
			for (Iterator it=getRoot().getChangedPropertiesActionsAndReferencesWithNotCompositeEditor().keySet().iterator(); it.hasNext(); ) {				
				String changedProperty = Ids.undecorate((String) it.next()); 
				if (conditionArgumentsPropertyNames.contains(changedProperty)) return true;
			}			
		}
		return false;
	}

	/**
	 * The name of this view as session object. <p>
	 */
	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}

	/**
	 * The name of this view as session object. <p>
	 */
	public String getViewObject() {
		return viewObject;
	}

	/**
	 * Prefix used in HTML code for the properties of this view. <p>
	 */
	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}

	/**
	 * Prefix used in HTML code for the properties of this view. <p>
	 */
	public String getPropertyPrefix() {
		if (propertyPrefix != null) return propertyPrefix; // The usual case
		if (isSection() || isGroup()) {
			View parent = getParent();
			if (parent == null) return null;
			return parent.getPropertyPrefix();			
		}
		if (isRepresentsElementCollection()) { 
			return getParent().getPropertyPrefix() + ":" + getMemberName() + // The : are decoded in dwr.Module  
					"." + getCollectionEditingRow() + "."; 
		}
		View parent = getParent();
		if (parent == null) return null;
		return parent.getPropertyPrefix() + getMemberName() + ".";
	}


	/**
	 * Refresh the displayed data of the collection from database. <p> 
	 * 
	 * This view must represents a collection in order to call this method.<br>
	 */
	private void refreshCollection() {
		this.mustRefreshCollection = true;
	}
	
	public void resetCollectionsCache() {		
		if (isRepresentsElementCollection()) return; // If it's a element collection there are no subviews
		collectionValues = null; 
		
		if (hasSections()) { 
			getSectionView(getActiveSection()).resetCollectionsCache();
		}
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.resetCollectionsCache();				
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				subview.resetCollectionsCache();				
			}
		}
	}

	public View getChangedSectionsView() {
		if (sectionChanged && hasSections()) return this; // If you change this try press Ctrl-2 in login page and in a detail without sections
		
		if (hasSections()) { 
			View result = getSectionView(getActiveSection()).getChangedSectionsView();
			if (result != null) return result;
		}
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				View result = subview.getChangedSectionsView();
				if (result != null) return result;
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				View result = subview.getChangedSectionsView();
				if (result != null) return result;
			}
		}						
		return null;
	}

	public boolean isReloadNeeded() {
		return reloadNeeded;		
	}
	
	/**
	 * If the property or the reference with not composite (single) editor is displayed 
	 * in this moment then the qualified name (in ModelName.memberName format) is returned. <p>
	 * 
	 * @param name Can be the simple or the qualified name of the member
	 * @return The qualified name in form ModelName.memberName or null if is not a property
	 * 		or a reference with not composite editor.
	 */
	public String getQualifiedNameForDisplayedPropertyOrReferenceWithNotCompositeEditor(String name) { 
		if (isRepresentsCollection() && !isCollectionDetailVisible()) return null;
		String [] nameTokens = name.split("\\.");
		if (nameTokens.length > 1) {
			if (hasSubview(nameTokens[0])) {
				return getSubview(nameTokens[0]).getQualifiedNameForDisplayedPropertyOrReferenceWithNotCompositeEditor(nameTokens[1]);
			}
		}
		for (Iterator it=getMetaMembers().iterator(); it.hasNext(); ) {
			MetaMember member = (MetaMember) it.next();
			if (member instanceof MetaProperty ||
				member instanceof MetaReference &&
				displayReferenceWithNotCompositeEditor((MetaReference) member)) 
			{
				if (member.getQualifiedName().equals(name)) {
					if (isHidden(name)) return null; 
					return name;
				}
				if (member.getName().equals(name)) {
					if (isHidden(name)) return null; 
					return isRepresentsAggregate()?getMemberName() + "." + member.getName():member.getQualifiedName();  
				}
			}
		}
		if (hasSubviews()) {
			Iterator itSubviews = getSubviews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				String qualifiedName = subview.getQualifiedNameForDisplayedPropertyOrReferenceWithNotCompositeEditor(name); 
				if (qualifiedName != null) return qualifiedName;
			}
		}
		if (hasGroups()) {
			Iterator itSubviews = getGroupsViews().values().iterator();
			while (itSubviews.hasNext()) {
				View subview = (View) itSubviews.next();
				String qualifiedName = subview.getQualifiedNameForDisplayedPropertyOrReferenceWithNotCompositeEditor(name);
				if (qualifiedName != null) return qualifiedName;
			}
		}						
		if (hasSections()) {
			// Only the displayed data matters here
			String qualifiedName = getSectionView(getActiveSection()).getQualifiedNameForDisplayedPropertyOrReferenceWithNotCompositeEditor(name);			
			if (qualifiedName != null) return qualifiedName;	
		}								
		return null;
	}
	
	/**
	 * If the property has actions associated to it. <p>
	 * 
	 * If the actions are showed or not in this moment has no effect in the result.<br>  
	 */
	public boolean propertyHasActions(String name) {
		return propertyHasActions(getMetaProperty(name));
	}
	
	/**
	 * If the property has actions associated to it. <p>
	 * 
	 * If the actions are showed or not in this moment has no effect in the result.<br>  
	 */
	public boolean propertyHasActions(MetaProperty p) {
		boolean isLastSearchKey = isLastSearchKey(p, true, true);
		if (isLastSearchKey) {
			if (isSearch() || isCreateNew() || isModify()) {
				return true;
			}
		}
		if (!getActionsNamesForReference(isLastSearchKey).isEmpty()) return true;
		if (!getActionsNamesForProperty(p, true).isEmpty()) return true;
		return false;
	}
	
	private boolean equals(Object a, Object b) { 
		if ("".equals(a)) a = null;
		if ("".equals(b)) b = null;
		if (a instanceof Map && ((Map) a).isEmpty()) a = null;
		if (b instanceof Map && ((Map) b).isEmpty()) b = null;
		return Is.equal(a, b);
	}
	
	public String getOnSelectCollectionElementAction() { 
		return getCollectionAction(onSelectCollectionElementAction, "");
	}

	public void setOnSelectCollectionElementAction(String onSelectCollectionElementAction) {
		this.onSelectCollectionElementAction = onSelectCollectionElementAction;
	}

	public boolean isVariousCollectionsInSameLine(MetaMember metaMember){
		Collection metaMembersLine = getMetaMembersInLine(metaMember.getName());
		if (metaMembersLine.size() <= 1) return false;
		
		Iterator it = metaMembersLine.iterator();
		boolean allCollection = true;
		while(it.hasNext() && allCollection){
			MetaMember mm = (MetaMember) it.next();
			if (!(mm instanceof MetaCollection)) allCollection = false;
		}
		
		return allCollection;
	}
	
	public boolean isVariousMembersInSameLine(MetaMember metaMember) { 
		return getMetaMembersInLine(metaMember.getName()).size() > 1;
	}
	
	/**
	 * MetaMembers in same line
	 */
	private Collection getMetaMembersInLine(String property){
		Collection members = getMetaMembers();
		Iterator it = members.iterator();
		boolean found = false;
		Collection line = new ArrayList();
		while (it.hasNext() && !found){
			MetaMember metaMember = (MetaMember) it.next();
			if (property.equals(metaMember.getName())) found = true;
			if (found){
				line.add(metaMember);
				boolean finLinea = false;
				while (it.hasNext() && !finLinea){
					MetaMember metaMember2 = (MetaMember) it.next();
					if (PropertiesSeparator.INSTANCE.equals(metaMember2)) finLinea = true;
					else line.add(metaMember2);
				}
			}
			else {
				if (PropertiesSeparator.INSTANCE.equals(metaMember)) line = new ArrayList();
				else line.add(metaMember);
			}
		}
		return line;
	}

	public boolean isFirstInLine(MetaMember metaMember){
		String property = metaMember.getName(); 
		return property.equals(((MetaMember) getMetaMembersInLine(property).iterator().next()).getName());
	}
	
	public boolean isDefaultListActionsForCollectionsIncluded() {
		return defaultListActionsForCollectionsIncluded;
	}

	public void setDefaultListActionsForCollectionsIncluded(boolean defaultListActionsForCollectionsIncluded) {		
		this.defaultListActionsForCollectionsIncluded = defaultListActionsForCollectionsIncluded;
	}
	
	public boolean isDefaultRowActionsForCollectionsIncluded() {
		return defaultRowActionsForCollectionsIncluded;
	}

	public void setDefaultRowActionsForCollectionsIncluded(boolean defaultRowActionsForCollectionsIncluded) {		
		this.defaultRowActionsForCollectionsIncluded = defaultRowActionsForCollectionsIncluded;
	}
	
	/**
	 * @since 4m1
	 */
	public void setTitleId(String id, Object ... arguments) { 
		this.title = null;
		this.titleId = id;
		this.titleArguments = arguments;
	}

	/**
	 * @since 4m1
	 */	
	public String getTitle() {
		if (title == null) {
			if (titleId == null) title="";
			else {
				if (titleArguments != null && titleArguments.length > 0) {
					title = XavaResources.getString(titleId, titleArguments);
				}
				else if (Labels.exists(titleId)) {
					title = Labels.get(titleId);
				}
				else {
					title = XavaResources.getString(titleId);
				}
				
			}
		}
		return title;
	}
	
	/**
	 * @since 4m1
	 */	
	public void setTitle(String title) { 
		this.title = title;
	}		 
	
	private Collection getFullOrderActionsNamesList() {
		if (fullOrderActionsNamesList==null) {
			fullOrderActionsNamesList = new ArrayList();
		}
		return fullOrderActionsNamesList; 
	}
	
	private void setFullOrderActionsNamesList(Collection collection) { 
		fullOrderActionsNamesList = collection; 
	}
	
	private Collection getFullOrderActionsNamesRow() {
		if (fullOrderActionsNamesRow==null) {
			fullOrderActionsNamesRow = new ArrayList();
		}
		return fullOrderActionsNamesRow; 
	}
	
	private void setFullOrderActionsNamesRow(Collection collection) { 
		fullOrderActionsNamesRow = collection; 
	}	
	
	public String getLabelStyleForProperty(MetaProperty p) throws XavaException {		
		return getMetaView().getLabelStyleForProperty(p);
	}
	
	public String getLabelStyleForReference(MetaReference ref) throws XavaException {		
		return getMetaView().getLabelStyleForReference(ref);
	}
	
	public void setCollapsed(String name, boolean collapsed) { 
		String frameId= getFrameId(name);
		setFrameClosed(frameId, collapsed);
	}
	
	public boolean isCollapsed(String name) { 
		String frameId= getFrameId(name);
		return isFrameClosed(frameId);
	}
	
	private String getFrameId(String name) {   		
		return Ids.decorate(getRequest(), "frame_" + getPropertyPrefix() + name);
	}
	
	private boolean isFrameStatusStored(String frameId) { 
		try {
			String[] prefsKeys= getRoot().getPreferences().keys();			
			return Arrays.asList(prefsKeys).contains(getFrameKey(frameId));
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_load_frame_status"),ex);
			return false;
		}
	}
	
	/**
	 * @since 5.1.1
	 */
	private String getFrameKey(String frameId) {
		String key = FRAME_CLOSED + frameId;
		if(key.length() > Preferences.MAX_NAME_LENGTH) {
			key = FRAME_CLOSED + frameId.hashCode();
		}
		return key;
	}
	
	public void setFrameClosed(String frameId, boolean frameClosed) {
		try {
			Preferences pref = getRoot().getPreferences();
			pref.putBoolean(getFrameKey(frameId), frameClosed);
			pref.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_store_frame_status"),ex);
		}
	}
	
	public boolean isFrameClosed(String frameId) { 
		try {			
			return getRoot().getPreferences().getBoolean(getFrameKey(frameId), false);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_load_frame_status"),ex);
			return false;
		}
	}		

	/**
	 * Add an action to the property. <p>
	 * 
	 * The action will be show even if the view or the property is not editable.<br> 
	 * 
	 * @since 4.5
	 */
	public void addActionForProperty(String property, String qualifiedActionName) throws XavaException {
		initializeActionsForProperty(property);
		getChangedActionsByProperty().get(property).add(qualifiedActionName);
		addPropertyWithChangedActions(property); 
	}
	
	/**
	 * Remove an action to the property. <p>
	 * 
	 * Only the actions added with addActionForProperty() can be removed.<br> 
	 * 
	 * @since 4.5
	 */
	public void removeActionForProperty(String property, String qualifiedActionName) throws XavaException {	
		if(getParentIfSectionOrGroup().changedActionsByProperty != null){
			if(getChangedActionsByProperty().containsKey(property)){
				getChangedActionsByProperty().get(property).remove(qualifiedActionName);
				addPropertyWithChangedActions(property); 
			}
		}
	}
	
	private void addPropertyWithChangedActions(String property) { 		
		if (getParentIfSectionOrGroup().propertiesWithChangedActions == null) getParentIfSectionOrGroup().propertiesWithChangedActions = new HashSet();
		getParentIfSectionOrGroup().propertiesWithChangedActions.add(property);
	}
	
	public boolean propertyHasChangedActions(String property) { 
		if (getParentIfSectionOrGroup().propertiesWithChangedActions == null) return false;
		return getParentIfSectionOrGroup().propertiesWithChangedActions.contains(property);
	}
	
	private void initializeActionsForProperty(String property) throws XavaException {
		if(getChangedActionsByProperty().get(property) == null){
			getChangedActionsByProperty().put(property, new ArrayList<String>());			
		}
	}
	
	private Map<String, Collection<String>> getChangedActionsByProperty() { 
		if (getParentIfSectionOrGroup().changedActionsByProperty == null) {
			getParentIfSectionOrGroup().changedActionsByProperty = new HashMap<String, Collection<String>>();
		}
		return getParentIfSectionOrGroup().changedActionsByProperty;
	}
	
	/**
	 * Deselects all items in collection
	 */
	public void collectionDeselectAll() {
		if (isCollectionFromModel()) {
			listSelected = null;
		} else {
			getCollectionTab().deselectAll();
		}
	}
	
	/**
	 * @param as 'ox_OpenXavaTest_CarrierWithCollectionsTogether__xava_collectionTab_fellowCarriers:1,3,2'
	 */
	public void deselectCollection(String deselect){
		// deselect = ox_OpenXavaTest_CarrierWithCollectionsTogether__xava_collectionTab_fellowCarriers:1,3,2
		StringTokenizer st = new StringTokenizer(deselect, ":");
		String name = st.nextToken();
		String a = Ids.undecorate(name);
		String collectionName = a.replace("xava_collectionTab_", "");
		View collectionView = getSubview(collectionName);
		org.openxava.tab.Tab collectionTab = collectionView.getCollectionTab();
		collectionTab.friendExecuteJspDeselect(deselect);
	}

	public void moveCollectionElement(int from, int to) throws Exception {
		MapFacade.moveCollectionElement(getParent().getModelName(), getParent().getKeyValues(), 
			getMemberName(), from, to);
	}
	
	/**
	 * @since 5.6
	 */
	public void reloadMetaModel() { 
		getFirstLevelView().reloadNeeded = true; 
		resetMembers();
	}

	public void setAddCollectionElementAction(String addCollectionElementAction) {
		this.addCollectionElementAction = addCollectionElementAction;
	}
	
	public boolean isPropertyUsedInCalculation(String qualifiedName) {  
		return !Is.emptyString(getDependentCalculationPropertyNameFor(qualifiedName));
	}
	
	public String getDependentCalculationPropertyNameFor(String qualifiedName) { 
		for (MetaProperty property: getMetaPropertiesQualified()) {
			if (property.usesForCalculation(qualifiedName)) {
				return property.getName(); 
			}
		}		
		return null;
	}
	
	private String getCollectionAction(String action, String defaultAction) {
		if (action == null && defaultAction == null) return null;
		String key = action + "" + defaultAction;		
		if (refinedCollectionActions == null) refinedCollectionActions = new HashMap<>();
		if (refinedCollectionActions.containsKey(key)) return refinedCollectionActions.get(key);
		List<String> result = new ArrayList<String>(1);
		if (action != null) result.add(action);
		else result.add(defaultAction);
		refine(result);
		String refinedAction = result.isEmpty() ? "" : result.get(0);
		refinedCollectionActions.put(key, refinedAction);
		return refinedAction;
	}
	
	private void refine(Collection<String> collection) {
		if (refiner == null) return;
		
		final String prefix = getMemberName() + ":";
		CollectionUtils.transform(collection, new Transformer() {			
			@Override
			public Object transform(Object obj) {
				return  prefix + obj.toString();
			}
		});
		
		try { 
			XObjects.execute(refiner, "refineForCollections", 
				         MetaModule.class, getModuleManager(getRequest()).getMetaModule(),
					     Collection.class, collection);
		} catch(Exception ex) {
			log.error(XavaResources.getString("controller_actions_error"), ex);			
		}
		
		CollectionUtils.transform(collection, new Transformer() {			
			@Override
			public Object transform(Object obj) {				
				return Strings.change(obj.toString(), prefix, "");
			}
		});		
	}	
	
	/**
	 * Add a new valid value to a property, generating a combo in the UI for this property.
	 * 
	 * For example, for a simple property like this:
     * <pre>
	 * private String color;
	 * </pre>
	 * You can add a combo programmatically in this way:
     * <pre>
     * getView().addValidValue("color", "wht", "White");
     * getView().addValidValue("color", "blk", "Black");
     * </pre>
	 * This creates a combo for color property with two values wht with label White and blk with label Black.
	 *  
	 * @param property The name of the property. It has to be in the model, but not in the view in this moment.
	 * @param value  The value that will be store in the property, the type has to match with the property type.
	 * @param label  The label shown in the combo.
	 * @since 5.8
	 */
	public void addValidValue(String property, Object value, String label) {
		if (isSection() || isGroup()) {
			getParent().addValidValue(property, value, label);
			return;
		}
		if (!getMetaModel().containsMetaProperty(property)) {
			throw new XavaException("property_not_found", property, getModelName()); 
		}	
		MetaProperty metaProperty = getMetaProperty(property);
		if (validValuesByProperty == null) validValuesByProperty = new HashMap<MetaProperty, Map>();
		Map validValues = validValuesByProperty.get(metaProperty);
		if (validValues == null) {
			validValues = new LinkedHashMap();
			validValuesByProperty.put(metaProperty, validValues);
		}
		validValues.put(value, label);
	}

	/**
	 * Removed a valid value previously added with addValidValue().
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @param value  The valid value stored in the property that we want to remove, the type has to match with the property type.
	 * @since 5.8
	 */	
	public void removeValidValue(String property, Object value) {
		if (isSection() || isGroup()) {
			getParent().removeValidValue(property, value);
			return;
		}
		Map validValues = getValidValues(property);
		if (validValues == null) return;
		validValues.remove(value);		
	}
	
	/**
	 * Remove all the valid values previously added with addValidValue(). <br>
	 * 
	 * The property is still displayed as a combo with a sigle blank option.
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @since 6.3
	 */
	public void clearValidValues(String property) { 
		if (isSection() || isGroup()) {
			getParent().clearValidValues(property);
			return;
		}
		Map validValues = getValidValues(property);
		if (validValues == null) return;
		validValues.clear();
		if (noBlankValidValuesProperties != null) noBlankValidValuesProperties.remove(property);
	}
	
	/**
	 * Remove all the valid values previously added with addValidValue() and disable the combo. <br>
	 * 
	 * After calling the method the property is displayed as regular text field, no longer as a combo.
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @since 6.3
	 */	
	public void disableValidValues(String property) { 
		if (isSection() || isGroup()) {
			getParent().disableValidValues(property);
			return;
		}
		if (validValuesByProperty == null) return;
		MetaProperty metaProperty = getMetaProperty(property);
		validValuesByProperty.remove(metaProperty);
		if (noBlankValidValuesProperties != null) noBlankValidValuesProperties.remove(property);
	}

	/**
	 * Removed the blank option of a valid value created with addValidValue().
	 * 
	 * By default using addValidValues() creates a combo that includes a blank option. This method removes that blank option.
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @since 6.3
	 */		
	public void removeBlankValidValue(String property) { 
		if (isSection() || isGroup()) {
			getParent().removeBlankValidValue(property);
			return;
		}
		if (noBlankValidValuesProperties == null) noBlankValidValuesProperties = new HashSet<>();
		noBlankValidValuesProperties.add(property);
	}
	
	/**
	 * If the property with valid values added via addValidValue() has a blank option. <p>
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @since 6.3
	 */
	public boolean hasBlankValidValue(String property) { 
		if (isSection() || isGroup()) {
			return getParent().hasBlankValidValue(property);
		}
		if (noBlankValidValuesProperties == null) return true;
		return !noBlankValidValuesProperties.contains(property);
	}
 


	/**
	 * If the property has valid values associated. <p>
	 * 
	 * Valid values are added via addValidValue().
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @since 5.8
	 */
	public boolean hasValidValues(String property) {
		if (isSection() || isGroup()) return getParent().hasValidValues(property);
		int idx = property.indexOf('.');
		View view = this;
		if (idx >= 0) {
			String subviewName = property.substring(0, idx);
			String member = property.substring(idx+1);
			view = getSubview(subviewName);
			if (view.isRepresentsElementCollection()) { 	
				int elementIndex = Integer.parseInt(Strings.firstToken(member, "."));
				if (elementIndex < 0) return false;
				property = Strings.noFirstTokenWithoutFirstDelim(member, ".");
			}
		}
		return view.getValidValues(property) != null;
	}
	
	/**
	 * List of valid values associated to the property. <p>
	 * 
	 * Valid values are added via addValidValue().
	 * 
	 * @param property  The name of the property. It has to be in the model, but not in the view in this moment.
	 * @return A map with value/label pairs.
	 * @since 5.8
	 */
	public Map getValidValues(String property) {
		if (isSection() || isGroup()) return getParent().getValidValues(property);
		if (validValuesByProperty == null) return null;
		MetaProperty metaProperty = getMetaProperty(property);
		Map validValues = validValuesByProperty.get(metaProperty);
		return validValues;		
	}
	
	/**
	 * @since 5.8
	 */
	public void setDescriptionsListCondition(String referenceName, String condition) throws XavaException {
		MetaReference metaReference = getMetaReference(referenceName);
		MetaDescriptionsList descriptionsList = getMetaView().getMetaDescriptionList(metaReference);
		if (descriptionsList == null) return;
		descriptionsList.setCondition(condition);
		refreshDescriptionsLists(); 
	}

	/**
	 * @since 5.8
	 */
	public String getDescriptionsListCondition(String referenceName) throws XavaException {
		MetaReference metaReference = getMetaReference(referenceName);
		MetaDescriptionsList descriptionsList = getMetaView().getMetaDescriptionList(metaReference);
		if (descriptionsList == null) return "";
		return descriptionsList.getCondition();
	}

}