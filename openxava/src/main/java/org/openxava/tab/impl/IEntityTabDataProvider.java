package org.openxava.tab.impl;

import org.openxava.util.*;
import java.util.*;

public interface IEntityTabDataProvider {
	
	/**
	* @throws SystemException
	 */
	DataChunk nextChunk(ITabProvider tabProvider, String modelName, List propertiesNames, Collection tabCalculators, Map keyIndexes) ;
	/**
	* @throws SystemException
	 */
	int getResultSize(ITabProvider tabProvider) ;
	Number getSum(ITabProvider tabProvider, String property);
		
}
