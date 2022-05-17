package org.openxava.test.filters;

import java.util.*;

import org.openxava.filters.*;


/**
 * @author Javier Paniza
 */

public class LimitZoneFilter extends BaseContextFilter {

	public Object filter(Object o) throws FilterException {		
		if (o == null) {
			return new Object [] { getLimitZone() };
		}		
		if (o instanceof Object []) {			
			List c = new ArrayList(Arrays.asList((Object []) o));
			c.add(0, getLimitZone());
			return c.toArray();			
		} 
		else {
			return new Object [] { getLimitZone(), o	};
		}		
	}

	private Integer getLimitZone() throws FilterException {
		try {
			return getInteger("xavatest_limitZone");			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new FilterException("Impossible to obtain limit zone associated to session");
		}
	}
	
}
