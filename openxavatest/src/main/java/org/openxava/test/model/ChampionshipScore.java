package org.openxava.test.model;

import java.util.*;

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
public class ChampionshipScore extends Identifiable {
	
    @Column(length = 50)
    @Required
    String championship;
	
	@Column(length=4)
    @DefaultValueCalculator(CurrentYearCalculator.class)
    @Required
    int year;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	Driver driver;
	
	int score;
	
    @OneToMany(mappedBy="championshipScore")
    @CollectionView("NoDriverNoChampionshipScore") 
    private Collection<RaceScore> racesScores;

}
