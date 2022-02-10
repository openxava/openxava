package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * tmr 
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class LivingBeing extends Identifiable {

	@Column(length=40) @Required
	String name;

}
