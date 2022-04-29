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
public class JournalistArticle extends Identifiable {
	
	@Column(length=80) @Required
	private String title;
	
	@ManyToMany(mappedBy="articles")
	private Collection<Journalist> journalists;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Collection<Journalist> getJournalists() {
		return journalists;
	}

	public void setJournalists(Collection<Journalist> journalists) {
		this.journalists = journalists;
	}

}
