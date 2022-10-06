package org.openxava.util; 

import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.*;

import org.apache.catalina.*;
import org.apache.catalina.core.*;
import org.apache.catalina.startup.*;
import org.apache.catalina.webresources.*;
import org.apache.commons.logging.*;
import org.openxava.application.meta.*;

/**
 * 
 * @author Javier Paniza
 */
public class AppServer { 
	
	private static final String I18N_DIR = "/WEB-INF/classes/i18n/"; 
	private final static Log log = LogFactory.getLog(AppServer.class);
	
	public static void run(String app) throws Exception {
		System.out.println(XavaResources.getString("starting_application"));
		System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "activation-*.jar,antlr-*.jar,aopalliance-repackaged-*.jar,bcprov-jdk15on-*.jar,byte-buddy-*.jar,castor-*.jar,classmate-*.jar,commons-*.jar,curvesapi-*.jar,dom4j-*.jar,dwr-*.jar,ecj-*.jar,ejb-api-*.jar,fontbox-*.jar,groovy-all-*.jar,hibernate-*.jar,hk2-*.jar,hsqldb-*.jar,htmlunit-*.jar,httpclient-*.jar,httpcore-*.jar,httpmime-*.jar,icu4j-*.jar,itext-*.jar,jackson-*.jar,jakarta.activation-*.jar,jakarta.annotation-api-*.jar,jakarta.inject-*.jar,jakarta.json-*.jar,jakarta.ws.rs-api-*.jar,jandex-*.jar,jasperreports-*.jar,javassist-*.jar,javax.activation-api-*.jar,javax.inject-*.jar,javax.mail-*.jar,javax.persistence-api-*.jar,jaxb-*.jar,jboss-logging-*.jar,jboss-transaction-*.jar,jcommon-*.jar,jersey-*.jar,jetty-*.jar,jfreechart-*.jar,jsoup-*.jar,junit-*.jar,lombok-*.jar,neko-htmlunit-*.jar,osgi-resource-locator-*.jar,pdfbox-*.jar,poi-*.jar,serializer-*.jar,stax-*.jar,tomcat-*.jar,validation-api-*.jar,websocket-*.jar,xalan-*.jar,xercesImpl-*.jar,xml-apis-*.jar,xmlbeans-*.jar,yasson-*.jar");
		System.setProperty("java.awt.headless", "true"); // In order keystrokes and PDF reports work with a headless Java
		
		String contextPath = Is.empty(app)?"":"/" + app;
		if (Is.empty(app)) app = MetaApplications.getMainMetaApplication().getName(); // To work in ROOT context
		createDefaultI18nFiles(app);
        String webappDir = new File("target/" + app).getAbsolutePath();
        
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
        System.out.println(XavaResources.getString("application_started_go", "http://localhost:" + tomcat.getConnector().getLocalPort() + contextPath)); 
        
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
        context.setParentClassLoader(Thread.currentThread().getContextClassLoader()); // To work with mvn exec:java from command line
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
	
}
