package org.openxava.util; 

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.*;

import org.apache.catalina.*;
import org.apache.catalina.core.*;
import org.apache.catalina.startup.*;
import org.apache.catalina.webresources.*;
import org.apache.commons.logging.*;
import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza
 */
public class AppServer { 
	
	private static final String I18N_DIR = "/WEB-INF/classes/i18n/"; 
	private final static Log log = LogFactory.getLog(AppServer.class);
	
	public static void run(String app) throws Exception {
		System.out.println(XavaResources.getString("starting_application"));
		System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "activation.jar,antlr.jar,byte-buddy.jar,classmate.jar,commons-*.jar,dom4j.jar,dsn.jar,dwr.jar,ejb.jar,groovy-all.jar,hibernate-*.jar,hk2-*.jar,imap.jar,itext.jar,jakarta.*.jar,jandex.jar,jasperreports-fonts.jar,jasperreports.jar,javassist.jar,javax.inject.jar,jaxb-*.jar,jboss-logging.jar,jersey-*.jar,jpa.jar,jsoup.jar,jta.jar,lombok.jar,mailapi.jar,mime-util.jar,ox-jdbc-adapters.jar,poi-*.jar,poi.jar,pop3.jar,slf4j-*.jar,smtp.jar,validation-api.jar,xmlbeans.jar,yasson.jar"); 
		createDefaultI18nFiles(app);
		// tmr ini
		long ini = System.currentTimeMillis();
		updateDTDsFromJar(app);
		long cuesta = System.currentTimeMillis() - ini;
		System.out.println("[AppServer.run] cuesta=" + cuesta); // tmp
		// tmr fin
        String webappDir = new File("target/" + app).getAbsolutePath();
        
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
        StandardContext context = (StandardContext) tomcat.addWebapp(contextPath, webappDir);
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", "target/classes", "/"));
        context.setResources(resources);
        
        tomcat.start();
       	if (tomcat.getConnector().getLocalPort() < 0) {
     		tomcat.stop();
     		return null;
       	}
       	return tomcat;
	}
	
	private static void createDefaultI18nFiles(String app) {
		try { 
			Path labelsEsPath = Paths.get("target/" + app + I18N_DIR + "Etiquetas" + app + "_es.properties");
			if (Files.exists(labelsEsPath)) {
				Files.copy(labelsEsPath, java.nio.file.Paths.get("target/" + app + I18N_DIR + "Etiquetas" + app + ".properties"), StandardCopyOption.REPLACE_EXISTING);
				Files.copy(Paths.get("target/" + app + I18N_DIR + "Mensajes" + app + "_es.properties"), Paths.get("target/" + app + I18N_DIR + "Mensajes" + app + ".properties"), StandardCopyOption.REPLACE_EXISTING);
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
				log.warn(XavaResources.getString("default_i18n_files_not_created"));
			}
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("default_i18n_files_not_created"), ex);
		}
	}
	
	private static boolean copyDefaultI18nFile(String app, String language) throws Exception {
		Path labelsPath = Paths.get("target/" + app + I18N_DIR + app + "-labels_" + language + ".properties");
		if (!Files.exists(labelsPath)) return false; 
		Files.copy(labelsPath, Paths.get("target/" + app + I18N_DIR + app + "-labels.properties"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get("target/" + app + I18N_DIR + app + "-messages_" + language + ".properties"), Paths.get("target/" + app + I18N_DIR + app + "-messages.properties"), StandardCopyOption.REPLACE_EXISTING);
		return true;
	}
	
	private static void updateDTDsFromJar(String app) { // tmr 
		try {
			// TMR ME QUEDÉ POR AQUÍ: NI SIQUIERA ARRANCA LA APLICACIÓN
			List<String> result = new ArrayList<>();
			Enumeration<URL> e = EditorsResources.class.getClassLoader().getResources("xava/dtds");
			while (e.hasMoreElements()) {
				URL url = e.nextElement();
				System.out.println("[AppServer.updateDTDsFromJar] url=" + url); // tmp
				/*
				if (url.getProtocol().equals("jar")) {
					String jarURL = url.getFile().replace("file:", "");
					jarURL = Strings.noLastTokenWithoutLastDelim(jarURL, "!");
					ZipFile zip = new ZipFile(jarURL);
					try {
						Enumeration<? extends ZipEntry> entries = zip.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							if (entry.getName().startsWith("META-INF/resources/xava/editors/" + folder + "/") && entry.getName().endsWith("." + extension)) {
								result.add(entry.getName().replace("META-INF/resources/xava/editors/", ""));
							}
						}
					}
					finally {
						if (zip != null) zip.close();
					}
				}
				*/
			}
			java.util.Collections.sort(result);
			// System.out.println("[AppServer.updateDTDsFromJar] result=" + result); // tmp
		}
		catch (Exception ex) {
			// tmr log.warn(XavaResources.getString("editors_resources_not_loaded", extension.toUpperCase()), ex);
		}
	}

}
