package org.openxava.test.model;

import java.util.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity @Getter @Setter 
public class ProductDefinition extends Nameable {
	
	@CollectionView("FromProductDefinition")
	@OneToMany(mappedBy="productDefinition", cascade=CascadeType.ALL)
	Collection<ProductPart> parts;

}
