package org.openxava.mapping;

import java.util.*;



import org.openxava.component.*;
import org.openxava.util.*;

/**
 * Utility class for mapping issues.
 * 
 * @author Javier Paniza
 */
abstract public class Mapping {

	
	
	/**
	* @throws XavaException
	 */
	public static Collection getSchemas() {			
		Collection r = new HashSet();
		for (Iterator it = MetaComponent.getAllLoaded().iterator(); it.hasNext();) {
			MetaComponent comp = (MetaComponent) it.next();
			String schema = comp.getEntityMapping().getSchema();
			if (schema != null)	r.add(schema);
			for (Iterator itMappings = comp.getAggregateMappings().iterator(); itMappings.hasNext();) {
				ModelMapping mapping = (ModelMapping) itMappings.next();
				String aggregateSchema = mapping.getSchema();							 
				if (aggregateSchema != null) r.add(aggregateSchema);								
			}
		}		
		return r;
	}
	
	/**
	* @throws XavaException
	 */
	public static Collection getTables() {		
		Collection r = new HashSet();
		for (Iterator it = MetaComponent.getAllLoaded().iterator(); it.hasNext();) {
			MetaComponent comp = (MetaComponent) it.next();
			r.add(comp.getEntityMapping().getTable());									
			for (Iterator itMappings = comp.getAggregateMappings().iterator(); itMappings.hasNext();) {
				ModelMapping mapping = (ModelMapping) itMappings.next();
				r.add(mapping.getTable());												
			}
		}		
		return r;
	}

	/**
	* @throws XavaException
	 */
	public static Collection getTablesBySchema(String schema) {			
		Collection r = new HashSet();
		boolean withoutSchema = Is.emptyString(schema);
		for (Iterator it = MetaComponent.getAllLoaded().iterator(); it.hasNext();) {
			MetaComponent comp = (MetaComponent) it.next();
			String sch = comp.getEntityMapping().getSchema();						 
			if (sch != null) {								
				if (sch.equals(schema))	r.add(comp.getEntityMapping().getTable());
			}
			else if (withoutSchema){
				r.add(comp.getEntityMapping().getTable());
			}
			
			for (Iterator itMappings = comp.getAggregateMappings().iterator(); itMappings.hasNext();) {
				ModelMapping mapping = (ModelMapping) itMappings.next();
				String aggregateSchema = mapping.getSchema();							
				if (aggregateSchema != null) {								
					if (aggregateSchema.equals(schema))	r.add(mapping.getTable());
				}
				else if (withoutSchema){
					r.add(mapping.getTable());
				}				
			}
		}		
		return r;
	}

}
