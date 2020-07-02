package org.openxava.component.parse;

import java.beans.*;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.lang.reflect.ParameterizedType;
import java.math.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.metamodel.*;

import org.apache.commons.logging.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.usertype.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.converters.typeadapters.*;
import org.openxava.filters.*;
import org.openxava.filters.meta.*;
import org.openxava.jpa.*;
import org.openxava.mapping.*;
import org.openxava.model.impl.*;
import org.openxava.model.meta.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.validators.meta.*;
import org.openxava.view.meta.*;


/**
 * Parse EJB3 Entities (POJOs with JPA annotations) into OpenXava components. <p>
 *
 * @author Javier Paniza
 */

public class AnnotatedClassParser implements IComponentParser { 
	
	private static Log log = LogFactory.getLog(AnnotatedClassParser.class);
	
	private static Collection<String> managedClassNames; 
	private static Collection<String> managedClassPackages;
	private static Map<Class, Collection<Class>> entityFirstLevelSubclasses;
	private static Map<String, MetaComponent> parsingComponents;
	
	public MetaComponent parse(String name) throws Exception {
		if (isParsingComponent(name)) return getParsingComponent(name);
		String className = getClassNameIfExists(name);
		if (className == null) return null;
		if (name.contains("_")) { 
			log.warn(XavaResources.getString("underscore_not_allowed_for_class_name", name));
		}
		MetaComponent component = new MetaComponent();
		component.setName(name);
		putParsingComponent(component);
		try {			
			MetaEntity entity = new MetaEntity();
			entity.setPOJOClassName(className);		
			entity.setName(name);
			entity.setAnnotatedEJB3(true);
			component.setMetaEntity(entity);
			component.setPackageName(Strings.noLastTokenWithoutLastDelim(className, "."));		
			
			Class pojoClass = entity.getPOJOClass();
			if (!pojoClass.isAnnotationPresent(Entity.class)) {
				component.setTransient(true);
			}

			IdClass idClass = (IdClass) getAnnotationInHierarchy(pojoClass, IdClass.class);
			if (idClass != null) {				
				entity.setPOJOKeyClass(idClass.value());
			}			
			
			EntityMapping mapping = new EntityMapping();
			mapping.setTable(getTable(name, pojoClass));
			component.setEntityMapping(mapping);
			
			// View
			parseViews(component, pojoClass);
	
			// Members				
			parseMembers(entity, pojoClass, mapping, null);
													
			// Tab
			parseTabs(component, pojoClass);
			
			// Other model level annotations
			processAnnotations(entity, pojoClass);
			
			return component;
		}
		finally {
			removeParsingComponent(component);
		}
	}

	public IPersistenceProvider getPersistenceProvider() { 
		return JPAPersistenceProvider.getInstance();
	}

	private Annotation getAnnotationInHierarchy(Class pojoClass, Class annotation) {
		if (Object.class.equals(pojoClass)) return null;
		if (pojoClass.isAnnotationPresent(annotation)) {
			return pojoClass.getAnnotation(annotation);
		}
		return getAnnotationInHierarchy(pojoClass.getSuperclass(), annotation);
	}


	private void removeParsingComponent(MetaComponent component) { 
		if (parsingComponents == null) return; // Difficult
		parsingComponents.remove(component.getName());
		if (parsingComponents.isEmpty()) parsingComponents = null; 		
	}


	private void putParsingComponent(MetaComponent component) { 
		if (parsingComponents == null) parsingComponents = new HashMap<String, MetaComponent>();
		parsingComponents.put(component.getName(), component);
	}


	// precondition: isParsingComponent(name)
	private MetaComponent getParsingComponent(String name) {
		return parsingComponents.get(name);
	}


	private boolean isParsingComponent(String name) { 		
		return parsingComponents != null && parsingComponents.containsKey(name);
	}


	private String getTable(String modelName, Class pojoClass) {
		Class superClass = pojoClass.getSuperclass(); 
		if (superClass.isAnnotationPresent(Entity.class)) {
			return getTable(superClass.getSimpleName(), superClass);
		}
		Table table = (Table) pojoClass.getAnnotation(Table.class);
		if (table != null) {
			String tableName = Is.emptyString(table.name())?modelName:table.name();
			return Is.emptyString(table.schema())?tableName:table.schema() + "." + tableName;			
		}
		else {
			return modelName;
		}
	}

	
	private void parseMembers(MetaModel model, Class pojoClass, ModelMapping mapping, String embedded) throws Exception {
		Class superClass = pojoClass.getSuperclass();
		if (model.getMetaComponent().isTransient() && !superClass.equals(java.lang.Object.class) ||
			superClass.isAnnotationPresent(Entity.class) || 
			superClass.isAnnotationPresent(MappedSuperclass.class))
		{
			parseMembers(model, superClass, mapping, embedded);
		}
		// Using declared fields in order to preserve the order in source code
		Map<String, PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(pojoClass);
		for (Field f: pojoClass.getDeclaredFields()) {
			PropertyDescriptor pd = propertyDescriptors.get(f.getName());
			if (pd == null) continue;			
			if (pd.getReadMethod() == null) {
				log.warn(XavaResources.getString("write_only_property_not_added", pd.getName())); 
				continue;
			}			
			addMember(model, mapping, pd, f, embedded);
			propertyDescriptors.remove(f.getName());
		}
		
		// We order the methods to be consistent with both Sun and JRockit, only JRockit returns the method as declared 			
		for (Method m: getOrderedDeclaredGetterMethods(pojoClass)) { 
			if (!Modifier.isPublic(m.getModifiers())) continue; 
			String propertyName = null;
			if (m.getName().startsWith("get")) {
				propertyName = Strings.firstLower(m.getName().substring(3));
			}
			else if (m.getName().startsWith("is")) {
				propertyName = Strings.firstLower(m.getName().substring(2));
			}
			else continue;
			PropertyDescriptor pd = propertyDescriptors.get(propertyName);
			if (pd == null) continue;
			addMember(model, mapping, pd, null, embedded);
		}
		
		parseAttributeOverrides(pojoClass, mapping);
	}
	
	private Collection<Method> getOrderedDeclaredGetterMethods(Class theClass) {
		return Arrays.stream(theClass.getDeclaredMethods())
			.map(Method::getName)
			.filter(m -> m.startsWith("is") || m.startsWith("get"))
			.sorted()
			.distinct()
			.map(n -> getDeclaredMethod(theClass, n))
			.filter(m -> m != null)
			.collect(Collectors.toList());
	}
	
	private Method getDeclaredMethod(Class theClass, String methodName) { 
		try {
			return theClass.getDeclaredMethod(methodName);
		} 
		catch (NoSuchMethodException e) {
			return null;
		} 
	}
	
	private void parseAttributeOverrides(AnnotatedElement element, ModelMapping mapping) throws XavaException {
		parseAttributeOverrides(element, mapping, null);
	}
	
	private void parseAttributeOverrides(AnnotatedElement element, ModelMapping mapping, String embedded) throws XavaException {
		String prefix = embedded == null?"":embedded + "_"; 
		if (element.isAnnotationPresent(AttributeOverride.class)) {
			AttributeOverride override = (AttributeOverride) element.getAnnotation(AttributeOverride.class);
			parseAttributeOverride(override, mapping, prefix); 
		}
		if (element.isAnnotationPresent(AttributeOverrides.class)) {
			AttributeOverrides overrides = (AttributeOverrides) element.getAnnotation(AttributeOverrides.class);
			for (AttributeOverride override: overrides.value()) {
				parseAttributeOverride(override, mapping, prefix); 
			}
		}
	}	

	private void parseAttributeOverride(AttributeOverride override, ModelMapping mapping, String prefix) throws XavaException {
		try {
			PropertyMapping pMapping = mapping.getPropertyMapping(prefix + override.name());
			pMapping.setColumn(override.column().name());			
		}
		catch (ElementNotFoundException ex) {					
			throw new XavaException("attribute_override_not_found", override.name(), mapping.getModelName()); 
			
		}
	}

	private void addMember(MetaModel model, ModelMapping mapping, PropertyDescriptor pd, Field f, String embedded) throws Exception {
		if (pd.getName().equals("class") || pd.getPropertyType() == null) return;
		if (isReference(model, pd, f)) addReference(model, mapping, pd, f, embedded, false); 
		else if (Collection.class.isAssignableFrom(pd.getPropertyType())) addCollection(model, mapping, pd, f);
		else if (pd.getPropertyType().isAnnotationPresent(Embeddable.class)) addEmbeddable(model, mapping, pd, f, embedded);
		else addProperty(model, mapping, pd, f, embedded);
	}
	
	
	private boolean isReference(MetaModel model, PropertyDescriptor pd, Field f) { 
		if ((f != null && f.isAnnotationPresent(ManyToOne.class)) || 
			pd.getReadMethod().isAnnotationPresent(ManyToOne.class) ||			
			(f != null && f.isAnnotationPresent(OneToOne.class)) || 
			pd.getReadMethod().isAnnotationPresent(OneToOne.class)) return true;
		if (model.containsMetaReference(pd.getName())) return true;
		return false;
	}
	
	private void addCollection(MetaModel model, ModelMapping mapping, PropertyDescriptor pd, Field field) throws Exception {
		if (model.containsMetaCollection(pd.getName())) {
			MetaCollection collectionFromParent = model.getMetaCollection(pd.getName());
			processAnnotations(collectionFromParent, pd.getReadMethod());
			processAnnotations(collectionFromParent, field); 
			return;
		}
		if (!(pd.getReadMethod().getGenericReturnType() instanceof ParameterizedType)) {
			log.warn(XavaResources.getString("collection_must_be_parametrized", pd.getName(), model.getName()));
			return;			
		}
		
		MetaCollection collection = new MetaCollection();
		collection.setName(pd.getName());
		MetaReference ref = new MetaReference();
		java.lang.reflect.Type[] types  = ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments();
		Class referencedModelClass = getGenericClass(model.getPOJOClass(), pd);
		if (referencedModelClass == null) {
			log.warn(XavaResources.getString("collection_not_added_not_generic_type", pd.getName(), model.getName()));
			return; 
		}
		ref.setReferencedModelName(referencedModelClass.getSimpleName());
		
		collection.setMetaReference(ref);
		model.addMetaCollection(collection);
		
		collection.setMetaCalculator(new MetaCalculator()); // This may be annuled by processAnnotations (below)
		
		processAnnotations(collection, pd.getReadMethod());
		processAnnotations(collection, field);								
	}
	
	private Class getGenericClass(Class finalClass, PropertyDescriptor pd) {
		java.lang.reflect.Type type  = ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0];
		if (type instanceof Class) return (Class) type;		
		if (!(finalClass.getGenericSuperclass() instanceof ParameterizedType)) return null;
		ParameterizedType superClassType = (ParameterizedType) finalClass.getGenericSuperclass();
		int i=0;
		for (java.lang.reflect.Type t: finalClass.getSuperclass().getTypeParameters()) {			
			if (t.equals(type)) {
				return (Class) superClassType.getActualTypeArguments()[i];
			}
			i++;
		}
		return null; 
	}
	
	private void addEmbeddable(MetaModel model, ModelMapping mapping, PropertyDescriptor pd, Field field, String parentEmbedded) throws Exception {
		String modelName = pd.getPropertyType().getSimpleName();
		if (!model.getMetaComponent().hasMetaAggregate(modelName)) {
			MetaAggregateForReference metaAggregate = new MetaAggregateForReference();			
			metaAggregate.setName(modelName);
			metaAggregate.setBeanClass(pd.getPropertyType().getName());
			metaAggregate.setPOJOClassName(pd.getPropertyType().getName());
			metaAggregate.setAnnotatedEJB3(true); 
			model.getMetaComponent().addMetaAggregate(metaAggregate);
			parseViews(model.getMetaComponent(), pd.getPropertyType(), modelName);
			String embedded = Is.emptyString(parentEmbedded) ? pd.getName() : parentEmbedded + "_" + pd.getName();  
			parseMembers(metaAggregate, pd.getPropertyType(), mapping, embedded);			
			parseAttributeOverrides(pd.getReadMethod(), mapping, embedded);			 
			parseAttributeOverrides(field, mapping, embedded);						
		}				
		addReference(model, mapping, pd, field, pd.getName(), true);
	}
	
	private void addAggregateForCollection(MetaModel model, String typeName, String containerReference) throws Exception {
		Class type = Class.forName(typeName);
		String modelName = type.getSimpleName();
		if (!model.getMetaComponent().hasMetaAggregate(modelName)) {
			MetaAggregateForCollection metaAggregate = new MetaAggregateForCollection();
			metaAggregate.setContainerModelName(model.getName());
			metaAggregate.setContainerReference(containerReference); 
			metaAggregate.setName(modelName);
			metaAggregate.setPOJOClassName(type.getName());
			metaAggregate.setAnnotatedEJB3(true); 
			Class pojoClass = metaAggregate.getPOJOClass();
			IdClass idClass = (IdClass) getAnnotationInHierarchy(pojoClass, IdClass.class);
			if (idClass != null) {				
				metaAggregate.setPOJOKeyClass(idClass.value());
			}			
			model.getMetaComponent().addMetaAggregate(metaAggregate);
			parseViews(model.getMetaComponent(), type, modelName);
			AggregateMapping mapping = new AggregateMapping();
			mapping.setModelName(modelName);
			mapping.setTable(getTable(modelName, pojoClass));
			model.getMetaComponent().addAggregateMapping(mapping);
			parseMembers(metaAggregate, type, mapping, null);
			processAnnotations(metaAggregate, type);			
		}						
	}
			
	private void addReference(MetaModel model, ModelMapping mapping, PropertyDescriptor pd, Field field, String embedded, boolean aggregate) throws XavaException {
		if (model.containsMetaReference(pd.getName())) {
			MetaReference ref = model.getMetaReference(pd.getName());
			ref.setReferencedModelName(pd.getPropertyType().getSimpleName());
			processAnnotations(ref, pd.getReadMethod());
			return;
		}
		MetaReference ref = new MetaReference();
		ref.setName(pd.getName());
		ref.setReferencedModelName(pd.getPropertyType().getSimpleName());
		ref.setAggregate(aggregate);
		model.addMetaReference(ref);
		processAnnotations(ref, pd.getReadMethod());
		processAnnotations(ref, field);
		if (!ref.isRequired() && ref.isKey() &&
			!(model instanceof MetaAggregateForCollection &&  
			Strings.firstLower(model.getContainerModelName()).equals(ref.getName())))  
		{
			ref.setRequired(true);			
		}		
		
		// The mapping part
		
		MetaModel metaModelReferenced = null;						
		// Self reference		
		if (ref.getReferencedModelName().equals(model.getName())) 
			metaModelReferenced = model;		 
		// Cyclical references
		else if (ref.getReferencedModelName().equals(ref.getMetaModel().getContainerModelName())) 
			metaModelReferenced = ref.getMetaModel().getMetaModelContainer();
		else if (isParsingComponent(ref.getReferencedModelName())) { 
			metaModelReferenced = getParsingComponent(ref.getReferencedModelName()).getMetaEntity();
		}
		// Other cases
		else metaModelReferenced = ref.getMetaModelReferenced();
		if (mapping != null && !(metaModelReferenced instanceof MetaAggregateForReference)) { 
			ReferenceMapping refMapping = new ReferenceMapping();
			refMapping.setReference(embedded==null?pd.getName():embedded + "_" + pd.getName());			
			
			for (Object oreferencedModelPropertyName: metaModelReferenced.getAllKeyPropertiesNames()) {
				String column = null;
				String referencedModelPropertyName = (String) oreferencedModelPropertyName;								
				if (field != null && field.isAnnotationPresent(JoinColumn.class)) {
					JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);			
					column = joinColumn.name();
				}
				else if (field != null && field.isAnnotationPresent(JoinColumns.class)) {
					column = getColumnFor(field.getAnnotation(JoinColumns.class), metaModelReferenced, referencedModelPropertyName); 
				}
				else if (pd.getReadMethod().isAnnotationPresent(JoinColumn.class)) {
					JoinColumn joinColumn = pd.getReadMethod().getAnnotation(JoinColumn.class);
					column = joinColumn.name();
				}
				else if (pd.getReadMethod().isAnnotationPresent(JoinColumns.class)) {
					column = getColumnFor(pd.getReadMethod().getAnnotation(JoinColumns.class), metaModelReferenced, referencedModelPropertyName); 
				}				 
				else if (field != null && field.isAnnotationPresent(PrimaryKeyJoinColumn.class)) { 
					column = metaModelReferenced.getMapping().getColumn(referencedModelPropertyName); 
				}
				else if (field != null && field.isAnnotationPresent(PrimaryKeyJoinColumns.class)) {
					column = getColumnFor(field.getAnnotation(PrimaryKeyJoinColumns.class), metaModelReferenced, referencedModelPropertyName); 
				}
				else if (pd.getReadMethod().isAnnotationPresent(PrimaryKeyJoinColumn.class)) { 
					column = metaModelReferenced.getMapping().getColumn(referencedModelPropertyName); 
				}
				else if (pd.getReadMethod().isAnnotationPresent(PrimaryKeyJoinColumns.class)) {
					column = getColumnFor(pd.getReadMethod().getAnnotation(PrimaryKeyJoinColumns.class), metaModelReferenced, referencedModelPropertyName); 
				} 
				
				if (Is.emptyString(column)) {			
					column = pd.getName() + "_" + metaModelReferenced.getMapping().getColumn(referencedModelPropertyName); 
				}		
				
				ReferenceMappingDetail detail = new ReferenceMappingDetail();
				detail.setReferencedModelProperty(referencedModelPropertyName);
				detail.setColumn(column);				
				refMapping.addDetail(detail);				
			}					
			mapping.addReferenceMapping(refMapping);					
		}
	}
	
	private String getColumnFor(JoinColumns joinColumns, MetaModel referencedModel, String referencedModelProperty) throws XavaException {
		String referencedColumn = referencedModel.getMapping().getColumn(referencedModelProperty);
		for (JoinColumn joinColumn: joinColumns.value()) {
			if (referencedColumn.trim().equalsIgnoreCase(joinColumn.referencedColumnName().trim())) {
				return joinColumn.name();
			}
		}
		return null;
	}
	
	private String getColumnFor(PrimaryKeyJoinColumns joinColumns, MetaModel referencedModel, String referencedModelProperty) throws XavaException { 
		String referencedColumn = referencedModel.getMapping().getColumn(referencedModelProperty);
		for (PrimaryKeyJoinColumn joinColumn: joinColumns.value()) {
			if (referencedColumn.trim().equalsIgnoreCase(joinColumn.referencedColumnName().trim())) {
				return joinColumn.name();
			}
		}
		return null;
	}



	private void parseViews(MetaComponent component, Class pojoClass) throws XavaException {
		parseViews(component, pojoClass, null);
	}
	
	private void parseViews(MetaComponent component, Class pojoClass, String modelName) throws XavaException {
		if (pojoClass.isAnnotationPresent(View.class)) {
			View view = (View) pojoClass.getAnnotation(View.class);
			addView(component, view, modelName);
		}
		if (pojoClass.isAnnotationPresent(Views.class)) {
			Views views = (Views) pojoClass.getAnnotation(Views.class);
			for (View view: views.value()) {
				addView(component, view, modelName);
			}
		}
		
		// For force create and add a view by default if it does not exist
		if (modelName == null) component.getMetaEntity().getMetaViewByDefault();
		else component.getMetaAggregate(modelName).getMetaViewByDefault();
	}
	
	private void addView(MetaComponent component, View view, String modelName) throws XavaException {
		MetaView metaView = new MetaView();
		metaView.setName(view.name());
		metaView.setExtendsView(view.extendsView()); 
		if (modelName != null) {
			metaView.setModelName(modelName);
		}		
		if (Is.empty(view.members())) metaView.setMembersNames("*");
		else addMembersToView(null, null, metaView, new StringTokenizer(view.members(), ";,{}[]#", true));
		component.addMetaView(metaView);
	}
	
	private String addMembersToView(String sectionName, String groupName, MetaView metaView, StringTokenizer st) throws XavaException {
		StringBuffer members = new StringBuffer();
		boolean stHasMoreTokens = st.hasMoreTokens();
		String token = stHasMoreTokens?st.nextToken().trim():null;
		boolean alignedByColumns = XavaPreferences.getInstance().isAlignedByColumns();
		while (stHasMoreTokens) {	
			stHasMoreTokens = st.hasMoreTokens();			
			String nextToken = stHasMoreTokens?st.nextToken().trim():"";
			if (token.equals("}")) {				
				metaView.setMembersNamesNotResetSections(members.toString());
				metaView.setAlignedByColumns(alignedByColumns);
				return nextToken;
			}
			else if (nextToken.equals("{")) {				
				String nestedSection = token;				 
				MetaView section = metaView.addSection(nestedSection, null, "", false); 
				nextToken = addMembersToView(nestedSection, null, section, st);
				if (",;".indexOf(nextToken) < 0) members.append(';');
			}		
			else if (token.equals("]")) {										
				metaView.addMetaGroup(groupName, null, members.toString(), alignedByColumns);	
				return nextToken;
			}
			else if (nextToken.equals("[")) {				
				String nestedGroup = token;
				members.append("__GROUP__" + nestedGroup); 
				nextToken = addMembersToView(null, nestedGroup, metaView, st);
				if (",;".indexOf(nextToken) < 0) members.append(',');
			}
			else {
				if (token.endsWith("()")) { // An action
					members.append("__ACTION__");
					String action = token.substring(0, token.length() - "()".length());
					members.append(action);
				}
				else if (token.endsWith("(ALWAYS)")) { // An always present action
					members.append("__ACTION__AE__");
					String action = token.substring(0, token.length() - "(ALWAYS)".length());
					members.append(action);
				}
				else if (token.equals("#")) {
					alignedByColumns = true;
				}
				else {	
					members.append(token); // A conventional member
				}
			}	
			token = nextToken;
		}
		if (groupName != null) throw new XavaException("group_unclosed", groupName); 		
		if (sectionName != null) throw new XavaException("section_unclosed", sectionName);		
		metaView.setMembersNamesNotResetSections(members.toString());
		metaView.setAlignedByColumns(alignedByColumns); 
		return null;
	}

	private void parseTabs(MetaComponent component, Class pojoClass) throws Exception { 
		if (pojoClass.isAnnotationPresent(Tab.class)) {
			Tab tab = (Tab) pojoClass.getAnnotation(Tab.class);
			addTab(component, tab);
		}
		if (pojoClass.isAnnotationPresent(Tabs.class)) {
			Tabs tabs = (Tabs) pojoClass.getAnnotation(Tabs.class);
			for (Tab tab: tabs.value()) {
				addTab(component, tab);
			}
		}
	}

	private void addTab(MetaComponent component, Tab tab) throws Exception {
		MetaTab metaTab = new MetaTab();		
		metaTab.setName(tab.name());
		metaTab.setBaseCondition(tab.baseCondition()); 
		metaTab.setDefaultOrder(tab.defaultOrder());
		metaTab.setEditor(tab.editor()); 
		metaTab.setEditors(tab.editors()); 
		if (!tab.filter().equals(VoidFilter.class)) {
			MetaFilter metaFilter = new MetaFilter();
			metaFilter.setClassName(tab.filter().getName());
			metaTab.setMetaFilter(metaFilter);
		}
		
		for (RowStyle rowStyle: tab.rowStyles()) {
			MetaRowStyle metaRowStyle = new MetaRowStyle();
			metaRowStyle.setStyle(rowStyle.style());
			metaRowStyle.setProperty(rowStyle.property());
			metaRowStyle.setValue(rowStyle.value());			
			metaTab.addMetaRowStyle(metaRowStyle);
		}			
		component.addMetaTab(metaTab);

		if (Is.emptyString(tab.properties())) {
			if (metaTab.getPropertiesNames().isEmpty()) {
				metaTab.setDefaultPropertiesNames("*");
			}
		}
		else {
			metaTab.setDefaultPropertiesNames(tab.properties());
		}		

	}

	private Collection<Class> getFirstLevelEntitySubclasses(Class pojoClass) throws Exception { 
		if (entityFirstLevelSubclasses == null) {
			entityFirstLevelSubclasses = new HashMap<Class, Collection<Class>>();
			for (String entityClassName: getManagedClassNames()) {
				Class entityClass = Class.forName(entityClassName);
				Class superClass = entityClass.getSuperclass();
				if (superClass.isAnnotationPresent(Entity.class)) {
					Collection<Class> subclasses = entityFirstLevelSubclasses.get(superClass);
					if (subclasses == null) {
						subclasses = new ArrayList<Class>();
						entityFirstLevelSubclasses.put(superClass, subclasses);
					}
					subclasses.add(entityClass);
				}				
			}
		}
		Collection<Class> result = entityFirstLevelSubclasses.get(pojoClass);
		return result == null?Collections.EMPTY_LIST:result;
	}


	private String getDiscriminatorColumn(Class pojoClass) { 
		if (pojoClass == null) return "DTYPE";
		if (!pojoClass.isAnnotationPresent(DiscriminatorColumn.class)) {
			return getDiscriminatorColumn(pojoClass.getSuperclass());
		}
		DiscriminatorColumn discriminatorColumn = (DiscriminatorColumn) 
			pojoClass.getAnnotation(DiscriminatorColumn.class);
		
		return discriminatorColumn.name();		
	}

	private boolean isDiscriminatorNumeric(Class pojoClass) { 
		if (pojoClass == null) return false;
		if (!pojoClass.isAnnotationPresent(DiscriminatorColumn.class)) {
			return isDiscriminatorNumeric(pojoClass.getSuperclass());
		}
		DiscriminatorColumn discriminatorColumn = (DiscriminatorColumn) 
			pojoClass.getAnnotation(DiscriminatorColumn.class);
		
		return discriminatorColumn.discriminatorType().equals(DiscriminatorType.INTEGER);
	}

	private String getDiscriminatorValue(Class pojoClass) { 
		if (!pojoClass.isAnnotationPresent(DiscriminatorValue.class)) {
			return pojoClass.getSimpleName();
		}
		DiscriminatorValue discriminatorValue = (DiscriminatorValue) pojoClass.getAnnotation(DiscriminatorValue.class);
		return discriminatorValue.value();
	}


	private void addProperty(MetaModel model, ModelMapping mapping, PropertyDescriptor pd, Field field, String embedded) throws Exception {		
		if (model.containsMetaProperty(pd.getName())) {
			processAnnotations(model.getMetaProperty(pd.getName()), pd.getReadMethod());			
			return;
		}
		MetaProperty property = new MetaProperty();
		property.setName(pd.getName());		
		
		if (pd.getPropertyType().isEnum()) {
			for (Object validValue: pd.getPropertyType().getEnumConstants()) {
				property.addValidValue(validValue);
			}
		}
		property.setTypeName(pd.getPropertyType().getName());		
		model.addMetaProperty(property);
		
		processAnnotations(property, pd.getReadMethod());
		processAnnotations(property, field);		
		
		if (!property.isRequired() && property.isKey()) {
			if (!(
					(field != null && field.isAnnotationPresent(GeneratedValue.class)) ||							
					pd.getReadMethod().isAnnotationPresent(GeneratedValue.class)
				)
				&&
				!(
					(field != null && field.isAnnotationPresent(Hidden.class)) ||							
					pd.getReadMethod().isAnnotationPresent(Hidden.class)
				)				
			)
			{	
				property.setRequired(true);
			}
		}
	
		if (field == null && pd.getWriteMethod() == null) {
			// It's calculated
			setCalculated(pd, property); 
		}
		
		
		
		// The mapping part
		if (mapping != null && field != null) { 
			PropertyMapping pMapping = new PropertyMapping(mapping);			
			pMapping.setProperty(embedded==null?pd.getName():embedded + "_" + pd.getName());
			// Column
			if (field != null && field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				pMapping.setColumn(column.name());
			}
			else if (pd.getReadMethod().isAnnotationPresent(Column.class)) {
				Column column = pd.getReadMethod().getAnnotation(Column.class);
				pMapping.setColumn(column.name());
			}
			if (Is.emptyString(pMapping.getColumn())) {
				pMapping.setColumn(pd.getName());
			}		
			// Formula 
			if (field != null && field.isAnnotationPresent(Formula.class)) {
				Formula formula = field.getAnnotation(Formula.class);
				pMapping.setFormula(formula.value());
			}
			else if (pd.getReadMethod().isAnnotationPresent(Formula.class)) {
				Formula formula = pd.getReadMethod().getAnnotation(Formula.class);
				pMapping.setFormula(formula.value());
			}			
			if (pMapping.hasFormula() && pd.getWriteMethod() == null) {
				property.setReadOnly(true);
			}
			// Converter (from Hibernate Type)
			if (field != null && field.isAnnotationPresent(Type.class)) {				
				Type type = field.getAnnotation(Type.class);
				addConverter(pMapping, type, field.getAnnotation(Columns.class));
			}
			else if (pd.getReadMethod().isAnnotationPresent(Type.class)) {
				Type type = pd.getReadMethod().getAnnotation(Type.class);
				addConverter(pMapping, type, pd.getReadMethod().getAnnotation(Columns.class));
			}
			else if (property.hasValidValues()) { 
				// To convert the parameters sent for filtering in the tabs
				setEnumConverter(pd, field, pMapping);
			}
			else {
				pMapping.setDefaultConverter();
			}		
			
			mapping.addPropertyMapping(pMapping);
		}		
	}


	private void setCalculated(PropertyDescriptor pd, MetaProperty property) {
		property.setReadOnly(true);		
		MetaCalculator metaCalculator = new MetaCalculator();		
		metaCalculator.setClassName(org.openxava.calculators.ModelPropertyCalculator.class.getName());
		MetaSet metaSet = new MetaSet();
		metaSet.setPropertyName("property");
		metaSet.setValue(property.getName());
		metaCalculator.addMetaSet(metaSet);
		if (pd.getReadMethod().isAnnotationPresent(Depends.class)) {
			Depends depends = pd.getReadMethod().getAnnotation(Depends.class);
			for (Object propertyFrom: Strings.toCollection(depends.value())) {
				MetaSet metaSetPropertyFrom = new MetaSet();
				metaSetPropertyFrom.setPropertyName("valueOfDependsProperty");
				metaSetPropertyFrom.setPropertyNameFrom((String) propertyFrom);
				metaCalculator.addMetaSet(metaSetPropertyFrom);
			}
		}		
		property.setMetaCalculator(metaCalculator);		
	}


	private void setEnumConverter(PropertyDescriptor pd, Field field,
			PropertyMapping pMapping) {
		Enumerated enumerated = null;
		Class enumType = null;
		if (field != null && field.isAnnotationPresent(Enumerated.class)) {				
			enumerated = field.getAnnotation(Enumerated.class);					
			enumType = field.getType();
		}
		else if (pd.getReadMethod().isAnnotationPresent(Enumerated.class)) {
			enumerated = pd.getReadMethod().getAnnotation(Enumerated.class);
			enumType = pd.getReadMethod().getReturnType();
		}
		if (enumerated == null || enumerated.value() == EnumType.ORDINAL) {
			pMapping.setConverterClassName(OrdinalEnumIntConverter.class.getName());
		}
		else {
			pMapping.setConverterClassName(StringEnumIntConverter.class.getName());
			MetaSet metaSet = new MetaSet();
			metaSet.setPropertyName("enumConstants");
			StringBuffer enumConstants = new StringBuffer(); 
			for (Object enumConstant: enumType.getEnumConstants()) {
				if (enumConstants.length() > 0) enumConstants.append(';');
				enumConstants.append(enumConstant);
				
			}
			metaSet.setValue(enumConstants.toString());
			pMapping.addMetaSet(metaSet);
		}
	}
	
	
	private void addConverter(PropertyMapping mapping, Type type, Columns columns) throws Exception {
		Class typeClass = null;
		try {
			typeClass = Class.forName(type.type());
		}
		catch (ClassNotFoundException ex) {
			// If type.type() is a type name and not a class we do not add it, this is not a big problem
			// since in JPA most data is obtained via JPA so converters are only used for a very few things.
			// The not supported combination is JPA + JDBCTabProvider + TypeDef, not a very common one.
			
			// If type.type() is a class name mistyped the JPA will complain, so we do not to do it here
			return;
		}
		if (CompositeUserType.class.isAssignableFrom(typeClass)) { 
			mapping.setMultipleConverterClassName(HibernateCompositeTypeConverter.class.getName());
			
			MetaSet typeMetaSet = new MetaSet(); 
			typeMetaSet.setPropertyName("type");
			typeMetaSet.setValue(type.type());
			mapping.addMetaSet(typeMetaSet);
			
			if (columns != null) {				
				MetaSet valueCountMetaSet = new MetaSet();
				valueCountMetaSet.setPropertyName("valuesCount");
				valueCountMetaSet.setValue(String.valueOf(columns.columns().length));
				mapping.addMetaSet(valueCountMetaSet);										

				int valueIndex = 0;
				for (Column column: columns.columns()) {
					CmpField cmp = new CmpField();
					cmp.setConverterPropertyName("value" + valueIndex++);
					cmp.setColumn(column.name());
					mapping.addCmpField(cmp);						
				}
			}	
			
			mapping.setColumn(""); 
		}
		else {				
			mapping.setConverterClassName(HibernateTypeConverter.class.getName());
			MetaSet metaSet = new MetaSet();
			metaSet.setPropertyName("type");
			metaSet.setValue(type.type());
			mapping.addMetaSet(metaSet);
		}
		
		// Parameters
		if (type.parameters().length > 0) {
			StringBuffer parameters = new StringBuffer();
			for (Parameter parameter: type.parameters()) {
				parameters.append(parameter.name());
				parameters.append('=');
				parameters.append('"');
				parameters.append(parameter.value());
				parameters.append('"');
				parameters.append(',');
			}
			
			MetaSet parameterMetaSet = new MetaSet();
			parameterMetaSet.setPropertyName("parameters");
			parameterMetaSet.setValue(parameters.toString());					
			mapping.addMetaSet(parameterMetaSet);
		}		
	}

	private void processAnnotations(MetaProperty property, AnnotatedElement element) throws XavaException {
		if (element == null) return;
		// key
		if (element.isAnnotationPresent(Id.class)) {
			property.setKey(true);
		}
		if (element.isAnnotationPresent(SearchKey.class)) { 
			property.setSearchKey(true);
		}

		// size
		if (element.isAnnotationPresent(javax.validation.constraints.Max.class)) {
			javax.validation.constraints.Max max = element.getAnnotation(javax.validation.constraints.Max.class);
			property.setSize((int) (Math.log10(max.value()) + 1));
		}
		else if (element.isAnnotationPresent(javax.validation.constraints.DecimalMax.class)) {
			javax.validation.constraints.DecimalMax max = element.getAnnotation(javax.validation.constraints.DecimalMax.class);			
			property.setSize((int) (Math.log10(new BigDecimal(max.value()).doubleValue()) + 1));
		}		
		else if (element.isAnnotationPresent(javax.validation.constraints.Size.class)) {
			javax.validation.constraints.Size size = element.getAnnotation(javax.validation.constraints.Size.class);			
			property.setSize(size.max());
		}
		else if (element.isAnnotationPresent(Column.class)) {
			Column column = element.getAnnotation(Column.class);
			if (column.length() == 255) {
				// 255 is the default value of length, this means that the length
				// is omitted, then we put 0 in order to OX calculate its default size.
				// This is can be a problem when the developer put 255 explicitly. 
				property.setSize(0);
			}
			else {
				property.setSize(column.length());
			}
			// This will take care of the scale for fractional size different than 2
			if (column.scale() > 0) {
				property.setScale(column.scale());
			}
			if (column.precision() > 0) {
				if (column.scale() > 0) {
					property.setSize(column.precision() + 1 + column.scale());
				} else {
					property.setSize(column.precision());
				}
			}
		}
		else if (element.isAnnotationPresent(javax.validation.constraints.Digits.class)) {
			javax.validation.constraints.Digits digits = element.getAnnotation(javax.validation.constraints.Digits.class);
			property.setSize(digits.integer() + 1 + digits.fraction());
			property.setScale(digits.fraction());
		}		
				
		// required
		if (element.isAnnotationPresent(Required.class)) {						
			property.setRequired(true);
			property.setRequiredMessage(filterMessage(element.getAnnotation(Required.class).message()));
		}
		else if (element.isAnnotationPresent(javax.validation.constraints.Min.class)) {
			javax.validation.constraints.Min min = element.getAnnotation(javax.validation.constraints.Min.class);
			if (min.value() > 0) { 
				property.setRequired(true);
				property.setRequiredMessage(filterMessage(element.getAnnotation(javax.validation.constraints.Min.class).message()));
			}
		}		
		
		// hidden
		if (element.isAnnotationPresent(Hidden.class)) {  						
			property.setHidden(true);
		}
		
		// version
		if (element.isAnnotationPresent(javax.persistence.Version.class)) {  						
			property.setVersion(true);
		}
		
		// transient
		if (element.isAnnotationPresent(javax.persistence.Transient.class)) {  						
			property.setTransient(true);
		}
		
		// stereotype
		if (element.isAnnotationPresent(Stereotype.class)) {
			Stereotype stereotype = element.getAnnotation(Stereotype.class);
			property.setStereotype(stereotype.value());
		}
		
		// default value calculator
		if (element.isAnnotationPresent(DefaultValueCalculator.class)) {
			DefaultValueCalculator calculator = element.getAnnotation(DefaultValueCalculator.class);
			MetaCalculator metaCalculator = new MetaCalculator();
			metaCalculator.setClassName(calculator.value().getName());
			for (PropertyValue put: calculator.properties()) {
				metaCalculator.addMetaSet(toMetaSet(put));
			}
			property.setMetaCalculatorDefaultValue(metaCalculator);
		}
		
		// generated value
		if (element.isAnnotationPresent(GeneratedValue.class)) {
			// The MetaCalculator does not have class because is only for having a 
			// calculator on-create
			MetaCalculator metaCalculator = new MetaCalculator();
			metaCalculator.setOnCreate(true);
			metaCalculator.setClassName(NullCalculator.class.getName()); 
			property.setMetaCalculatorDefaultValue(metaCalculator);
			if (element.isAnnotationPresent(DefaultValueCalculator.class)) {
				log.warn("default_value_calculator_generated_value_incompatible");
			}
		}
		
		// validator
		if (element.isAnnotationPresent(PropertyValidator.class)) {			
			PropertyValidator validator = element.getAnnotation(PropertyValidator.class);
			addPropertyValidator(property, validator);
		}
		if (element.isAnnotationPresent(PropertyValidators.class)) {
			PropertyValidator [] validators = element.getAnnotation(PropertyValidators.class).value();
			for (PropertyValidator validator: validators) {				
				addPropertyValidator(property, validator);				
			}
		}		
		
		// calculation
		if (element.isAnnotationPresent(Calculation.class)) {			
			Calculation calculation = element.getAnnotation(Calculation.class);
			property.setCalculation(calculation.value());
		}		
						
		// for View
		for (Object oMetaView: property.getMetaModel().getMetaViews()) {
			MetaView metaView = (MetaView) oMetaView;			
			MetaPropertyView propertyView = new MetaPropertyView();
			propertyView.setPropertyName(property.getName());
			boolean mustAddMetaView = false;
			
			// Action
			mustAddMetaView = addAction(element, metaView, propertyView, mustAddMetaView);			
		
			// OnChange
			if (element.isAnnotationPresent(OnChange.class)) {
				OnChange onChange = element.getAnnotation(OnChange.class);
				if (isForView(metaView, onChange.forViews(), onChange.notForViews())) {
					propertyView.setOnChangeActionClassName(onChange.value().getName());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(OnChanges.class)) {
				OnChange [] onChanges = element.getAnnotation(OnChanges.class).value();				
				for (OnChange onChange: onChanges) {
					if (isForView(metaView, onChange.forViews(), onChange.notForViews())) {
						propertyView.setOnChangeActionClassName(onChange.value().getName());
						mustAddMetaView = true;				
					}
				}					
			}
		
			// LabelFormat
			if (element.isAnnotationPresent(LabelFormat.class)) {
				LabelFormat labelFormat = element.getAnnotation(LabelFormat.class);
				if (isForView(metaView, labelFormat.forViews(), labelFormat.notForViews())) {
					propertyView.setLabelFormat(labelFormat.value().ordinal());
					mustAddMetaView = true;				
				}
			}					
			if (element.isAnnotationPresent(LabelFormats.class)) {
				LabelFormat [] labelFormats = element.getAnnotation(LabelFormats.class).value();
				for (LabelFormat labelFormat: labelFormats) {
					if (isForView(metaView, labelFormat.forViews(), labelFormat.notForViews())) {
						propertyView.setLabelFormat(labelFormat.value().ordinal());
						mustAddMetaView = true;
					}
				}					
			}
			
			// ReadOnly
			if (element.isAnnotationPresent(ReadOnly.class)) {
				ReadOnly readOnly = element.getAnnotation(ReadOnly.class);
				if (isForView(metaView, readOnly.forViews(), readOnly.notForViews())) {
					propertyView.setReadOnly(true);
					mustAddMetaView = true;
					propertyView.setReadOnlyOnCreate(readOnly.onCreate());
				}
			}					
			
			// Editor
			if (processEditorAnnotation(element, metaView, propertyView)) {
				mustAddMetaView = true;
			}
			
			// DisplaySize
			if (element.isAnnotationPresent(DisplaySize.class)) {
				DisplaySize displaySize = element.getAnnotation(DisplaySize.class);
				if (isForView(metaView, displaySize.forViews(), displaySize.notForViews())) {
					propertyView.setDisplaySize(displaySize.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(DisplaySizes.class)) {
				DisplaySize [] displaySizes = element.getAnnotation(DisplaySizes.class).value();				
				for (DisplaySize displaySize: displaySizes) {
					if (isForView(metaView, displaySize.forViews(), displaySize.notForViews())) {
						propertyView.setDisplaySize(displaySize.value());
						mustAddMetaView = true;				
					}
				}					
			}			
			
			// LabelStyle
			if (element.isAnnotationPresent(LabelStyle.class)) {
				LabelStyle labelStyle = element.getAnnotation(LabelStyle.class);
				if (isForView(metaView, labelStyle.forViews(), labelStyle.notForViews())) {
					propertyView.setLabelStyle(labelStyle.value());
					mustAddMetaView = true;	
				}
			}
			if (element.isAnnotationPresent(LabelStyles.class)){
				LabelStyle [] labelStyles = element.getAnnotation(LabelStyles.class).value();
				for(LabelStyle labelStyle : labelStyles){
					if (isForView(metaView, labelStyle.forViews(), labelStyle.notForViews())) {
						propertyView.addLabelStyle(labelStyle.value());
						mustAddMetaView = true;
					}
				}
			}
						
			if (mustAddMetaView) metaView.addMetaViewProperty(propertyView);
		}
		
		// Not applicable
		if (element.isAnnotationPresent(ListProperties.class)) {
			notApply(property.getName(), ListProperties.class, "collections");
		}
		if (element.isAnnotationPresent(ListsProperties.class)) {
			notApply(property.getName(), ListsProperties.class, "collections");
		}		
		if (element.isAnnotationPresent(DescriptionsList.class)) {
			notApply(property.getName(), DescriptionsList.class, "references");
		}
		if (element.isAnnotationPresent(DescriptionsLists.class)) {
			notApply(property.getName(), DescriptionsLists.class, "references");
		}		
		if (element.isAnnotationPresent(CollectionView.class)) {
			notApply(property.getName(), CollectionView.class, "collections");
		}
		if (element.isAnnotationPresent(CollectionViews.class)) {
			notApply(property.getName(), CollectionViews.class, "collections");
		}		
		if (element.isAnnotationPresent(ListAction.class)) {
			notApply(property.getName(), ListAction.class, "collections");
		}
		if (element.isAnnotationPresent(ListActions.class)) {
			notApply(property.getName(), ListActions.class, "collections");
		}				
		if (element.isAnnotationPresent(ListSubcontroller.class)) {
			notApply(property.getName(), ListSubcontroller.class, "collections");
		}
		if (element.isAnnotationPresent(ListSubcontrollers.class)) {
			notApply(property.getName(), ListSubcontrollers.class, "collections");
		}
		if (element.isAnnotationPresent(RowAction.class)) {
			notApply(property.getName(), RowAction.class, "collections");
		}
		if (element.isAnnotationPresent(RowActions.class)) {
			notApply(property.getName(), RowActions.class, "collections");
		}
		if (element.isAnnotationPresent(EditOnly.class)) {
			notApply(property.getName(), EditOnly.class, "collections");
		}
		if (element.isAnnotationPresent(SearchAction.class)) {
			notApply(property.getName(), SearchAction.class, "references");
		}
		if (element.isAnnotationPresent(SearchActions.class)) {
			notApply(property.getName(), SearchActions.class, "references");
		}		
		if (element.isAnnotationPresent(ReferenceView.class)) {
			notApply(property.getName(), ReferenceView.class, "references");
		}
		if (element.isAnnotationPresent(ReferenceViews.class)) {
			notApply(property.getName(), ReferenceViews.class, "references");
		}												
		if (element.isAnnotationPresent(NoFrame.class)) {
			notApply(property.getName(), NoFrame.class, "references");
		}
		if (element.isAnnotationPresent(NoCreate.class)) {
			notApply(property.getName(), NoCreate.class, "references & collections");
		}
		if (element.isAnnotationPresent(NoModify.class)) {
			notApply(property.getName(), NoModify.class, "references & collections");
		}
		if (element.isAnnotationPresent(AsEmbedded.class)) {
			notApply(property.getName(), AsEmbedded.class, "references & collections");
		}
		if (element.isAnnotationPresent(EditAction.class)) {
			notApply(property.getName(), EditAction.class, "collections");
		}				
		if (element.isAnnotationPresent(EditActions.class)) {
			notApply(property.getName(), EditActions.class, "collections");
		}				
		if (element.isAnnotationPresent(DetailAction.class)) {
			notApply(property.getName(), DetailAction.class, "collections");
		}
		if (element.isAnnotationPresent(DetailActions.class)) {
			notApply(property.getName(), DetailActions.class, "collections");
		}
		if (element.isAnnotationPresent(ViewAction.class)) {
			notApply(property.getName(), ViewAction.class, "collections");
		}								
		if (element.isAnnotationPresent(ViewActions.class)) {
			notApply(property.getName(), ViewActions.class, "collections");
		}						
		if (element.isAnnotationPresent(NoSearch.class)) {
			notApply(property.getName(), NoSearch.class, "references");
		}
		if (element.isAnnotationPresent(Condition.class)) {
			notApply(property.getName(), Condition.class, "collections");
		}
		if (element.isAnnotationPresent(HideDetailAction.class)) {
			notApply(property.getName(), HideDetailAction.class, "collections");
		}
		if (element.isAnnotationPresent(HideDetailActions.class)) {
			notApply(property.getName(), HideDetailActions.class, "collections");
		}
		if (element.isAnnotationPresent(NewAction.class)) {
			notApply(property.getName(), NewAction.class, "collections");
		}								
		if (element.isAnnotationPresent(NewActions.class)) {
			notApply(property.getName(), NewActions.class, "collections");
		}
		if (element.isAnnotationPresent(RemoveAction.class)) {
			notApply(property.getName(), RemoveAction.class, "collections");
		}								
		if (element.isAnnotationPresent(RemoveActions.class)) {
			notApply(property.getName(), RemoveActions.class, "collections");
		}								
		if (element.isAnnotationPresent(RemoveSelectedAction.class)) {
			notApply(property.getName(), RemoveSelectedAction.class, "collections");
		}								
		if (element.isAnnotationPresent(RemoveSelectedActions.class)) {
			notApply(property.getName(), RemoveSelectedActions.class, "collections");
		}
		if (element.isAnnotationPresent(SaveAction.class)) {
			notApply(property.getName(), SaveAction.class, "collections");
		}
		if (element.isAnnotationPresent(SaveActions.class)) {
			notApply(property.getName(), SaveActions.class, "collections");
		}
		if (element.isAnnotationPresent(XOrderBy.class)) {
			notApply(property.getName(), XOrderBy.class, "collections");
		}
		if (element.isAnnotationPresent(RowStyle.class)) {
			notApply(property.getName(), RowStyle.class, "collections");
		}
		if (element.isAnnotationPresent(RowStyles.class)) {
			notApply(property.getName(), RowStyles.class, "collections");
		}
		if (element.isAnnotationPresent(OnChangeSearch.class)) {
			notApply(property.getName(), OnChangeSearch.class, "references");
		}																						
		if (element.isAnnotationPresent(OnChangeSearchs.class)) {
			notApply(property.getName(), OnChangeSearchs.class, "references");
		}																						
		if (element.isAnnotationPresent(OnSelectElementAction.class)) {
			notApply(property.getName(), OnSelectElementAction.class, "collections");
		}
		if (element.isAnnotationPresent(OnSelectElementActions.class)) {
			notApply(property.getName(), OnSelectElementActions.class, "collections");
		}
		if (element.isAnnotationPresent(SearchListCondition.class)) {
			notApply(property.getName(), SearchListCondition.class, "references & collections");
		}
		if (element.isAnnotationPresent(SearchListConditions.class)) {
			notApply(property.getName(), SearchListConditions.class, "references & collections");
		}
		if (element.isAnnotationPresent(Tree.class)) {
			notApply(property.getName(), Tree.class, "collections");
		}
		if (element.isAnnotationPresent(Trees.class)) {
			notApply(property.getName(), Trees.class, "collections");
		}
	}

	private static String filterMessage(String message) {
		if (Is.emptyString(message)) return null;
		if (message.startsWith("{") && message.endsWith("}")) {			
			return message.substring(1, message.length() - 1);
		}		
		return	"'" + message + "'";
	}


	private void processAnnotations(MetaCollection collection, AnnotatedElement element) throws Exception {
		if (element == null) return;	
		boolean cascadeAndSelfReference = false;
		if (element.isAnnotationPresent(OrderColumn.class)) {
			if (element instanceof Field) {
				Field field = (Field) element; 
				if (List.class.isAssignableFrom(field.getType())) {
					collection.setSortable(true);
				}
				else {
					log.warn(XavaResources.getString("order_column_requires_list", collection.getName()));
				}
			}
		}
		if (element.isAnnotationPresent(OneToMany.class)) {
			collection.setMetaCalculator(null);			
			OneToMany oneToMany = element.getAnnotation(OneToMany.class);
			collection.getMetaReference().setRole(oneToMany.mappedBy());
			if (isCascade(oneToMany.cascade())) {							
				if (!collection.getMetaModel().getName().equals(collection.getMetaReference().getReferencedModelName())) {
					addAggregateForCollection(collection.getMetaModel(), getClassNameFor(collection.getMetaReference().getReferencedModelName()), oneToMany.mappedBy());					
				}
				else {
					collection.getMetaModel().setContainerModelName(collection.getMetaModel().getName()); 
					collection.getMetaModel().setContainerReference(oneToMany.mappedBy());
					cascadeAndSelfReference = true;					
				}				
			}
			collection.setOrphanRemoval(oneToMany.orphanRemoval());
			if (collection.isSortable()) { // By now, only needed when sortable
				// MetaModel metaModelReferenced = collection.getMetaModel().getMetaModelContainer(); // Not in this way, because it would store MetaEntity for aggregate collections producing many lateral problems
				MetaModel metaModelReferenced = MetaComponent.get(collection.getMetaReference().getReferencedModelName()).getMetaEntity();
				MetaReference inverseRef = metaModelReferenced.getMetaReference(oneToMany.mappedBy());
				inverseRef.setReferencedModelCorrespondingCollection(collection.getName());
			}
		}
		else if (element.isAnnotationPresent(ManyToMany.class)) {
			ManyToMany manyToMany = element.getAnnotation(ManyToMany.class);
			collection.setInverseCollection(manyToMany.mappedBy());
			collection.getMetaReference().setAggregate(isCascade(manyToMany.cascade()));
			if (isCascade(manyToMany.cascade())) {
				addAggregateForCollection(collection.getMetaModel(), getClassNameFor(collection.getMetaReference().getReferencedModelName()), manyToMany.mappedBy());
			}
			// For the rest ManyToMany collections are processed as calculated one
		}
		else if (element.isAnnotationPresent(ElementCollection.class)) {
			collection.setElementCollection(true);
			addAggregateForCollection(collection.getMetaModel(), getClassNameFor(collection.getMetaReference().getReferencedModelName()), null);
		}
		else if (element.isAnnotationPresent(Condition.class)) {			
			collection.setMetaCalculator(null); 
		}		
		
		if (element.isAnnotationPresent(javax.validation.constraints.Size.class)) {
			javax.validation.constraints.Size size = element.getAnnotation(javax.validation.constraints.Size.class);
			collection.setMinimum(size.min());
			collection.setMaximum(size.max());
		}
		
		if (element.isAnnotationPresent(Condition.class)) {
			Condition condition = element.getAnnotation(Condition.class);
			collection.setCondition(condition.value());
		}
		
		if (element.isAnnotationPresent(XOrderBy.class)) {
			XOrderBy orderBy = element.getAnnotation(XOrderBy.class);
			collection.setOrder(wrapWithDollars(orderBy.value()));			
		}
		else if (element.isAnnotationPresent(OrderBy.class)) {
			OrderBy orderBy = element.getAnnotation(OrderBy.class);
			collection.setOrder(wrapWithDollars(orderBy.value()));
		}		
					
		for (Object oMetaView: collection.getMetaModel().getMetaViews()) {
			MetaView metaView = (MetaView) oMetaView;				
			MetaCollectionView collectionView = new MetaCollectionView();
			collectionView.setCollectionName(collection.getName());
			boolean mustAddMetaView = false;
			
			if (cascadeAndSelfReference) {
				collectionView.setAsAggregate(true);
				mustAddMetaView = true;
			}			
			
			// ListProperties
			if (element.isAnnotationPresent(ListProperties.class)) {
				ListProperties listProperties = element.getAnnotation(ListProperties.class);
				if (isForView(metaView, listProperties.forViews(), listProperties.notForViews())) {
					collectionView.setPropertiesList(listProperties.value());
					mustAddMetaView = true;
				}
			}
			if (element.isAnnotationPresent(ListsProperties.class)) {
				ListProperties [] listsProperties = element.getAnnotation(ListsProperties.class).value();				
				for (ListProperties listProperties: listsProperties) {
					if (isForView(metaView, listProperties.forViews(), listProperties.notForViews())) {
						collectionView.setPropertiesList(listProperties.value());
						mustAddMetaView = true;
					}					
				}
			}
			
			// CollectionView
			if (element.isAnnotationPresent(CollectionView.class)) {
				CollectionView view = element.getAnnotation(CollectionView.class);
				if (isForView(metaView, view.forViews(), view.notForViews())) {
					collectionView.setViewName(view.value());
					mustAddMetaView = true;
				}
			}
			if (element.isAnnotationPresent(CollectionViews.class)) {
				CollectionView [] views = element.getAnnotation(CollectionViews.class).value();				
				for (CollectionView view: views) {
					if (isForView(metaView, view.forViews(), view.notForViews())) {
						collectionView.setViewName(view.value());
						mustAddMetaView = true;
					}
				}
			}
			
			// RowStyle 
			if (element.isAnnotationPresent(RowStyle.class)) {
				RowStyle rowStyle = element.getAnnotation(RowStyle.class);
				if (isForView(metaView, rowStyle.forViews(), rowStyle.notForViews())) {
					collectionView.addMetaRowStyle(toMetaRowStyle(rowStyle));
					mustAddMetaView = true;
				}
			}
			if (element.isAnnotationPresent(RowStyles.class)) {
				RowStyle [] rowStyles = element.getAnnotation(RowStyles.class).value();				
				for (RowStyle rowStyle: rowStyles) {
					if (isForView(metaView, rowStyle.forViews(), rowStyle.notForViews())) {
						collectionView.addMetaRowStyle(toMetaRowStyle(rowStyle));
						mustAddMetaView = true;
					}
				}
			}
			
			// ListAction
			if (element.isAnnotationPresent(ListAction.class)) {
				ListAction action = element.getAnnotation(ListAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.addActionListName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(ListActions.class)) {
				ListAction [] actions = element.getAnnotation(ListActions.class).value();
				for (ListAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						collectionView.addActionListName(action.value());
						mustAddMetaView = true;
					}
				}				
			}

			// ListSubcontroller
			if (element.isAnnotationPresent(ListSubcontroller.class)) {
				ListSubcontroller subcontroller = element.getAnnotation(ListSubcontroller.class);
				if (isForView(metaView, subcontroller.forViews(), subcontroller.notForViews())) {
					collectionView.addSubcontrollerListName(subcontroller.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(ListSubcontrollers.class)) {
				ListSubcontroller [] subcontrollers = element.getAnnotation(ListSubcontrollers.class).value();
				for (ListSubcontroller subcontroller: subcontrollers) {				
					if (isForView(metaView, subcontroller.forViews(), subcontroller.notForViews())) {
						collectionView.addSubcontrollerListName(subcontroller.value());
						mustAddMetaView = true;
					}
				}				
			}
			
			// RowAction
			if (element.isAnnotationPresent(RowAction.class)) {
				RowAction action = element.getAnnotation(RowAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.addActionRowName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(RowActions.class)) {
				RowAction [] actions = element.getAnnotation(RowActions.class).value();
				for (RowAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						collectionView.addActionRowName(action.value());
						mustAddMetaView = true;
					}
				}				
			}						
			
			// DetailAction
			if (element.isAnnotationPresent(DetailAction.class)) {
				DetailAction action = element.getAnnotation(DetailAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.addActionDetailName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(DetailActions.class)) {
				DetailAction [] actions = element.getAnnotation(DetailActions.class).value();
				for (DetailAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						collectionView.addActionDetailName(action.value());
						mustAddMetaView = true;
					}
				}				
			}			
			
			// NewAction
			if (element.isAnnotationPresent(NewAction.class)) {
				NewAction action = element.getAnnotation(NewAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setNewActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(NewActions.class)) {
				NewAction [] actions = element.getAnnotation(NewActions.class).value();
				for (NewAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getNewActionName())) {
							collectionView.setNewActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), NewAction.class, metaView.getName());
						}
					}
				}				
			}
			
			if (element.isAnnotationPresent(AddAction.class)) {
				AddAction action = element.getAnnotation(AddAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setAddActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(AddActions.class)) { 
				AddAction [] actions = element.getAnnotation(AddActions.class).value();
				for (AddAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getAddActionName())) {
							collectionView.setAddActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), AddAction.class, metaView.getName());
						}
					}
				}				
			}			
			
			// SaveAction
			if (element.isAnnotationPresent(SaveAction.class)) {
				SaveAction action = element.getAnnotation(SaveAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setSaveActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(SaveActions.class)) {
				SaveAction [] actions = element.getAnnotation(SaveActions.class).value();
				for (SaveAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getSaveActionName())) {
							collectionView.setSaveActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), SaveAction.class, metaView.getName());
						}
					}
				}				
			}			
			
			// HideDetailAction
			if (element.isAnnotationPresent(HideDetailAction.class)) {
				HideDetailAction action = element.getAnnotation(HideDetailAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setHideActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(HideDetailActions.class)) {
				HideDetailAction [] actions = element.getAnnotation(HideDetailActions.class).value();
				for (HideDetailAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getHideActionName())) {
							collectionView.setHideActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), HideDetailAction.class, metaView.getName());
						}
					}
				}				
			}
			
			// OnSelectedElementAction
			if (element.isAnnotationPresent(OnSelectElementAction.class)) {
				OnSelectElementAction action = element.getAnnotation(OnSelectElementAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setOnSelectElementActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(OnSelectElementActions.class)) {
				OnSelectElementAction [] actions = element.getAnnotation(OnSelectElementActions.class).value();
				for (OnSelectElementAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getOnSelectElementActionName())) {
							collectionView.setOnSelectElementActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), OnSelectElementAction.class, metaView.getName());
						}
					}
				}				
			}
			
			
			// RemoveAction
			if (element.isAnnotationPresent(RemoveAction.class)) {
				RemoveAction action = element.getAnnotation(RemoveAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setRemoveActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(RemoveActions.class)) {
				RemoveAction [] actions = element.getAnnotation(RemoveActions.class).value();
				for (RemoveAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getRemoveActionName())) {
							collectionView.setRemoveActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), RemoveAction.class, metaView.getName());
						}
					}
				}				
			}
			
			// RemoveSelectedAction
			if (element.isAnnotationPresent(RemoveSelectedAction.class)) {
				RemoveSelectedAction action = element.getAnnotation(RemoveSelectedAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setRemoveSelectedActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(RemoveSelectedActions.class)) {
				RemoveSelectedAction [] actions = element.getAnnotation(RemoveSelectedActions.class).value();
				for (RemoveSelectedAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getRemoveSelectedActionName())) {
							collectionView.setRemoveSelectedActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), RemoveSelectedAction.class, metaView.getName());
						}
					}
				}				
			}
			
			// EditAction
			if (element.isAnnotationPresent(EditAction.class)) {
				EditAction action = element.getAnnotation(EditAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setEditActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(EditActions.class)) {
				EditAction [] actions = element.getAnnotation(EditActions.class).value();
				for (EditAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getEditActionName())) {
							collectionView.setEditActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), EditAction.class, metaView.getName());
						}
					}
				}				
			}
			
			// ViewAction
			if (element.isAnnotationPresent(ViewAction.class)) {
				ViewAction action = element.getAnnotation(ViewAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					collectionView.setViewActionName(action.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(ViewActions.class)) {
				ViewAction [] actions = element.getAnnotation(ViewActions.class).value();
				for (ViewAction action: actions) {				
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						if (Is.emptyString(collectionView.getViewActionName())) {
							collectionView.setViewActionName(action.value());
							mustAddMetaView = true;				
						}
						else {
							duplicateAnnotationForView(collection.getName(), ViewAction.class, metaView.getName());
						}
					}
				}				
			}
			
												
			// EditOnly
			if (element.isAnnotationPresent(EditOnly.class)) {
				EditOnly editOnly = element.getAnnotation(EditOnly.class);
				if (isForView(metaView, editOnly.forViews(), editOnly.notForViews())) {					
					collectionView.setEditOnly(true);
					mustAddMetaView = true;
				}
			}

			// AsEmbedded
			if (element.isAnnotationPresent(AsEmbedded.class)) {
				AsEmbedded asAggregate = element.getAnnotation(AsEmbedded.class);
				if (isForView(metaView, asAggregate.forViews(), asAggregate.notForViews())) {					
					collectionView.setAsAggregate(true);
					mustAddMetaView = true;
				}
			}
			
			// ReadOnly
			if (element.isAnnotationPresent(ReadOnly.class)) {
				ReadOnly readOnly = element.getAnnotation(ReadOnly.class);
				if (isForView(metaView, readOnly.forViews(), readOnly.notForViews())) {					
					collectionView.setReadOnly(true);
					mustAddMetaView = true;
				}
			}
			
			// Editor
			if (processEditorAnnotation(element, metaView, collectionView)) {
				mustAddMetaView = true;
			}
			
			// NoCreate
			if (element.isAnnotationPresent(NoCreate.class)) {
				NoCreate noCreate = element.getAnnotation(NoCreate.class);
				if (isForView(metaView, noCreate.forViews(), noCreate.notForViews())) {					
					collectionView.setCreateReference(false);
					mustAddMetaView = true;
				}
			}
			
			// NoModify
			if (element.isAnnotationPresent(NoModify.class)) {
				NoModify noModify = element.getAnnotation(NoModify.class);
				if (isForView(metaView, noModify.forViews(), noModify.notForViews())) {					
					collectionView.setModifyReference(false);
					mustAddMetaView = true;
				}
			}
			
			// SearchListCondition
			if (element.isAnnotationPresent(SearchListCondition.class)) {
				SearchListCondition searchListCondition = element.getAnnotation(SearchListCondition.class);
				if (isForView(metaView, searchListCondition.forViews(), searchListCondition.notForViews())) {
					collectionView.setSearchListCondition(searchListCondition.value());
					mustAddMetaView = true;
				}
			}			
			// SearchListConditions
			if (element.isAnnotationPresent(SearchListConditions.class)) {
				SearchListCondition[] searchListConditions = element.getAnnotation(SearchListConditions.class).value();
				for (SearchListCondition searchListCondition : searchListConditions) {
					if (isForView(metaView, searchListCondition.forViews(), searchListCondition.notForViews())) {
						collectionView.setSearchListCondition(searchListCondition.value());
						mustAddMetaView = true;
					}
				}
			}			

			// Path
			if (element.isAnnotationPresent(Tree.class)) {
				Tree path = element.getAnnotation(Tree.class);
				if (isForView(metaView, path.forViews(), path.notForViews())) {
					collectionView.setPath(path);
					mustAddMetaView = true;
				}
			}
			
			// Paths
			if (element.isAnnotationPresent(Trees.class)) {
				Tree[] paths = element.getAnnotation(Trees.class).value();
				for (Tree path : paths) {
					if (isForView(metaView, path.forViews(), path.notForViews())) {
						collectionView.setPath(path);
						mustAddMetaView = true;
					}
				}
			}
			
			// Collapsed
			if (element.isAnnotationPresent(Collapsed.class)) {						
				Collapsed collapsed = element.getAnnotation(Collapsed.class);
				if (isForView(metaView, collapsed.forViews(), collapsed.notForViews())) {
					collectionView.setCollapsed(true);
					mustAddMetaView = true;				
				}
			}
			
			if (mustAddMetaView) {				
				metaView.addMetaViewCollection(collectionView);
			}

		} 			
		
		
		// Not applicable
		if (collection.isElementCollection()) {			
			Class [] onlyForCollectionAnnotations = {
				CollectionView.class, CollectionViews.class,
				RowStyle.class, RowStyles.class, 
				EditAction.class, EditActions.class, 
				ViewAction.class, ViewActions.class, 
				NewAction.class, NewActions.class,
				AddAction.class, AddActions.class, 
				SaveAction.class, SaveActions.class, 
				HideDetailAction.class, HideDetailActions.class, 
				RemoveAction.class, RemoveActions.class, 
				ListAction.class, ListActions.class,
				ListSubcontroller.class, ListSubcontrollers.class,
				RowAction.class, RowActions.class, 
				DetailAction.class, DetailActions.class, 
				OnSelectElementAction.class, OnSelectElementActions.class, 
				Tree.class, Trees.class 					
			};
			notApply(element, collection.getName(), onlyForCollectionAnnotations, "@OneToMany/@ManyToMany collections");			
			Class [] forReferencesAndCollectionAnnotations = {
				NoCreate.class, 
				NoModify.class, 
				AsEmbedded.class, 
				SearchListCondition.class, SearchListConditions.class 
			};
			notApply(element, collection.getName(), forReferencesAndCollectionAnnotations, "references & @OneToMany/@ManyToMany collections");
		}
		if (element.isAnnotationPresent(Action.class)) {
			notApply(collection.getName(), Action.class, "properties & references");
		}
		if (element.isAnnotationPresent(Actions.class)) {
			notApply(collection.getName(), Actions.class, "properties & references");
		}		
		if (element.isAnnotationPresent(Hidden.class)) {
			notApply(collection.getName(), Hidden.class, "properties");
		}
		if (element.isAnnotationPresent(DescriptionsList.class)) {
			notApply(collection.getName(), DescriptionsList.class, "references");
		}
		if (element.isAnnotationPresent(DescriptionsLists.class)) {
			notApply(collection.getName(), DescriptionsLists.class, "references");
		}		
		if (element.isAnnotationPresent(Required.class)) {
			notApply(collection.getName(), Required.class, "properties & references");
		}
		if (element.isAnnotationPresent(Stereotype.class)) {
			notApply(collection.getName(), Stereotype.class, "properties");
		}				
		if (element.isAnnotationPresent(DefaultValueCalculator.class)) {
			notApply(collection.getName(), DefaultValueCalculator.class, "properties & references");
		}
		if (element.isAnnotationPresent(OnChange.class)) {
			notApply(collection.getName(), OnChange.class, "properties & references");
		}
		if (element.isAnnotationPresent(OnChanges.class)) {
			notApply(collection.getName(), OnChanges.class, "properties & references");
		}				
		if (element.isAnnotationPresent(SearchAction.class)) {
			notApply(collection.getName(), SearchAction.class, "references");
		}
		if (element.isAnnotationPresent(SearchActions.class)) {
			notApply(collection.getName(), SearchActions.class, "references");
		}		
		if (element.isAnnotationPresent(ReferenceView.class)) {
			log.warn(XavaResources.getString("for_collection_CollectionView_instead_of_ReferenceView"));
			notApply(collection.getName(), ReferenceView.class, "references");
		}
		if (element.isAnnotationPresent(ReferenceViews.class)) {
			log.warn(XavaResources.getString("for_collection_CollectionViews_instead_of_ReferenceViews"));
			notApply(collection.getName(), ReferenceViews.class, "references");
		}				
		if (element.isAnnotationPresent(NoFrame.class)) {
			notApply(collection.getName(), NoFrame.class, "references");
		}
		if (element.isAnnotationPresent(Depends.class)) {
			notApply(collection.getName(), Depends.class, "properties");
		}
		if (element.isAnnotationPresent(LabelFormat.class)) {
			notApply(collection.getName(), LabelFormat.class, "properties & references");
		}
		if (element.isAnnotationPresent(LabelFormats.class)) {
			notApply(collection.getName(), LabelFormats.class, "properties & references");
		}				
		if (element.isAnnotationPresent(PropertyValidator.class)) {
			notApply(collection.getName(), PropertyValidator.class, "properties");
		}
		if (element.isAnnotationPresent(PropertyValidators.class)) {
			notApply(collection.getName(), PropertyValidators.class, "properties");
		}
		if (element.isAnnotationPresent(NoSearch.class)) {
			notApply(collection.getName(), NoSearch.class, "references");
		}		
		if (element.isAnnotationPresent(DisplaySize.class)) {
			notApply(collection.getName(), DisplaySize.class, "properties");
		}
		if (element.isAnnotationPresent(DisplaySizes.class)) {
			notApply(collection.getName(), DisplaySizes.class, "properties");
		}
		if (element.isAnnotationPresent(SearchKey.class)) {
			notApply(collection.getName(), SearchKey.class, "properties");
		}														
		if (element.isAnnotationPresent(OnChangeSearch.class)) {
			notApply(collection.getName(), OnChangeSearch.class, "references");
		}
		if (element.isAnnotationPresent(OnChangeSearchs.class)) {
			notApply(collection.getName(), OnChangeSearchs.class, "references");
		}		
		if (element.isAnnotationPresent(LabelStyle.class)) {
			notApply(collection.getName(), LabelStyle.class, "properties & references");
		}
	}


	private void notApply(AnnotatedElement element, String name, Class[] annotations, String validMemberTypes) { 
		for (Class annotation: annotations) {
			if (element.isAnnotationPresent(annotation)) {
				notApply(name, annotation, validMemberTypes);
			}
		}
	}


	private MetaRowStyle toMetaRowStyle(RowStyle rowStyle) { 
		MetaRowStyle metaRowStyle = new MetaRowStyle();
		metaRowStyle.setProperty(rowStyle.property());
		metaRowStyle.setStyle(rowStyle.style());
		metaRowStyle.setValue(rowStyle.value());
		return metaRowStyle;
	}
	
	private String wrapWithDollars(String orderBy) {
		StringTokenizer st = new StringTokenizer(orderBy, " ,", true);
		StringBuffer result = new StringBuffer();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (" ".equals(token) || ",".equals(token) || 
					"desc".equalsIgnoreCase(token) || 
					"asc".equalsIgnoreCase(token))
			{
				result.append(token);
			}
			else {
				result.append("${");
				result.append(token);
				result.append('}');
			}
		}		
		return result.toString();
	}


	private void processAnnotations(MetaReference ref, AnnotatedElement element) throws XavaException {
		if (element == null) return;
		
		if (ref.getName().contains("_")) { 
			log.warn(XavaResources.getString("underscore_not_allowed_for_reference_name", ref.getName()));
		}

		// key
		if (element.isAnnotationPresent(Id.class)) {
			ref.setKey(true);
		}
		else if (element.isAnnotationPresent(EmbeddedId.class)) { 
			ref.setKey(true);
			MetaModel embeddedIdMetaModel = ref.getMetaModelReferenced();
			ref.getMetaModel().setPOJOKeyClass(embeddedIdMetaModel.getPOJOClass());			
			for (Object metaProperty: embeddedIdMetaModel.getMetaProperties()) {
				((MetaProperty) metaProperty).setKey(true);
			}
			for (Object metaReference: embeddedIdMetaModel.getMetaReferences()) {
				((MetaReference) metaReference).setKey(true);
			}			
		}
		
		// SearchKey
		if (element.isAnnotationPresent(SearchKey.class)) { 						
			ref.setSearchKey(true);
		} 

		// Required
		if (element.isAnnotationPresent(Required.class)) {						
			ref.setRequired(true);
		}
		else if (element.isAnnotationPresent(ManyToOne.class)) {
			ManyToOne manyToOne = element.getAnnotation(ManyToOne.class);
			ref.setRequired(!manyToOne.optional());
			ref.setAggregate(false); 
		}
		
		// Default value calculator
		if (element.isAnnotationPresent(DefaultValueCalculator.class)) {
			DefaultValueCalculator calculator = element.getAnnotation(DefaultValueCalculator.class);
			MetaCalculator metaCalculator = new MetaCalculator();
			metaCalculator.setClassName(calculator.value().getName());
			for (PropertyValue put: calculator.properties()) {
				metaCalculator.addMetaSet(toMetaSet(put));
			}
			ref.setMetaCalculatorDefaultValue(metaCalculator);
		}
		
		// OnChange 
		if (element.isAnnotationPresent(OnChange.class)) {
			OnChange onChange = element.getAnnotation(OnChange.class);
			MetaPropertyView propertyView = new MetaPropertyView();			
			String lastKeyProperty = (String) XCollections.last(ref.getMetaModelReferenced().getKeyPropertiesNames());			
			propertyView.setPropertyName(ref.getName() + "." + lastKeyProperty);
			propertyView.setOnChangeActionClassName(onChange.value().getName());
			for (Object oview: ref.getMetaModel().getMetaViews()) {
				MetaView view = (MetaView) oview;
				if (isForView(view, onChange.forViews(), onChange.notForViews())) {
					view.addMetaViewProperty(propertyView);
				}
			}
		}				
		if (element.isAnnotationPresent(OnChanges.class)) {
			OnChange [] onChanges = element.getAnnotation(OnChanges.class).value();				
			for (OnChange onChange: onChanges) {
				MetaPropertyView propertyView = new MetaPropertyView();
				String lastKeyProperty = (String) XCollections.last(ref.getMetaModelReferenced().getKeyPropertiesNames());
				propertyView.setPropertyName(ref.getName() + "." + lastKeyProperty);
				propertyView.setOnChangeActionClassName(onChange.value().getName());
				for (Object oview: ref.getMetaModel().getMetaViews()) {
					MetaView view = (MetaView) oview;
					if (isForView(view, onChange.forViews(), onChange.notForViews())) {
						view.addMetaViewProperty(propertyView);
					}
				}				
			}			
		}		
				
		// for View
		for (Object oMetaView: ref.getMetaModel().getMetaViews()) {
			MetaView metaView = (MetaView) oMetaView;			
			MetaReferenceView referenceView = new MetaReferenceView();
			referenceView.setReferenceName(ref.getName());						
			
			boolean mustAddMetaView = false;
			
			// Action 
			mustAddMetaView = addAction(element, metaView, referenceView, mustAddMetaView);			

			// DescriptionsList
			if (element.isAnnotationPresent(DescriptionsList.class)) {
				DescriptionsList descriptionsList = element.getAnnotation(DescriptionsList.class);
				if (isForView(metaView, descriptionsList.forViews(), descriptionsList.notForViews())) {
					referenceView.setMetaDescriptionsList(createMetaDescriptionsList(descriptionsList));									
					mustAddMetaView = true;				
				}
			}								
			if (element.isAnnotationPresent(DescriptionsLists.class)) {
				DescriptionsList [] descriptionsLists = element.getAnnotation(DescriptionsLists.class).value();				
				for (DescriptionsList descriptionsList: descriptionsLists) {
					if (isForView(metaView, descriptionsList.forViews(), descriptionsList.notForViews())) {
						referenceView.setMetaDescriptionsList(createMetaDescriptionsList(descriptionsList));											
						mustAddMetaView = true;				
					}
				}					
			}
									
			// SearchAction
			if (element.isAnnotationPresent(SearchAction.class)) {
				SearchAction action = element.getAnnotation(SearchAction.class);
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					MetaSearchAction metaSearch = new MetaSearchAction();
					metaSearch.setActionName(action.value());
					referenceView.setMetaSearchAction(metaSearch);				
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(SearchActions.class)) {
				SearchAction [] searchActions = element.getAnnotation(SearchActions.class).value();				
				for (SearchAction action: searchActions) {
					if (isForView(metaView, action.forViews(), action.notForViews())) {
						MetaSearchAction metaSearch = new MetaSearchAction();
						metaSearch.setActionName(action.value());
						referenceView.setMetaSearchAction(metaSearch);				
						mustAddMetaView = true;				
					}
				}					
			}			
			
			// ReferenceView
			if (element.isAnnotationPresent(ReferenceView.class)) {
				ReferenceView viewName = element.getAnnotation(ReferenceView.class);
				if (isForView(metaView, viewName.forViews(), viewName.notForViews())) {
					referenceView.setViewName(viewName.value());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(ReferenceViews.class)) {
				ReferenceView [] viewNames = element.getAnnotation(ReferenceViews.class).value();				
				for (ReferenceView viewName: viewNames) {
					if (isForView(metaView, viewName.forViews(), viewName.notForViews())) {
						referenceView.setViewName(viewName.value());
						mustAddMetaView = true;				
					}
				}					
			}			
			
			// NoFrame
			if (element.isAnnotationPresent(NoFrame.class)) {
				NoFrame noFrame = element.getAnnotation(NoFrame.class);
				if (isForView(metaView, noFrame.forViews(), noFrame.notForViews())) {
					referenceView.setFrame(false);
					mustAddMetaView = true;				
				}
			}
						
			// NoCreate
			if (element.isAnnotationPresent(NoCreate.class)) {
				NoCreate noCreate = element.getAnnotation(NoCreate.class);
				if (isForView(metaView, noCreate.forViews(), noCreate.notForViews())) {
					referenceView.setCreate(false);
					mustAddMetaView = true;				
				}
			}
			
			// NoModify
			if (element.isAnnotationPresent(NoModify.class)) {
				NoModify noModify = element.getAnnotation(NoModify.class);
				if (isForView(metaView, noModify.forViews(), noModify.notForViews())) {
					referenceView.setModify(false);
					mustAddMetaView = true;				
				}
			}
			
			// NoSearch
			if (element.isAnnotationPresent(NoSearch.class)) {
				NoSearch noSearch = element.getAnnotation(NoSearch.class);
				if (isForView(metaView, noSearch.forViews(), noSearch.notForViews())) {
					referenceView.setSearch(false);
					mustAddMetaView = true;				
				}
			}
												
			// LabelFormat
			if (element.isAnnotationPresent(LabelFormat.class)) {
				LabelFormat labelFormat = element.getAnnotation(LabelFormat.class);
				if (isForView(metaView, labelFormat.forViews(), labelFormat.notForViews())) {
					
					MetaDescriptionsList metaDescriptionsList = referenceView.getMetaDescriptionsList();
					if (metaDescriptionsList != null) {
						metaDescriptionsList.setLabelFormat(labelFormat.value().ordinal());
					}
					else {						
						log.warn(XavaResources.getString("label_format_for_reference_requires_descriptons_list", ref.getName()));
					}
					mustAddMetaView = true;				
				}
			}					
			if (element.isAnnotationPresent(LabelFormats.class)) {
				LabelFormat [] labelFormats = element.getAnnotation(LabelFormats.class).value();				
				for (LabelFormat labelFormat: labelFormats) {
					if (isForView(metaView, labelFormat.forViews(), labelFormat.notForViews())) {
						MetaDescriptionsList metaDescriptionsList = referenceView.getMetaDescriptionsList();
						if (metaDescriptionsList != null) {
							metaDescriptionsList.setLabelFormat(labelFormat.value().ordinal());
						}
						else {
							log.warn(XavaResources.getString("label_format_for_reference_requires_descriptons_list", ref.getName()));							
						}						
					}
				}					
			}
			
			// LabelStyle
			if (element.isAnnotationPresent(LabelStyle.class)) {
				LabelStyle labelStyle = element.getAnnotation(LabelStyle.class);
				MetaDescriptionsList metaDescriptionsList = referenceView.getMetaDescriptionsList();
				if (isForView(metaView, labelStyle.forViews(), labelStyle.notForViews())) {
					metaDescriptionsList.setLabelStyle(labelStyle.value());
					mustAddMetaView = true;
				}
			}
			if (element.isAnnotationPresent(LabelStyles.class)){
				LabelStyle [] labelStyles = element.getAnnotation(LabelStyles.class).value();
				for(LabelStyle labelStyle : labelStyles){
					MetaDescriptionsList metaDescriptionsList = referenceView.getMetaDescriptionsList();
					if (isForView(metaView, labelStyle.forViews(), labelStyle.notForViews())) {
						metaDescriptionsList.addLabelStyle(labelStyle.value());
						mustAddMetaView = true;
					}
				}
			}
			
			if (processEditorAnnotation(element, metaView, referenceView)) {
				mustAddMetaView = true;
			}
			
			// AsAggregate
			if (element.isAnnotationPresent(AsEmbedded.class)) {
				AsEmbedded asAggregate = element.getAnnotation(AsEmbedded.class);
				if (isForView(metaView, asAggregate.forViews(), asAggregate.notForViews())) {
					referenceView.setAsAggregate(true);
					mustAddMetaView = true;				
				}
			}
			
			// ReadOnly
			if (element.isAnnotationPresent(ReadOnly.class)) {
				ReadOnly readOnly = element.getAnnotation(ReadOnly.class);
				if (isForView(metaView, readOnly.forViews(), readOnly.notForViews())) {
					referenceView.setReadOnly(true);
					mustAddMetaView = true;		
					referenceView.setReadOnlyOnCreate(readOnly.onCreate()); 
				}
			}	
			 
			// OnChangeSearch
			if (element.isAnnotationPresent(OnChangeSearch.class)) {
				OnChangeSearch onChangeSearch = element.getAnnotation(OnChangeSearch.class);
				if (isForView(metaView, onChangeSearch.forViews(), onChangeSearch.notForViews())) {
					referenceView.setOnChangeSearchActionClassName(onChangeSearch.value().getName());
					mustAddMetaView = true;				
				}
			}
			if (element.isAnnotationPresent(OnChangeSearchs.class)) {
				OnChangeSearch [] onChangeSearchs = element.getAnnotation(OnChangeSearchs.class).value();				
				for (OnChangeSearch onChangeSearch: onChangeSearchs) {
					if (isForView(metaView, onChangeSearch.forViews(), onChangeSearch.notForViews())) {
						referenceView.setOnChangeSearchActionClassName(onChangeSearch.value().getName());
						mustAddMetaView = true;				
					}
				}					
			}
			
			// SearchListCondition
			if (element.isAnnotationPresent(SearchListCondition.class)) {
				SearchListCondition searchListCondition = element.getAnnotation(SearchListCondition.class);
				if (isForView(metaView, searchListCondition.forViews(), searchListCondition.notForViews())) {
					referenceView.setSearchListCondition(searchListCondition.value());
					mustAddMetaView = true;
				}
			}
			if (element.isAnnotationPresent(SearchListConditions.class)) {
				SearchListCondition[] searchListConditions = element.getAnnotation(SearchListConditions.class).value();
				for (SearchListCondition searchListCondition : searchListConditions) {
					if (isForView(metaView, searchListCondition.forViews(), searchListCondition.notForViews())) {
						referenceView.setSearchListCondition(searchListCondition.value());
						mustAddMetaView = true;
					}
				}
			}
			
			// Collapsed
			if (element.isAnnotationPresent(Collapsed.class)) {						
				Collapsed collapsed = element.getAnnotation(Collapsed.class);
				if (isForView(metaView, collapsed.forViews(), collapsed.notForViews())) {
					referenceView.setCollapsed(true);
					mustAddMetaView = true;				
				}
			}
			
			if (mustAddMetaView) {	
				metaView.addMetaViewReference(referenceView);
			}
			
		}


		// Not applicable
		if (element.isAnnotationPresent(ListProperties.class)) {
			notApply(ref.getName(), ListProperties.class, "collections");
		}
		if (element.isAnnotationPresent(ListsProperties.class)) {
			notApply(ref.getName(), ListsProperties.class, "collections");
		}		
		if (element.isAnnotationPresent(Hidden.class)) {
			notApply(ref.getName(), Hidden.class, "properties");
		}
		if (element.isAnnotationPresent(Stereotype.class)) {
			notApply(ref.getName(), Stereotype.class, "properties");
		}
		if (element.isAnnotationPresent(CollectionView.class)) {
			notApply(ref.getName(), CollectionView.class, "collections");
		}
		if (element.isAnnotationPresent(CollectionViews.class)) {
			notApply(ref.getName(), CollectionViews.class, "collections");
		}						
		if (element.isAnnotationPresent(ListAction.class)) {
			notApply(ref.getName(), ListAction.class, "collections");
		}
		if (element.isAnnotationPresent(ListActions.class)) {
			notApply(ref.getName(), ListActions.class, "collections");
		}		
		if (element.isAnnotationPresent(ListSubcontroller.class)) {
			notApply(ref.getName(), ListSubcontroller.class, "collections");
		}
		if (element.isAnnotationPresent(ListSubcontrollers.class)) {
			notApply(ref.getName(), ListSubcontrollers.class, "collections");
		}		
		if (element.isAnnotationPresent(RowAction.class)) {
			notApply(ref.getName(), RowAction.class, "collections");
		}
		if (element.isAnnotationPresent(RowActions.class)) {
			notApply(ref.getName(), RowActions.class, "collections");
		}		
		if (element.isAnnotationPresent(EditOnly.class)) {
			notApply(ref.getName(), EditOnly.class, "collections");
		}
		if (element.isAnnotationPresent(Depends.class)) {
			notApply(ref.getName(), Depends.class, "properties");
		}
		if (element.isAnnotationPresent(PropertyValidator.class)) {
			notApply(ref.getName(), PropertyValidator.class, "properties");
		}
		if (element.isAnnotationPresent(PropertyValidators.class)) {
			notApply(ref.getName(), PropertyValidators.class, "properties");
		}														
		if (element.isAnnotationPresent(EditAction.class)) {
			notApply(ref.getName(), EditAction.class, "collections");
		}				
		if (element.isAnnotationPresent(EditActions.class)) {
			notApply(ref.getName(), EditActions.class, "collections");
		}
		if (element.isAnnotationPresent(DetailAction.class)) {
			notApply(ref.getName(), DetailAction.class, "collections");
		}
		if (element.isAnnotationPresent(DetailActions.class)) {
			notApply(ref.getName(), DetailActions.class, "collections");
		}
		if (element.isAnnotationPresent(ViewAction.class)) {
			notApply(ref.getName(), ViewAction.class, "collections");
		}												
		if (element.isAnnotationPresent(ViewActions.class)) {
			notApply(ref.getName(), ViewActions.class, "collections");
		}										
		if (element.isAnnotationPresent(DisplaySize.class)) {
			notApply(ref.getName(), DisplaySize.class, "properties");
		}
		if (element.isAnnotationPresent(DisplaySizes.class)) {
			notApply(ref.getName(), DisplaySizes.class, "properties");
		}
		if (element.isAnnotationPresent(Condition.class)) {
			notApply(ref.getName(), Condition.class, "collections");
		}
		if (element.isAnnotationPresent(HideDetailAction.class)) {
			notApply(ref.getName(), HideDetailAction.class, "collections");
		}
		if (element.isAnnotationPresent(HideDetailActions.class)) {
			notApply(ref.getName(), HideDetailActions.class, "collections");
		}				
		if (element.isAnnotationPresent(NewAction.class)) {
			notApply(ref.getName(), NewAction.class, "collections");
		}				
		if (element.isAnnotationPresent(NewActions.class)) {
			notApply(ref.getName(), NewActions.class, "collections");
		}
		if (element.isAnnotationPresent(RemoveAction.class)) {
			notApply(ref.getName(), RemoveAction.class, "collections");
		}				
		if (element.isAnnotationPresent(RemoveActions.class)) {
			notApply(ref.getName(), RemoveActions.class, "collections");
		}				
		if (element.isAnnotationPresent(RemoveSelectedAction.class)) {
			notApply(ref.getName(), RemoveSelectedAction.class, "collections");
		}						
		if (element.isAnnotationPresent(RemoveSelectedActions.class)) {
			notApply(ref.getName(), RemoveSelectedActions.class, "collections");
		}
		if (element.isAnnotationPresent(SaveAction.class)) {
			notApply(ref.getName(), SaveAction.class, "collections");
		}
		if (element.isAnnotationPresent(SaveActions.class)) {
			notApply(ref.getName(), SaveActions.class, "collections");
		}
		if (element.isAnnotationPresent(XOrderBy.class)) {
			notApply(ref.getName(), XOrderBy.class, "collections");
		}
		if (element.isAnnotationPresent(RowStyle.class)) {
			notApply(ref.getName(), RowStyle.class, "collections");
		}
		if (element.isAnnotationPresent(RowStyles.class)) {
			notApply(ref.getName(), RowStyles.class, "collections");
		}						
		if (element.isAnnotationPresent(OnSelectElementAction.class)) {
			notApply(ref.getName(), OnSelectElementAction.class, "collections");
		}
		if (element.isAnnotationPresent(OnSelectElementActions.class)) {
			notApply(ref.getName(), OnSelectElementActions.class, "collections");
		}		
		if (element.isAnnotationPresent(Tree.class)) {
			notApply(ref.getName(), Tree.class, "collections");
		}
		if (element.isAnnotationPresent(Trees.class)) {
			notApply(ref.getName(), Trees.class, "collections");
		}
	}


	private boolean processEditorAnnotation(AnnotatedElement element,
			MetaView metaView, MetaMemberView memberView) {
		boolean mustAddMetaView = false;
		if (element.isAnnotationPresent(Editor.class)) {
			Editor editor = element.getAnnotation(Editor.class);
			if (isForView(metaView, editor.forViews(), editor.notForViews())) {
				memberView.setEditor(editor.value());
				mustAddMetaView = true;				
			}
		}
		if (element.isAnnotationPresent(Editors.class)) {
			Editor [] editors = element.getAnnotation(Editors.class).value();				
			for (Editor editor: editors) {
				if (isForView(metaView, editor.forViews(), editor.notForViews())) {
					memberView.setEditor(editor.value());
					mustAddMetaView = true;				
				}
			}					
		}
		return mustAddMetaView;
	}


	private boolean addAction(AnnotatedElement element, MetaView metaView, MetaMemberView memberView, boolean mustAddMetaView) {
		if (element.isAnnotationPresent(Action.class)) {
			Action action = element.getAnnotation(Action.class);
			if (isForView(metaView, action.forViews(), action.notForViews())) {
				memberView.addActionName(action.value());
				if (action.alwaysEnabled()) {
					memberView.addAlwaysEnabledActionName(action.value());
				}
				mustAddMetaView = true;				
			}
		}					
		if (element.isAnnotationPresent(Actions.class)) {
			Action [] actions = element.getAnnotation(Actions.class).value();				
			for (Action action: actions) {
				if (isForView(metaView, action.forViews(), action.notForViews())) {
					memberView.addActionName(action.value());
					if (action.alwaysEnabled()) {
						memberView.addAlwaysEnabledActionName(action.value());
					}
					mustAddMetaView = true;
				}
			}					
		}
		return mustAddMetaView;
	}

	private MetaDescriptionsList createMetaDescriptionsList(DescriptionsList descriptionsList) {
		MetaDescriptionsList metaDescriptionList = new MetaDescriptionsList();
		metaDescriptionList.setDescriptionPropertiesNames(descriptionsList.descriptionProperties());
		metaDescriptionList.setDepends(descriptionsList.depends());
		metaDescriptionList.setCondition(descriptionsList.condition());
		metaDescriptionList.setOrderByKey(descriptionsList.orderByKey());
		metaDescriptionList.setOrder(descriptionsList.order());
		metaDescriptionList.setShowReferenceView(descriptionsList.showReferenceView());
		metaDescriptionList.setForTabs(descriptionsList.forTabs());
		metaDescriptionList.setNotForTabs(descriptionsList.notForTabs());
		if (!descriptionsList.filter().equals(VoidFilter.class)) {
			MetaFilter metaFilter = new MetaFilter();
			metaFilter.setClassName(descriptionsList.filter().getName());
			metaDescriptionList.setMetaFilter(metaFilter);
		}
		return metaDescriptionList;
	}
	
	private void processAnnotations(MetaModel metaModel, AnnotatedElement element) throws XavaException {		
		if (element.isAnnotationPresent(EntityValidator.class)) {			
			EntityValidator validator = element.getAnnotation(EntityValidator.class);
			addEntityValidator(metaModel, validator);
		}
		if (element.isAnnotationPresent(EntityValidators.class)) {
			EntityValidator [] validators = element.getAnnotation(EntityValidators.class).value();
			for (EntityValidator validator: validators) {				
				addEntityValidator(metaModel, validator);				
			}
		}
		if (element.isAnnotationPresent(RemoveValidator.class)) {			
			RemoveValidator validator = element.getAnnotation(RemoveValidator.class);
			addRemoveValidator(metaModel, validator);
		}
		if (element.isAnnotationPresent(RemoveValidators.class)) {
			RemoveValidator [] validators = element.getAnnotation(RemoveValidators.class).value();
			for (RemoveValidator validator: validators) {				
				addRemoveValidator(metaModel, validator);				
			}
		}		
	}

	private void addEntityValidator(MetaModel metaModel, EntityValidator validator) {
		MetaValidator metaValidator = createEntityValidator(validator);
		metaModel.addMetaValidator(metaValidator);
	}


	/**
	 * Creates a MetaValidator for entity validation from a EntityValidator annotation. <p>
	 */	
	public static MetaValidator createEntityValidator(EntityValidator validator) { 
		MetaValidator metaValidator = new MetaValidator();
		metaValidator.setClassName(validator.value().getName());
		metaValidator.setMessage(filterMessage(validator.message())); 
		for (PropertyValue put: validator.properties()) {
			metaValidator.addMetaSet(toMetaSet(put));
		}
		metaValidator.setOnlyOnCreate(validator.onlyOnCreate());
		return metaValidator;
	}
	
	private void addRemoveValidator(MetaModel metaModel, RemoveValidator validator) {
		MetaValidator metaValidator = new MetaValidator();
		metaValidator.setClassName(validator.value().getName());
		for (PropertyValue put: validator.properties()) {
			metaValidator.addMetaSet(toMetaSet(put));
		}		
		metaModel.addMetaValidatorRemove(metaValidator);
	}	
	
	private void addPropertyValidator(MetaProperty metaProperty, PropertyValidator validator) {
		MetaValidator metaValidator = createPropertyValidator(validator, metaProperty.getName(), metaProperty.getMetaModel().getName());
		metaProperty.addMetaValidator(metaValidator);
	}

	/**
	 * Creates a MetaValidator for property validation from a PropertyValidator annotation. <p>
	 */
	public static MetaValidator createPropertyValidator(PropertyValidator validator) { 
		return createPropertyValidator(validator, null, null);
	}

	/**
	 * Creates a MetaValidator for property validation from a PropertyValidator annotation. <p>
	 */
	private static MetaValidator createPropertyValidator(PropertyValidator validator, String property, String model) { 
		MetaValidator metaValidator = new MetaValidator();
		metaValidator.setClassName(validator.value().getName());
		metaValidator.setMessage(filterMessage(validator.message())); 
		for (PropertyValue put: validator.properties()) {
			if (property != null && (Is.emptyString(put.value()) || !Is.emptyString(put.from()))) {				
				if (XavaPreferences.getInstance().isFailOnAnnotationMisuse()) {			
					throw new XavaException("property_value_for_property_validator_incorrect", property, model);
				}
				log.warn(XavaResources.getString("property_value_for_property_validator_incorrect", property, model));
			}
			metaValidator.addMetaSet(toMetaSet(put));
		}
		metaValidator.setOnlyOnCreate(validator.onlyOnCreate());
		return metaValidator;
	}
	
	
	private Map<String, PropertyDescriptor> getPropertyDescriptors(Class pojoClass) throws IntrospectionException {
		BeanInfo info = Introspector.getBeanInfo(pojoClass);
		Map<String, PropertyDescriptor> result = new HashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor pd: info.getPropertyDescriptors()) {
			if (pd.getName().equals("metaClass")) continue; 
			result.put(pd.getName(), pd);				
		}
		return result;
	}
	
	private String getClassNameFor(String name) throws XavaException { 
		String className = getClassNameIfExists(name);
		if (className == null) throw new XavaException("not_jpa_entity_nor_transient_model", name);
		return className;
	}
	
	private String getClassNameIfExists(String name) throws XavaException { 
		try {
			// This first in order that works fine with XML components without persistence.xml
			String className = "org.openxava.session." + name;
			Class.forName(className);
			return className;
		}
		catch (ClassNotFoundException ex) {				
		}
		try {
			String className = "org.openxava.model.transients." + name;
			Class.forName(className);
			return className;
		}
		catch (ClassNotFoundException ex) {				
		}		
		String suffix = "." + name;
		// If it's a managed entity
		for (String className: getManagedClassNames()) {
			if (className.endsWith(suffix)) return className;
		}
		// Maybe it's not a managed entity, but a transient object used for UI generation
		String className = null;
		for (String packageName: getManagedClassPackages()) {
			className = packageName + name;			
			try {			
				Class.forName(className);
				return className;
			}
			catch (ClassNotFoundException ex) {				
			}
		}
		return null; 
	}
		
	private static Collection<String> getManagedClassPackages() {
		if (managedClassPackages == null) {
			managedClassPackages = new HashSet<String>();			
			for (String className: getManagedClassNames()) {
				try {				
					Class clazz = Class.forName(className);
					managedClassPackages.add(Strings.noLastToken(className, "."));
					clazz = clazz.getSuperclass();
					while ((clazz != null) && clazz.isAnnotationPresent(MappedSuperclass.class)) {
						managedClassPackages.add(Strings.noLastToken(clazz.getName(), "."));
						clazz = clazz.getSuperclass();
					}
				}
				catch (ClassNotFoundException ex) {				
				}
			}
		}
		return managedClassPackages;
	}


	/**
	 * Only for using from MetaApplication class. <p>
	 */
	public static Collection<String> friendMetaApplicationGetManagedClassNames() { 
		return managedClassNames = obtainManagedClassNamesFromFileClassPath(); // If we change this test if generatePortlets works with the database running
	}
	
	public static Collection<String> getManagedClassNames() {
		if (managedClassNames == null) {
			try {
				managedClassNames = obtainManagedClassNamesUsingJPA();
			}
			catch (Exception ex) {				
				// When no database connection is available, no session factory can
				// be created, but sometimes (maybe from junit test, or code generation)  
				// it's needed to parse the entities anyways, then we'll
				// try to obtain managed classes without hibernate		
				if (!XavaPreferences.getInstance().isHibernatePersistence()) { // If we work with Hibernate + XML components it's normal not to have a persistence.xml 
					// We always have to print the stack trace of ex, because the error can
					// be other than no connection, then the developer needs info for debug
					log.warn(XavaResources.getString("managed_classes_not_from_hibernate"), ex);
				}
				managedClassNames = obtainManagedClassNamesFromFileClassPath();
				if (managedClassNames.isEmpty() && !XavaPreferences.getInstance().isHibernatePersistence()) { // If we work with Hibernate + XML components it's normal not to have JPA entities 
					managedClassNames = null;
					if (ex instanceof RuntimeException) throw (RuntimeException) ex;
					else throw new RuntimeException(ex);
				}
			}
		}
		return managedClassNames;
	}
	
	private static Collection<String> obtainManagedClassNamesFromFileClassPath() { 
		Collection<String> classNames = new ArrayList<String>(); 
		URL url = getAnchorURL();		
		if (url != null) {			
			File baseClassPath=new File(Strings.change(Strings.noLastToken(url.getPath(), "/"), "%20", " "));
			fillManagedClassNamesFromFileClassPath(classNames, baseClassPath, null); 
		}
		else {			
			log.warn(XavaResources.getString("jpa_managed_classes_anchor_not_found", "xava.properties, application.xml, aplicacion.xml"));
		}
		return classNames;
	}
		
	private static void fillManagedClassNamesFromFileClassPath(Collection classNames, File dir, String base) {   
		File [] files = dir.listFiles();
		for (int i=0; i<files.length; i++ ) {
			File file = files[i];
			String basePackage = base == null?"":base + dir.getName() + ".";
			if (file.isDirectory()) {				 
				fillManagedClassNamesFromFileClassPath(classNames, file, basePackage);
			}
			else if (file.getName().endsWith(".class")) {				
				String modelName = file.getName().substring(0, file.getName().length() - ".class".length());
				String className = basePackage + modelName;
				try { 
					Class entityClass = Class.forName(className);
					if (entityClass.isAnnotationPresent(Entity.class)) {						
						classNames.add(className);
					}					
				}
				catch (ClassNotFoundException ex) {					
				}				
			}
		}		
	}

	private static URL getAnchorURL() {  
		URL url = getFileURL("xava.properties");		
		if (url == null) url = getFileURL("application.xml");
		if (url == null) url = getFileURL("aplicacion.xml");
		return url;
	}
	
	private static URL getFileURL(String file) {  
		try {
			for (Enumeration en=ClassLoader.getSystemResources(file); en.hasMoreElements(); ) {
				URL url = (URL) en.nextElement();
				if (url != null) {
					if ("file".equals(url.getProtocol())) {
						return url;
					}
				}
			}
			return null;
		}
		catch (Exception ex) {
			return null;
		}
	}

	private static Collection obtainManagedClassNamesUsingJPA() {		
		Collection<String> managedClassNames = new ArrayList<String>();
		EntityManager manager = XPersistence.createManager();
		for (ManagedType t: manager.getMetamodel().getManagedTypes()) {
			Class<?> clazz = t.getJavaType();
			if (clazz == null || clazz.isInterface()) continue;
			if (clazz.isAnnotationPresent(MappedSuperclass.class)) continue; 
			String className = clazz.getName();
			managedClassNames.add(className);
		}
		manager.close();
		return managedClassNames;
	}

	private void notApply(String memberName, Class annotation, String validMemberTypes) throws XavaException {
		if (XavaPreferences.getInstance().isFailOnAnnotationMisuse()) {			
			throw new XavaException("annotation_not_applicable", annotation.getName(), memberName, validMemberTypes);
		}
		log.warn(XavaResources.getString("annotation_not_applicable", annotation.getName(), memberName, validMemberTypes));
		
	}
	
	private void duplicateAnnotationForView(String memberName, Class annotation, String viewName) throws XavaException {
		if (XavaPreferences.getInstance().isFailOnAnnotationMisuse()) {
			throw new XavaException("duplicate_annotation_for_view", annotation.getName(), viewName, memberName);
		}
		log.warn(XavaResources.getString("duplicate_annotation_for_view", annotation.getName(), viewName, memberName));
	}
	
	private static MetaSet toMetaSet(PropertyValue propertyValue) {
		MetaSet metaSet = new MetaSet();		
		metaSet.setPropertyName(propertyValue.name()); 
		metaSet.setPropertyNameFrom(propertyValue.from());
		metaSet.setValue(propertyValue.value());
		return metaSet;
	}

	private boolean isCascade(CascadeType[] types) {
		for (CascadeType type: types) {
			if (type == CascadeType.ALL || type == CascadeType.REMOVE) return true;
		}		
		return false;
	}
	
	private boolean isForView(MetaView view, String forViews, String notForViews) {
		if (Is.emptyStringAll(forViews, notForViews)) return true;
		if (!Is.emptyString(forViews) && !Is.emptyString(notForViews)) {
			log.warn(XavaResources.getString("forViews_and_notForViews_not_compatible")); 
		}
		if (!Is.emptyString(forViews)) {
			StringTokenizer st = new StringTokenizer(forViews, ",");
			while (st.hasMoreTokens()) {
				String viewName = st.nextToken().trim();
				if (view.getName().equals(viewName)) return true;
				if (Is.emptyString(view.getName()) && "DEFAULT".equals(viewName)) return true;
			}
			return false;
		}
		else {
			StringTokenizer st = new StringTokenizer(notForViews, ",");
			while (st.hasMoreTokens()) {
				String viewName = st.nextToken().trim();
				if (view.getName().equals(viewName)) return false;
				if (Is.emptyString(view.getName()) && "DEFAULT".equals(viewName)) return false;
			}	
			return true;
		}				
	}
	
		
}
