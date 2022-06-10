package org.openxava.calculators;





/**
 * @author Miguel Embuena
 */
public class LongCalculator implements ICalculator {

	private long value;
	

	public Object calculate() throws Exception {
		return new Long(value);
	}

	public long getValue() {
		return value;
	}

	public void setValue(long l) {
		value = l;
	}

}
