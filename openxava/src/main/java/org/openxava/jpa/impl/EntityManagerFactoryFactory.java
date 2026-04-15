package org.openxava.jpa.impl;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.util.XavaException;
import org.openxava.util.XavaPreferences;
import org.openxava.util.XavaResources;

/**
 * Factory for creating EntityManagerFactory instances.
 * This class delegates to the configured IEntityManagerFactoryProvider implementation.
 * 
 * @author Javier Paniza
 * @since 7.5
 */
public class EntityManagerFactoryFactory {
    
    private static Log log = LogFactory.getLog(EntityManagerFactoryFactory.class);
    private static IEntityManagerFactoryProvider provider;
    
    /**
     * Creates an EntityManagerFactory with the provided properties.
     * 
     * @param properties Properties for creating the EntityManagerFactory
     * @return A Map.Entry containing the original properties and the created EntityManagerFactory
     */
    public static Map.Entry<Map, EntityManagerFactory> createEntityManagerFactory(Map properties) {
        long startTime = System.currentTimeMillis();
        Map.Entry<Map, EntityManagerFactory> result = getProvider().createEntityManagerFactory(properties);
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("EntityManagerFactory created in " + elapsed + " ms"); // tmr
        return result;
    }
    
    /**
     * Gets the configured IEntityManagerFactoryProvider instance.
     * 
     * @return The provider instance
     */
    private static IEntityManagerFactoryProvider getProvider() {
        if (provider == null) {
            try {
                long startTime = System.currentTimeMillis();
                provider = (IEntityManagerFactoryProvider) Class.forName(
                        XavaPreferences.getInstance().getEntityManagerFactoryProviderClass()).newInstance();
                long elapsed = System.currentTimeMillis() - startTime;
                System.out.println("Provider instantiated in " + elapsed + " ms"); // tmr
            } 
            catch (Exception ex) {
                log.warn(XavaResources.getString("provider_creation_error", "EntityManagerFactory"), ex);
                throw new XavaException("provider_creation_error", "EntityManagerFactory");
            }
        }
        return provider;
    }
}
