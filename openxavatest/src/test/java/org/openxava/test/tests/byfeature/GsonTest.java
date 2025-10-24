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
        
        // TMR ME QUEDÉ POR AQUÍ: FALLA CON UN STACKOVERFLOW, POR LAS REFERENCIAS CIRCULARES EN LA METADATA

        // Configure Gson
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setPrettyPrinting()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaredClass() == Class.class;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })                
                .create();

        String json = null;
        MetaComponent parsed = null;
        Exception exception = null;

        try {
            // Serialize MetaComponent to JSON
            json = gson.toJson(metaComponent);
            System.out.println("Generated JSON:\n" + json);

            // Deserialize JSON back to MetaComponent
            parsed = gson.fromJson(json, MetaComponent.class);
        } catch (Exception ex) {
            exception = ex;
            ex.printStackTrace();
        }

        // Verify Gson did not throw any exception
        assertNull("Gson threw an exception while processing MetaComponent: " + exception, exception);

        // Verify the deserialized object is valid
        assertNotNull("Deserialized MetaComponent should not be null", parsed);
        assertEquals("Component name should match", "Product", parsed.getName());
    }
}
