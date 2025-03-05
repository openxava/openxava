package org.openxava.jpa.impl;

import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

/**
 * Interface for creating the JPA EntityManagerFactory instances used by OpenXava.
 * <p>
 * This is part of a Factory pattern implementation for EntityManagerFactory creation for your application,
 * which allows extending or replacing the default mechanism without modifying OpenXava core code.
 * </p>
 * <p>
 * The pattern consists of:
 * </p>
 * <ol>
 * <li>This interface (IEntityManagerFactoryProvider)</li>
 * <li>A default implementation (EntityManagerFactoryProvider)</li>
 * </ol>
 * <p>
 * To create a custom provider, extend EntityManagerFactoryProvider or implement
 * this interface directly.
 * </p>
 * <p>
 * To use your custom provider, add the following line to your application's xava.properties file:
 * </p>
 * <pre>
 * entityManagerFactoryProviderClass=com.yourcompany.yourapp.jpa.impl.MyEntityManagerFactoryProvider
 * </pre>
 * <p>
 * Example:
 * </p>
 * <pre>
 * // EntityManagerFactoryProvider is the default implementation 
 * // and implements IEntityManagerFactoryProvider
 * public class MyEntityManagerFactoryProvider extends EntityManagerFactoryProvider {
 *
 *     @Override
 *     public Entry<Map, EntityManagerFactory> createEntityManagerFactory(Map properties) {
 *         if (properties.containsKey("whatever")) {
 *             EntityManagerFactory myEntityManagerFactory = createMyEntityManagerFactory(properties);
 *             Map myProperties = refineProperties(properties); // If needed
 *             return new SimpleEntry<>(myProperties, myEntityManagerFactory);
 *         }
 *         // The standard way as fallback
 *         return super.createEntityManagerFactory(properties);
 *     }
 *
 *    ... 
 *
 * }
 * </pre>
 * 
 * @author Javier Paniza
 * @since 7.5
 */
public interface IEntityManagerFactoryProvider {

    Map.Entry<Map, EntityManagerFactory> createEntityManagerFactory(Map properties);

}
