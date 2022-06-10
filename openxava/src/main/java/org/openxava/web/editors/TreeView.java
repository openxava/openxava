package org.openxava.web.editors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.OrderBy;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.annotations.Tree;
import org.openxava.util.Is;
import org.openxava.util.Strings;
import org.openxava.util.XavaException;
import org.openxava.util.XavaResources;

/**
 * 
 * @author Federico Alcántara
 */
public class TreeView {
	
	private Log log = LogFactory.getLog(TreeView.class);
	
	private String pathProperty;
	private String idProperties;
	private String orderProperty;
	private boolean initialExpandedState;
	private int keyIncrement;
	private String pathSeparator;
	private List<String>idPropertiesList;
	private boolean entityObject;
	private boolean orderDefined;
	private String idSeparator;
	@SuppressWarnings("unused")
	@Tree
	private String defaultPathAnnotation;
	private Tree treeAnnotation;
	
	@SuppressWarnings("rawtypes")
	private Class nodeClass;
	
	@SuppressWarnings("rawtypes")
	private Class parentClass;
	private String collectionName;
	private Map<String, Boolean> expandedStates;
	
	private String treeViewReaderName;
	private ITreeViewReader treeViewReader;
	
	public TreeView(){
	}
	
	@SuppressWarnings("rawtypes")
	public TreeView(Tree path, Class nodeClass, Class parent, String collectionName, String treeViewReaderName) throws Exception {
		this.treeViewReaderName = treeViewReaderName;
		parseTreeView(path, nodeClass, parent, collectionName);
	}
	
	/**
	 * Parse the @TreeView annotation.
	 * @param nodeClass Object to be parsed.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void parseTreeView(Tree path, Class nodeClass, Class parentClass, String collectionName) throws Exception {
		this.treeAnnotation = path;
		this.nodeClass = nodeClass;
		this.parentClass = parentClass;
		this.collectionName = collectionName;
		if (treeAnnotation == null) {
			treeAnnotation = this.getClass().getDeclaredField("defaultPathAnnotation").getAnnotation(Tree.class);
		}
		if (Is.empty(treeAnnotation.pathProperty())) {
			throw new XavaException("error.collectionDoesNotRepresentATreeView");
		}
		this.pathProperty = treeAnnotation.pathProperty();
		this.idSeparator = treeAnnotation.idSeparator();
		parseNodeProperty();
		this.orderProperty = null;
		this.initialExpandedState = treeAnnotation.initialExpandedState();
		this.keyIncrement = treeAnnotation.orderIncrement();
		if (this.keyIncrement < 2) {
			this.keyIncrement = 2;
		}
		setPathSeparator(treeAnnotation.pathSeparator());
		if (nodeClass.getClass().isAnnotationPresent(Id.class)) {
			entityObject = true;
		} else {
			entityObject = false;
		}
		parseOrderDefined();
		parseEntityObject();
		
		
	}
	
	/**
	 * Takes care of the id properties, either identifying the Ids or getting them from idProperties
	 * of @Path 
	 * @throws Exception
	 */
	private void parseNodeProperty() throws Exception {
		if (treeAnnotation == null || Is.empty(treeAnnotation.idProperties())) {
			StringBuffer idPropertiesBuffer = new StringBuffer("");
			addIdProperties(nodeClass, idPropertiesBuffer);
			idProperties = idPropertiesBuffer.toString();
		} else {
			idProperties = treeAnnotation.idProperties();
		}
		if (Is.empty(idProperties)) {
			throw new Exception(XavaResources.getString("error.nodePropertiesUndefined"));
		}
		// Clean the properties.
		String[] properties = idProperties.split(",");
		idPropertiesList = new ArrayList<String>();
		for (String property : properties) {
			if (!Is.empty(property.trim())) {
				idPropertiesList.add(property.trim());
			}
		}
	}
	
	/**
	 * Gather all field/properties annotated with id.
	 * @param nodeItemClass Class of the node item.
	 * @param idPropertiesBuffer stores the list of properties.
	 */
	@SuppressWarnings("rawtypes")
	private void addIdProperties(Class nodeItemClass, StringBuffer idPropertiesBuffer){
		addFieldProperties(nodeItemClass, idPropertiesBuffer);
		addMethodProperties(nodeItemClass, idPropertiesBuffer);
		if (nodeItemClass.getSuperclass() != null) {
			addIdProperties(nodeItemClass.getSuperclass(), idPropertiesBuffer);
		}
	}
	
	/**
	 * Finds the fields annotated with @Id.
	 * @param nodeItemClass Class of the node item.
	 * @param idPropertiesBuffer stores the list of properties.
	 */
	@SuppressWarnings("rawtypes")
	private void addFieldProperties(Class nodeItemClass, StringBuffer idPropertiesBuffer) {
		for (Field field : nodeItemClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				if (idPropertiesBuffer.length() > 0) {
					idPropertiesBuffer.append(",");
				}
				idPropertiesBuffer.append(field.getName());
			}
		}
	}
	
	/**
	 * Finds the methods annotated with @Id.
	 * @param nodeItemClass Class of the node item.
	 * @param idPropertiesBuffer stores the list of properties.
	 */
	@SuppressWarnings("rawtypes")
	private void addMethodProperties(Class nodeItemClass, StringBuffer idPropertiesBuffer) {
		for (Method method : nodeItemClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Id.class)) {
				if (idPropertiesBuffer.length() > 0) {
					idPropertiesBuffer.append(",");
				}
				String fieldName = method.getName();
				if (fieldName.startsWith("get") && fieldName.length() > 3) {
					fieldName = fieldName.substring(3, 4).toLowerCase()
							+ fieldName.substring(4);
					idPropertiesBuffer.append(fieldName);
				}
			}
		}
	}

	/**
	 * Determines if the property orderProperty was defined for the object.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void parseOrderDefined() {
		orderDefined = false;
		OrderBy orderBy = null;
		if (Is.empty(orderProperty)) {
			try {
				Field collectionField = parentClass.getDeclaredField(collectionName);
				if (collectionField.isAnnotationPresent(OrderBy.class)) {
					orderBy = collectionField.getAnnotation(OrderBy.class);
				}
			} catch (SecurityException e) {
				log.error(e);
			} catch (NoSuchFieldException e) {
				log.debug(e.getMessage());
			}
			
			if (orderBy == null){
				Method collectionMethod = null;
				try {
					collectionMethod = parentClass.getDeclaredMethod("get" + Strings.firstUpper(collectionName), new Class[]{});
				} catch (Exception e) {
					log.debug(e);
				}
				if (collectionMethod == null){
					try {
						collectionMethod = parentClass.getDeclaredMethod("is" + Strings.firstUpper(collectionName), new Class[]{});
					} catch (Exception e) {
						log.debug(e);
					}
				}
				if (collectionMethod != null && collectionMethod.isAnnotationPresent(OrderBy.class)) {
					orderBy = collectionMethod.getAnnotation(OrderBy.class);
				}
			}
			if (orderBy != null){
				String [] fieldNames = orderBy.value().split(",");
				if (fieldNames.length > 0) {
					orderProperty = fieldNames[fieldNames.length - 1].trim();
				}
			}
		}
		if (!Is.empty(orderProperty)) {
			try {
				Object itemObject = nodeClass.newInstance();
				Class propertyType = PropertyUtils.getPropertyType(itemObject, orderProperty);
				if (propertyType.isAssignableFrom(Integer.class)) {
					orderDefined = true;
				}
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	/**
	 * Determines if the object is an entity.
	 */
	@SuppressWarnings("unchecked")
	private void parseEntityObject() {
		entityObject = nodeClass.isAnnotationPresent(javax.persistence.Entity.class);
	}
	
	/**
	 * Returns the node name as it is used in the path
	 * @param object Object to be inspected.
	 * @return The node name. It can be compound with multiple values 
	 * (when you have multiple id, for example).
	 */
	public String getNodeName(Object object) {
		String returnValue = "";
		Object value;
			for (String propertyName : idPropertiesList) {
				value = propertyName;
				try {
					value = PropertyUtils.getProperty(object, propertyName);
				} catch (IllegalAccessException e) {
					log.debug(e);
				} catch (InvocationTargetException e) {
					log.debug(e);
				} catch (NoSuchMethodException e) {
					log.debug(e);
				}
				if (!Is.empty(returnValue)) {
					returnValue = returnValue + getIdSeparator();
				}
				if (value != null) {
					returnValue = returnValue + value.toString();
				}
			}
			if (idPropertiesList.size() > 1) {
				returnValue = "<" + returnValue + ">";
			}
		return returnValue;
	}
	
	/**
	 * Returns the parent path of the node.
	 * @param object Object to be inspected.
	 * @return The path excluding the node name.
	 * @throws Exception
	 */
	public String getNodePath(Object object) throws Exception {
		String returnValue = "";
		try {
			Object value = PropertyUtils.getProperty(object, pathProperty);
			if (value != null) {
				returnValue = value.toString();
			}
		} catch (IllegalAccessException e) {
			log.error(e);
			throw new Exception(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e);
			throw new Exception(e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error(e);
			throw new Exception(e.getMessage());
		}
		return returnValue;
	}
	/**
	 * Sets the node path.
	 * @param object Object to be updated.
	 * @param value New path to be used.
	 * @throws Exception
	 */
	public void setNodePath(Object object, String value) throws Exception {
		PropertyUtils.setProperty(object, getPathProperty(), value);
	}
	
	/**
	 * Returns the expanded state of the object. If the expanded state is not
	 * persisted, then it returns the appropriate value according to the 
	 * definition of the expandedState property of @TreeView annotation.
	 * @param object Object to be inspected.
	 * @return State of the object.
	 */
	public boolean getNodeExpandedState(Object object) {
		Boolean returnValue = null;
		if (object != null) {
			returnValue =
				getExpandedStates().get(getNodeName(object));
		}
		if (returnValue == null) {
			returnValue = getInitialExpandedState();
			getExpandedStates().put(getNodeName(object), returnValue);
		}
		return returnValue;
	}
	
	/**
	 * Returns the complete path of the node including the node name.
	 * @param object Object to be inspected.
	 * @return Complete path.
	 * @throws Exception
	 */
	public String getNodeFullPath(Object object) throws Exception {
		String returnValue = "";
		String nodePath = getNodePath(object);
		String nodeName = getNodeName(object);
		if (nodePath.endsWith(getPathSeparator())) {
			returnValue = nodePath + nodeName;
		} else {
			returnValue = nodePath + getPathSeparator() + nodeName;
		}
		return returnValue;
	}
	
	/**
	 * If the property orderProperty of @TreeView annotation is defined,
	 *  returns the order of the object within its parent; otherwise returns 0.
	 * @param object Object to be inspected.
	 * @return Current order or zero.
	 */
	public int getNodeOrder(Object object) {
		Integer returnValue = 0;
		if (isOrderDefined()) {
			try {
				returnValue = (Integer)PropertyUtils.getProperty(object, getOrderProperty());
				if (returnValue == null) {
					returnValue = 0;
				}
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.error(e);
			}
		}
		return returnValue;
	}
	
	/**
	 * Changes the order of the node within its parent. Ignored if orderProperty is not defined.
	 * @param object Object to be updated.
 	 * @param value New order to be applied.
	 */
	public void setNodeOrder(Object object, int value) {
		if (isOrderDefined()) {
			try {
				PropertyUtils.setProperty(object, getOrderProperty(), value);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			} catch (NoSuchMethodException e) {
				log.error(e);
			}
		}
	}
	
	/**
	 * Sets a new expanded state for the object. If expandedProperty is not defined it is ignored. 
	 * @param object Object to be modified.
	 * @param value New state.
	 */
	public void setNodeExpandedState(Object object, boolean value) {
		Boolean currentState = null;
		if (object !=null) {
			getExpandedStates().get(getNodeName(object));
			if (currentState == null) {
				getExpandedStates().put(getNodeName(object), value);
			}
		}
	}

	/**
	 * @return the pathProperty
	 */
	public String getPathProperty() {
		return pathProperty;
	}
	/**
	 * @param pathProperty the pathProperty to set
	 */
	protected void setPathProperty(String pathProperty) {
		this.pathProperty = pathProperty;
	}
	/**
	 * @param idProperties the idProperties to set
	 */
	protected void setIdProperties(String idProperties) {
		this.idProperties = idProperties;
	}

	/**
	 * @return the idProperties
	 */
	public String getIdProperties() {
		return idProperties;
	}

	/**
	 * @return the orderProperty
	 */
	public String getOrderProperty() {
		return orderProperty;
	}
	/**
	 * @param orderProperty the orderProperty to set
	 */
	protected void setOrderProperty(String orderProperty) {
		this.orderProperty = orderProperty;
	}

	/**
	 * @return the initialExpandedState
	 */
	public boolean getInitialExpandedState() {
		return initialExpandedState;
	}

	/**
	 * @param initialExpandedState the initialExpandedState to set
	 */
	protected void setInitialExpandedState(boolean initialExpandedState) {
		this.initialExpandedState = initialExpandedState;
	}

	/**
	 * @return the keyIncrement
	 */
	public int getKeyIncrement() {
		return keyIncrement;
	}
	/**
	 * @param keyIncrement the keyIncrement to set
	 */
	protected void setKeyIncrement(int keyIncrement) {
		this.keyIncrement = keyIncrement;
	}

	/**
	 * @param pathSeparator the pathSeparator to set
	 */
	protected void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}

	/**
	 * @return the pathSeparator
	 */
	public String getPathSeparator() {
		return pathSeparator;
	}

	/**
	 * @param entityObject the entityObject to set
	 */
	protected void setEntityObject(boolean entityObject) {
		this.entityObject = entityObject;
	}

	/**
	 * @return the entityObject
	 */
	public boolean isEntityObject() {
		return entityObject;
	}

	public boolean isOrderDefined() {
		return orderDefined;
	}

	/**
	 * @param idSeparator the idSeparator to set
	 */
	protected void setIdSeparator(String idSeparator) {
		this.idSeparator = idSeparator;
	}

	/**
	 * @return the idSeparator
	 */
	public String getIdSeparator() {
		return idSeparator;
	}

	/**
	 * @param expandedStates the expandedStates to set
	 */
	public void setExpandedStates(Map<String, Boolean> expandedStates) {
		this.expandedStates = expandedStates;
	}

	/**
	 * @return the expandedStates
	 */
	public Map<String, Boolean> getExpandedStates() {
		if (expandedStates == null) {
			expandedStates = new HashMap<String, Boolean>();
		}
		return expandedStates;
	}
	
	/**
	 * Creates the implementation of TreeView reader
	 * @return Object implementing the ITreeViewReader
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public ITreeViewReader getTreeViewReaderImpl() {
		if (treeViewReader == null && !Is.emptyString(treeViewReaderName)) {
			try {
				Class clazz = Class.forName(treeViewReaderName);
				if (ITreeViewReader.class.isAssignableFrom(clazz)) {
					treeViewReader = (ITreeViewReader)clazz.newInstance();
				}
			} catch (ClassNotFoundException e) {
				log.error(e);
			} catch (InstantiationException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			}
			if (treeViewReader == null) {
				treeViewReader = new TreeViewReaderImpl();
			}
		}
		return treeViewReader;
	}

}
