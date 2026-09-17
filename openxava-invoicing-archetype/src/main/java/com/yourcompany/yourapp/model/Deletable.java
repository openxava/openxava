package com.yourcompany.yourapp.model;
 
import jakarta.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
 
@MappedSuperclass @Getter @Setter
public class Deletable extends Identifiable {
 
    @Hidden
    @Column(columnDefinition="BOOLEAN DEFAULT FALSE")
    boolean deleted;
 
}