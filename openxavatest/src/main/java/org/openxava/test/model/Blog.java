package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(members="title, date; body; comments;")
public class Blog extends BlogComment {
	
	@Column(length=50) @Required
	private String title;
		
	@OneToMany(mappedBy="parent", cascade=CascadeType.REMOVE)
	private Collection<BlogComment> comments;
	
	public Collection<BlogComment> getComments() {
		return comments;
	}

	public void setComments(Collection<BlogComment> comments) {
		this.comments = comments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
