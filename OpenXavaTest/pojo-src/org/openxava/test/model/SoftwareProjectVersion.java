package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class SoftwareProjectVersion extends ProjectVersion {
	
	private Date releaseDate;
	
	@ElementCollection
	private Collection<Feature> features;

	public Collection<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Collection<Feature> features) {
		this.features = features;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	
}
