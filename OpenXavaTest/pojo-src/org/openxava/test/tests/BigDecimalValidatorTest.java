package org.openxava.test.tests;

import java.math.*;

import org.openxava.util.*;
import org.openxava.validators.*;

import junit.framework.TestCase;

/**
 * @author Ana Andres
 * Created on 15 abr. 2020
 */
public class BigDecimalValidatorTest extends TestCase {

	public void testSevenDecimals() throws Exception {
		BigDecimalValidator validator = new BigDecimalValidator();
		Messages errors = new Messages();
		BigDecimal value = new BigDecimal("0.0000000");
		validator.setMaximumFractionDigits(7);
		validator.setMaximumIntegerDigits(2);
		validator.validate(errors, value, "", "");
		assertTrue(Is.empty(errors.toString()));
	}
	
}
