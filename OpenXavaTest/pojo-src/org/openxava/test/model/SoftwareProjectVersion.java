package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class SoftwareProjectVersion extends ProjectVersion  {
	
	private Date releaseDate;
	
	@ElementCollection
	@ListProperties("description, estimatedDays+[softwareProjectVersion.totalDays]") // tmp
	private Collection<Feature> features;
	
	@Calculation("sum(features.estimatedDays) + 100") @ReadOnly
	private int totalDays; // tmp 

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

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}
	
}
