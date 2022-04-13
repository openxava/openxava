package org.openxava.model.meta;



import org.openxava.component.*;
import org.openxava.util.*;


/**
 * @author: Javier Paniza
 */
abstract public class MetaAggregate extends MetaModel {
	
	
	
	static MetaAggregate getAggregate(String name) throws ElementNotFoundException, XavaException {
		int idx = name.indexOf('.');
		if (idx < 0) {
			throw new ElementNotFoundException("aggregate_need_qualified", name);
		}
		String component = name.substring(0, idx);
		String aggregate = name.substring(name.lastIndexOf('.') + 1);
		return MetaComponent.get(component).getMetaAggregate(aggregate);
	}
	
	
	public void setName(String newName) {
		super.setName(newName);
	}
	
	public String getId() {
		return getMetaComponent().getName() + "." + getName();		
	}
				
}