package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * tmp
 * TMP ME QUEDÉ POR AQUÍ: INTENTANDO REPRODUCIR EL PROBLEMA. SIN ÉXITO.
 * @author Javier Paniza
 */

@View(name="Simple", members="name")
@Entity @Getter @Setter
public class ProductPart extends Nameable {
	
	@ManyToOne
	ProductDefinition productDefinition;
	
	@ManyToOne
	ProductPart parentPart;
	
	@CollectionView("Simple")
	@OneToMany(mappedBy="parentPart", cascade = CascadeType.ALL)
	Collection<ProductPart> subparts;

}
