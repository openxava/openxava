package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.model.*;


/**
 * 
 * @author Sebastien Diot
 */

@MappedSuperclass
public abstract class AbtractWallEntry<W extends AbstractWall<?>> extends Identifiable {
	
	@ManyToOne(optional=false)	
	private W wall;

	public void setWall(W wall) {
		this.wall = wall;
	}
	
	public W getWall() {
		return wall;
	}
	
}
