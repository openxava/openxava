package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Report extends Identifiable {

	@Column(length=50)
	private String name;
	
	@AsEmbedded
	@ManyToOne(fetch=FetchType.LAZY)
	private Ranges ranges;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ranges getRanges() {
		return ranges;
	}

	public void setRanges(Ranges ranges) {
		this.ranges = ranges;
	}
	
}
