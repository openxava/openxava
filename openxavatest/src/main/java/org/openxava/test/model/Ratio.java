package org.openxava.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that represents a ratio with description and value.
 * 
 * @author Javier Paniza
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ratio {
	
	String description;
	int value;
}
