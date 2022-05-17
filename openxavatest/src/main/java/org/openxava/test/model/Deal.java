package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members=
	"main { id, name }" +
	"assured { assured }" 
) 
@Tab(properties="id, name, assured.name") 
public class Deal { 
	
	@Id
	private Long id;
	
	@Column(length=40)
	private String name;
	
	@OneToOne 
	@PrimaryKeyJoinColumn
	private DealAssured assured;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DealAssured getAssured() {
		return assured;
	}

	public void setAssured(DealAssured assured) {
		this.assured = assured;
	} 
	
} 