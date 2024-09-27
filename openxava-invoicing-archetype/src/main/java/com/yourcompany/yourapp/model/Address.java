package com.yourcompany.yourapp.model;
 
import javax.persistence.*;

import lombok.*;
 
@Embeddable // We use @Embeddable instead of @Entity
@Getter @Setter
public class Address {
 
    @Column(length = 30) // The members are annotated as in entity case
    String street;
 
    @Column(length = 5)
    int zipCode;
 
    @Column(length = 20)
    String city;
 
    @Column(length = 30)
    String state;
 
}