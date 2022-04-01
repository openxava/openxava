package org.openxava.hibernate.impl;

import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.event.spi.*;
import org.openxava.converters.*;
import org.openxava.mapping.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

public class ReferenceConverterToDBListener implements PreInsertEventListener, PreUpdateEventListener {	

	private static Log log = LogFactory.getLog(ReferenceConverterToDBListener.class);
	
	public boolean onPreInsert(PreInsertEvent ev) {		
		if (!(ev.getEntity() instanceof IModel)) return false;
		applyConverter((IModel) ev.getEntity(), 
				Arrays.asList(ev.getPersister().getPropertyNames()),
				ev.getState()); 		
		return false;
	}

	public boolean onPreUpdate(PreUpdateEvent ev) {		
		if (!(ev.getEntity() instanceof IModel)) return false;
		applyConverter((IModel) ev.getEntity(), 
				Arrays.asList(ev.getPersister().getPropertyNames()),
				ev.getState());		
		return false;
	}
	
	private void applyConverter(IModel model, List propertyNames, Object [] state) {
		String currentReference = "";
		try {			
			MetaModel metaModel = model.getMetaModel();
			ModelMapping mapping = metaModel.getMapping();
			if (!mapping.hasReferenceConverters()) return;
			Collection referenceMappings =  mapping.getReferenceMappingsWithConverter();
			Iterator it = referenceMappings.iterator();
			while (it.hasNext()) {
				ReferenceMapping referenceMapping = (ReferenceMapping) it.next();
				if (mapping.isReferenceOverlappingWithSomeProperty(referenceMapping.getReference())) continue;
				PropertiesManager pm = new PropertiesManager(model);
				Object referencedObject = pm.executeGet(referenceMapping.getReference());
				MetaReference metaReference = metaModel.getMetaReference(referenceMapping.getReference());
				currentReference = metaReference.getName();
				if (referencedObject == null) {
					referencedObject = metaReference.getMetaModelReferenced().getPOJOClass().newInstance(); 
				}
			
				Collection detailMappings = referenceMapping.getDetails();
				Iterator itd = detailMappings.iterator();
				while (itd.hasNext()) {
					ReferenceMappingDetail referenceMappingDetail = (ReferenceMappingDetail) itd.next();
					if (referenceMappingDetail.hasConverter()) {
						IConverter conv = referenceMappingDetail.getConverter();
						PropertiesManager pm2 = new PropertiesManager(referencedObject);
						Object propertyValue = pm2.executeGet(referenceMappingDetail.getReferencedModelProperty());
						pm2.executeSet(referenceMappingDetail.getReferencedModelProperty(),conv.toDB(propertyValue));
					}
				}
                int i;
                if(metaReference.getMetaModel() instanceof MetaAggregate)
                    i = propertyNames.indexOf(referenceMapping.getReference());
                else
                    i = propertyNames.indexOf(currentReference);
                state[i]=referencedObject;
			}
		} 
		catch(Exception ex){ 
			log.error(ex.getMessage(), ex);
			throw new HibernateException(XavaResources.getString("generator.conversion_error", currentReference, model.getClass(), ""));
		}
	}

}

