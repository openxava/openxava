package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Getter @Setter
public class ArticleDescriptionSelection {
	
	@ManyToOne
	@OnChange(OnChangeVoidAction.class)
    private ArticleDescription articleDescription;

}
