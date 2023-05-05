package org.openxava.filters;

import lombok.*;

/**
 * Used in {@link org.openxava.web.dwr.Calendar} to filter dates. <p>
 * 
 * @since 7.1
 * @author Chungyen Tsai
 *
 */


@Getter
@Setter
public class DateRangeFilter implements IFilter {

	private Object start;
	private Object end;

	@Override
	public Object filter(Object o) throws FilterException {
		return new Object[] { getStart(), getEnd() };
	}

}
