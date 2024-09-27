package com.yourcompany.yourapp.util; // in 'util' package

import java.io.*;
import java.math.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
 
public class InvoicingPreferences {
 
    private final static String FILE_PROPERTIES="invoicing.properties";
    private static Log log = LogFactory.getLog(InvoicingPreferences.class);
    private static Properties properties; // We store the properties here
 
    private static Properties getProperties() {
        if (properties == null) { // We use lazy initialization
            PropertiesReader reader = // PropertiesReader is a utility class from OpenXava
                new PropertiesReader( InvoicingPreferences.class, FILE_PROPERTIES);
            try {
                properties = reader.get();
            }
            catch (IOException ex) {
                log.error( XavaResources.getString( // To read a i18n message
                    "properties_file_error", FILE_PROPERTIES), ex);
                properties = new Properties();
            }
        }
        return properties;
    }
 
    public static BigDecimal getDefaultVatPercentage() { // The only public method
        return new BigDecimal(getProperties().getProperty("defaultVatPercentage"));
    }
}