package org.openxava.test.model;

import org.openxava.model.*;
import org.openxava.test.actions.*;
import org.openxava.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@View(name="ConfirmName", members="artistStudio; name; age") 
public class Artist extends Identifiable {
	
	@ManyToOne
	private Studio artistStudio; // Not the same name of parent entity, to test a case
	
	@Column(length=40) @Required
	@OnChange(forViews="ConfirmName", value=OnChangeArtistNameAction.class) 
	private String name;
	
	@Max(90l)	
	private Integer age;
		
	public Studio getArtistStudio() {
		return artistStudio;
	}

	public void setArtistStudio(Studio artistStudio) {
		this.artistStudio = artistStudio;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}
