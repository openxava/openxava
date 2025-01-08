package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

import lombok.*;

/**
 * 
 * @since 7.0.5
 * @author Javier Paniza
 */

@Entity @Getter @Setter
@View(members="year; contributions; sectionA { } sectionB { } sectionC { } ")
public class AnnualContribution {
	
	@Id @Column(length=4)
	@OnChange(OnChangeYearAction.class)
	int year;
	
	@ElementCollection
	Collection<ContributionDetail> contributions;

}
