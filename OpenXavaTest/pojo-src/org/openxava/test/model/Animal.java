package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.model.*;

import lombok.*;

/**
 * tmr
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class Animal extends Identifiable {
	
	public enum Type { HERBIVORE, CARNIVORE, OMNIVORE };
	Type type;	
		
}
