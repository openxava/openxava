package org.openxava.test.filters;

import java.util.*;

import org.openxava.filters.*;

public class DefaultNameEnvFilter extends BaseContextFilter {

	public Object filter(Object o) throws FilterException {		
		if (o == null) {
			return new Object [] { getDefaultName() };
		}		
		if (o instanceof Object []) {			
			List c = new ArrayList(Arrays.asList((Object []) o));
			c.add(0, getDefaultName());
			return c.toArray();			
		} 
		else {
			return new Object [] { getDefaultName(), o	};
		}		
	}

	private String getDefaultName() throws FilterException {
		try {
			return getEnvironment().getValue("XAVATEST_DEFAULT_NAME");			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new FilterException("Impossible to obtain default name for this module");
		}
	}
	
}
