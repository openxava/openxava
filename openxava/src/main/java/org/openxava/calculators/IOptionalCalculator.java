package org.openxava.calculators;

/**
 * A calculator that can choose do no calculation.
 * 
 * @author Javier Paniza
 */
public interface IOptionalCalculator extends ICalculator {
	
	/**
	 * The result only is valid if this method return <tt>true</tt>.
	 */
	boolean isCalculate();

}
