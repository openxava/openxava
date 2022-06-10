package org.openxava.filters;




/**
 * Put a % before and after.
 * 
 * @author Javier Paniza
 */
public class TotalLikeFilter implements IFilter {
	
	
	
	/**
	 * @see org.openxava.filters.IFilter#filter(java.lang.Object)
	 */
	public Object filter(Object o) throws FilterException {
		if (o == null) return "%";
		return "%" + o.toString() + "%";
	}


}
