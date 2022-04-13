package org.openxava.model.impl;


import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;


public interface IMapFacadeImpl {
	
	Object create(UserInfo userInfo, String modelName, Map values)
		throws 
			CreateException, ValidationException, 
			XavaException, RemoteException;
	
	Map getValuesByAnyProperty(UserInfo userInfo, String modelName, Map searchingValues, Map memberNames)
		throws FinderException, XavaException, RemoteException; 	
		
	Map getValues(UserInfo userInfo, String modelName, Map keyValues, Map memberNames)
		throws FinderException, XavaException, RemoteException;
		
	Map getValues(UserInfo userInfo, String modelName, Object modelObject, Map memberNames)
		throws XavaException, RemoteException;
	
	Map getKeyValues(UserInfo userInfo, String modelName, Object modelObject) 
		throws XavaException, RemoteException;	
	
	void setValues(UserInfo userInfo, String modelName, Map keyValues, Map values)
		throws FinderException, ValidationException, XavaException, RemoteException;
	
	void setValuesNotTracking(UserInfo userInfo, String modelName, Map keyValues, Map values)
			throws FinderException, ValidationException, XavaException, RemoteException; 
				
	void delete(UserInfo userInfo, String modelName, Map keyValues)
		throws RemoveException, XavaException, ValidationException, RemoteException;
		
	Object findEntity(UserInfo userInfo, String modelName, Map keyValues)
		throws FinderException, java.rmi.RemoteException;
		
	Map createReturningValues(UserInfo userInfo, String modelName, Map values)
				throws CreateException, XavaException, ValidationException, RemoteException;
				
	Messages validate(UserInfo userInfo, String modelName, Map values) throws XavaException, RemoteException;
	
	Messages validateIncludingMissingRequired(UserInfo userInfo, String modelName, Map values, String containerReference) throws XavaException, RemoteException;   

	Object createAggregate(UserInfo userInfo, String modelName, Map keyContainer, String collectionName, Map values)
		throws CreateException,ValidationException, XavaException, RemoteException; 
	
	Map createAggregateReturningKey(UserInfo userInfo, String modelName, Map containerKeyValues, String collectionName, Map values)  
		throws CreateException,ValidationException, XavaException, RemoteException; 

	
	Object createAggregate(UserInfo userInfo, String modelName, Map keyContainer, int counter, Map values)
		throws CreateException,ValidationException, XavaException, RemoteException; 
 
	Object createAggregate(UserInfo userInfo, String modelName, Object container, int counter, Map values) 
		throws CreateException,ValidationException, XavaException, RemoteException;
		
	Map createAggregateReturningKey(UserInfo userInfo, String modelName, Map containerKeyValues, int counter, Map values) 
		throws CreateException,ValidationException, XavaException, RemoteException; 
		 
	Map createReturningKey(UserInfo userInfo, String modelName, Map values)		 
		throws CreateException, XavaException, ValidationException, RemoteException;
	
	Map createNotValidatingCollections(UserInfo userInfo, String modelName, Map values) 
		throws CreateException, XavaException, ValidationException, RemoteException; 

	void addCollectionElement(UserInfo userInfo, String modelName, Map keyValues, String collectionName, Map collectionElementKeyValue) 
		throws FinderException, ValidationException, XavaException,  RemoteException; 
	
	void removeCollectionElement(UserInfo userInfo, String modelName, Map keyValues, String collectionName, Map collectionElementKeyValue) 
		throws RemoveException, FinderException, ValidationException, XavaException,  RemoteException; 
	
	void moveCollectionElementToAnotherCollection(UserInfo userInfo, 
		String sourceModelName, Map sourceKeyValues, String sourceCollectionName, 
		String targetModelName, Map targetKeyValues, String targetCollectionName,
		Map collectionElementKeyValues)   
		throws FinderException,	ValidationException, XavaException, RemoteException;
	
	void moveCollectionElement(UserInfo userInfo, String modelName, Map keyValues, String collectionName, int from, int to)  
		throws FinderException, XavaException,  RemoteException; 	
		 			
	Object getKey(MetaModel metaModel, Map keyValues) throws XavaException, RemoteException;
	
	void reassociate(Object entity) throws RemoteException;
	
	void commit(UserInfo userInfo) throws RemoteException; 
	
}