package org.openxava.util;

import java.sql.*;
import java.util.logging.*;

/**
 * 
 * @author Javier Paniza
 */

public class DBServer {
	
	private static int port;
	private static String dbName;
	
	public static void start(String dbName) throws Exception {
		start(dbName, 1666); 
	}
	
	/** @since 7.1 */
	public static void start(String dbName, int port) throws Exception { 
		DBServer.port = port;
		DBServer.dbName = dbName;
		unzipLobs();
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
		zipLobs();
	}

	/**
	 * Before starting HSQLDB, restores the .lobs file from .lobs.zip if it exists. <p>
	 * The .lobs file is too large for Git, so it is stored compressed.
	 * @since 8.0
	 */
	private static void unzipLobs() throws Exception {
		var zip = java.nio.file.Path.of("data", dbName + ".lobs.zip");
		if (!java.nio.file.Files.exists(zip)) return;
		long start = System.currentTimeMillis();
		try (var zis = new java.util.zip.ZipInputStream(java.nio.file.Files.newInputStream(zip))) {
			zis.getNextEntry();
			zis.transferTo(java.nio.file.Files.newOutputStream(java.nio.file.Path.of("data", dbName + ".lobs")));
		}
		java.nio.file.Files.delete(zip);
		// We use System.out instead of log because at this point the logging system is not yet configured
		System.out.println("[DBServer]: " + XavaResources.getString("lobs_decompressed", System.currentTimeMillis() - start) + ".");
	}

	/**
	 * After shutting down HSQLDB, compresses the .lobs file to .lobs.zip and deletes the .lobs. <p>
	 * The .lobs file is too large for Git, so it is stored compressed.
	 * @since 8.0
	 */
	private static void zipLobs() {
		if (dbName == null) return;
		var lobs = java.nio.file.Path.of("data", dbName + ".lobs");
		if (!java.nio.file.Files.exists(lobs)) return;
		long start = System.currentTimeMillis();
		try (var zos = new java.util.zip.ZipOutputStream(java.nio.file.Files.newOutputStream(
				java.nio.file.Path.of("data", dbName + ".lobs.zip")))) {
			zos.putNextEntry(new java.util.zip.ZipEntry(dbName + ".lobs"));
			java.nio.file.Files.copy(lobs, zos);
			zos.closeEntry();
			java.nio.file.Files.delete(lobs);
			// We use System.out instead of log because at this point the logging system is already shut down
			System.out.println("[DBServer]: " + XavaResources.getString("lobs_compressed", System.currentTimeMillis() - start) + ".");
		}
		catch (Exception ex) {
			// We use System.out instead of log because at this point the logging system is already shut down
			System.out.println("[DBServer]: " + XavaResources.getString("lobs_compression_error", ex.getMessage()));
			ex.printStackTrace();
		}
	}
	
	public static void runManager() {
		String [] url = { "--url", "jdbc:hsqldb:hsql://localhost:1666" };
		org.hsqldb.util.DatabaseManagerSwing.main(url);
	}

}
