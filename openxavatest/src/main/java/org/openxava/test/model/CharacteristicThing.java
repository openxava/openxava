package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.jpa.XPersistence;

/**
 * Create on 30/04/2010 (10:15:33)
 * @author Ana Andres
 */

@Entity
public class CharacteristicThing {

	@Id @Column(length=5) @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer number;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	private Thing thing;
	
	private String description;

	public static CharacteristicThing findByNumber(int number) throws NoResultException {
		Query query = XPersistence.getManager().createQuery("from CharacteristicThing where number = :number");
		query.setParameter("number", number);
		return (CharacteristicThing) query.getSingleResult();
	}
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
