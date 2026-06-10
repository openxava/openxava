package org.openxava.session;

import java.util.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */
public class CutCollectionElements implements java.io.Serializable {

	private String parentModel;
	
	private Map<String, Object> parentKey;
	
	private String collectionName;
	
	private String collectionModel;
	
	private Collection<Map<String, Object>> elements;
	
	public void clear() {
		parentModel = null;
		parentKey = null;
		collectionName = null;
		elements = null;
	}
	
	public void addElement(Map<String, Object> element) {
		if (elements == null) elements = new ArrayList<>();
		elements.add(element);
	}
	
	public int elementsCount() {
		if (elements == null) return 0;
		return elements.size();
	}
	
	public String getParentModel() {
		return parentModel;
	}

	public void setParentModel(String parentModel) {
		this.parentModel = parentModel;
	}

	public Map<String, Object> getParentKey() {
		return parentKey;
	}

	public void setParentKey(Map<String, Object> parentKey) {
		this.parentKey = parentKey;
	}

	public Collection<Map<String, Object>> getElements() {
		return elements == null?Collections.EMPTY_LIST:elements;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getCollectionModel() {
		return collectionModel;
	}

	public void setCollectionModel(String collectionModel) {
		this.collectionModel = collectionModel;
	}
	
}
