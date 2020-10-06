package org.openxava.util; 

import java.io.*;
import org.apache.catalina.startup.*;
import org.apache.commons.logging.*;

import java.nio.file.*;
import java.nio.file.Files;
import java.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class AppServer { 
	
	private static final String I18N_DIR = "web/WEB-INF/classes/"; 
	private final static Log log = LogFactory.getLog(AppServer.class);
	
	public static void run(String app) throws Exception {
		System.out.println(XavaResources.getString("starting_application"));
		createDefaultI18nFiles(app); 
        String webappDir = new File("web").getAbsolutePath();
        String contextPath = Is.empty(app)?"":"/" + app;
        Tomcat tomcat = null;
        int initialPort = XavaPreferences.getInstance().getApplicationPort();
        int finalPort = initialPort + 10;
        for (int port = initialPort; port < finalPort; port++) {
        	tomcat = startTomcat(webappDir, contextPath, port);
        	if (tomcat != null) {
        		if (port > initialPort) {
        			System.out.println(XavaResources.getString("port_for_faster_startup", "applicationPort=" + port)); 
        		}
        		break;
        	}
        }
        if (tomcat == null) {
        	System.err.println(XavaResources.getString("app_startup_failed_after_trying_ports"));
        	System.exit(1);
        }
       	System.out.println(XavaResources.getString("application_started_go", "http://localhost:" + tomcat.getConnector().getLocalPort() + "/" + app));
        
        tomcat.getServer().await();
	}
	
	private static Tomcat startTomcat(String webappDir, String contextPath, int port) throws Exception { 
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp"); 
        tomcat.setPort(port);
        tomcat.getConnector();
        tomcat.enableNaming();
        tomcat.addWebapp(contextPath, webappDir);
        tomcat.start();
       	if (tomcat.getConnector().getLocalPort() < 0) {
     		tomcat.stop();
     		return null;
       	}
       	return tomcat;
	}
	
	private static void createDefaultI18nFiles(String app) {
		try { 
			Path labelsEsPath = Paths.get(I18N_DIR + "Etiquetas" + app + "_es.properties");
			if (Files.exists(labelsEsPath)) {
				Files.copy(labelsEsPath, java.nio.file.Paths.get(I18N_DIR + "Etiquetas" + app + ".properties"), StandardCopyOption.REPLACE_EXISTING);
				Files.copy(Paths.get(I18N_DIR + "Mensajes" + app + "_es.properties"), Paths.get(I18N_DIR + "Mensajes" + app + ".properties"), StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				if (copyDefaultI18nFile(app, "en")) return; 
				if (copyDefaultI18nFile(app, Locale.getDefault().getLanguage())) return;				
				String [] popularLanguages = { "de", "fr", "it", "es", "ru", "zh", "ja", "in", "pl", "sr", "sv", "ca" };
				for (String language: popularLanguages) {
					if (copyDefaultI18nFile(app, language)) return;
				}
				for (String language: Locale.getISOLanguages()) {
					if (copyDefaultI18nFile(app, language)) return;
				}				
			}
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("default_i18n_files_not_created"), ex);
		}
	}
	
	private static boolean copyDefaultI18nFile(String app, String language) throws Exception {
		Path labelsPath = Paths.get(I18N_DIR + app + "-labels_" + language + ".properties");
		if (!Files.exists(labelsPath)) return false; 
		Files.copy(labelsPath, Paths.get(I18N_DIR + app + "-labels.properties"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get(I18N_DIR + app + "-messages_" + language + ".properties"), Paths.get(I18N_DIR + app + "-messages.properties"), StandardCopyOption.REPLACE_EXISTING);		
		return true;
	}

}
