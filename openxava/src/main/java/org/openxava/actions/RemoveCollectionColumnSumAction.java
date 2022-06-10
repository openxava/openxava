package org.openxava.actions;

import org.openxava.view.*;

/**
 * @since 5.9
 * @author Javier Paniza 
 */

public class RemoveCollectionColumnSumAction extends TabBaseAction {
	
	private String collection;
	private String property;
	
	public void execute() throws Exception {
		View collectionView = getView().getSubview(collection);
		collectionView.removeCollectionSumProperty(property);
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

}
