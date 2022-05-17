package org.openxava.calculators;





/**
 * @author Javier Paniza
 */
public class StringCalculator implements ICalculator {
	
	private String string;
	

	public Object calculate() throws Exception {
		return string;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

}
