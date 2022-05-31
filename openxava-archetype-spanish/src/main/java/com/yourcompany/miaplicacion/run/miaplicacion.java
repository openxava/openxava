package com.yourcompany.miaplicacion.run;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 *
 * With Eclipse: Right mouse button > Run As > Java Application
 */

public class miaplicacion {

	public static void main(String[] args) throws Exception {
		DBServer.start("openxava-archetype-spanish-db"); // To use your own database comment this line and configure web/META-INF/context.xml
		AppServer.run("openxava-archetype-spanish"); // Use AppServer.run("") to run in root context
	}

}
