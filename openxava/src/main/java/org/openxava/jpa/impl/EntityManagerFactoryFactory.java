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
        return getProvider().createEntityManagerFactory(properties);
    }
    
    /**
     * Gets the configured IEntityManagerFactoryProvider instance.
     * 
     * @return The provider instance
     */
    private static IEntityManagerFactoryProvider getProvider() {
        if (provider == null) {
            try {
                provider = (IEntityManagerFactoryProvider) Class.forName(
                        XavaPreferences.getInstance().getEntityManagerFactoryProviderClass()).newInstance();
            } 
            catch (Exception ex) {
                log.warn(XavaResources.getString("provider_creation_error", "EntityManagerFactory"), ex);
                throw new XavaException("provider_creation_error", "EntityManagerFactory");
            }
        }
        return provider;
    }
}
