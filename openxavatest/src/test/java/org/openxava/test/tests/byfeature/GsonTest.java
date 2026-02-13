package org.openxava.test.tests.byfeature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.*;
import org.openxava.component.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.google.gson.*;

/**
 * 
 * @author Javier Paniza
 */
public class GsonTest {
	
	static {
		XPersistence.setPersistenceUnit("junit");
		DataSourceConnectionProvider.setUseHibernateConnection(true);
	}

    @Test
    public void testMetaComponentToFromJSON() {
        MetaComponent metaComponent = MetaComponent.get("Product");

        // Configure Gson with exclusion strategy to avoid circular references
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        String fieldName = f.getName();
                        Class<?> fieldType = f.getDeclaredClass();
                        String declaringClassName = f.getDeclaringClass().getSimpleName();
                        
                        // Skip Class fields
                        if (fieldType == Class.class) {
                            return true;
                        }
                        
                        // Skip Log instances
                        if (org.apache.commons.logging.Log.class.isAssignableFrom(fieldType)) {
                            return true;
                        }
                        
                        // Skip Format instances (DecimalFormat, DateFormat, etc.) to avoid duplicate field issues
                        if (java.text.Format.class.isAssignableFrom(fieldType)) {
                            return true;
                        }
                        
                        // Skip IPersistenceProvider
                        if ("persistenceProvider".equals(fieldName)) {
                            return true;
                        }
                        
                        // Skip parent reference fields that create circular references
                        if ("metaComponent".equals(fieldName) || 
                            "metaModel".equals(fieldName) ||
                            "metaView".equals(fieldName) ||
                            "metaTab".equals(fieldName) ||
                            "container".equals(fieldName) ||
                            "parent".equals(fieldName) ||
                            "modelMapping".equals(fieldName) ||
                            "metaViewParent".equals(fieldName)) {
                            return true;
                        }
                        
                        // Skip Maps that contain circular references in ModelMapping
                        if ("ModelMapping".equals(declaringClassName) && 
                            ("propertyMappings".equals(fieldName) || 
                             "referenceMappings".equals(fieldName))) {
                            return true;
                        }
                        
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return java.text.Format.class.isAssignableFrom(clazz);
                    }
                })                
                .create();

        String json = null;
        MetaComponent parsed = null;
        Exception exception = null;

        try {
            // Serialize MetaComponent to JSON
            json = gson.toJson(metaComponent);

            // Deserialize JSON back to MetaComponent
            parsed = gson.fromJson(json, MetaComponent.class);
        } catch (Exception ex) {
            exception = ex;
        }

        // Verify Gson did not throw any exception
        assertNull("Gson threw an exception while processing MetaComponent: " + exception, exception);

        // Verify the deserialized object is valid
        assertNotNull("Deserialized MetaComponent should not be null", parsed);
        assertEquals("Component name should match", "Product", parsed.getName());
    }
}
