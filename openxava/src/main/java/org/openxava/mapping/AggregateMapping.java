package org.openxava.mapping;



import org.openxava.model.meta.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class AggregateMapping extends ModelMapping {

	private String modelName;
		
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
