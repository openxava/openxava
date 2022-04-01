package org.openxava.filters;

/**
 * Do nothing. <p>
 * 
 * @author Javier Paniza
 */
public class VoidFilter implements IFilter {

	public Object filter(Object o) throws FilterException {
		return o;
	}

}
