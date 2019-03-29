package org.openxava.util;

import java.io.*;




/**
 * Some utilities for work with files and directories. <p>
 * 
 * @author Javier Paniza
 */

public class Files {
	
	
	
	public static boolean deleteDir(String dirURL) {
        return deleteDir(new File(dirURL));
    }	
		
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean deleted = deleteDir(new File(dir, children[i]));
                if (!deleted) {
                    return false;
                }
            }
        }    
        return dir.delete();
    }	
	
	/**
	 * @since 5.9
	 */
	public static void createFileIfNotExist(String fileName) throws Exception { 
		File f = new File(fileName);
		if (!f.exists()) {
			File dir = new File(Strings.noLastToken(fileName, "/")); 
			if (!dir.exists()) dir.mkdirs();
			f.createNewFile();
		}
	}
	
	/**
	 * @since 5.9
	 */
	public static String getOpenXavaBaseDir() { 
		return System.getProperty("user.home") + "/.openxava/"; 
	}

}
