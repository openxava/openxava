package org.openxava.actions;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;
import org.openxava.web.servlets.*;

/**
 * 
 * @author Laurent Wibaux
 */

public class SimpleTemplaterAction extends TabBaseAction 
implements IModelAction { 

	public static final String COLLECTION = "__COLLECTION__";
	
	protected static final int MAX_DEPTH = 5;
	
	@SuppressWarnings("unused")
	private String modelName;
	
	private String template;
	
	private String depth;
				
	public void execute() throws Exception {
		if (getView().isKeyEditable()) {
			addError("save_before_reporting");
			return;
		}
        Messages errors = MapFacade.validate(getModelName(), getView().getValues());
        if (errors.contains()) throw new ValidationException(errors); 
		String tpl = getTemplate();
		if (tpl == null || tpl.equals("")) tpl = "/" + getModelName() + ".html";
		Map<String, Object> parameters = getParameters();
		String report = SimpleTemplater.getInstance().buildOutputUsingResourceTemplate(tpl, parameters);
		getRequest().getSession().setAttribute(GenerateSimpleHTMLReportServlet.SESSION_REPORT, report);
	}
	
	protected Map<String, Object> getParameters() 
	throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Object entity = MapFacade.findEntity(getModelName(), getView().getKeyValuesWithValue());
        parameters.putAll(getEntityParameters(entity, getIntDepth()));
		parameters.put("fields", getFieldsTable(entity, getIntDepth()));
		parameters.put("values", getValuesTable(entity, getIntDepth()));	
		return parameters;
	}
		
	public void setModel(String modelName) { 
		this.modelName = modelName;
	}

	public String getFieldsTable(Object entity, int depth) 
	throws Exception {
		return getFieldsOrValuesTable(entity, depth, true);
	}

	public String getValuesTable(Object entity, int depth) 
	throws Exception {
		return getFieldsOrValuesTable(entity, depth, false);
	}

	private String getFieldsOrValuesTable(Object entity, int depth, boolean fields)
	throws Exception {
		Map<String, String> parameters = getEntityParameters(entity, depth);
		TreeSet<String> ordered = new TreeSet<String>();
		for (String key : parameters.keySet()) ordered.add(key);
		String table = "<table>\r\n";
		for (String key : ordered) {
			String value = parameters.get(key);
			if (fields || (!value.equals(COLLECTION) && !value.equals(""))) {
				table += "\t<tr><td>" + key + "</td><td>";
				if (parameters.get(key).equals(COLLECTION)) table += COLLECTION;
				else if (fields) table += "${" + key + "}";
				else table += parameters.get(key);
				table += "</td></tr>\r\n";
			}
		}
		table += "</table>\r\n";
		return table;
	}


	public String getCollectionTable(Class<?> collectionEntityClass, String collectionName) 
	throws Exception {
		return getCollectionTable(collectionEntityClass, collectionName, 1);
	}
	
	
	public String getCollectionTable(Class<?> collectionEntityClass, String collectionName, int maxDepth) 
	throws Exception {
		Map<String, String> parameters = getClassParameters(collectionEntityClass, maxDepth);
		String table = "<table>\r\n";
		table += "\t<tr>";
		for (String key : parameters.keySet()) {
			if (!parameters.get(key).equals(COLLECTION)) table += "<th>" + key + "</th>";
		}
		table += "</tr>";
		table += "<!-- $$for(" + collectionName + ") --><tr>\r\n";
		for (String key : parameters.keySet()) {
			if (!parameters.get(key).equals(COLLECTION)) table += "<td>${" + key + "}</td>";
		}
		table += "</tr><!-- $$endfor(" + collectionName + ") -->\r\n";
		table += "</table>\r\n";
		return table;
	}
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getDepth() {
		return depth;
	}

	private int getIntDepth() {
		try {
			return Integer.parseInt(getDepth());
		} catch (NumberFormatException nfe) {
			return 4;
		}
	}


	/**
	 * Get a map of [name, value] for the passed object
	 * @param 	entity - the Object to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */
	protected static Map<String, String> getEntityParameters(Object entity) 
	throws Exception {
		return getEntityParameters(entity, entity.getClass(), "", 0);
	}
	
	
	/**
	 * Get a map of [name, value] for the passed object
	 * @param 	entity - the Object to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */
	protected Map<String, String> getEntityParameters(Object entity, int maxDepth) 
	throws Exception {
		return getEntityParameters(entity, entity.getClass(), "", MAX_DEPTH-maxDepth);
	}

	/**
	 * Get a map of [name, value] for the passed object
	 * @param 	entity - the Object to parse
	 * @param	parentName - the prefix to put in front of every name
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */
	protected Map<String, String> getEntityParameters(Object entity, String parentName, int maxDepth) 
	throws Exception {
		return getEntityParameters(entity, entity.getClass(), parentName + ".", MAX_DEPTH-maxDepth);
	}

	
	/**
	 * Get a map of [name, ""] for the passed class
	 * @param 	class - the Class to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */
	protected Map<String, String> getClassParameters(Class<?> aClass, int maxDepth) 
	throws Exception {
		return getEntityParameters(null, aClass, "", MAX_DEPTH-maxDepth);
	}

	
	private static Map<String, String> getEntityParameters(Object entity, Class<?> entityClass, String parentName, int depth) 
	throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		if (depth >= MAX_DEPTH) return parameters;
		Field fields[] = entityClass.getDeclaredFields();
		for (int i=0; i<fields.length; i++) {
			if (Modifier.isStatic(fields[i].getModifiers())) continue;
			if (fields[i].getName().indexOf('$') != -1) continue;
			String name = fields[i].getName();
			Object value = getValue(entity, name);
			if (value != null && isPrintable(value)) {
				parameters.put(parentName + name, getPrintableValue(value));
			} else if (fields[i].isAnnotationPresent(javax.persistence.ManyToOne.class)) {
				Map<String, String> mtoParameters = getEntityParameters(value, fields[i].getType(), 
						parentName + name + ".", depth + 1);
				if (mtoParameters.size() > 0) parameters.putAll(mtoParameters);
			} else if (fields[i].isAnnotationPresent(javax.persistence.OneToMany.class) || 
					fields[i].isAnnotationPresent(javax.persistence.ManyToMany.class)) {
				parameters.put(parentName + name, COLLECTION);			
			} else {
				parameters.put(parentName + name, "");
			}
		}
		return parameters;
	}
	
	private static Object getValue(Object o, String name) {
		if (o == null) return null;
		String method = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
		try {
			Method m = o.getClass().getMethod(method);
			return m.invoke(o);
		} catch (Exception e) {
			// Failed: it could be a boolean, with a isField getter
		}		
		method = "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
		try {
			Method m = o.getClass().getMethod(method);
			return m.invoke(o);
		} catch (Exception e) {
			return null;
		}		
	}

	private static String getPrintableValue(Object value) { 
		if (value == null) return "";
		if (value instanceof Date) return DateFormat.getInstance().format((Date) value);
		String sValue = "" + value.toString();
		if (sValue.endsWith(".0")) sValue = sValue.substring(0, sValue.length()-2);
		return sValue.trim();
	}	
	
	private static boolean isPrintable(Object o) {
		if (o instanceof Date) return true;
		if (o instanceof String) return true;
		if (o instanceof Float) return true;
		if (o instanceof Double) return true;
		if (o instanceof Integer) return true;
		if (o instanceof Long) return true;
		if (o instanceof Boolean) return true;
		if (o instanceof Byte) return true;
		if (o instanceof Character) return true;
		if (o instanceof Short) return true;
		if (o instanceof Enum<?>) return true;
		return false;
	}
	
	/**
	 * Get a list of maps of [name, value] for each one of the objects in the passed collection
	 * @param 	collection - the Collection to parse
	 * @return  the list of maps
	 */
	protected static Vector<Map<String, String>> getCollectionParametersList(Collection<?> collection) 
	throws Exception {
		return getCollectionParametersList(collection, 2);
	}
	
	/**
	 * Get a list of maps of [name, value] for each one of the objects in the passed collection
	 * @param 	collection - the Collection to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the list of maps
	 */
	protected static Vector<Map<String, String>> getCollectionParametersList(Collection<?> collection, int maxDepth) 
	throws Exception {
		Vector<Map<String, String>> list = new Vector<Map<String, String>>();
		if (collection != null) {
			for (Object entity : collection) {
				list.add(getEntityParameters(entity, entity.getClass(), "", MAX_DEPTH-maxDepth));
			}
		}
		return list;
	}
}