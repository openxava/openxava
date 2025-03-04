package org.openxava.jpa.impl;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * 
 * @author Javier Paniza
 * @since 7.5
 */
public interface IEntityManagerFactoryProvider {

    Map.Entry<Map, EntityManagerFactory> createEntityManagerFactory(Map properties);

}
