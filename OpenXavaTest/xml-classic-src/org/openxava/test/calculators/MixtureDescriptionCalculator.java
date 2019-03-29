package org.openxava.test.calculators;

import org.openxava.util.*;
import org.openxava.calculators.*;

public class MixtureDescriptionCalculator implements ICalculator {
	
	private String colorName1;	
	
	private String colorName2;
	
	private String description;
	
	public Object calculate() {
		String one = getColorName1().trim();
		one = Strings.repeat(10 - one.length(), "-") + getColorName1().trim();
		String two = getColorName2().trim();
		two = Strings.repeat(10 - two.length(), "-") + getColorName2().trim();
		return one + "&" + two + ":" + getDescription();
	}

	public String getColorName1() {
		return colorName1;
	}

	public void setColorName1(String colorName1) {
		this.colorName1 = colorName1;
	}

	public String getColorName2() {
		return colorName2;
	}

	public void setColorName2(String colorName2) {
		this.colorName2 = colorName2;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
