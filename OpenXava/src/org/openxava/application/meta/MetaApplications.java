package org.openxava.application.meta;


import java.util.*;



import org.openxava.application.meta.xmlparse.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class MetaApplications {
	
	private static Collection applicationNames;

	private static Map metaAplicacions;
	
	
	
	/**
	 * Only call this from parser.
	 */
	public static void _addMetaApplication(MetaApplication application) throws XavaException {
		if (metaAplicacions == null) {
			throw new XavaException("only_from_parse", "MetaApplications._addMetaApplication");
		}
		metaAplicacions.put(application.getName(), application);
	}
	
	/**
	 * @return Collection of <tt>MetaApplication</tt>. Not null.
	 */
	public static Collection getMetaApplications() throws XavaException {
		if (metaAplicacions == null) {
			configure();
		}
		return metaAplicacions.values();
	}
	
	private static void configure() throws XavaException {
		metaAplicacions = new HashMap();
		ApplicationParser.configureApplications();
	}
	
	public static MetaApplication getMetaApplication(String name) throws ElementNotFoundException, XavaException {
		if (metaAplicacions == null) {
			configure();
		}
		MetaApplication result = (MetaApplication) metaAplicacions.get(name);
		if (result == null) {
			throw new ElementNotFoundException("application_not_found", name);
		}
		return result;
	}

	public static Collection getApplicationsNames() throws XavaException {
		if (applicationNames == null) {
			applicationNames = new ArrayList();
			Iterator it = getMetaApplications().iterator();
			while (it.hasNext()) {
				MetaApplication ap = (MetaApplication) it.next();
				applicationNames.add(ap.getName());
			}
		}
		return applicationNames;
	}
	
}
