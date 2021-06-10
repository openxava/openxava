package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * tmp
 * @author Javier Paniza
 */
@Entity @Getter @Setter 
public class ProductDefinition extends Nameable {
	
	@CollectionView("FromProductDefinition")
	@OneToMany(mappedBy="productDefinition", cascade=CascadeType.ALL)
	Collection<ProductPart> parts;

}
