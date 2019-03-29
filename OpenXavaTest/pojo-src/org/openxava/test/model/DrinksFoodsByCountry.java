package org.openxava.test.model;

import org.openxava.annotations.*;

/**
 * 
 * @author Jeromy Altuna
 */
@View(members = "country; foods; drinks;")
public class DrinksFoodsByCountry {
		
	@Required
	@DefaultValueCalculator(
		org.openxava.test.calculators.EnumCountryCalculator.class
	)
	private EnumCountry country;
		
	@Depends("country")
	public String getFoods() {
		return country.foods();
	}
	
	@Depends("country")
	public String getDrinks() {
		return country.drinks();
	}
	
	public EnumCountry getCountry() {
		return country;
	}
	public void setCountry(EnumCountry country) {
		this.country = country;
	}
}
