package org.openxava.filters;

import java.util.*;



import org.openxava.util.*;


/**
 * Convert a year in a date range. <p>
 * 
 * @author Javier Paniza
 */
public class YearToDateRangeFilter implements IFilter {

	
	
	/**
	 * @see org.openxava.filters.IFilter#filter(java.lang.Object)
	 */
	public Object filter(Object o) throws FilterException {
		if (o == null) {
			Object [] r = {
				Dates.create(1, 1, 1),
				Dates.create(1, 1, 1)
			};
			return r;
		}
		if (!(o instanceof Number)) {
			throw new FilterException("filter_invalid_argument_type", getClass().getName(), "Number");
		}
		int ano = ((Number) o).intValue();
		Date f1 = Dates.create(1, 1, ano);
		Date f2 = Dates.create(1, 1, ano + 1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(f2);
		cal.add(Calendar.MILLISECOND, -1);
		f2 = cal.getTime();
		Object [] r = {
			f1, f2
		};
		return r;
	}

}
