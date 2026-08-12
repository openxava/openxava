package org.openxava.util;

import java.sql.*;
import java.util.logging.*;

/**
 * 
 * @author Javier Paniza
 */

public class DBServer {
	
	private static int port;
	
	public static void start(String dbName) throws Exception {
		start(dbName, 1666); 
	}
	
	/** @since 7.1 */
	public static void start(String dbName, int port) throws Exception { 
		DBServer.port = port;
		new Thread(() -> {
			org.hsqldb.Server hsqlServer = new org.hsqldb.Server();
	        hsqlServer.setSilent(true);
	        hsqlServer.setDatabaseName(0, "");
	        hsqlServer.setDatabasePath(0, "file:data/" + dbName);
	        hsqlServer.setPort(port);
	        hsqlServer.start();     
	        Logger.getLogger("").setLevel(Level.INFO);
		}).start();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
	}
	
	/**
	 * Shuts down the HSQLDB server cleanly, persisting all in-memory data
	 * to the <code>.script</code> file and clearing the <code>.log</code> file.
	 * @since 8.0
	 */
	public static void shutdown() {
		try (Connection con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:" + port, "sa", "");
			Statement st = con.createStatement()) {
			st.execute("SHUTDOWN");
		}
		catch (SQLException ex) {
			// Server may already be down or not yet started
		}
	}
	
	public static void runManager() {
		String [] url = { "--url", "jdbc:hsqldb:hsql://localhost:1666" };
		org.hsqldb.util.DatabaseManagerSwing.main(url);
	}

}
