package org.openxava.util.impl;

import java.util.*;
import javax.persistence.*;


/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(EmailSubscription.class)
@Table(name="OXEMAILSUBSCRIPTIONS", indexes = {@Index(columnList = "module")})
public class EmailSubscription implements java.io.Serializable {
	
	public static EmailSubscription find(EntityManager manager, String email, String module) {
		return manager.find(EmailSubscription.class, new EmailSubscription(email, module));
	}
	
	public static Collection<EmailSubscription> findByModule(EntityManager manager, String module) {
		Query query = manager.createQuery("from EmailSubscription s where s.module = :module");
		query.setParameter("module", module);
		return query.getResultList();
	}
	
	public static boolean remove(EntityManager manager, String email, String module) {
		EmailSubscription subscription =  EmailSubscription.find(manager, email, module);
		if (subscription == null) return false;
		manager.remove(subscription);
		return true;
	}
	
	public static int removeByEmailAndModuleLike(EntityManager manager, String email, String moduleLike) {
		Query query = manager.createQuery("delete from EmailSubscription s where s.email = :email and s.module like :moduleLike");
		query.setParameter("email", email);
		query.setParameter("moduleLike", moduleLike);
		return query.executeUpdate();
	}
		
	public EmailSubscription() {
	}
	
	public EmailSubscription(String email, String module) {
		this.email = email;
		this.module = module;
	}
	
	@Id @Column(length=50)
	private String email;
	
	@Id @Column(length=80)
	private String module;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailSubscription other = (EmailSubscription) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		return true;
	}
	
}
