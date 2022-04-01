package org.openxava.calculators;




/**
 * @author Javier Paniza
 */
public class ByPassCalculator implements ICalculator {
	
	private Object source;
	

	public Object calculate() throws Exception {
		return source;
	}

	public Object getSource() {
		return source;
	}
	public void setSource(Object source) {
		this.source = source;
	}
	
}
