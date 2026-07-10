package org.openxava.tab.impl;

import org.openxava.util.*;
import java.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class EntityTabBean implements IEntityTabDataProvider {
	
	private EntityTabDataProvider dataProvider = new EntityTabDataProvider();
	
	public EntityTabBean() {
	}
	
	/**
	* @throws SystemException
	 */
	public DataChunk nextChunk(ITabProvider tabProvider, String modelName, List propertiesNames, Collection tabCalculators, Map keyIndexs) {
		return dataProvider.nextChunk(tabProvider, modelName, propertiesNames, tabCalculators, keyIndexs);
	}

	public int getResultSize(ITabProvider tabProvider) {	
		return dataProvider.getResultSize(tabProvider);		
	}
	
	public Number getSum(ITabProvider tabProvider, String property) { 	
		return dataProvider.getSum(tabProvider, property);				
	}
		
}
