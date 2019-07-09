package org.openxava.school.boot;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.sql.*;

import org.apache.catalina.*;
import org.apache.catalina.startup.*;
import org.apache.tomcat.util.descriptor.web.*;

public class TomcatBoot {
	
    public static void main(String[] args) throws Exception {
    	// TMP ME QUEDÉ POR AQUÍ: VIENDO QUE HACER CON TEMP
    	System.out.println("[TomcatBoot.main] System.getProperties()=" + System.getProperties()); // tmp
    	/*
        String contextPath = "MySchool";
        String webappDir = new File("web").getAbsolutePath();
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp"); // tmp Probar dejar en otro sitio
        tomcat.setPort(8080);
        tomcat.getConnector();
        tomcat.enableNaming();          
        tomcat.addWebapp(contextPath, webappDir);
        tomcat.start();

        System.out.println("Application started. Go to http://localhost:8080/MySchool"); // tmp i18n, MySchool a piñon 
        
        tomcat.getServer().await();
        */            
    }

}
