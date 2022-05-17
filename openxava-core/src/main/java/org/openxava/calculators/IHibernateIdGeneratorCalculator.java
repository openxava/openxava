package org.openxava.calculators;

/**
 * Calculate ids using Hibernate native id generators. <p>
 * 
 * These calculators do not calculate, instead delegate calculation
 * in hibernate. <p>
 * 
 * It's used for generate id using Hibernate id generator. Instead of
 * programming the calculation with this calculate we use it to generate
 * the xml code to insert in .hbml.xml file. <p>
 * 
 * @author Javier Paniza
 */

public interface IHibernateIdGeneratorCalculator extends ICalculator {
	
	/**
	 * Returns the xml code to insert inside 'id' element in .hbml file.
	 */
	String hbmGeneratorCode();

}
