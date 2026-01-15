package org.openxava.test.run;

import java.util.*;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 */

public class openxavatest {

	public static void main(String[] args) throws Exception {
		Locale.setDefault(Locale.US); 
		DBServer.start("openxavatest-db"); // To use your own database comment this line and configure web/META-INF/context.xml
		AppServer.run("openxavatest", "../openxava/target/classes"); // Use AppServer.run("") to run in root context
	}

}
