package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class ArticleDescription extends Identifiable {
	
	@OneToOne
	@SearchKey
	Article article;
	
	@Column(length=50) 
	String description;
	
}
