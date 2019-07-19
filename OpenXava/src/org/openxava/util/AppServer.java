package org.openxava.util; 

import java.io.*;
import java.util.jar.*;
import java.util.logging.*;

import org.apache.catalina.*;
import org.apache.catalina.startup.*;

/**
 * tmp
 * @author Javier Paniza
 */
public class AppServer { 
	
	public static void run(String app) throws Exception {
        String webappDir = new File("web").getAbsolutePath();
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp"); 
        tomcat.setPort(8080);
        tomcat.getConnector();
        tomcat.enableNaming();          
        tomcat.addWebapp("/" + app, webappDir);
        tomcat.start();
        System.out.println("Application started. Go to http://localhost:8080/" + app); // tmp i18n 
        
        tomcat.getServer().await();
	}

}
