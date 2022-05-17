package org.openxava.test.model;

import org.openxava.annotations.*;
import org.openxava.model.*;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class Journal extends Identifiable {
	
	@Required
	private Date date;
	
	@Column(length=40) @Required
	private String description;

	@OneToMany(mappedBy="theJournal",cascade=CascadeType.ALL)	
	private Collection<JournalEntry> entries;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<JournalEntry> getEntries() {
		return entries;
	}

	public void setEntries(Collection<JournalEntry> entries) {
		this.entries = entries;
	}
	
}
