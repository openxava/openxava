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
	
	public static void updateOpenXavaJar() throws Exception // tmp
	{
	  Manifest manifest = new Manifest();
	  manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
	  JarOutputStream target = new JarOutputStream(new FileOutputStream("output.jar"), manifest);
	  add(new File("classes"), target);
	  target.close();
	}

	private static void add(File source, JarOutputStream target) throws IOException
	{
	  BufferedInputStream in = null;
	  try
	  {
	    if (source.isDirectory())
	    {
	      String name = source.getPath().replace("\\", "/");
	      if (!name.isEmpty())
	      {
	        if (!name.endsWith("/"))
	          name += "/";
	        JarEntry entry = new JarEntry(name);
	        entry.setTime(source.lastModified());
	        target.putNextEntry(entry);
	        target.closeEntry();
	      }
	      for (File nestedFile: source.listFiles())
	        add(nestedFile, target);
	      return;
	    }

	    JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
	    entry.setTime(source.lastModified());
	    target.putNextEntry(entry);
	    in = new BufferedInputStream(new FileInputStream(source));

	    byte[] buffer = new byte[1024];
	    while (true)
	    {
	      int count = in.read(buffer);
	      if (count == -1)
	        break;
	      target.write(buffer, 0, count);
	    }
	    target.closeEntry();
	  }
	  finally
	  {
	    if (in != null)
	      in.close();
	  }
	}


}
