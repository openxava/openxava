package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@DiscriminatorColumn(name="TYPE")
@DiscriminatorValue("HUM")
@Table(name="PERSON")
@AttributeOverrides(
	@AttributeOverride(name="name", column=@Column(name="PNAME"))
)
@View(name="WithGroup", members="name; group [ sex ]")
public class Human extends Nameable {

	@Enumerated(EnumType.STRING)
	private Sex sex;
	public enum Sex { MALE, FEMALE };
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTHOR")
	@DescriptionsList(descriptionProperties="author")
	private Author favoriteAuthor;

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Author getFavoriteAuthor() {
		return favoriteAuthor;
	}

	public void setFavoriteAuthor(Author favoriteAuthor) {
		this.favoriteAuthor = favoriteAuthor;
	}

}
