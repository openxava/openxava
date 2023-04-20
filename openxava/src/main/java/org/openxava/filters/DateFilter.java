package org.openxava.filters;

import lombok.*;

@Getter
@Setter
public class DateFilter implements IFilter {

	private Object start;
	private Object end;

	@Override
	public Object filter(Object o) throws FilterException {
		return new Object[] { getStart(), getEnd() };
	}

}
