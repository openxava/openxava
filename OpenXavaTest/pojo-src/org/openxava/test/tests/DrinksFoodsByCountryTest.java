package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.tests.*;

import static org.openxava.test.model.EnumCountry.*;

/**
 * 
 * @author Jeromy Altuna
 */
public class DrinksFoodsByCountryTest extends ModuleTestBase {
	
	public DrinksFoodsByCountryTest(String testName) {
		super(testName, "DrinksFoodsByCountry");
	}
	
	public void testDefaultValueOnInit() throws Exception {
		EnumCountry country = values()[Integer.valueOf(getValue("country"))];
		
		assertValue("country", String.valueOf(country.ordinal()));
		assertValue("foods" , country.foods().toUpperCase());
		assertValue("drinks", country.drinks().toUpperCase()); 	
	}
	
	public void testValidValuesWithRequiredValue() throws Exception {
		String[][] validValues = {
			{"0", "DOMINICAN REPUBLIC"},	
			{"1", "PERU"},
			{"2", "SPAIN"}
		};
		assertValidValues("country", validValues);
	}	
	
	public void testSelectedCountry() throws Exception {
		for(EnumCountry country : values()) {
			setValue("country", String.valueOf(country.ordinal())); 
			assertValue("foods", country.foods().toUpperCase());
			assertValue("drinks", country.drinks().toUpperCase());
		}
	}	
}
