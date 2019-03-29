package org.openxava.filters.meta;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.filters.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * @author Javier Paniza
 */
public class MetaFilter implements Serializable {
	
	private static Log log = LogFactory.getLog(MetaFilter.class);
	
	private IFilter filter;
	private String className;
	private Collection metaSets;
	
	
	public void _addMetaSet(MetaSet metaSet) {
		if (metaSets == null) {
			metaSets = new ArrayList();
		}
		metaSets.add(metaSet);		
	}
	
	public IFilter createFilter() throws XavaException {
		try {
			Object o = Class.forName(getClassName()).newInstance();
			if (!(o instanceof IFilter)) {
				throw new XavaException("implements_required", getClassName(), IFilter.class.getName());
			}
			IFilter filter = (IFilter) o;
			if (hasMetaSets()) {
				assignPropertyValues(filter);
			}						
			return filter;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_filter_error", getClassName());
		}
	}

	private void assignPropertyValues(IFilter filter) throws Exception {
		PropertiesManager mp = new PropertiesManager(filter);
		Iterator it = getMetaSets().iterator();
		while (it.hasNext()) {
			MetaSet metaSet = (MetaSet) it.next();
			mp.executeSetFromString(metaSet.getPropertyName(), metaSet.getValue());			
		}		
	}

	/**
	 * @return Not null
	 */
	public Collection getMetaSets() {
		return metaSets==null?new ArrayList():metaSets;
	}
	
	public boolean hasMetaSets() {
		return metaSets != null;
	}

	public String getClassName() {
		return className;
	}
	
	public String getPropertyNameForPropertyFrom(String nameForPropertyFrom) throws ElementNotFoundException {
		if (metaSets==null) 		 
			throw new ElementNotFoundException("filter_not_value_from", nameForPropertyFrom);
		Iterator it = metaSets.iterator();
		while (it.hasNext()) {
			MetaSet metaSet = (MetaSet) it.next();
			if (metaSet.getPropertyNameFrom().equals(nameForPropertyFrom)) {
				return metaSet.getPropertyName();
			}
		}	
		throw new ElementNotFoundException("filter_not_value_from", nameForPropertyFrom);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Object filter(Object[] objects) throws FilterException, XavaException {
		return getFilter().filter(objects);		
	}
	
	public IFilter getFilter() throws XavaException {
		if (filter == null) {
			filter = createFilter();
		}
		return filter;
	}

}
