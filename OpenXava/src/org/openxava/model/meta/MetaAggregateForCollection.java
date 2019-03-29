package org.openxava.model.meta;




import org.openxava.component.*;
import org.openxava.mapping.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MetaAggregateForCollection extends MetaAggregate {
		
	public ModelMapping getMapping() throws XavaException {
		return getMetaComponent().getAggregateMapping(getName());
	}
		
	public void setMetaComponent(MetaComponent metaComponent) {
		super.setMetaComponent(metaComponent);
	}
			
	public Class getBeanClass() throws XavaException {
		throw new UnsupportedOperationException ("Still not supported");
		
	}
	
	public String getContainerReference() {
		if (super.getContainerReference() == null) setContainerReference(Strings.firstLower(getContainerModelName()));  
		return super.getContainerReference();
	}
	
}

