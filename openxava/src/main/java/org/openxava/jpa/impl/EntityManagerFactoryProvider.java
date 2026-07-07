package org.openxava.jpa.impl;

import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.openxava.util.XavaResources;
import org.openxava.jpa.impl.PersistenceXml;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of IEntityManagerFactoryProvider.
 * 
 * @author Javier Paniza
 * @since 7.5
 */
public class EntityManagerFactoryProvider implements IEntityManagerFactoryProvider {
    
    private static Log log = LogFactory.getLog(EntityManagerFactoryProvider.class);
    
    /**
     * Creates an EntityManagerFactory with the provided properties.
     * 
     * @param properties Properties for creating the EntityManagerFactory, including the persistence unit name
     * @return A Map.Entry containing the original properties and the created EntityManagerFactory
     */
    public Map.Entry<Map, EntityManagerFactory> createEntityManagerFactory(Map properties) {
        String persistenceUnit = (String) properties.get("xava.persistenceUnit");
        Map factoryProperties = properties;
        EntityManagerFactory entityManagerFactory = null;
        
        try {
            if (PersistenceXml.getPropetyValue(persistenceUnit, "hibernate.implicit_naming_strategy") == null) { 
                factoryProperties = new HashMap(properties);
                factoryProperties.put("hibernate.implicit_naming_strategy", "legacy-jpa"); 
            }
            silenceLogger("org.hibernate.boot.registry.classloading.internal"); // To avoid a warning exception with Envers in development environment
            silenceLogger("org.hibernate.orm.boot"); // To avoid HHH160246 warning about @OrderColumn on mappedBy associations
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit, factoryProperties);
        }
        catch (NoSuchFieldError ex) {
            log.error(XavaResources.getString("incorrect_openxava_upgrade")); 
            throw ex;
        }
        catch (ParserConfigurationException ex) {
            log.error(XavaResources.getString("incorrect_openxava_upgrade"));
            throw new RuntimeException(ex);
        }    
        catch (HibernateException ex) {
            // In case there is no connection to database and dialect is not set we get
            // a too generic error, so we set a hibernate.dialect (whatever) and try again
            // to get a meaningful message.
            factoryProperties = new HashMap(factoryProperties);
            factoryProperties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect"); // HSQLDialect but it could be whatever else
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit, factoryProperties);
        }
        
        return new SimpleEntry<>(properties, entityManagerFactory);
    }

	private static void silenceLogger(String loggerName) {
		try {
			((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName))
				.setLevel(ch.qos.logback.classic.Level.ERROR);
		}
		catch (NoClassDefFoundError | ClassCastException ex) {
			java.util.logging.Logger.getLogger(loggerName).setLevel(java.util.logging.Level.SEVERE);
		}
	}
}
