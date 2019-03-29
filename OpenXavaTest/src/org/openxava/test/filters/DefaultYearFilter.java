package org.openxava.test.filters;

import java.util.*;

import org.openxava.filters.*;

/**
 * Default year is obtained from a session object. <p>
 * 
 * @author Javier Paniza
 */

public class DefaultYearFilter extends BaseContextFilter {

	public Object filter(Object o) throws FilterException {		
		if (o == null) {
			return new Object [] { getDefaultYear() };
		}		
		if (o instanceof Object []) {			
			List c = new ArrayList(Arrays.asList((Object []) o));
			c.add(0, getDefaultYear());
			return c.toArray();			
		} 
		else {
			return new Object [] { getDefaultYear(), o	};
		}		
	}

	private Integer getDefaultYear() throws FilterException {
		try {
			return getInteger("xavatest_defaultYear");			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new FilterException("Impossible to obtain default year associated with the session");
		}
	}
	
}
