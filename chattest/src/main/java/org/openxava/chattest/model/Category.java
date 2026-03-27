package org.openxava.chattest.model;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;
import lombok.*;

@Entity @Getter @Setter
public class Category extends Identifiable {

    @Column(length=50) @Required
    String name;

}
