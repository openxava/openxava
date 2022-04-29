package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;


/**
 * 
 * @author Javier Paniza 
 */
@Entity
public class Conference extends Nameable {
	
	@OneToMany(mappedBy = "mainFor", cascade=CascadeType.REMOVE)
	private Collection<ConferenceTrack> mainTracks;

	@OneToMany(mappedBy = "secondaryFor", cascade=CascadeType.REMOVE)	
	private Collection<ConferenceTrack> secondaryTracks;

	public Collection<ConferenceTrack> getMainTracks() {
		return mainTracks;
	}

	public void setMainTracks(Collection<ConferenceTrack> mainTracks) {
		this.mainTracks = mainTracks;
	}

	public Collection<ConferenceTrack> getSecondaryTracks() {
		return secondaryTracks;
	}

	public void setSecondaryTracks(Collection<ConferenceTrack> secondaryTracks) {
		this.secondaryTracks = secondaryTracks;
	}

}
