package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/** 
 * 
 * @author Javier Paniza
 */

@Entity
public class Doc extends Identifiable {
	
	@Column(length=40) @Required
	private String title;
	
	@HtmlText 
	@Lob
	private String content;
	
	private int worldCount;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getWorldCount() {
		return worldCount;
	}

	public void setWorldCount(int worldCount) {
		this.worldCount = worldCount;
	}

}
