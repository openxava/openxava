package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

/**
 * tmr 
 * TMR ME QUEDÉ POR AQUÍ: FALTA CREAR LAS TABLAS Y PROBARLO
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class ProjectTeam extends Nameable {
	
	@OneToMany(mappedBy="team")
	@OrderColumn 
	List<ProjectMember> members; 

}
