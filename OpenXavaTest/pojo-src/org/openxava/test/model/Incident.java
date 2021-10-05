package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.web.editors.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View( members = 
	"incident { title; description; discussion }" +
	"activity { activity }"
)
// DO NOT ADD @Tab we use this class to test default properties without @Tab
public class Incident extends Identifiable {
	
	public static Incident findFirst() { 
		Query query = XPersistence.getManager().createQuery("from Incident"); 
 		return (Incident) query.getResultList().get(0);  				
	}
	
	@Column(length=50) @Required
	private String title;
	
	// tmr @Stereotype("SIMPLE_HTML_TEXT")
	@HtmlText(simple=true) // tmr
	private String description;
	
	// tmpr @Stereotype("DISCUSSION")
	@Discussion // tmr
	@Column(length=32) 
	private String discussion;
	
	@OneToMany(mappedBy="incident", cascade=CascadeType.REMOVE)
	private Collection<IncidentActivity> activity; 
	
	@PreRemove
	private void removeDiscussion() { 
		DiscussionComment.removeForDiscussion(discussion);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiscussion() {
		return discussion;
	}

	public void setDiscussion(String discussion) {
		this.discussion = discussion;
	}

	public Collection<IncidentActivity> getActivity() {
		return activity;
	}

	public void setActivity(Collection<IncidentActivity> activity) {
		this.activity = activity;
	}

}
