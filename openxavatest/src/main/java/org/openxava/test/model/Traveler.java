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
public class Traveler extends Identifiable {

    @Column(length = 50) @Required
    String name;

    @DescriptionsList
    @ManyToOne(fetch = FetchType.LAZY)
    Journey lastJourney;

}
