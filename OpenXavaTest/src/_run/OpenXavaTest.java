package _run; 

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 * 
 * With Eclipse: Right mouse button > Run As > Java Application
 */

public class OpenXavaTest { 

	public static void main(String[] args) throws Exception {
		DBServer.start("OpenXavaTestDB"); // Comment this line if you use your own database
        AppServer.run("OpenXavaTest");
	}

}
