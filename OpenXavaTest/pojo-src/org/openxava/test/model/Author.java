package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.test.annotations.*;

@Entity
@GoodName
@View(members="header [ author; biography ]; humans")
@Tab(defaultOrder="${author} asc")
public class Author {
	
	@Id @Column(length=30)
	private String author; // The same name of the reference used from Book: for testing a bug
	
	@Stereotype("MEMO") // Must be MEMO to test a case
	private String biography;
	
	@OneToMany(mappedBy="favoriteAuthor")
	@CollectionView(value="WithGroup")
	@ListAction("Author.showAllAuthors")
	@ListAction("Author.showSelectedAuthors")
	private Collection<Human> humans;
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public Collection<Human> getHumans() {
		return humans;
	}

	public void setHumans(Collection<Human> humans) {
		this.humans = humans;
	}

}
