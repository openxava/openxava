package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class SocialNetwork extends Nameable {
	
	@OneToMany(mappedBy="socialNetwork")
	private Collection<Community> communities;

	public Collection<Community> getCommunities() {
		return communities;
	}

	public void setCommunities(Collection<Community> communities) {
		this.communities = communities;
	}
	
}
