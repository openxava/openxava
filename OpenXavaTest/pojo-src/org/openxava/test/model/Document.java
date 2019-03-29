package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Document extends Nameable {

	@Column(length=50)
	@Stereotype("DOCUMENT_LIBRARY")
	private String files;

	public void setFiles(String files) {
		this.files = files;
	}

	public String getFiles() {
		return files;
	} 
}
