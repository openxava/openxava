package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

// WARNING! DON'T CREATE A TEST CASE FOR LivingBeing, TO TEST A CASE WITH Mammal
@Entity @Getter @Setter
@View(members = "name")
public class LivingBeing extends Identifiable {

	@Column(length=40) @Required
	String name;

}
