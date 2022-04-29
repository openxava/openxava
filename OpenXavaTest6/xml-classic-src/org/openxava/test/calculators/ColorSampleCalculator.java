package org.openxava.test.calculators;

import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

public class ColorSampleCalculator implements ICalculator {
	
	private String colorName;

	public Object calculate() throws Exception {
		if ("red".equalsIgnoreCase(colorName) ||
			"rojo".equalsIgnoreCase(colorName)) return "RED";
		if ("black".equalsIgnoreCase(colorName) ||
			"negro".equalsIgnoreCase(colorName)) return "BLACK";
		return "nocolor";
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	
}
