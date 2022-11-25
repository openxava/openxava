package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * tmr ME QUEDÉ POR AQUÍ. ENTIDADES HECHAS, FALTA CREAR TABLAS, COMPROBAR QUE FUNCIONA,
 * tmr						AÑADIR CONDICIÓN EN DESCRIPTIONSLIST, REPRODUCIR BUG,
 * tmr						HACER PRUEBA JUNIT. ARREGLAR BUG.
 * @author Javier Paniza 
 */

@Entity @Getter @Setter
public class ProductsEvaluation2 extends Identifiable {
	
	@Column(length=40) @Required
	String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	Family2 family;
	
	@ElementCollection
	@ListProperties("product, evaluation")
	Collection<ProductEvaluation2> evaluations;

}
