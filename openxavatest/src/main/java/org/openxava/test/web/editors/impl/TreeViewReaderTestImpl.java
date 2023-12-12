/**
 * 
 */
package org.openxava.test.web.editors.impl;

import java.util.*;

import org.openxava.model.*;
import org.openxava.web.editors.*;

/**
 * @author Federico Alcantara
 *
 */
public class TreeViewReaderTestImpl implements ITreeViewReader {

	private String collectionModelName;
	
	@SuppressWarnings("rawtypes")
	private Map[] allKeys;
	
	private int lastReadRow = -1;
	
	private Object lastReadObject = null;
	
	/**
	 * @see org.openxava.web.editors.ITreeViewReader#initialize(java.lang.String, java.lang.String[], java.util.Map, java.util.Map[])
	 */
	@SuppressWarnings("rawtypes")
	public void initialize(String parentModelName, Map parentKey, String collectionModelName,  Map[] allKeys) {
		this.collectionModelName = collectionModelName;
		this.allKeys = allKeys;
	}

	/**
	 * @see org.openxava.web.editors.ITreeViewReader#close()
	 */
	public void close() throws Exception {
	}

	/**
	 * @see org.openxava.web.editors.ITreeViewReader#getRowObject()
	 */
	public Object getObjectAt(int rowIndex) throws Exception {
		if (rowIndex != lastReadRow) {
			lastReadObject = MapFacade.findEntity(collectionModelName, allKeys[rowIndex]);
			lastReadRow = rowIndex;
		}
		return lastReadObject;
	}

}
