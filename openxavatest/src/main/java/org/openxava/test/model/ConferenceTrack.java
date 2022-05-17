package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
public class ConferenceTrack extends Nameable {
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Conference mainFor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Conference secondaryFor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	private ConferenceTrack parrallelTrack; 

	public Conference getMainFor() {
		return mainFor;
	}

	public void setMainFor(Conference mainFor) {
		this.mainFor = mainFor;
	}

	public Conference getSecondaryFor() {
		return secondaryFor;
	}

	public void setSecondaryFor(Conference secondaryFor) {
		this.secondaryFor = secondaryFor;
	}

	public ConferenceTrack getParrallelTrack() {
		return parrallelTrack;
	}

	public void setParrallelTrack(ConferenceTrack parrallelTrack) {
		this.parrallelTrack = parrallelTrack;
	}

}
