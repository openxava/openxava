package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity
@Getter
@Setter
public class Lender extends Identifiable{

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("PersonalInformation")
	Customer customer;
	
}
