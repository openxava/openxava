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
		DBServer.start("MySchoolDB");
        AppServer.run("MySchool");
	}

}
