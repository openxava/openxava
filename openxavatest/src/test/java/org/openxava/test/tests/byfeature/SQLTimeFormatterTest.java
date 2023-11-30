package org.openxava.test.tests.byfeature;


import java.util.*;

import org.openxava.formatters.*;
import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class SQLTimeFormatterTest extends TestCase {
	
	public SQLTimeFormatterTest(String name) {
		super(name);
	}
	
			
	public void testNoMillisecons() throws Exception {		
	    SQLTimeFormatter formatter = new SQLTimeFormatter();
		for (int i=0; i<2; i++) {
			java.sql.Time time = (java.sql.Time) formatter.parse(null, "11:05:27");
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			int hours = cal.get(Calendar.HOUR);
			int minutes = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);
			int millis = cal.get(Calendar.MILLISECOND);
			assertEquals(11, hours);
			assertEquals(5, minutes);
			assertEquals(27, seconds);
			assertEquals(0, millis); // Must be 0, because some database, like PostgreSQL, round to up when millis > 500, so adds a extra second
			Thread.sleep(10);
		}
	}
	
}
