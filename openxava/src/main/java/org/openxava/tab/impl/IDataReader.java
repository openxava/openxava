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
	   */
	  DataChunk nextChunk() throws SystemException;
	  
	  /**
	   * Record (or object, or row) count of last consult.
	   */
	  int getResultSize() throws SystemException; 
	  
	  /**
	   * If you call this method the next time that you call
	   * {@link #nextChunk} you will obtain the first chunk and
	   * fresh data from db.
	   */
	  void reset() throws SystemException;


}
