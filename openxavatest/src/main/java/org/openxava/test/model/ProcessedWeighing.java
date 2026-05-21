package org.openxava.test.model;

import java.time.*;
import java.util.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.Identifiable;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 * @since 7.7
 */

@Entity @Getter @Setter
public class ProcessedWeighing extends Identifiable {
    
    @Required
    LocalDate date;
 
    @Column(length=50)
    String description;
    
    @ElementCollection
    @ListProperties("weighing.id, weighing.grossWeight, weighing.description")
    Collection<ProcessedWeighingDetail> details;
}
