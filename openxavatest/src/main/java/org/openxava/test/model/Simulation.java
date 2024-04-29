package org.openxava.test.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity @Data
@View(name="NoDetails", members="product")
@Tab(properties = "product.number, product.description")
public class Simulation implements Serializable {
	
	@Id
	@OneToOne
	@NoCreate
	@NoModify
	@ReferenceView("Simple")
	private Product product;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "simulation", cascade = CascadeType.REMOVE)
	@ListProperties("product.number, product.description, weightPercentage+")
	@OrderColumn
	private List<SimulationDetail> details;
	
}