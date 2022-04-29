package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@Embeddable
public class ProjectNote {
	
	@Column(length=50) @Required
	private String note;
	private Date date;
	
	@Stereotype("WEBURL") @Column(length=100)  
	private String docURL; 
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDocURL() {
		return docURL;
	}
	public void setDocURL(String docURL) {
		this.docURL = docURL;
	}

}
