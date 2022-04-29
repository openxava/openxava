package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * To test @UniqueConstraint in @SecondaryTable. These
 * unique constraints that are to be placed on the table.
 * The name element of UniqueConstrainst is only indicative, 
 * because Hibernate does not create the constraint in the 
 * table, with the indicated name.We have mapped the 
 * constraint name manually in Table.
 * 
 * It also allows testing UniqueConstraint on a single column.   
 * 
 * @author Jeromy Altuna
 */
@Entity
@SecondaryTable(
	name="APPLICATIONUSER_INFO",
	uniqueConstraints={
		@UniqueConstraint(name="not_repeat_user_info",
			columnNames={"name", "birthdate", "sex"})
	}
)
public class ApplicationUser extends Identifiable {
	
	//For testing constraint on a single column.
	@Required
	@Column(length=8, unique=true) 
	private String nic; 
	
	@Column(length=40, table="APPLICATIONUSER_INFO")
	private String name;
	
	@Column(length=40, table="APPLICATIONUSER_INFO")
	private Date birthdate;
	
	@Column(table="APPLICATIONUSER_INFO")
	@Enumerated(EnumType.STRING)
	private Sex sex;
	public enum Sex { MALE, FEMALE }
	
	@OneToMany(mappedBy="user", cascade=CascadeType.REMOVE)
	private Collection<Nickname> nicknames = new ArrayList<Nickname>();

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Collection<Nickname> getNicknames() {
		return nicknames;
	}

	public void setNicknames(Collection<Nickname> nicknames) {
		this.nicknames = nicknames;
	}
	
}