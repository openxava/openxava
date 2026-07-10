package org.openxava.tab.impl;

import org.openxava.util.*;
import java.util.*;

import org.openxava.model.*;

/**
 * Extended TableModel. <p>
 *
 * Allows to obtain a object from a row, refresh and to know the result size.<br>
 *  
 * Util for use in {@link EntityTab}.<br>
 *
 * @author  Javier Paniza
 */

public interface IXTableModel extends IObjectTableModel, IRefreshTableModel {

	// Included in IObjectTableModel, but by CORBA whims
	Object getObjectAt(int rowIndex) throws FinderException;
	// Included in IRefreshTableModel, but by CORBA whims
	void refresh() throws TabException;
	
	void removeAllRows();
	
	/**
	 * @since 5.0
	 */
	void removeRow(Map keyValues) throws FinderException; 
  
	/**
	 * Total count of objects represented by this table model. <p>
	 * Cantidad de objetos total representados por el table model. <p>
	 * 
	 * <code>getRowCount()</code> in another side, 
	 * return the loaded objects count, not total.<br>
	 * @throws SystemException
	 */
	int getTotalSize() ;
	/**
	* @throws SystemException
	 */
	Number getSum(String property) ;
	
	
	/** @since 5.7 */
	int getChunkSize(); 
	
	/** @since 5.7 */
	boolean isAllLoaded(); 
	
}
