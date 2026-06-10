package org.openxava.test.model;

import jakarta.persistence.*;

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
