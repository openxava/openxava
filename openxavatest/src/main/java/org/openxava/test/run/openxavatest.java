package org.openxava.test.run;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 *
 * With Eclipse: Right mouse button > Run As > Java Application
 */

public class openxavatest {

	public static void main(String[] args) throws Exception {
		// tmr DBServer.start("openxavatest-db"); // To use your own database comment this line and configure web/META-INF/context.xml
		AppServer.run("openxavatest"); // Use AppServer.run("") to run in root context
	}

}
