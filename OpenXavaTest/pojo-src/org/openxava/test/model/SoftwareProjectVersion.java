package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PROJECTVERSION") // TMP ME QUEDÉ POR AQUÍ: INTENTANDO PROBAR CON PROPIEDADES @Calculation
public class SoftwareProjectVersion extends Nameable /* tmp extends ProjectVersion */ {
	
	private Date releaseDate;
	
	@ElementCollection
	@ListProperties("description, estimatedDays[softwareProjectVersion.totalDays]") // tmp
	private Collection<Feature> features;
	
	@Calculation("sum(features.estimatedDays)")
	private BigDecimal totalDays; // tmp

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

	public BigDecimal getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(BigDecimal totalDays) {
		this.totalDays = totalDays;
	}
	
}
