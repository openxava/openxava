package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Declares that a property depends on other one(s). <p>
 * 
 * Applies to properties.<p>
 * 
 * OpenXava uses this info in order to know when to recalculate
 * values of a property in the user interface.<br>
 * Example:
 * <pre>
 * &nbsp;@Depends("unitPrice")  	
 * &nbsp;public BigDecimal getUnitPriceInPesetas() {
 * &nbsp;&nbsp;&nbsp;if (unitPrice == null) return null;
 * &nbsp;&nbsp;&nbsp;return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
 * &nbsp;}
 * </pre>
 * In this case if the unitPrice changes in the user interface, the value of 
 * unitPriceInPesetas is recalculated and redisplayed.<br>
 * To define several properties, separate them using commas. To depend on a reference, use the
 * qualified name of the key property (that is, for subfamily, use subfamily.number). Like this:
 * <pre>
 * &nbsp;@ManyToOne
 * &nbsp;private Subfamily subfamily;
 * 
 * &nbsp;@ManyToOne
 * &nbsp;private Subfamily subfamilyTo;
 * 
 * &nbsp;@Depends("subfamily.number, subfamilyTo.number") 
 * &nbsp;public String getRangeDescription() { 		
 * &nbsp;&nbsp;&nbsp;int subfamilyNumber = getSubfamily() == null ? 0 : getSubfamily().getNumber();
 * &nbsp;&nbsp;&nbsp;int subfamilyToNumber = getSubfamilyTo() == null ? 0 : getSubfamilyTo().getNumber();
 * &nbsp;&nbsp;&nbsp;return "FROM SUBFAMILY " + subfamilyNumber + " TO SUBFAMILY " + subfamilyToNumber;
 * &nbsp;}
 * </pre>
 *
 * Since version 7.5.3, it is possible to indicate just the reference names, without the key. 
 * So, the above @Depends could also be written in this way:
 * <pre>
 * &nbsp;@Depends("subfamily, subfamilyTo") 
 * &nbsp;public String getRangeDescription() { 		
 * </pre>
 *
 * Note: Since 7.5.3, we can use subfamily instead of subfamily.number for references.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Depends {

	/**
	 * Comma separated list of the properties this property depend on.
	 */
	String value();
	
}
