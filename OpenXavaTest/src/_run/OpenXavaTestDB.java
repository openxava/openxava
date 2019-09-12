package _run; 

import org.openxava.util.*;

/**
 * Execute this class to start the test database but not the Tomcat.
 * 
 * With Eclipse: Right mouse button > Run As > Java Application
 */

public class OpenXavaTestDB { 

	public static void main(String[] args) throws Exception {
		DBServer.start("OpenXavaTestDB"); 
	}

}
