package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 *  
 * @author Javier Paniza
 */

@Entity
public class Composite extends Nameable {

	
	@ManyToOne @JoinColumn(name="PARENT_ID")
	@OnChange(OnChangeVoidAction.class) // Needed to test a case	
	private Composite composite;

	
	@OneToMany(mappedBy="composite", cascade=CascadeType.REMOVE) 
	private Collection<Composite> children;
	
	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}		

	
	public Collection<Composite> getChildren() {
		return children;
	}

	public void setChildren(Collection<Composite> children) {
		this.children = children;
	}

}
