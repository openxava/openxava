package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Journalist extends Nameable {
	
	@ManyToMany(cascade=CascadeType.REMOVE)
	private Collection<JournalistArticle> articles;

	public Collection<JournalistArticle> getArticles() {
		return articles;
	}

	public void setArticles(Collection<JournalistArticle> articles) {
		this.articles = articles;
	}

}
