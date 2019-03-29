package org.openxava.tab.impl;

import java.util.*;

import org.openxava.tab.meta.*;

/**
 * Provides data and specific behavior from the underlying query technology. <p>
 * 
 * @author  Javier Paniza
 */

public interface ITabProvider extends ISearch, IDataReader {
	 
	void setMetaTab(MetaTab metaTab);
	
	Number getSum(String property);  
  
	String getSelectBase(); 
  
	void setChunkSize(int chunkSize); 
  
	String toQueryField(String propertyName); 
  
	void setCurrent(int index);
	
	Collection<TabConverter> getConverters();
	
	boolean usesConverters();
  
}
