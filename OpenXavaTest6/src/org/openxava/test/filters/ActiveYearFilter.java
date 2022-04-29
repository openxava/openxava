package org.openxava.test.filters;

import java.util.*;

import org.openxava.filters.*;

/**
 * Active year is obtained from a session object with application scope. <p>
 * 
 * @author Javier Paniza
 */

public class ActiveYearFilter extends BaseContextFilter {

	public Object filter(Object o) throws FilterException {		
		if (o == null) {
			return new Object [] { getActiveYear() };
		}		
		if (o instanceof Object []) {			
			List c = new ArrayList(Arrays.asList((Object []) o));
			c.add(0, getActiveYear());
			return c.toArray();			
		} 
		else {
			return new Object [] { getActiveYear(), o	};
		}		
	}

	private Integer getActiveYear() throws FilterException {
		try {
			return getInteger("xavatest_activeYear");			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new FilterException("Impossible to obtain active year associated with the session");
		}
	}
	
}
