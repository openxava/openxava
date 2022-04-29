package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
@View(name="NoDriverNoChampionshipScore", members="race; year; score")
public class RaceScore extends Identifiable {
	
    @ManyToOne
    ChampionshipScore championshipScore;
	
    @Column(length = 50)
    @Required
    String race;
	
	@Column(length=4)
    @DefaultValueCalculator(CurrentYearCalculator.class)
    @Required
    int year;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	Driver driver;
	
	int score;

}
