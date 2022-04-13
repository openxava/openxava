package org.openxava.web.editors;

import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

/** 
 * 
 * @author Javier Paniza
 */
@Entity
@Table(name="OXDISCUSSIONCOMMENTS", indexes={@Index(columnList="discussionId")}) 
public class DiscussionComment extends Identifiable {
	
	@Column(length=32)
	private String discussionId;
	
	@Column(length=30) 
	private String userName;
	
	
	private Timestamp time;

	@Lob @Column(length=16777216) 
	@Stereotype("HTML_TEXT") 
	private String comment;
	
	public static Collection<DiscussionComment> findByDiscussion(String discussionId) {
		if (discussionId == null) return Collections.EMPTY_LIST;
		Query query = XPersistence.getManager().createQuery("from DiscussionComment c where c.discussionId = :discussionId");
		query.setParameter("discussionId", discussionId);
		return query.getResultList();
	}
	
	public static void removeForDiscussion(String discussionId) {
		for (DiscussionComment comment: findByDiscussion(discussionId)) {
			XPersistence.getManager().remove(comment);
		}
	}	

	
	@PrePersist
	private void setTime() {
		time = new Timestamp(new Date().getTime());
	}

	public String getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}
	
}
