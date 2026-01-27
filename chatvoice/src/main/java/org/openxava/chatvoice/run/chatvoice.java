package org.openxava.chatvoice.run;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 *
 * With OpenXava Studio/Eclipse: Right mouse button > Run As > Java Application
 */

public class chatvoice {

	public static void main(String[] args) throws Exception {
		DBServer.start("chatvoice-db"); // To use your own database comment this line and configure src/main/webapp/META-INF/context.xml
		AppServer.run("chatvoice"); // Use AppServer.run("") to run in root context
	}

}
