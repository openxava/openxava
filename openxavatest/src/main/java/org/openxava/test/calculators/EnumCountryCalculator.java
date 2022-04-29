package org.openxava.test.calculators;

import java.util.*;
import org.openxava.calculators.*;

import static org.openxava.test.model.EnumCountry.*;

/**
 * 
 * @author Jeromy Altuna
 */
public class EnumCountryCalculator implements ICalculator {
		
	private static final long serialVersionUID = -7916981544300283656L;

	@Override
	public Object calculate() throws Exception {
		int index = new Random().nextInt(values().length);		
		return values()[index];
	}
}
