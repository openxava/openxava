package org.openxava.test.tests.byfeature;


import java.math.BigDecimal;

import org.openxava.util.Is;
import org.openxava.util.Messages;
import org.openxava.validators.BigDecimalValidator;

import junit.framework.TestCase;

/**
 * @author Ana Andres Created on 15 abr. 2020
 */
public class BigDecimalValidatorTest extends TestCase {

	public void testSevenDecimals() throws Exception {
		BigDecimalValidator validator = new BigDecimalValidator();
		validator.setMaximumFractionDigits(7);
		validator.setMaximumIntegerDigits(2);
		Messages errors = new Messages();

		BigDecimal value = new BigDecimal("0.0000000");
		validator.validate(errors, value, "", "");
		assertTrue(Is.empty(errors.toString()));
		
		value = new BigDecimal("0.0000007");
		validator.validate(errors, value, "", "");
		assertTrue(Is.empty(errors.toString()));
		
		value = new BigDecimal("0.00001007");
		validator.validate(errors, value, "", "");
		assertTrue(errors.contains("greater_number_fraction"));
		
		value = new BigDecimal("111.009");
		validator.validate(errors, value, "", "");
		assertTrue(errors.contains("greater_than_the_awaited"));
	}

}
