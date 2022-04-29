package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.util.*;

/**
 * Create on 20/12/2010 (16:17:35)
 * @author Ana Andres
 */

@IdClass(MixtureKey.class)
@Entity
public class Mixture {
	
	@Id
	private String colorName1;
	
	@Id
	private String colorName2;
	
	private String description;
	
	public String getFullDescription(){
		String one = colorName1.trim();
		one = Strings.repeat(10 - one.length(), "-") + colorName1.trim();
		String two = colorName2.trim();
		two = Strings.repeat(10 - two.length(), "-") + colorName2.trim();
		
		return one + "&" + two + ":" + description;
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
