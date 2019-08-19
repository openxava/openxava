package org.openxava.web.meta;

import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * Editor associated to a type.
 * 
 * @author Javier Paniza
 */
public class MetaEditor {
	
	private static Log log = LogFactory.getLog(MetaEditor.class);
	
	private boolean formatterFromType;
	private Object formatter; 
	private String propertiesURL;
	private String name; 
	private java.lang.String url;	
	private Map properties;
	private Collection stereotypesIDepend;
	private Collection propertiesIDepend;
	private String formatterClassName;
	private Collection formatterMetaSets;
	private boolean format = true;
	private boolean frame = false;
	private boolean alwaysReload = false; 
	private boolean composite = false; 
	private String listFormatterClassName;
	private Collection listFormatterMetaSet;
	private Object listFormatter; 
	private String icon; 
	private String initAction; 
	private String releaseAction; 
	private boolean selectableItems;  

	public void _addListFormatterMetaSet(MetaSet metaSet) {
		if (listFormatterMetaSet == null) listFormatterMetaSet = new ArrayList();
		listFormatterMetaSet.add(metaSet);
	}
	
	public void _addFormatterMetaSet(MetaSet metaSet) {
		if (formatterMetaSets == null) formatterMetaSets = new ArrayList();
		formatterMetaSets.add(metaSet);
	}
	
	public java.lang.String getUrl() {
		return url + getPropertiesURL();
	}

	public void setUrl(java.lang.String string) {
		url = string;
	}

	public void addProperty(String name, String value) {
		if (properties == null) properties = new HashMap();
		properties.put(name, value);
		propertiesURL = null;		
	}
	
	public boolean hasProperty(String name) { 
		return properties == null?false:properties.containsKey(name);
	}
	
	/** @since 6.2 */
	public String getProperty(String name) { 
		return properties == null?null:(String) properties.get(name);
	}
	
	private String getPropertiesURL() {
		if (propertiesURL == null) {
			if (properties == null) propertiesURL="";
			else {
				StringBuffer sb = new StringBuffer("?");
				Iterator it = properties.entrySet().iterator();
				while (it.hasNext()) {					
					Map.Entry e = (Map.Entry) it.next();
					sb.append(e.getKey());
					sb.append("=");
					sb.append(e.getValue());
					if (it.hasNext()) sb.append("&");
				}
				propertiesURL = sb.toString();
			}
		}		
		return propertiesURL; 
	}
	
	public void setDependsStereotypes(String stereotypes) {
		if (stereotypes == null) return;
		StringTokenizer st = new StringTokenizer(stereotypes, ",");
		stereotypesIDepend = new ArrayList();
		while (st.hasMoreTokens()) {
			stereotypesIDepend.add(st.nextToken().trim());			
		}
	}
	
	public void setDependsProperties(String properties) {				
		if (properties == null) return;
		StringTokenizer st = new StringTokenizer(properties, ",");
		propertiesIDepend = new ArrayList();
		while (st.hasMoreTokens()) {
			propertiesIDepend.add(st.nextToken().trim());			
		}
	}
	
	public boolean depends(MetaProperty p) {		
		if (dependsStereotype(p)) return true;		
		return dependsPropertyName(p);
	}		
		
	public boolean dependsStereotype(MetaProperty p) {
		if (stereotypesIDepend == null) return false; 
		if (!p.hasStereotype()) return false;
		return stereotypesIDepend.contains(p.getStereotype()); 
	}
	
	private boolean dependsPropertyName(MetaProperty p) {
		if (propertiesIDepend == null) return false;		
		return propertiesIDepend.contains(p.getName());
	}

	public boolean hasFormatter() throws XavaException {				
		return !Is.emptyString(formatterClassName); 
	}
		
	public boolean hasMultipleValuesFormatter() throws XavaException { 
		return !Is.emptyString(formatterClassName) && getFormatterObject(formatterClassName, formatterMetaSets) instanceof IMultipleValuesFormatter;
	}
	
	public Object getFormatterObject() throws XavaException { 
		return getFormatterObject(formatterClassName, formatterMetaSets);
	}
		
	public IFormatter getFormatter() throws XavaException {
		return (IFormatter) getFormatterObject(); 
	}
	
	public Object getListFormatterObject() throws XavaException { 
		if (listFormatter == null) { 
			listFormatter = createFormatterObject(listFormatterClassName, listFormatterMetaSet);
		}
		return listFormatter;
	}

	public IFormatter getListFormatter() throws XavaException {
		return (IFormatter) getListFormatterObject(); 
	}
	
	public IMultipleValuesFormatter getMultipleValuesFormatter() throws XavaException {  
		return (IMultipleValuesFormatter) getFormatterObject(); 
	}
	
	public IMetaPropertyFormatter getMetaPropertyFormatter() throws XavaException {   
		return (IMetaPropertyFormatter) getFormatterObject(); 
	}
	
	/**
	 * @return Not null
	 * @throws XavaException For example, if className is empty string
	 */
	
	private Object getFormatterObject(String className, Collection metaSets) throws XavaException{
		if (formatter == null) {
			formatter = createFormatterObject(className, metaSets);
		}
		return formatter;
	}

	private Object createFormatterObject(String className, Collection metaSets) { 
		if (Is.emptyString(className)) {
			throw new XavaException("no_formatter_class_error");
		}
		try {
			Object formatter =  Class.forName(className).newInstance();
			if (metaSets != null) {
				PropertiesManager pm = new PropertiesManager(formatter);
				for (Iterator it = metaSets.iterator(); it.hasNext(); ) {
					MetaSet metaSet = (MetaSet) it.next();
					pm.executeSetFromString(metaSet.getPropertyName(), metaSet.getValue());
				}
			}
			if (!(formatter instanceof IFormatter || formatter instanceof IMultipleValuesFormatter || formatter instanceof IMetaPropertyFormatter)) { 
				throw new XavaException("implements_required", className, IFormatter.class.getName() + " or " + IMultipleValuesFormatter.class.getName());
			}
			return formatter;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_formatter_error", className);
		}
	}
	
	public String getFormatterClassName() {
		return formatterClassName;
	}

	public void setFormatterClassName(String string) {
		formatterClassName = string;
	}

	public boolean isFormat() {
		return format;
	}

	public void setFormat(boolean b) {
		format = b;
	}

	public boolean isFrame() {
		return frame;
	}

	public void setFrame(boolean b) {
		frame = b;
	}

	public boolean isFormatterFromType() {
		return formatterFromType;
	}
	public void setFormatterFromType(boolean formatterFromType) {
		this.formatterFromType = formatterFromType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * If this editor depends of some other property or stereotype. <p>
	 */
	public boolean dependsOnSomeOther() { 
		if (propertiesIDepend != null && !propertiesIDepend.isEmpty()) return true;
		if (stereotypesIDepend != null && !stereotypesIDepend.isEmpty()) return true;
		return false;
	}

	public void setAlwaysReload(boolean alwaysReload) {
		this.alwaysReload = alwaysReload;
	}

	public boolean isAlwaysReload() {
		return alwaysReload;
	}
	
	public boolean isComposite() {
		return composite;
	}

	public void setComposite(boolean composite) {
		this.composite = composite;
	}

	public String getListFormatterClassName() {
		return listFormatterClassName;
	}

	public void setListFormatterClassName(String listFormatterClassName) {
		this.listFormatterClassName = listFormatterClassName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getInitAction() {
		return initAction;
	}

	public void setInitAction(String initAction) {
		this.initAction = initAction;
	}

	public String getReleaseAction() {
		return releaseAction;
	}

	public void setReleaseAction(String releaseAction) {
		this.releaseAction = releaseAction;
	}

	public boolean isSelectableItems() {
		return selectableItems;
	}

	public void setSelectableItems(boolean selectableItems) {
		this.selectableItems = selectableItems;
	}

}