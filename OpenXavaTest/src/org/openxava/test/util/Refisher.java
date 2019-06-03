package org.openxava.test.util;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.model.meta.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class Refisher {
	
	private static Log log = LogFactory.getLog(Refisher.class);  	
	final public static String FILE_NAME = Files.getOpenXavaBaseDir() + "refisher.log";
	
	public void refine(MetaModule metaModule, Collection metaActions) {
		log("A.1"); // tmp
	}
	
	public void refineForCollections(MetaModule metaModule, Collection<String> actionNames) {
		log("A.2"); // tmp
	}
	
	public boolean accept(String model, String unqualifiedAction) {
		log("A.3: model=" + model + ", unqualifiedAction=" + unqualifiedAction); // tmp
		return true;
	}	
	
	public void refine(MetaModule metaModule, Collection<MetaMember> metaMembers, View view) {
		System.out.println("[Refisher.refine] metaModule=" + metaModule.getName() + ", metaMembers=" + metaMembers + ", view=" + view.getModelName() + "(" + view.getMemberName() + ")"); // tmp
		log("B.1"); // tmp
	}
	
	public void polish(MetaModule metaModule, MetaTab metaTab) {
		log("B.2"); // tmp
	}
	
	private static void log(String line) {
		try {
			System.out.println("[Refisher.log] " + line); // tmp
			createFileIfNotExist();
			FileOutputStream f = new FileOutputStream(FILE_NAME, true); 
			PrintStream p = new PrintStream(f);
			p.println(line);
			p.close();
			f.close();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("log_tracker_log_failed"), ex);
		}
	}
	
	private static void createFileIfNotExist() throws Exception { 
		Files.createFileIfNotExist(FILE_NAME); 
	}

}
