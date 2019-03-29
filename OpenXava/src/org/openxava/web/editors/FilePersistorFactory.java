package org.openxava.web.editors;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * For obtaining a instance of the {@link IFilePersistor} configured in the system. <p>
 * 
 * @author Jeromy Altuna
 */
public class FilePersistorFactory {
	
	private static Log log = LogFactory.getLog(FilePersistorFactory.class);
	private static IFilePersistor instance;
	
	public static IFilePersistor getInstance() {
		if (instance == null) {
			try {
				instance = (IFilePersistor) Class.forName(XavaPreferences.getInstance().getFilePersistorClass()).newInstance();
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new RuntimeException(XavaResources.getString("file_persistor_creation_error", 
										   XavaPreferences.getInstance().getFilePersistorClass()));
			}
		}		
		return instance;
	}	
}
