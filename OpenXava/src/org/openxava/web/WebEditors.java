package org.openxava.web;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.filters.meta.*;
import org.openxava.formatters.*;
import org.openxava.model.meta.MetaMember;
import org.openxava.model.meta.MetaModel;
import org.openxava.model.meta.MetaProperty;
import org.openxava.model.meta.MetaReference;
import org.openxava.tab.meta.*;
import org.openxava.util.ElementNotFoundException;
import org.openxava.util.Is;
import org.openxava.util.Locales;
import org.openxava.util.Messages;
import org.openxava.util.Strings;
import org.openxava.util.XavaException;
import org.openxava.util.XavaResources;
import org.openxava.view.meta.MetaDescriptionsList;
import org.openxava.view.meta.MetaView;
import org.openxava.web.meta.MetaEditor;
import org.openxava.web.meta.MetaWebEditors;

/**
 * @author Javier Paniza
 */

public class WebEditors { 	

	private static Log log = LogFactory.getLog(WebEditors.class);
	final private static String PREFIX = "editors/";
	
	public static boolean mustToFormat(MetaProperty p, String viewName) throws XavaException { 
		try {
			return getMetaEditorFor(p, viewName).isFormat(); 
		}
		catch (ElementNotFoundException ex) {
			return true; 
		}
	}
	
	public static boolean hasMultipleValuesFormatter(MetaProperty p, String viewName) throws XavaException { 
		try {
			return getMetaEditorFor(p, viewName).hasMultipleValuesFormatter();
		}
		catch (ElementNotFoundException ex) {
			return false; 
		}
	}	
	
	public static boolean hasFrame(MetaProperty p, String viewName) throws XavaException { 
		try {
			return getMetaEditorFor(p, viewName).isFrame(); 
		}
		catch (ElementNotFoundException ex) {
			return false; 
		}
	}

	public static Object parse(HttpServletRequest request, MetaProperty p, String [] strings, Messages errors, String viewName) throws XavaException { 
		try {
			if (!(p.isKey() && p.isHidden())) {
				MetaEditor ed = getMetaEditorFor(p, viewName);
				if (ed.hasFormatter()) { 				
					return parse(request, ed.getFormatterObject(), p, strings);
				}
				else if (ed.isFormatterFromType()){			
					MetaEditor edType = MetaWebEditors.getMetaEditorForTypeOfProperty(p); 
					if (edType != null && edType.hasFormatter()) {
						return parse(request, edType.getFormatterObject(), p, strings);
					}
				}
			}
			return p.parse(strings == null?null:strings[0], Locales.getCurrent());
		}
		catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			String messageId = p.isNumber()?"numeric":"no_expected_type";
			errors.add(messageId, p.getName(), p.getMetaModel().getName());
			return null;
		}		
	}
	
	private static Object parse(HttpServletRequest request, Object formatter, MetaProperty p, String [] strings) throws Exception { 
		if (formatter instanceof IFormatter) {				
			return ((IFormatter) formatter).parse(request, strings == null?null:strings[0]);
		}
		if (formatter instanceof IMultipleValuesFormatter) { 
			return ((IMultipleValuesFormatter) formatter).parse(request, strings);
		}
		if (formatter instanceof IMetaPropertyFormatter) {				
			return ((IMetaPropertyFormatter) formatter).parse(request, p, strings == null?null:strings[0]);
		}
		throw new XavaException("formatter_incorrect_class"); 
	}
		
	public static Object parse(HttpServletRequest request, MetaProperty p, String string, Messages errors, String viewName) throws XavaException { 
		String [] strings = string == null?null:new String [] { string };
		return parse(request, p, strings, errors, viewName); 
	}
		
	public static String format(HttpServletRequest request, MetaProperty p, Object object, Messages errors, String viewName) throws XavaException {
		Object result = formatToStringOrArray(request, p, object, errors, viewName, false);
		if (result instanceof String []) return arrayToString((String []) result);		
		return (String) result;
	}
	
	public static String format(HttpServletRequest request, MetaProperty p, Object object, Messages errors, String viewName, boolean fromList) throws XavaException {
		Object result = formatToStringOrArray(request, p, object, errors, viewName, fromList);
		if (result instanceof String []) return arrayToString((String []) result);		
		return (String) result;
	}
	
	private static String arrayToString(String[] strings) {
		if (strings == null) return "";
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			result.append(strings[i]);
			if (i < strings.length - 1) result.append('/');
		}
		return result.toString();
	}	

	/** 
	 * @return If has a multiple converter return a array of string else return a string
	 */
	public static Object formatToStringOrArray(HttpServletRequest request, MetaProperty p, Object object, Messages errors, String viewName, boolean fromList) throws XavaException {
		return formatToStringOrArrayImpl(request, p, object, errors, viewName, fromList);
	}
	
	public static Object formatTitle(HttpServletRequest request, MetaProperty p, Object object, Messages errors, String viewName, boolean fromList) throws XavaException { 
		Object result = formatToStringOrArrayImpl(request, p, object, errors, viewName, fromList);
		if (result != null && hasMarkup(result)) {
			return p.getLabel(); 
		}
		return result; 		
	}

	private static boolean hasMarkup(Object result) { 
		return result.toString().contains("<") && result.toString().contains(">");
	}

	public static Object formatToStringOrArrayImpl(HttpServletRequest request, MetaProperty p, Object object, Messages errors, String viewName, boolean fromList) throws XavaException {  
		try {
			MetaEditor ed = getMetaEditorFor(p, viewName);
			if (fromList && !Is.empty(ed.getListFormatterClassName())){
				return format(request, ed.getListFormatterObject(), p, object);
			}						
			else if (fromList && p.hasValidValues()){
				return p.getValidValueLabel(object);
			}
			else if (ed.hasFormatter()) {				
				return format(request, ed.getFormatterObject(), p, object);
			}
			else if (ed.isFormatterFromType()){
				MetaEditor edType = MetaWebEditors.getMetaEditorForType(p.getTypeName());
				if (edType != null && edType.hasFormatter()) {				
					return format(request, edType.getFormatterObject(), p, object);
				}
			}					
			return p.format(object, Locales.getCurrent());									
		}
		catch (Exception ex) {
			log.warn(ex.getMessage(), ex);			
			errors.add("no_convert_to_string", p.getName(), p.getMetaModel().getName());
			return "";
		}
	}
	
	private static Object format(HttpServletRequest request, Object formatter, MetaProperty p, Object object) throws Exception { 
		if (formatter instanceof IFormatter) {				
			return ((IFormatter) formatter).format(request, object);
		}
		if (formatter instanceof IMultipleValuesFormatter) { 
			return ((IMultipleValuesFormatter) formatter).format(request, object);
		}
		if (formatter instanceof IMetaPropertyFormatter) {				
			return ((IMetaPropertyFormatter) formatter).format(request, p, object);
		}
		throw new XavaException("formatter_incorrect_class");
	}
	
	public static String getUrl(MetaProperty p, String viewName) throws XavaException {
		try {				
			return PREFIX + getMetaEditorFor(p, viewName).getUrl();
		}
		catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			return PREFIX + "notAvailableEditor.jsp";
		}		
	}	
	
	public static String getUrl(MetaTab metaTab) throws ElementNotFoundException, XavaException {
		try {					
			String editor = metaTab.getEditor();
			if (!Is.emptyString(editor)) {
				try {
					return PREFIX + MetaWebEditors.getMetaEditorByName(editor).getUrl();
				}
				catch (Exception ex) {
					log.warn(XavaResources.getString("tab_editor_problem_using_default", editor), ex);
				}
			}
			return PREFIX + MetaWebEditors.getMetaEditorFor(metaTab).getUrl();
		}
		catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			return PREFIX + "notAvailableEditor.jsp";
		}		
	}
	
	public static Collection<String> getEditors(MetaTab metaTab) throws ElementNotFoundException, XavaException { 
		if (!Is.emptyString(metaTab.getEditors())) {
			if (!Is.emptyString(metaTab.getEditor())) {
				log.warn(XavaResources.getString("editors_over_editor", metaTab.getEditor(), metaTab.getName(), metaTab.getModelName()));
			}
			return Strings.toCollection(metaTab.getEditors());
		}
		Collection<String> editors = new ArrayList<String>();
		String customEditor = metaTab.getEditor();
		if (!Is.emptyString(customEditor)) editors.add(customEditor);
		for (MetaEditor metaEditor: MetaWebEditors.getMetaEditorsFor(metaTab)) {
			if (Is.emptyString(customEditor) || !"List".equals(metaEditor.getName())) {
				editors.add(metaEditor.getName());
			}
		}
		return editors;
	}
	
	public static String getUrl(String editor, MetaTab metaTab) throws ElementNotFoundException, XavaException { 
		if (!Is.emptyString(editor)) {
			try {
				return PREFIX + MetaWebEditors.getMetaEditorByName(editor).getUrl();
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("tab_editor_problem_using_default", editor), ex); 
			}
		}
		return getUrl(metaTab);
	}
	
	/**
	 * 
	 * @return Null if the editor does not exists. A default value if exists but has not icon or the param is null or empty.  
	 */
	public static String getIcon(String editor) throws ElementNotFoundException, XavaException { 
		if (Is.emptyString(editor)) return "view-list"; // A good default because icon is only used for list, by now. If we remove this line test with a editor for tabs with no name
		MetaEditor metaEditor = MetaWebEditors.getMetaEditorByName(editor);
		if (metaEditor == null) {
			log.warn(XavaResources.getString("editor_not_exist", editor)); 
			return null;
		}
		String result = metaEditor.getIcon(); 		
		return Is.emptyString(result)?"view-list":result; 
	}	
		
	public static MetaEditor getMetaEditorFor(MetaMember m, String viewName) throws ElementNotFoundException, XavaException {
		if (m.getMetaModel() != null) {
			try {				
				MetaView metaView = m.getMetaModel().getMetaView(viewName);				
				String editorName = metaView.getEditorFor(m);
				if (!Is.emptyString(editorName)) {
					MetaEditor metaEditor = MetaWebEditors.getMetaEditorByName(editorName);
					if (metaEditor != null) {						
						return metaEditor;
					}
					else {
						log.warn(XavaResources.getString("editor_by_name_for_property_not_found", editorName, m.getName()));
					}
				}
			}
			catch (ElementNotFoundException ex) {
			}
		}
		return MetaWebEditors.getMetaEditorFor(m);
	}
				
	/** 
	 * If a depends on b
	 */
	public static boolean depends(MetaProperty a, MetaProperty b, String viewName) {		
		try {			
			if (a.depends(b)) return true;
			return getMetaEditorFor(a, viewName).depends(b);
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("a_depends_b_warning", a, b), ex); 
			return false;
		}
	}

	/**
	 * If the property depends of some other property displayed in the view. <p>
	 */
	public static boolean dependsOnSomeOther(MetaProperty metaProperty, String viewName) {
		try {			
			return getMetaEditorFor(metaProperty, viewName).dependsOnSomeOther();
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("a_depends_b_warning", metaProperty.getName(), "some other"), ex);
			return false;
		}
	}
	
	public static String getEditorURLDescriptionsList(String tabName, String tabModelName, String propertyKey, int index, String prefix, String qualifiedName, String name){
		if (qualifiedName.indexOf('.') < 0) return "";

		String url = "";
		String defaultURL = "";
		MetaModel metaModel = MetaModel.get(tabModelName);
		String reference = Strings.noLastTokenWithoutLastDelim(qualifiedName, ".");
		MetaReference metaReference = null;
		try {
			metaReference =  metaModel.getMetaReference(reference);
		}
		catch (ElementNotFoundException ex) {
			return "";
		}
		metaModel = metaReference.getMetaModel();
		Collection<MetaView> metaViews = metaModel.getMetaViews();
		for (MetaView metaView : metaViews){
			MetaDescriptionsList metaDescriptionsList = metaView.getMetaDescriptionList(metaReference);			
			if (metaDescriptionsList == null) continue;
			if (!Is.empty(metaDescriptionsList.getDepends())) continue;
			Collection<String> forTabs = Is.empty(metaDescriptionsList.getForTabs()) ?
				new ArrayList<String>():
				Strings.toCollection(metaDescriptionsList.getForTabs());
			Collection<String> notForTabs = Is.empty(metaDescriptionsList.getNotForTabs()) ?
				new ArrayList<String>():
				Strings.toCollection(metaDescriptionsList.getNotForTabs());
			
			if (notForTabs.contains(tabName) || (Is.empty(tabName) && notForTabs.contains("DEFAULT"))) continue;
			
			String descriptionPropertiesNames = metaDescriptionsList.getDescriptionPropertiesNames();
			if (Is.empty(descriptionPropertiesNames)) descriptionPropertiesNames = metaDescriptionsList.getDescriptionPropertyName();
			if (descriptionPropertiesNames.contains(name)) {
				url = "comparatorsDescriptionsList.jsp"
					+ "?propertyKey=" + propertyKey
					+ "&index=" + index
					+ "&prefix=" + prefix
					+ "&editable=true" 
					+ "&model=" + metaReference.getReferencedModelName()
					+ "&keyProperty=" + metaReference.getKeyProperty(propertyKey)
					+ "&keyProperties=" + metaReference.getKeyProperties()
					+ "&descriptionProperty=" + metaDescriptionsList.getDescriptionPropertyName()
					+ "&descriptionProperties=" + metaDescriptionsList.getDescriptionPropertiesNames()
					+ "&parameterValuesProperties=" + metaReference.getParameterValuesPropertiesInDescriptionsList(metaView)
					+ "&condition=" + refineURLParam(metaDescriptionsList.getCondition())
					+ "&filter=" + getFilterClass(metaDescriptionsList) 
					+ "&orderByKey=" + metaDescriptionsList.isOrderByKey()
					+ "&order=" + metaDescriptionsList.getOrder();
				if (forTabs.contains(tabName)) return url;
				if (forTabs.isEmpty()) defaultURL = url;
			}
		}
		return defaultURL;
	}

	private static String getFilterClass(MetaDescriptionsList metaDescriptionsList) {
		MetaFilter metaFilter = metaDescriptionsList.getMetaFilter();
		return metaFilter == null?"":metaFilter.getClassName();
	}

	private static String refineURLParam(String condition) {
		return condition.replace("%", "%25");
	}

}
