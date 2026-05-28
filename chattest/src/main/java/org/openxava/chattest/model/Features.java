package org.openxava.chattest.model;

import java.math.*;
import jakarta.persistence.*;
import lombok.*;

@Embeddable @Getter @Setter
public class Features {

    @Column(length=20)
    String color;

    BigDecimal weightInKilos;

}
