package org.openxava.model.meta;




import org.openxava.component.*;
import org.openxava.mapping.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MetaAggregateForCollection extends MetaAggregate {
		
	/**
	* @throws XavaException
	 */
	public ModelMapping getMapping() {
		return getMetaComponent().getAggregateMapping(getName());
	}
		
	public void setMetaComponent(MetaComponent metaComponent) {
		super.setMetaComponent(metaComponent);
	}
			
	/**
	* @throws XavaException
	 */
	public Class getBeanClass() {
		throw new UnsupportedOperationException ("Still not supported");
		
	}
	
	public String getContainerReference() {
		if (super.getContainerReference() == null) setContainerReference(Strings.firstLower(getContainerModelName()));  
		return super.getContainerReference();
	}
	
}

