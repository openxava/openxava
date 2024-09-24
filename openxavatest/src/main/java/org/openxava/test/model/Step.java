package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Chungyen Tsai
 */
@Entity
@Getter
@Setter
@View(members="id; description; path; treeOrder")
public class Step {
	@Id
	private int id;
	
	private String path;
	
	private String description;
	
	@ManyToOne
	private TreeContainer parentContainer;
	
	private int treeOrder;

}
