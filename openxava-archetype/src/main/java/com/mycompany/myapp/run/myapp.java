package com.mycompany.myapp.run;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 *
 * With OpenXava Studio/Eclipse: Right mouse button > Run As > Java Application
 */

public class myapp {

	public static void main(String[] args) throws Exception {
		DBServer.start("myapp-db"); // To use your own database comment this line and configure src/main/webapp/META-INF/context.xml
		AppServer.run("myapp"); // Use AppServer.run("") to run in root context
	}

}
