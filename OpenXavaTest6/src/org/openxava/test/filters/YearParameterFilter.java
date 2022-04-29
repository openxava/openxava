package org.openxava.test.filters;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.filters.*;

/**
 * The year is obtained from the parameter year in the module URL. <p>
 * 
 * @author Javier Paniza
 */

public class YearParameterFilter implements IRequestFilter {

	private HttpServletRequest request;

	public Object filter(Object o) throws FilterException {		
		if (o == null) {
			return new Object [] { getYearParameter() };
		}		
		if (o instanceof Object []) {			
			List c = new ArrayList(Arrays.asList((Object []) o));
			c.add(0, getYearParameter());
			return c.toArray();			
		} 
		else {
			return new Object [] { getYearParameter(), o	};
		}		
	}

	private Integer getYearParameter() throws FilterException {
		String year = request.getParameter("year");
		return year == null?new Integer(0):new Integer(year);
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;		
	}
	
}
