package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(name="EditableMembers") 
public class Community extends Nameable {
	
	@ManyToOne
	@DescriptionsList
	private SocialNetwork socialNetwork;   
	
	@ManyToMany
	// @ListAction("ManyToMany.new") // Don't uncomment in order to test. Not needed since 5.7 because we add it by default 
	@EditAction(forViews="EditableMembers", value="ManyToMany.edit") 
	@OrderBy("name asc") 	
	private Collection<Human> members;

	public void setMembers(Collection<Human> members) {
		this.members = members;
	}

	public Collection<Human> getMembers() {
		return members;
	}

	public SocialNetwork getSocialNetwork() {
		return socialNetwork;
	}

	public void setSocialNetwork(SocialNetwork socialNetwork) {
		this.socialNetwork = socialNetwork;
	}
	
}
