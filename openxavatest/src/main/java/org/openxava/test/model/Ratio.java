package org.openxava.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa un ratio con descripción y valor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ratio {
	
	String description;
	int value;
}
