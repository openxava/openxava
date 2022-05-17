package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Tab(defaultOrder="${make} asc")
public class Car extends Identifiable {
	
	@Column(length=20) @Required
	private String make;
	
	@Column(length=50) @Required
	private String model;
	
	public enum Color { UNSPECIFIED, RED, YELLOW, WHITE, BLACK }
	@Editor("CarColor")
	private Color color;
	
	@ElementCollection
	private Collection<CarPhoto> photos;

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Collection<CarPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(Collection<CarPhoto> photos) {
		this.photos = photos;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
