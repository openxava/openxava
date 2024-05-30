package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
public class LeadStatus extends Identifiable {
	
	@Column(length=40) @Required
	String description;
	
	boolean finished;

}
