package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.validators.*;

@Entity
@EntityValidator(value=BookValidator.class,
	message="The synopsis does not match with the title",
	properties= { 
		@PropertyValue(name="title"), 
		@PropertyValue(name="synopsis")
	}
)
public class Book extends Identifiable {
	
	@Required(message="{title_required}") 
	@PropertyValidator(value=BookTitleValidator.class, message="{rpg_book_not_allowed}")
	private String title; 
	
	@ManyToOne 
	private Author author;
		 
	private boolean outOfPrint;  
	
	private BigInteger pages;  
	
	@Required(message="Please, enter a synopsis for the book") 
	@Stereotype("MEMO")
	private String synopsis;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public boolean isOutOfPrint() {
		return outOfPrint;
	}

	public void setOutOfPrint(boolean outOfPrint) {
		this.outOfPrint = outOfPrint;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public BigInteger getPages() {
		return pages;
	}

	public void setPages(BigInteger pages) {
		this.pages = pages;
	}

}
