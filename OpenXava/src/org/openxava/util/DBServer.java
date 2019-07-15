package org.openxava.util;

import java.util.logging.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

public class DBServer {
	
	public static void start(String dbName) throws Exception {
		new Thread(() -> {
			org.hsqldb.Server hsqlServer = new org.hsqldb.Server();
	        hsqlServer.setSilent(true);
	        hsqlServer.setDatabaseName(0, "");
	        hsqlServer.setDatabasePath(0, "file:data/" + dbName);
	        hsqlServer.setPort(1666);
	        hsqlServer.start();        
	        Logger.getLogger("").setLevel(Level.INFO);
		}).start();
	}

}
