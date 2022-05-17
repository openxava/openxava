package org.openxava.test.filters;



import org.openxava.filters.*;

/**
 * @author Javier Paniza
 */
public class Invoice20020001Filter implements IFilter {

	public Object filter(Object o) throws FilterException {
		Object [] r = null;
		if (o == null) {
			r = new Object[2];
			r[0] = new Integer(2002);
			r[1] = new Integer(1);
		}
		else if (o instanceof Object []) {
			Object [] a = (Object []) o; 
			r = new Object[a.length + 2];
			r[0] = new Integer(2002);
			r[1] = new Integer(1);
			for (int i = 0; i < a.length; i++) {
				r[i+2]=a[i];
			}			
		}
		else {
			r = new Object[3];
			r[0] = new Integer(2002);
			r[1] = new Integer(1);
			r[2] = o;
		}
		
		return r;
	}

}
