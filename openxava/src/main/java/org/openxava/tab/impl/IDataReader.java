package org.openxava.tab.impl;

import org.openxava.util.*;

/**
 * Read data by chunks.
 * 
 * @author Javier Paniza 
 */
public interface IDataReader {
	
	  /**
	   * Obtain the next data chunk. <p> 
	   * 
	   * This method can be call from a <code>TableModel</code> to
	   * obtain data on demand.
	   * @throws SystemException
	   */
	  DataChunk nextChunk() ;
	  
	  /**
	   * Record (or object, or row) count of last consult.
	   * @throws SystemException
	   */
	  int getResultSize() ; 
	  
	  /**
	   * If you call this method the next time that you call
	   * {@link #nextChunk} you will obtain the first chunk and
	   * fresh data from db.
	   * @throws SystemException
	   */
	  void reset() ;


}
