package com.yourcompany.yourapp.model;
 
import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
 
@Entity  // This marks Customer class as an entity
@Getter @Setter // This makes all fields below publicly accessible
@View(name="Simple", // This view is used only when “Simple” is specified
members="number, name" // Shows only number and name in the same line
)
public class Customer {
 
    @Id  // The number property is the key property. Keys are required by default
    @Column(length=6)  // The column length is used at the UI level and the DB level
    int number;
 
    @Column(length=50)  // The column length is used at the UI level and the DB level
    @Required  // A validation error will be shown if the name property is left empty
    String name;
 
    @Embedded // This is the way to reference an embeddable class
    Address address; // A regular Java reference
}