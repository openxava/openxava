package org.openxava.hibernate.impl;

import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.event.spi.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

public class DefaultValueCalculatorsListener implements PreInsertEventListener {

	private static Log log = LogFactory.getLog(DefaultValueCalculatorsListener.class);
	
	public boolean onPreInsert(PreInsertEvent ev) {		
		String modelName = "unknow"; 		
		try {
			Object entity = ev.getEntity();
			if (!(entity instanceof IModel)) return false;
			IModel model = (IModel) entity;
			MetaModel metaModel = model.getMetaModel();
			if (!metaModel.hasDefaultCalculatorOnCreate()) return false;
			boolean multipleKey = metaModel.getAllKeyPropertiesNames().size() > 1;			
			modelName = metaModel.getName();
			PropertiesManager pm = new PropertiesManager(model);
			List propertyNames = Arrays.asList(ev.getPersister().getPropertyNames());			
			Object [] state = ev.getState();			
			for (Iterator itProperties=metaModel.getMetaPropertiesWithDefaultValueOnCreate().iterator(); itProperties.hasNext();) {
				MetaProperty pr = (MetaProperty) itProperties.next();
				if (pr.isKey() && !multipleKey) continue; // made by id generator				
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
					((IAggregateOidCalculator) calculator).setContainer(DefaultValueIdentifierGenerator.getCurrentContainerKey());
					((IAggregateOidCalculator) calculator).setCounter(DefaultValueIdentifierGenerator.getCurrentCounter());
				}
				DefaultValueIdentifierGenerator.resetAggregateOidInfo();
				
				Object value = calculator.calculate();				
				pm.executeSet(pr.getName(), value);				
				int i = propertyNames.indexOf(pr.getName());				
				if (i >= 0)	state[i]=value;				
			}				
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new HibernateException(XavaResources.getString("entity_create_error", modelName, ex.getLocalizedMessage()));
		}
		return false;
	}

}

