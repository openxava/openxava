package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.annotations.File; // tmp ¿En migración?
import org.openxava.jpa.*;
import org.openxava.model.*;

/**
 * tmp Redoc
 * Model to test concatenated reports using 
 * {@link org.openxava.test.actions.MovieReportAction MovieReportAction} and to 
 * test stereotypes FILE and FILES. <p>
 * 
 * @author Jeromy Altuna
 */
@Entity
@Table(name="Movie")
/* tmp
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
*/
@Tab(properties="title, director, writers, releaseDate, trailer")
public class Movie3 extends Identifiable {

	private String title;
	private String director;
	private String writers;
	private String starring;
	
	private Date releaseDate;
	
	@File
	@Column(length=32)
	private String trailer;
	
	
	@Stereotype("IMAGES_GALLERY")
	@Column(length=32)
	private String photographs;
	
	@Files
	@Column(length=32)	
	private String scripts;

	
	public static Movie3 findById(String id) {
		Query query = XPersistence.getManager().createQuery("from Movie m where m.id = :id");
		query.setParameter("id", id);
		return (Movie3) query.getSingleResult();
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
