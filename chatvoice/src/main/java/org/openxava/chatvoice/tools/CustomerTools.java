package org.openxava.chatvoice.tools;

import javax.persistence.*;

import dev.langchain4j.agent.tool.Tool;

/**
 * Tools for customer-related operations using LangChain4j
 */
public class CustomerTools {
	
	/**
	 * Returns the total number of customers in the database
	 * 
	 * @return The count of customers
	 */
	@Tool("Get the total number of customers in the database")
	public long getCustomerCount() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		try {
			Long count = em.createQuery("SELECT COUNT(p) FROM Person p", Long.class)
					.getSingleResult();
			return count != null ? count : 0L;
		} finally {
			em.close();
			emf.close();
		}
	}
	
}
