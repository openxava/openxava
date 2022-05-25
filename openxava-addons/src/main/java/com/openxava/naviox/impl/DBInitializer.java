package com.openxava.naviox.impl;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.*;

/**
 * tmr
 * 
 * @since 7.0
 * @author Javier Paniza
 */

public class DBInitializer {
	
	private static Log log = LogFactory.getLog(Modules.class);

	public static void init() {
		try {
			Class dbClass = Class.forName("com.openxava.naviox.impl.DB");
			XObjects.execute(dbClass, "init");
		} 
		catch (ClassNotFoundException e) {
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			// Never
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}		
	}

}
