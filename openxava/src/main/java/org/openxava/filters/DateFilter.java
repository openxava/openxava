package org.openxava.filters;

import lombok.*;

public class DateFilter implements IFilter {

	@Getter @Setter
	Object start;
	@Getter @Setter
	Object end;
	
	
	@Override
	public Object filter(Object o) throws FilterException {
		return new Object[] {
				getStart(),
				getEnd()
		};
	}

}
