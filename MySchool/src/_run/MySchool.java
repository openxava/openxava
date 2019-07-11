package _run; 

import org.openxava.util.*;

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
		org.hsqldb.Server hsqlServer = new org.hsqldb.Server();
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, "");
        hsqlServer.setDatabasePath(0, "file:data/my-school-db");
        hsqlServer.setPort(1666);
        hsqlServer.start();
        // tmp fin
        
        AppServer.run("MySchool");
	}

}
