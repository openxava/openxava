package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * For testing the default schema behaviour. <p>
 *  
 * @author Javier Paniza
 */

@Entity
@Tab(defaultOrder="${description} asc", properties="id, description") // failed to change default schema in as400 
@View(name="IssueWeb", members="id, description")
public class Issue {
	
	@Id @Column(length=5) @Required
	private String id;

	@Column(length=40) @Required
	private String description;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Worker worker;
	
	@OneToMany(mappedBy="issue", cascade=CascadeType.REMOVE)
	private Collection<Comment> comments;
	
	/*
	 * It fails in collection with @Condition in as400 only:
	 * 	SELECT COMPANYB.Comment.id, COMPANYB.Comment.date, COMPANYB.Comment.comment 
	 * 	from COMPANYB.Comment 
	 * 	WHERE COMPANYA.Comment.issue_id = ?
	 * First it go fine, after change schema it not change schema in 'where'
	 */ 
	@Condition("${issue.id} = ${this.id}")
	public Collection<Comment> getCommentsWithCondition(){
		if (Is.empty(this.id)) return Collections.EMPTY_LIST;
		Query query = XPersistence.getManager().createQuery("from Comment where issue.id = :id");
		query.setParameter("id", this.id);
		return query.getResultList();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Editor(value="UserAttribute", forViews="IssueWeb")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<Comment> getComments() {
		return comments;
	}

	public void setComments(Collection<Comment> comments) {
		this.comments = comments;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

}
