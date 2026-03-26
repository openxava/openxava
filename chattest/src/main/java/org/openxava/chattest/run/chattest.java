package org.openxava.chattest.run;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 */

public class chattest {

	public static void main(String[] args) throws Exception {
		DBServer.start("chattest-db"); // To use your own database comment this line and configure src/main/webapp/META-INF/context.xml
		AppServer.run("chattest", "../openxava/target/classes", "../openxava-7.7-chat-jdk17/target/classes"); // Use AppServer.run("") to run in root context
	}

}
