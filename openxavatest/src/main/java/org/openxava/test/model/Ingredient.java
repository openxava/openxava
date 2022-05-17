package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.jpa.XPersistence;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(name="Sections", members="section1{ name; section2 { partOf, favouriteFormula } }")
public class Ingredient {
	
	@Id @Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="ID")
	private String oid;
	
	@Column(length=40) @Required
	private String name;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARTOF")
	@DescriptionsList
	private Ingredient partOf;
	
	@ManyToOne(fetch=FetchType.LAZY) @DescriptionsList
	private Formula favouriteFormula; // For testing cyclic references

	public static Ingredient findByName(String name) throws NoResultException {
		Query query = XPersistence.getManager().createQuery("from Ingredient where name = :name"); 
		query.setParameter("name", name); 			
		return (Ingredient) query.getSingleResult();		  		
	}
	
	public Formula getFavouriteFormula() {
		return favouriteFormula;
	}

	public void setFavouriteFormula(Formula favouriteFormula) {
		this.favouriteFormula = favouriteFormula;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ingredient getPartOf() {
		return partOf;
	}

	public void setPartOf(Ingredient partOf) {
		this.partOf = partOf;
	}

}
