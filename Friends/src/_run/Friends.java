package _run; 

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 * 
 * With Eclipse: Right mouse button > Run As > Java Application
 */

public class Friends { 

	public static void main(String[] args) throws Exception {
		DBServer.start("FriendsDB"); // Comment this line if you use your own database
        AppServer.run("Friends");
	}

}
