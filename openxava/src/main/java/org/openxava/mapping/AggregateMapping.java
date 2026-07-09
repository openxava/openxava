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


	/**
	* @throws XavaException
	 */
	public MetaModel getMetaModel() {		
		return getMetaComponent().getMetaAggregate(getModelName());
	}
		
}
