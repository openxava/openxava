package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

// WARNING! DON'T CREATE A TEST CASE FOR Animal, TO TEST A CASE WITH Mammal
@Entity @Getter @Setter
@View(extendsView = "super.DEFAULT", members = "; type")
public class Animal extends LivingBeing {
	
	public enum Type { HERBIVORE, CARNIVORE, OMNIVORE };
	Type type;	
		
}
