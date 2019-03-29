package org.openxava.util;

import java.io.*;

/**
 * Utilities to working with <tt>java.io.InputStream</tt>). <p>
 * 
 * @author Javier Paniza
 */

public class InputStreams {
	
	
	/**
	 * Returns the content of the input stream in string format. <p>
	 * 
	 * @param in Cannot be null
	 * @return Never null
	 * @throws IOException  If some problem
	 */
	public static String toString(InputStream in) throws IOException {		
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];	    
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));	    		        
	    }	    	    
	    return out.toString();
	}

}
