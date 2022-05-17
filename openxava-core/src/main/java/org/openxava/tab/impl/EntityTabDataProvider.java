package org.openxava.tab.impl;

import java.io.*;
import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.converters.*;
import org.openxava.mapping.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;


/**
 * 
 * @author Javier Paniza
 */
public class EntityTabDataProvider implements IEntityTabDataProvider, Serializable {
	
	private static Log log = LogFactory.getLog(EntityTabDataProvider.class);
	
	private String componentName;
	private IConnectionProvider connectionProvider;
	private boolean xmlComponent;
		
	public DataChunk nextChunk(ITabProvider tabProvider, String modelName, List propertiesNames, Collection tabCalculators, Map keyIndexes /*, Collection tabConverters*/) throws RemoteException {		
		DataChunk tv = null;
		try {
			tv = tabProvider.nextChunk();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_next_chunk_error"));
		}
		
		List data = tv.getData();
		int l = data.size();
		
		// Conversion
		try {
			Collection<TabConverter> tabConverters = tabProvider.getConverters(); 
			if (tabConverters != null) {
				for (int i = 0; i < l; i++) {
					data.set(i, doConversions((Object[]) data.get(i), tabConverters));
				}
			}
		}
		catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_conversion_error"));
		}
				
		// Calculations
		try {
			if (tabCalculators != null) {
				for (int i = 0; i < l; i++) {
					data.set(i, doCalculations(modelName, (Object[]) data.get(i), tabCalculators, keyIndexes, propertiesNames));
				}
			}
		}
		catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_calculate_properties_error"));
		}

		return tv;
	}
	
	public IConnectionProvider getConnectionProvider() throws RemoteException {
		if (connectionProvider == null) {			 		
			try {
				connectionProvider = DataSourceConnectionProvider.createByComponent(getComponentName());
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new RemoteException(XavaResources.getString("error_obtaining_connection_provider"));
			}
		}
		return connectionProvider;		
	}	

	private Object[] doCalculations(String modelName, Object[] row, Collection tabCalculators, Map keyIndexes, List propertiesNames) throws XavaException {
		Object entity = null;
		Iterator itCalculators = tabCalculators.iterator();
		while (itCalculators.hasNext()) {
			TabCalculator tabCalculator = (TabCalculator) itCalculators.next();
			try {
				if (xmlComponent) { 	
					PropertiesManager mpCalculator =
						tabCalculator.getPropertiesManager();
					MetaSetsContainer metaCalculator =
						tabCalculator.getMetaCalculator();	
					if (metaCalculator.containsMetaSets()) {
						Iterator itMetaSets =
							metaCalculator.getMetaSetsWithoutValue().iterator();							
						int idx = tabCalculator.getPropertyName().indexOf('.');
						String ref = "";
						if (idx >= 0) {
							ref = tabCalculator.getPropertyName().substring(0, idx + 1);
						}
						while (itMetaSets.hasNext()) {
							MetaSet metaSet = (MetaSet) itMetaSets.next();
							Object value =
								getValue(ref + metaSet.getPropertyNameFrom(), row, propertiesNames);
							try {	
								mpCalculator.executeSet(
									metaSet.getPropertyName(),
									value);
							}
							catch (PropertiesManagerException ex) {
								throw new XavaException("calculator_property_not_found", metaSet.getPropertyName(), value.getClass().getName());
							}
						}
					}
				}
				ICalculator calculator = tabCalculator.getCalculator();
				if (calculator instanceof IModelCalculator) {
					if (entity == null) entity = getEntity(modelName, row, keyIndexes);
					((IModelCalculator) calculator).setModel(getEntityForCalculator(entity, tabCalculator));
				}
				if (calculator instanceof IEntityCalculator) {
					if (entity == null) entity = getEntity(modelName, row, keyIndexes); 
					((IEntityCalculator) calculator).setEntity(getEntityForCalculator(entity, tabCalculator));
				}				
				if (calculator instanceof IJDBCCalculator) {
					((IJDBCCalculator) calculator).setConnectionProvider(getConnectionProvider());
				}
				row[tabCalculator.getIndex()] = calculator.calculate();
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("tab_calculate_property_warning", tabCalculator.getPropertyName()), ex);
				row[tabCalculator.getIndex()] = "ERROR";
			}
		}
		return row;		
	}
	
	private Object getEntityForCalculator(Object entity, TabCalculator tabCalculator) throws Exception {
		int idx = tabCalculator.getPropertyName().lastIndexOf('.');
		if (idx < 0) return entity;
		String ref = tabCalculator.getPropertyName().substring(0, idx);
		PropertiesManager pm = new PropertiesManager(entity);
		return pm.executeGet(ref);
	}


	/**
	 * Return the entity associated to the sent row. <p>
	 *
	 * @param row  Complete row with tabular data
	 * @param keyIndexes Map with names and indexes of key
	 * @exception  FinderException  If cannot find the row
	 * @exception  NullPointerException  If <tt>row == null</tt>.
	 */
	private Object getEntity(String modelName, Object[] row, Map keyIndexes)
		throws FinderException, XavaException, RemoteException {
		if (keyIndexes == null) return null;
		Iterator it = keyIndexes.entrySet().iterator();
		Map key = new HashMap();		
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			String propertyName = (String) e.getKey();
			int index = ((Integer) e.getValue()).intValue();			
			key.put(propertyName, row[index]);						
		}		
		return MapFacade.findEntity(modelName, key);
	}
		
	private Object[] doConversions(Object[] row, Collection<TabConverter> tabConverters) throws XavaException {				
		for (TabConverter tabConverter: tabConverters) {
			try {				
				int idx = tabConverter.getIndex();				
				if (tabConverter.hasMultipleConverter()) {					
					IMultipleConverter converter = tabConverter.getMultipleConverter();
					PropertiesManager mp = new PropertiesManager(converter);					
					Iterator itCmpFields = tabConverter.getCmpFields().iterator();
					while (itCmpFields.hasNext()) {
						CmpField field = (CmpField) itCmpFields.next();
						Object value = row[tabConverter.getIndex(field)]; 
						mp.executeSet(field.getConverterPropertyName(), value);					
					}										
					row[idx] = converter.toJava();															
				}
				else {				
					IConverter converter = tabConverter.getConverter();					
					row[idx] = converter.toJava(row[idx]);					
				}
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("tab_conversion_property_warning", tabConverter.getPropertyName()),ex);
				row[tabConverter.getIndex()] = "ERROR";
			}
		}
		return row;		
	}
	
	private Object getValue(String propertyName, Object[] values, List propertiesNames)
		throws XavaException {
		return values[propertiesNames.indexOf(propertyName)];
	}
	
	public int getResultSize(ITabProvider tabProvider) {	
		try {
			return tabProvider.getResultSize();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new EJBException(XavaResources.getString("tab_result_size_error"));
		}
	}	
	
	public Number getSum(ITabProvider tabProvider, String property) { 	
		try {
			return tabProvider.getSum(property);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new EJBException(XavaResources.getString("total_problem")); 
		}
	}
		
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		if (Is.equal(this.componentName, componentName)) return;
		this.xmlComponent = !MetaComponent.get(componentName).getMetaEntity().isAnnotatedEJB3();  
		this.componentName = componentName;
	}

}
