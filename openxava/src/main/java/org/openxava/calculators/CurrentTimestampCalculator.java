package org.openxava.calculators;

/**
 * Returns the current time in <code>java.sql.Timestamp</code> format. <p>
 * 
 * @author Eduardo Escrihuela
 */

public class CurrentTimestampCalculator implements ICalculator {
	
	public Object calculate() throws Exception { 
		return new java.sql.Timestamp (new java.util.Date().getTime()); 
	}
	
}
