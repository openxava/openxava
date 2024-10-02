package com.yourcompany.yourapp.model;
 
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import lombok.*;
 
@MappedSuperclass // Marked as mapped superclass instead of entity
@Getter @Setter
public class Identifiable {
 
    @Id @GeneratedValue(generator="system-uuid") @Hidden
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length=32)
    String oid; // Property definition includes OpenXava and JPA annotations
 
}