package org.openxava.validators;



import org.openxava.util.*;

/**
 * Assert that a comparation is true. <p>
 * 
 * @author Javier Paniza
 */

public class ComparatorValidator implements IValidator {
	
	private Comparable value1;
	private Comparable value2;
	private String operator;
	private String name1;
	private String name2;
	
	
	
	public void validate(Messages errors) throws Exception { 
		if (operator == null) operator = "=";
		operator = operator.trim().toUpperCase();
		if (operator.equals("LT") || operator.equals("<")) {
			if (value1.compareTo(value2) >= 0) {
				errors.add("must_to_be_less", name1, name2);
			}
		}	
		else if (operator.equals("GT") || operator.equals(">")) {
			if (value1.compareTo(value2) <= 0) {
				errors.add("must_to_be_greater", name1, name2);
			}
		}
		else if (operator.equals("LE") || operator.equals("<=")) {
			if (value1.compareTo(value2) > 0) {
				errors.add("must_to_be_less_or_equal", name1, name2);
			}
		}
		else if (operator.equals("GE") || operator.equals(">=")) {
			if (value1.compareTo(value2) < 0) {
				errors.add("must_to_be_greater_or_equal", name1, name2);
			}
		}
		else if (operator.equals("EQ") || operator.equals("=") || operator.equals("==")) {
			if (value1.compareTo(value2) != 0) {
				errors.add("must_to_be_equal", name1, name2);
			}
		}
		else if (operator.equals("NE") || operator.equals("!=")) {
			if (value1.compareTo(value2) == 0) {
				errors.add("must_to_be_not_equal", name1, name2);
			}
		}
		else {
			throw new Exception(XavaResources.getString("unknow_operator", operator, "EQ, NE, GT, LT, GE, LE, =, ==, !=, >, <, >=, <="));
		}
	}

	/**
	 * Operator used for comparing. <p>
	 * 
	 * Values allowed are EQ, NE, GT, LT, GE, LE, =, ==, !=, >, <, >=, <=. <br>
	 * Default value is =.
	 */
	public String getOperator() {
		return operator;
	}
	/**
	 * Operator used for comparing. <p>
	 * 
	 * Values allowed are EQ, NE, GT, LT, GE, LE, =, ==, !=, >, <, >=, <=. <br>
	 * Default value is =.
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * First value to compare. <p>
	 */
	public Comparable getValue1() {
		return value1;
	}
	/**
	 * First value to compare. <p>
	 */
	public void setValue1(Comparable value1) {
		this.value1 = value1;
	}

	/**
	 * Second value to compare. <p>
	 */	
	public Comparable getValue2() {
		return value2;
	}
	/**
	 * Second value to compare. <p>
	 */
	public void setValue2(Comparable value2) {
		this.value2 = value2;
	}

	/**
	 * Name (used for retrieve label from i18n files) for first value.
	 */
	public String getName1() {
		return name1;
	}
	/**
	 * Name (used for retrieve label from i18n files) for first value.
	 */
	public void setName1(String name1) {
		this.name1 = name1;
	}

	/**
	 * Name (used for retrieve label from i18n files) for second value.
	 */	
	public String getName2() {
		return name2;
	}
	/**
	 * Name (used for retrieve label from i18n files) for second value.
	 */
	public void setName2(String name2) {
		this.name2 = name2;
	}

}
