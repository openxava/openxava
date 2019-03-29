package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

/**
 * Model to test concatenated reports using 
 * {@link org.openxava.test.actions.MovieReportAction MovieReportAction} and to 
 * test stereotypes FILE and FILES. <p>
 * 
 * @author Jeromy Altuna
 */
@Entity
@View(members=
	"data sheet ["   +
	"   title, releaseDate;" +	
	"   director;"   +
	"   writers;"    +
	"   starring;"   +
	"] " +
	"Multimedia 1 {" +
	"   trailer;"    +
	"   scripts;" +
	"}" +
	"Multimedia 2 {" +
	"   photographs" +
	"}"
)
@Tab(properties="title, director, writers, releaseDate, trailer")
public class Movie extends Identifiable {

	private String title;
	private String director;
	private String writers;
	private String starring;
	
	private Date releaseDate;
	
	@Stereotype("FILE")
	@Column(length=32)
	private String trailer;
	
	@Stereotype("FILES")
	@Column(length=32)	
	private String scripts;
	
	@Stereotype("IMAGES_GALLERY")
	@Column(length=32)
	private String photographs;
	
	public static Movie findById(String id) {
		Query query = XPersistence.getManager().createQuery("from Movie m where m.id = :id");
		query.setParameter("id", id);
		return (Movie) query.getSingleResult();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getWriters() {
		return writers;
	}

	public void setWriters(String writers) {
		this.writers = writers;
	}

	public String getStarring() {
		return starring;
	}

	public void setStarring(String starring) {
		this.starring = starring;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	public String getScripts() {
		return scripts;
	}

	public void setScripts(String scripts) {
		this.scripts = scripts;
	}

	public String getPhotographs() {
		return photographs;
	}

	public void setPhotographs(String photographs) {
		this.photographs = photographs;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}	
}
