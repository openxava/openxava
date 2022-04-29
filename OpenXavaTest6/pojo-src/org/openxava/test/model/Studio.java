package org.openxava.test.model;


import java.util.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity 
public class Studio extends Identifiable { 
	
	@Column(length= 40) @Required 
	private String name;
	 
	@OneToMany(mappedBy="artistStudio") @AsEmbedded 
	private Collection<Artist> artists;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Artist> getArtists() {
		return artists;
	}

	public void setArtists(Collection<Artist> artists) {
		this.artists = artists;
	}
	
} 