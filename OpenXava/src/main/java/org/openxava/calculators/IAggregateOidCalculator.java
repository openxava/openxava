package org.openxava.calculators;

/**
 * Calculator for the oid of an aggregete. <p>
 * 
 * The oid of an aggregate can be calculated by an calculator of any type,
 * but if this type is used some additional useful info is provided.<br>
 * 
 * @author Javier Paniza
 */
public interface IAggregateOidCalculator extends ICalculator {
	
	/**
	 * The container object of aggregate. <p>
	 */
	void setContainer(Object container);
	
	/**
	 * A number that can be used to create the oid. <p>
	 * 
	 * Usually this is a sequential number
	 */
	void setCounter(int counter);

}
