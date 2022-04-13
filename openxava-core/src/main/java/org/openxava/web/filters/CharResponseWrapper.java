package org.openxava.web.filters;

import java.io.*;

import javax.servlet.http.*;

/**
 * @since 6.5.3
 * @author Javier Paniza
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
	
	private CharArrayWriter output;
	
	public String toString() {
	    return output.toString();
	}
	public CharResponseWrapper(HttpServletResponse response){
	    super(response);
	    output = new CharArrayWriter();
	}
	
	public PrintWriter getWriter(){
	    return new PrintWriter(output);
	}
	
}