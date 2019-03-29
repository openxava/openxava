package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.hibernate.envers.*;
import org.openxava.annotations.*;

/**
 * To test @javax.validation.constraints.Size with min and max elements on a 
 * collection that simulate embedding
 *  
 * @author Jeromy Altuna
 */
@Entity
@Audited
@AuditOverrides({
	@AuditOverride(forClass=Nameable.class, name="name", isAudited=true)
})
@Tab(properties="name, file") 
public class Exam extends Nameable {
	
	@Column(length=32) @Stereotype("FILE") @NotAudited
	private String file;
	
	@javax.validation.constraints.Size(min=1, max=4)
	@OneToMany(mappedBy="exam", cascade=CascadeType.ALL)
	private Collection<Question> questioning;
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Collection<Question> getQuestioning() {
		return questioning;
	}

	public void setQuestioning(Collection<Question> questioning) {
		this.questioning = questioning;
	}	
}