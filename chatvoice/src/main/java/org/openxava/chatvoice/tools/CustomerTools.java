package org.openxava.chatvoice.tools;

import java.util.*;
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
			Long count = em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class)
					.getSingleResult();
			return count != null ? count : 0L;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			em.close();
			emf.close();
		}
	}
	
	/**
	 * Returns all customers from the database
	 * 
	 * @return A list of all customers with their details
	 */
	@Tool("Get all customers from the database")
	public List<Map<String, Object>> getAllCustomers() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		try {
			List<Object[]> results = em.createQuery(
					"SELECT c.number, c.name, c.address, c.city, c.country FROM Customer c", 
					Object[].class)
					.getResultList();
			
			List<Map<String, Object>> customers = new ArrayList<>();
			for (Object[] row : results) {
				Map<String, Object> customer = new HashMap<>();
				customer.put("number", row[0]);
				customer.put("name", row[1]);
				customer.put("address", row[2]);
				customer.put("city", row[3]);
				customer.put("country", row[4]);
				customers.add(customer);
			}
			return customers;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			em.close();
			emf.close();
		}
	}
	
	/**
	 * Modifies a specific field of a customer
	 * 
	 * @param customerNumber The customer number (ID)
	 * @param fieldName The name of the field to modify (name, address, city, country)
	 * @param newValue The new value for the field
	 * @return A message indicating success or failure
	 */
	@Tool("Modify a specific field of a customer by customer number")
	public String modifyCustomerField(int customerNumber, String fieldName, String newValue) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			
			Query query = em.createQuery(
					"UPDATE Customer c SET c." + fieldName + " = :newValue WHERE c.number = :number");
			query.setParameter("newValue", newValue);
			query.setParameter("number", customerNumber);
			
			int updatedCount = query.executeUpdate();
			
			em.getTransaction().commit();
			
			if (updatedCount > 0) {
				return "Successfully updated " + fieldName + " to '" + newValue + "' for customer " + customerNumber;
			} else {
				return "Customer with number " + customerNumber + " not found";
			}
		} catch (Exception ex) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			ex.printStackTrace();
			throw ex;
		} finally {
			em.close();
			emf.close();
		}
	}
	
}
