package org.openxava.test.model;

import java.time.*;

import javax.persistence.*;

import org.openxava.model.*;

import lombok.*;

@Entity
@Getter
@Setter
public class UserWithBirthday extends Identifiable{

	String userName;
	
	LocalDate birthday;
	
}
