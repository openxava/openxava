package org.openxava.chattest.model;

import java.math.*;
import javax.persistence.*;
import lombok.*;

@Embeddable @Getter @Setter
public class Features {

    @Column(length=20)
    String color;

    BigDecimal weightInKilos;

}
