package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * To test @UniqueConstraint in @Table. These unique 
 * constraints that are to be placed on the table.
 * The name element of UniqueConstrainst is only indicative, 
 * because Hibernate does not create the constraint in the 
 * table, with the indicated name.We have mapped the 
 * constraint name manually in Table.
 * 
 * @author Jeromy Altuna
 */

@Entity
@View(name="OnlyNickname", members="nickname")
@Table(
	uniqueConstraints={
			@UniqueConstraint(name="not_repeat_nickname", 
				columnNames={"nickname"})
})
public class Nickname extends Identifiable {
	
	@Required
	private String nickname;
	
	@ManyToOne
	private ApplicationUser user;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public ApplicationUser getUser() {
		return user;
	}

	public void setUser(ApplicationUser user) {
		this.user = user;
	}	
}
