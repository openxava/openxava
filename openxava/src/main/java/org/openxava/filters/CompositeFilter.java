package org.openxava.filters;

import lombok.*;

/**
 * To sum two IFilter. <p>
 * 
 * Used in {@link org.openxava.web.dwr.Calendar} to nest filters. 
 * 
 * @since 7.1.5
 * @author Chungyen Tsai
 *
 */


@Getter @Setter
public class CompositeFilter implements IFilter{
	
	IFilter oldFilter;
	IFilter newFilter;
	
	public CompositeFilter (IFilter oldFilter, IFilter newFilter) {
		this.oldFilter = oldFilter;
		this.newFilter = newFilter;
	}
	
	@Override
	public Object filter(Object o) throws FilterException {
		Object [] oldValues = (Object[]) oldFilter.filter(o);
		Object [] newValues = (Object[]) newFilter.filter(o);
        Object[] sum = sumArrays(oldValues, newValues);
		return sum;
	}
    
    public static Object[] sumArrays(Object[] arr1, Object[] arr2) {
        int length1 = arr1.length;
        int length2 = arr2.length;
        Object[] sum = new Object[length1 + length2];
        System.arraycopy(arr1, 0, sum, 0, length1);
        System.arraycopy(arr2, 0, sum, length1, length2);
        return sum;
    }
}
