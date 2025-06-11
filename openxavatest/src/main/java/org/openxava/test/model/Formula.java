package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Views({
	@View(members=
		"name;" +
		"selectedIngredientSize, selectedIngredientNames;" +
		"ingredients { ingredients }" +		
		"recipe { recipe }" +
		"sourceCode { sourceCode }" 
	),
	@View(name="Simple", members="name")
})
@Tab(properties="name, recipe") 
public class Formula {
	
	@Id @Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="ID")	
	private String oid;
	
	@Column(length=40) @Required
	private String name;
	
	@Transient @ReadOnly @LabelFormat(LabelFormatType.SMALL)
	private Integer selectedIngredientSize;

	@Transient @ReadOnly @LabelFormat(LabelFormatType.SMALL)
	private String selectedIngredientNames;
	
	@OnSelectElementAction("Formula.onSelectIngredient") 
	@OneToMany(mappedBy="formula", cascade=CascadeType.REMOVE)
	// tmr @ListProperties("image, ingredient.name") 
	@ListProperties("image, ingredient.name, dosis") // dosis does not exist, to test a case
	private Collection<FormulaIngredient> ingredients;	
	
	@Stereotype("HTML_TEXT")	
	@ReadOnly // If you remove it, rewrite setRecipe()
	private String recipe;
	
	@TextArea
	public String getSourceCode() { 
		return recipe;
	}

	public void setSourceCode(String sourceCode) {
		this.recipe = sourceCode;
	}		
	
	public static Formula findByName(java.lang.String name) throws NoResultException {
		javax.persistence.Query query = org.openxava.jpa.XPersistence.getManager().createQuery("from Formula as o where o.name = :name"); 
		query.setParameter("name", name); 			
		return (Formula) query.getSingleResult();		  		
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
	
	public Collection<FormulaIngredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(Collection<FormulaIngredient> ingredients) {
		this.ingredients = ingredients;
	}	

	public String getRecipe() {
		return recipe;
	}

	public void setRecipe(String recipe) {
		// Given recipe is read only, so it allows setSourceCode() works
	}
	
	public Integer getSelectedIngredientSize() {
		return selectedIngredientSize;
	}

	public void setSelectedIngredientSize(Integer selectedIngredientSize) {
		this.selectedIngredientSize = selectedIngredientSize;
	}

	public String getSelectedIngredientNames() {
		return selectedIngredientNames;
	}

	public void setSelectedIngredientNames(String selectedIngredientNames) {
		this.selectedIngredientNames = selectedIngredientNames;
	}


}
