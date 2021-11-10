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
	List<ProjectMember> members; 

}
