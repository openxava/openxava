package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
abstract public class ProjectVersion extends Nameable {
	
	abstract public Collection<Feature> getFeatures(); // In this way to test a case

}
