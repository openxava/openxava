package org.openxava.test.filters;

import org.openxava.filters.*;

/**
 * @author Javier Paniza
 */
public class NumbersToLettersFilter implements IFilter {
	
	public Object filter(Object o) throws FilterException {
		if (o instanceof String) {
			return toLetter((String) o);
		}
		if (o instanceof Object []) {
			Object [] argv = (Object []) o;
			for (int i = 0; i < argv.length; i++) {				
				if (argv[i] instanceof String) {
					argv[i] = toLetter((String) argv[i]);
				}				 
			}
			return argv;
		}				
		return o;		
	}
	
	private String toLetter(String s) {
		if (s.equalsIgnoreCase("1")) return "UNO";
		if (s.equalsIgnoreCase("2")) return "DOS";		 		
		if (s.equalsIgnoreCase("3")) return "TRES";
		if (s.equalsIgnoreCase("4")) return "CUATRO";		
		return s;
	}

}
