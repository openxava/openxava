package org.openxava.filters;

import java.util.*;

import lombok.*;

public class DateFilter implements IFilter {

	@Getter @Setter
	Date start;
	@Getter @Setter
	Date end;
	
	
	@Override
	public Object filter(Object o) throws FilterException {
		return new Object[] {
				getStart(),
				getEnd()
		};
	}

}
