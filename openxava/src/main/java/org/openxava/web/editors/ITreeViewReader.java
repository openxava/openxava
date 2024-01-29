/**
 * 
 */
package org.openxava.web.editors;

import java.util.*;

/**
 * Defines the contract for tree view readers.
 * @author Federico Alcantara
 *
 */
public interface ITreeViewReader {

	/**
	 * Initializes the reader
	 * @param parentModelName name of the parent model of the collection
	 * @param parentKey key for the parent object
	 * @param collectionModelName name of the collection model
	 * @param allKeys all the keys to be read from the collection
	 * @param columnNames name of the columns in their column order.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	void initialize(String parentModelName, Map parentKey, String collectionModelName, Map[] allKeys) throws Exception;
	
	/**
	 * Ends the process of reading
	 * @throws Exception
	 */
	void close() throws Exception;
	
	/**
	 * Returns the object representing the column
	 * @param rowIndex index of the row to read
	 * @return
	 */
	Object getObjectAt(int rowIndex) throws Exception;
	
}
