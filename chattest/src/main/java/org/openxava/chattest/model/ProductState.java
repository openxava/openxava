package org.openxava.chattest.model;

import jakarta.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;
import lombok.*;

@Entity @Getter @Setter
public class ProductState extends Identifiable {

    @Column(length=50) @Required
    String name;

}
