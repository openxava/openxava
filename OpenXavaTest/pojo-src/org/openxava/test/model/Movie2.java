package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier paniza
 */
@Entity
@Table(name="MOVIE")
public class Movie2 extends Identifiable {

	@Required
	private String title;
		
	@Required
	@Stereotype("FILES")
	@Column(length=32)	
	private String scripts;
		

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getScripts() {
		return scripts;
	}

	public void setScripts(String scripts) {
		this.scripts = scripts;
	}

}
