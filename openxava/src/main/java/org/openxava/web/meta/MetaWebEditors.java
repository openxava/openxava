package org.openxava.web.meta;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.beanutils.*;
import org.apache.commons.collections.*;
import org.apache.commons.logging.*;
import org.openxava.model.meta.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.web.meta.xmlparse.*;



/**
 * 
 * @author Javier Paniza
 */

public class MetaWebEditors {
	
	private static Log log = LogFactory.getLog(MetaEditor.class);
		
	private static Map editorsByName; 
	private static Map editorsByType;
	private static Map editorsByStereotype;
	private static Map<String, MetaEditor> editorsByAnnotation; 
	private static Map editorsByModelProperty;
	private static Map editorsByReferenceModel;
	private static Map editorsByCollectionModel; 
	private static Map editorsByTabModel; 
	private static MetaEditor editorForReferences;
	private static MetaEditor editorForCollections;
	private static MetaEditor editorForElementCollections; 
	private static Collection<MetaEditor> editorsForTabs;
	private static Map<String, MetaEditor> editorsByMember;  
	
	public static void addMetaEditorForType(String type, MetaEditor editor) throws XavaException {
		if (editorsByType == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForType");
		}
		editorsByType.put(type, editor);		
	}
	
	public static void addMetaEditorForAnnotation(String annotation, MetaEditor editor) throws XavaException { 
		if (editorsByAnnotation == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForAnnotation");
		}
		editorsByAnnotation.put(annotation, editor);		
	}

	public static void addMetaEditorForReferenceModel(String model, MetaEditor editor) throws XavaException {
		if (editorsByReferenceModel == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForReferenceModel");
		}
		editorsByReferenceModel.put(model, editor);		
	}
	
	public static void addMetaEditorForCollectionModel(String model, MetaEditor editor) throws XavaException { 
		if (editorsByCollectionModel == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForCollectionModel");
		}
		editorsByCollectionModel.put(model, editor);		
	}
	
	public static void addMetaEditorForTabModel(String model, MetaEditor editor) throws XavaException { 
		if (editorsByTabModel == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForTabModel");
		}
		editorsByTabModel.put(model, editor);		
	}
	
	public static void addMetaEditorForStereotype(String stereotype, MetaEditor editor) throws XavaException {		
		if (editorsByStereotype == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForStereotype");
		}
		editorsByStereotype.put(stereotype, editor);		
	}
	
	public static void addMetaEditorForModelProperty(String property, String model, MetaEditor editor) throws XavaException {
		if (editorsByModelProperty == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForModelProperty");
		}
		editorsByModelProperty.put(createPropertyModelKey(property, model), editor);		
	}
	
	public static void addMetaEditor(MetaEditor editor) throws XavaException {
		if (editorsByModelProperty == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditor");
		}		
		if (!Is.emptyString(editor.getName())) {
			editorsByName.put(editor.getName(), editor);
		}
	}

	
	private static String createPropertyModelKey(String property, String model) {
		return model + "::" + property;
	}
	
	/**
	 * @return Null if no editor registered for the specified type
	 */
	public static MetaEditor getMetaEditorForType(String type)	throws XavaException {
		return (MetaEditor) getEditorsByType().get(type);
	}
	
	/**
	 * @return Null if no editor registered for the model used in references
	 */
	public static MetaEditor getMetaEditorForReferenceModel(String model)	throws XavaException {
		return (MetaEditor) getEditorsByReferenceModel().get(model);
	}
	
	private static MetaEditor getMetaEditorForCollectionModel(String model) throws XavaException { 
		return (MetaEditor) getEditorsByCollectionModel().get(model);
	}

	private static MetaEditor getMetaEditorForTabModel(String model) throws XavaException {  
		return (MetaEditor) getEditorsByTabModel().get(model);
	}	
	
	/**
	 * It's like getMetaEditorForType but extract the type of property. <p>
	 * 
	 * Also it considers valid-values and Enums. 
	 * 
	 * @return Null if no editor registered for the type of the specified property
	 */
	public static MetaEditor getMetaEditorForTypeOfProperty(MetaProperty p)	throws XavaException { 					
		String typeName = p.getType().getName();
		if (p.hasValidValues() && "int".equals(typeName)) typeName=EditorsParser.VALID_VALUES_TYPE;				
		MetaEditor r = (MetaEditor) getMetaEditorForType(typeName);
		if (r == null && p.hasValidValues()) {
			// If it's a valid-values and the type is not int we assume that is a Java 5 Enum 
			r = (MetaEditor) getMetaEditorForType("java.lang.Enum");
			if (r == null) r = (MetaEditor) getMetaEditorForType("Enum");
		}		
		return r;
	}
	
	// tmr public static MetaEditor getMetaEditorForAnnotation(MetaMember member)	throws XavaException {
	private static MetaEditor getMetaEditorForAnnotation(MetaMember member, String viewName)	throws XavaException { // tmr
		if (member.getMetaModel() == null) return null;
		if (!member.getMetaModel().isPOJOAvailable()) return null;
		// tmr String memberId = member.getMetaModel().getName() + "." + member.getSimpleName();
		String memberId = member.getMetaModel().getName() + ":" + viewName + ":" + member.getSimpleName(); // tmr
		if (editorsByMember != null && editorsByMember.containsKey(memberId)) {
			return editorsByMember.get(memberId);
		}		 
		Annotation[] annotations = member.getAnnotations(); 
		if (annotations != null) for (Annotation a: annotations) {
			MetaEditor editor = getEditorsByAnnotation().get(a.annotationType().getName());
			if (editor != null) {
				if (!isForViews(viewName, a)) continue; // tmr
				MetaEditor clonedEditor = null;				 
				for (Method m: a.annotationType().getMethods()) {
					// tmr if (Is.anyEqual(m.getName(), "equals", "toString", "hashCode", "annotationType")) continue;
					if (Is.anyEqual(m.getName(), "equals", "toString", "hashCode", "annotationType", "forViews", "notForViews")) continue; // tmr
					Object value = null;
					try {
						value = XObjects.execute(a, m.getName());
					} catch (Exception e) {
						log.warn(XavaResources.getString("impossible_get_value_annotation_attribute", m.getName(), a.annotationType().getName()), e);
						
					}
					if (clonedEditor == null) clonedEditor = editor.cloneMetaEditor();
					clonedEditor.addProperty(m.getName(), value.toString());
				}
				if (editorsByMember == null) editorsByMember = new HashMap<>();
				if (clonedEditor != null) editor = clonedEditor;
				editorsByMember.put(memberId, editor);
				return editor;
			}
		}
		return null;
	}
	

	/**
	 * @return Null if no editor registered for the specified stereotype
	 */
	public static MetaEditor getMetaEditorForStereotype(String stereotype)	throws XavaException {
		return (MetaEditor) getEditorsByStereotype().get(stereotype);
	}
	
	
	/**
	 * @return Null if no editor registered for the specified property/model
	 */
	public static MetaEditor getMetaEditorForModelProperty(String property, String model) throws XavaException {
		return (MetaEditor) getEditorsByModelProperty().get(createPropertyModelKey(property, model));
	}
	
	/**
	 * @return Null if no editor registered with the name
	 */
	public static MetaEditor getMetaEditorByName(String name) throws XavaException {
		return (MetaEditor) getEditorsByName().get(name);
	}	
		
	private synchronized static Map getEditorsByType() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)
		if (editorsByType == null) {
			init();
			EditorsParser.setupEditors();
		}
		return editorsByType;
	}
	
	private synchronized static Map<String, MetaEditor> getEditorsByAnnotation() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)
		if (editorsByAnnotation == null) {
			init();
			EditorsParser.setupEditors();
		}
		return editorsByAnnotation;
	}
	
	private synchronized static Map getEditorsByReferenceModel() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records) 
		if (editorsByReferenceModel == null) {
			init();
			EditorsParser.setupEditors();
		}
		return editorsByReferenceModel;
	}
	
	private synchronized static Map getEditorsByCollectionModel() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)  
		if (editorsByCollectionModel == null) {
			init();
			EditorsParser.setupEditors();
		}
		return editorsByCollectionModel;
	}
	
	private synchronized static Map getEditorsByTabModel() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)   
		if (editorsByTabModel == null) {
			init();
			EditorsParser.setupEditors();
		}
		return editorsByTabModel;
	}	
	
	
	private synchronized static Map getEditorsByStereotype() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)
		if (editorsByStereotype == null) {
			init();
			EditorsParser.setupEditors();								
		}	
		return editorsByStereotype;
	}
	
	private synchronized static Map getEditorsByModelProperty() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)		
		if (editorsByModelProperty == null) {
			init();
			EditorsParser.setupEditors();			
		}		
		return editorsByModelProperty;
	}
	
	private synchronized static Map getEditorsByName() throws XavaException { // synchronized needed for starting as first module a module with list that starts in detail (wihout records)
		if (editorsByName == null) {
			init();
			EditorsParser.setupEditors();
		}
		return editorsByName;
	}	
		
	private static void init() { 
		editorsByType = new HashMap();
		editorsByStereotype = new HashMap();
		editorsByAnnotation = new HashMap<>(); 
		editorsByModelProperty = new HashMap();
		editorsByName = new HashMap();
		editorsByReferenceModel = new HashMap(); 
		editorsByCollectionModel = new HashMap();
		editorsByTabModel = new HashMap(); 
		editorsForTabs = new ArrayList<MetaEditor>(); 
	}

	
	/**	 
	 * @return Not null
	 * @throws ElementNotFoundException If no editor for property	 
	 */
	// tmr public static MetaEditor getMetaEditorFor(MetaProperty p) throws ElementNotFoundException, XavaException {							
	private static MetaEditor getMetaEditorFor(MetaProperty p, String viewName) throws ElementNotFoundException, XavaException { // tmr
		if (p.hasMetaModel()) {			
			MetaEditor r = (MetaEditor) getMetaEditorForModelProperty(p.getName(), p.getMetaModel().getName());
			if (r != null) {				
				return r;				
			}
		}				
		if (p.hasStereotype()) {			
			MetaEditor r = (MetaEditor) getMetaEditorForStereotype(p.getStereotype());				
			if (r != null) {				
				return r;
			}
		}
		MetaEditor r = (MetaEditor) getMetaEditorForAnnotation(p, viewName);
		if (r != null) {
			return r;
		}
		r = (MetaEditor) getMetaEditorForTypeOfProperty(p);
		if (r == null) {
			throw new ElementNotFoundException("editor_not_found", p.getId());
		}		
		return r;
	}
	
	// tmr public static MetaEditor getMetaEditorFor(MetaReference ref) throws ElementNotFoundException, XavaException {							
	private static MetaEditor getMetaEditorFor(MetaReference ref, String viewName) throws ElementNotFoundException, XavaException { // tmr
		MetaEditor r = (MetaEditor) getMetaEditorForReferenceModel(ref.getReferencedModelName());	
		if (r == null && !ref.isUsedForCollection()) r = (MetaEditor) getMetaEditorForAnnotation(ref, viewName);
		if (r == null) {
			if (editorForReferences == null) {
				throw new ElementNotFoundException("editor_for_references_required");
			}
			return editorForReferences; 
		}		
		return r;
	}
	
	// tmr public static MetaEditor getMetaEditorFor(MetaCollection col) throws ElementNotFoundException, XavaException {
	private static MetaEditor getMetaEditorFor(MetaCollection col, String viewName) throws ElementNotFoundException, XavaException { // tmr
		MetaEditor r = (MetaEditor) getMetaEditorForCollectionModel(col.getMetaReference().getReferencedModelName());	
		if (r == null) r = (MetaEditor) getMetaEditorForAnnotation(col, viewName);
		if (r == null) {
			r = col.isElementCollection()?editorForElementCollections:editorForCollections;
			if (r == null) {
				throw new ElementNotFoundException(col.isElementCollection()?"editor_for_element_collections_required":"editor_for_collections_required");  
			}
		}		
		return r;
	}
	
	public static MetaEditor getMetaEditorFor(MetaTab tab) throws ElementNotFoundException, XavaException {
		MetaEditor r = (MetaEditor) getMetaEditorForTabModel(tab.getModelName());		
		if (r != null) return r;
		Collection<MetaEditor> editors = getMetaEditorsFor(tab);
		if (editors.isEmpty()) {
			throw new ElementNotFoundException("editor_for_tabs_required");
		}
		return editors.iterator().next();
	}
	
	public static Collection<MetaEditor> getMetaEditorsFor(MetaTab tab) throws ElementNotFoundException, XavaException {
		MetaEditor customEditor = (MetaEditor) getMetaEditorForTabModel(tab.getModelName());
		if (customEditor == null) {
			Collection<MetaEditor> newEditorsForTabs = new ArrayList<>(editorsForTabs);
			newEditorsForTabs.removeAll(getEditorsForTabToRemove(tab));
			return newEditorsForTabs;
		} else {
			Collection<MetaEditor> result = new ArrayList<MetaEditor>();
			result.add(customEditor);
			for (MetaEditor editor: editorsForTabs) {
				if (!"List".equals(editor.getName())) result.add(editor); 
			}
			result.removeAll(getEditorsForTabToRemove(tab));
			return result;
		}
	}	
	
	private static Collection<MetaEditor> getEditorsForTabToRemove(MetaTab tab) {
		List<MetaEditor> editorToDelete = new ArrayList<>();
		boolean contains = false;
		for (MetaEditor editor: editorsForTabs) {
			if (editor.hasHasSet() && !"List".equals(editor.getName())) {
				List<MetaProperty> mpList = new ArrayList<>(tab.getMetaModel().getMetaProperties());
				for (MetaProperty mp : mpList) {
					if (editor.stereotypeMatches(mp) ||
							editor.annotationMatches(editor, mp) ||
							editor.typeMatches(mp)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					editorToDelete.add(editor);
				} else {
					contains = false;
				}
			}
		}
		return editorToDelete;
	}

	/* tmr
	public static MetaEditor getMetaEditorFor(MetaMember member) throws ElementNotFoundException, XavaException {
		if (member instanceof MetaProperty) return getMetaEditorFor((MetaProperty) member);
		if (member instanceof MetaReference) return getMetaEditorFor((MetaReference) member);
		if (member instanceof MetaCollection) return getMetaEditorFor((MetaCollection) member); 
		throw new ElementNotFoundException("editor_not_found", member.getId());
	}
	*/
	
	// tmr ini
	public static MetaEditor getMetaEditorFor(MetaMember member, String viewName) throws ElementNotFoundException, XavaException {
		if (member instanceof MetaProperty) return getMetaEditorFor((MetaProperty) member, viewName);
		if (member instanceof MetaReference) return getMetaEditorFor((MetaReference) member, viewName);
		if (member instanceof MetaCollection) return getMetaEditorFor((MetaCollection) member, viewName); 
		throw new ElementNotFoundException("editor_not_found", member.getId());
	}	
	// tmr fin

	public static void addMetaEditorForReferences(MetaEditor editor) { 
		editorForReferences = editor; 		
	}
	
	public static void addMetaEditorForCollections(MetaEditor editor) {  
		editorForCollections = editor; 		
	}
	
	public static void addMetaEditorForElementCollections(MetaEditor editor) {   
		editorForElementCollections = editor; 		
	}	
	
	public static void addMetaEditorForTabs(MetaEditor editor) {   
		if (editorsForTabs == null) {
			throw new XavaException("only_from_parse", "MetaWebEditors.addMetaEditorForTabs");
		}
		if (Is.emptyString(editor.getName())) editorsForTabs.add(editor);
		else {
			BeanPropertyValueEqualsPredicate predicate =
				new BeanPropertyValueEqualsPredicate("name", editor.getName());
			if (!CollectionUtils.exists(editorsForTabs, predicate)) {
				editorsForTabs.add(editor);
			}
		}
	}
	
	private static boolean isForViews(String viewName, Annotation annotation) { // tmr
		String forViews = getAnnotationAttributeValue(annotation, "forViews");
		String notForViews = getAnnotationAttributeValue(annotation, "notForViews");
		return isForView(viewName, forViews, notForViews);
	}
	
	private static String getAnnotationAttributeValue(Annotation annotation, String attribute) { // tmr Refactor, ¿Uso en otro sitio? Al menos en esta clase sí
		Object value = null;
		try {
			value = XObjects.execute(annotation, attribute);
		}
		catch (NoSuchMethodException ex) {			
		} 
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_get_value_annotation_attribute", attribute, annotation.annotationType().getName()), ex);			
		}		
		return value==null?null:value.toString();
	}
	
	private static boolean isForView(String view, String forViews, String notForViews) { // tmr 
		// tmr Refactor con AnnotatedClassParser.isForView()
		// tmr 		¿Aquí como estático? ¿Mover a MetaModel? ¿En MetaView? ¿Cómo estático en MetaView? ¿A una clase de utilidad?
		if (Is.emptyStringAll(forViews, notForViews)) return true;
		if (!Is.emptyString(forViews) && !Is.emptyString(notForViews)) {
			log.warn(XavaResources.getString("forViews_and_notForViews_not_compatible")); 
		}
		if (!Is.emptyString(forViews)) {
			StringTokenizer st = new StringTokenizer(forViews, ",");
			while (st.hasMoreTokens()) {
				String viewName = st.nextToken().trim();
				if (viewName.equals(view)) return true;
				if (Is.emptyString(view) && "DEFAULT".equals(viewName)) return true;
			}
			return false;
		}
		else {
			StringTokenizer st = new StringTokenizer(notForViews, ",");
			while (st.hasMoreTokens()) {
				String viewName = st.nextToken().trim();
				if (viewName.equals(view)) return false;
				if (Is.emptyString(view) && "DEFAULT".equals(viewName)) return false;
			}	
			return true;
		}				
	}
		
}