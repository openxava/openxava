package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity @Getter @Setter
@View(extendsView = "super.DEFAULT", members = "; pregnancyPeriodInWeeks")
public class Mammal extends Animal {
	
	int pregnancyPeriodInWeeks;

}
