package org.openxava.school.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Teacher {
	
	@Id @Column(length=5) @Required   
	private String id;
	
	@Column(length=40) @Required
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
