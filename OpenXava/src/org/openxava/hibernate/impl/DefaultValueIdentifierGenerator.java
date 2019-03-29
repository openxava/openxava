package org.openxava.hibernate.impl;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.id.*;
import org.hibernate.service.*;
import org.hibernate.type.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * Executes the default-value-calculator for key properties. <p> 
 * 
 * @author Javier Paniza
 */

public class DefaultValueIdentifierGenerator implements IdentifierGenerator, Configurable {
	
	private static Log log = LogFactory.getLog(DefaultValueIdentifierGenerator.class);
	
	static class AggregateOidInfo {
		public int counter;
		public Object containerKey;
	}
	
	final private static ThreadLocal currentAggregateOidInfo = new ThreadLocal();
	
	private String property;

	public Serializable generate(SharedSessionContractImplementor implementor, Object object) throws HibernateException { 
		String modelName = "unknow";
		try {			
			IModel model = (IModel) object;
			MetaModel metaModel = model.getMetaModel();
			modelName = metaModel.getName();
			MetaProperty pr = (MetaProperty) metaModel.getMetaProperty(getProperty()); 
			PropertiesManager pm = new PropertiesManager(model);
			MetaCalculator metaCalculator = pr.getMetaCalculatorDefaultValue();
			ICalculator calculator = metaCalculator.createCalculator();
			PropertiesManager pmCalculator = new PropertiesManager(calculator);
			for (Iterator itSets=metaCalculator.getMetaSetsWithoutValue().iterator(); itSets.hasNext();) {
				MetaSet set = (MetaSet) itSets.next();
				pmCalculator.executeSet(set.getPropertyName(), pm.executeGet(set.getPropertyNameFrom()));
			}
			if (calculator instanceof IJDBCCalculator) {
				((IJDBCCalculator) calculator).setConnectionProvider(DataSourceConnectionProvider.getByComponent(metaModel.getMetaComponent().getName()));
			}
			if (calculator instanceof IModelCalculator) {
				((IModelCalculator) calculator).setModel(model);
			}
			if (calculator instanceof IEntityCalculator) {
				((IEntityCalculator) calculator).setEntity(model);
			}			
			if (calculator instanceof IAggregateOidCalculator) {
				((IAggregateOidCalculator) calculator).setContainer(getCurrentContainerKey());
				((IAggregateOidCalculator) calculator).setCounter(getCurrentCounter());
			}
			resetAggregateOidInfo();
			return (Serializable) calculator.calculate();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new HibernateException(XavaResources.getString("entity_create_error", modelName, ex.getLocalizedMessage()));
		}
	}

	static void resetAggregateOidInfo() { 	
		currentAggregateOidInfo.set(null);				
	}

	public String getProperty() {
		return property;
	}

	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException { 
		property = params.getProperty("property");	
	}
	
	static Object getCurrentContainerKey() { 
		AggregateOidInfo info = (AggregateOidInfo) currentAggregateOidInfo.get();
		if (info == null) return null;
		return info.containerKey;
	}
	
	public static void setCurrentContainerKey(Object containerKey) {
		AggregateOidInfo info = (AggregateOidInfo) currentAggregateOidInfo.get();
		if (info == null) {
			info = new AggregateOidInfo();			
			currentAggregateOidInfo.set(info);
		}
		info.containerKey = containerKey;		
	}
	
	static int getCurrentCounter() {	
		AggregateOidInfo info = (AggregateOidInfo) currentAggregateOidInfo.get();
		if (info == null) return 0;
		return info.counter;
	}
	
	public static void setCurrentCounter(int counter) {
		AggregateOidInfo info = (AggregateOidInfo) currentAggregateOidInfo.get();
		if (info == null) {
			info = new AggregateOidInfo();			
			currentAggregateOidInfo.set(info);
		}
		info.counter = counter;		
	}
	
}
