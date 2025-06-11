package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class ProjectTeam extends Nameable {
	
	@OneToMany(mappedBy="team")
	@OrderColumn 
	@CollectionView("Simple")
	// tmr puede que tenga que quitar phobias
	@ListProperties("name, project.name, phobias") // phobias does not exist in ProjectMember, to test a case 
	List<ProjectMember> members;
	 
	

}
