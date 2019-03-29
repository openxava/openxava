package org.openxava.test.filters;

import java.util.*;

import org.openxava.filters.*;

/**
 * @author Javier Paniza
 */

public class CurrentYearFilter implements IFilter {


	public Object filter(Object o) throws FilterException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());		
		Integer year = new Integer(cal.get(Calendar.YEAR));
		Object [] r = null;
		if (o == null) {
			r = new Object[1];
			r[0] = year;					
		}
		else if (o instanceof Object []) {
			Object [] a = (Object []) o; 
			r = new Object[a.length + 1];
			r[0] = year;
			for (int i = 0; i < a.length; i++) {
				r[i+1]=a[i];
			}			
		}
		else {
			r = new Object[2];
			r[0] = year;
			r[1] = o;			
		}
		
		return r;
	}

}
