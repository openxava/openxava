package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Jeromy Altuna
 */
@Entity
@View(members=
    "user ["            +
    "   name; nickname" +
    "], " +
    "attachments; "     +
    "listOfNicknamesThatAreUsedByOthersUsers"
) 
public class UserWithNickname extends Nameable {
	
	@OneToOne @ReferenceView("OnlyNickname")
	@AsEmbedded @NoFrame
	private Nickname nickname;	
	
	@Embedded
	private Attachments attachments;
	
	public Collection<Nickname> getListOfNicknamesThatAreUsedByOthersUsers() {
		TypedQuery<Nickname> query = XPersistence.getManager().createQuery(
									 "from Nickname", Nickname.class);
		return query.getResultList();
	}
	
	public Nickname getNickname() {
		return nickname;
	}

	public void setNickname(Nickname nickname) {
		this.nickname = nickname;
	}

	public Attachments getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachments attachments) {
		this.attachments = attachments;
	}	
}
