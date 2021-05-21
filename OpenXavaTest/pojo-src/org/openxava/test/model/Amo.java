package org.openxava.test.model;

import javax.persistence.*;

import lombok.*;

/**
 * tmp A borrar
 * 
 * @author javi
 */

@Entity @Getter @Setter
public class Amo extends Nameable {
	
	@ManyToOne
	Perro perro;

}
