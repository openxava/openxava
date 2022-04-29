package org.openxava.test.model;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class FormulaIngredient {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FORMULA")
	private Formula formula;

	@Id @Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="ID")
	private String oid;
	
	
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="INGREDIENT")
	@DescriptionsList
	private Ingredient ingredient;
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ACCENTUATE")
	@DescriptionsList(condition="${partOf.oid} = ?", depends="this.ingredient")
	private Ingredient accentuate;	
	
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="ANOTHERFORMULA")
	@DescriptionsList
	private Formula anotherFormula;
	
	@Stereotype("IMAGE")
	private byte [] image; 
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}	
		
	public Ingredient getAccentuate() {
		return accentuate;
	}
	
	public void setAccentuate(Ingredient accentuate) {
		this.accentuate = accentuate;
	}	

	public Formula getAnotherFormula() {
		return anotherFormula;
	}

	public void setAnotherFormula(Formula anotherFormula) {
		this.anotherFormula = anotherFormula;
	}
		
	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	

}
