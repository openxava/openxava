package org.openxava.tab.impl;

import java.rmi.*;
import java.util.*;

public interface IEntityTabDataProvider {
	
	DataChunk nextChunk(ITabProvider tabProvider, String modelName, List propertiesNames, Collection tabCalculators, Map keyIndexes) throws RemoteException;
	int getResultSize(ITabProvider tabProvider) throws RemoteException;
	Number getSum(ITabProvider tabProvider, String property);
		
}
