package org.openxava.test.model;

import java.time.*;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter @Setter

public class LedgerPeriod extends Nameable {

    LocalDate startDate;

    LocalDate endDate;

}