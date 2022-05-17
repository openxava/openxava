package org.openxava.mapping;



import org.openxava.model.meta.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class AggregateMapping extends ModelMapping {

	private String modelName;
	
		
	public void addReferenceMapping(ReferenceMapping referenceMapping) throws XavaException { 		
		super.addReferenceMapping(referenceMapping);
		if (!getMetaModel().containsMetaReference(referenceMapping.getReference())) {
			MetaReference r = new MetaReference();
			r.setReferencedModelName(Strings.firstUpper(referenceMapping.getReference()));
			r.setAggregate(false); 
			getMetaModel().addMetaReference(r);
		}

	}
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}


	public MetaModel getMetaModel() throws XavaException {		
		return getMetaComponent().getMetaAggregate(getModelName());
	}
		
}
