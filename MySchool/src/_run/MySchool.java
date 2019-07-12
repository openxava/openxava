package _run; 

import java.io.*;
import java.util.logging.*;

import org.apache.catalina.startup.*;
import org.openxava.util.*;
import java.io.*;
import java.util.logging.*;



/**
 * Execute this class to start the application.
 * 
 * With Eclipse: Right mouse button > Run As > Java Application
 * 
 * @author Javier Paniza
 */

public class MySchool { 

	public static void main(String[] args) throws Exception {
		
		// tmp ini
		// TMP ME QUEDÉ POR AQUÍ OPTIMIZANDO ARRANCA. VOLVÍ A COMPARARLO CON JETTY Y SIGUE SIENDO MÁS RÁPIDO (1 SEGUNDO)
		long ini = System.currentTimeMillis(); // tmp
		/*
		org.hsqldb.Server hsqlServer = new org.hsqldb.Server();
        //hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, "");
        hsqlServer.setDatabasePath(0, "file:data/my-school-db");
        hsqlServer.setPort(1666);
        hsqlServer.start();        
        */
        // tmp fin
        
        // tmp AppServer.run("MySchool");
        // tmp ini
		Logger.getLogger("").setLevel(Level.INFO);
        String webappDir = new File("web").getAbsolutePath();
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp"); 
        tomcat.setPort(8080);
        tomcat.getConnector();
        tomcat.enableNaming();          
        tomcat.addWebapp("/MySchool", webappDir);
        tomcat.start();
        
        long cuesta = System.currentTimeMillis() - ini; // tmp
        System.out.println("[MySchool.main] cuesta=" + cuesta); // tmp
        System.out.println("Application started. Go to http://localhost:8080/MySchool"); // tmp i18n 

        tomcat.getServer().await();
        // tmp fin
	}

}
