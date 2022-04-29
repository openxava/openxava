package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class SocialNetwork extends Identifiable  {
	
	@Stereotype("NO_HTML_IN_LIST")
	@Column(length=50) @Required
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(mappedBy="socialNetwork")
	private Collection<Community> communities;

	public Collection<Community> getCommunities() {
		return communities;
	}

	public void setCommunities(Collection<Community> communities) {
		this.communities = communities;
	}
	
}
