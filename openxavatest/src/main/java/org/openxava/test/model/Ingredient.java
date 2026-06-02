package org.openxava.test.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

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
	@UUID32
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
		TypedQuery<Ingredient> query = XPersistence.getManager().createQuery("SELECT i FROM Ingredient i WHERE i.name = :name", Ingredient.class); 
		query.setParameter("name", name); 			
		return query.getSingleResult();		  		
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
