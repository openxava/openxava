package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

/**
 * tmp
 * @author Javier Paniza
 */
@Entity @Getter @Setter 
public class ProductDefinition extends Nameable {
	
	@OneToMany(mappedBy="productDefinition", cascade=CascadeType.ALL)
	Collection<ProductPart> parts;

}
