package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import lombok.*;

/**
 * Traveler entity extending Identifiable with a name and a reference to the last Journey.
 * 
 * @author Javier Paniza
 */
@Entity @Getter @Setter
@View(members = "name; lastJourney; nextJourney")
@View(name = "Slow", members = "name; slowJourney")
@Tab(defaultOrder = "name", properties="name, lastJourney.name, lastJourney.description, nextJourney.name, nextJourney.description")
@Tab(name="Slow", properties="name, slowJourney.name")
public class Traveler extends Identifiable {

    @Column(length = 50) @Required
    String name;

    @DescriptionsList(orderByKey = true, descriptionProperties = "slowName") // slowName to test on demand fetch in server side
    @ManyToOne(fetch = FetchType.LAZY)
    Journey lastJourney;

    @DescriptionsList(orderByKey = true, descriptionProperties = "name") 
    @ManyToOne(fetch = FetchType.LAZY)
    Journey nextJourney;

    @DescriptionsList(orderByKey = true, descriptionProperties = "ultraSlowName")
    @ManyToOne(fetch = FetchType.LAZY)
    Journey slowJourney;  // TMR ME QUEDÉ POR AQUÍ: PARA HACER UN TEST QUE VERIFIQUE NO CARGA ELEMENTOS AL PULSAR NEW  

}
