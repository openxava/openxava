package org.openxava.types;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.ParameterizedType;
import org.openxava.util.*;

/**
 * In java an Enum and in database a String corresponding
 * to the position in the string that it's in property 'strings'. <p>
 * 
 * Example of use:
 * <pre>
 * @Type(type=&quot;types.EnumStringType&quot;,
 *   parameters={
 *     @Parameter(name=&quot;strings&quot;, value=&quot;Short,Large&quot;), // These are the values stored on the database
 *       @Parameter(name=&quot;enumType&quot;, value=&quot;modelo.Accion$Plazo&quot;)
 *   }
 * )
 * @Column(name = &quot;plazo&quot;, length = 50, nullable = true) // plazo is a varchar database field
 * private Plazo plazo;
 * public enum Plazo {CORTO,LARGO} // These are the i18n labels shown on the UI
 * </pre>
 *
 * @author David Buedo
 */

public class EnumStringType implements UserType, ParameterizedType {

    private static Log log = LogFactory.getLog(EnumStringType.class);

    private String strings;
    private String enumType;
    private String[] splitStrings;

    public int[] sqlTypes() {       
        return new int[] { Types.VARCHAR };
    }

    public Class returnedClass() {
        return Enum.class;      
    }

    public boolean equals(Object obj1, Object obj2) throws HibernateException {
        if (obj1 == obj2) return true;
        if (obj1 == null) return false;
        return obj1.equals(obj2);
    }

    public int hashCode(Object obj) throws HibernateException {
        return obj.hashCode();
    }

	public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor sessionImplementor, Object owner) throws HibernateException, SQLException { 
        Object o = resultSet.getObject(names[0]);
        if (o == null) return null;
        if (!(o instanceof String)) { 
            throw new HibernateException(XavaResources.getString("conversion_java_string_expected"));
        }
        assertParameters();
        String value  = (String) o;
        if (Is.emptyString(value)) return null;
        splitStrings = strings.split(",");
        int idx = -1;
        int i=0;
        for(String s: splitStrings) {
            if(s.equalsIgnoreCase(value)) { idx = i; break; }
            i++;
        }
        if (idx < 0) {
            throw new HibernateException(XavaResources.getString("conversion_java_valid_values", value,  strings));
        }
        try {
        	Object values = Class.forName(enumType).getMethod("values", (Class<?> []) null).invoke(null, (Object []) null); 
            return ((Object []) values)[idx];
        } 
        catch (Exception ex) {
            String message = XavaResources.getString("hibernate_type_enum_error", enumType, getClass()); 
            log.error(message, ex);
            throw new HibernateException(message);
        }       
    }

	public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException { 		
        if (value == null) {
            if (log.isTraceEnabled()) {
                log.trace( "binding '' to parameter: " + index );
            }           
            ps.setString(index, "");
            return;
        }
        if (!(value instanceof Enum)) {     
            throw new HibernateException(XavaResources.getString("conversion_db_enum_expected"));
        }
        assertParameters();
        int ivalue = ((Enum) value).ordinal();
        try {
            splitStrings = strings.split(",");
            String string = splitStrings[ivalue];
            if (log.isTraceEnabled()) {
                log.trace( "binding '" + string + "' to parameter: " + index );
            }           
            ps.setString(index, string);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new HibernateException(XavaResources.getString("conversion_db_valid_values", value, strings)); 
        }
    }

    public void setParameterValues(Properties parameters) {
        if (parameters == null) return;
        strings = parameters.getProperty("strings");
        enumType = parameters.getProperty("enumType");
    }

    private void assertParameters() throws HibernateException {
        if (Is.emptyString(strings)) {
            throw new HibernateException(XavaResources.getString("conversion_valid_values_strings_required", getClass().getName())); 
        }
        if (Is.emptyString(enumType)) {
            throw new HibernateException(XavaResources.getString("hibernate_type_parameter_required", "enumType", getClass().getName())); 
        }       
    }


    public Object deepCopy(Object obj) throws HibernateException {
        return obj;     
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object obj) throws HibernateException {
        return (Serializable) obj;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    /**
     * Full qualified class for the Enum. &lt;p&gt;
     * 
     * For example: &quot;model.EntityName$EnumName&quot;
     */
    public String getEnumType() {
        return enumType;
    }
    /**
     * Full qualified class for the Enum. &lt;p&gt;
     * 
     * For example: &quot;model.EntityName$EnumName&quot;
     */
    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }

    /**
     * String that corresponds with the valid values for this enum. &lt;p&gt;
     * 
     * For example, &quot;ALTA,MEDIA,BAJA&quot;, means:
     * &lt;ul&gt;
     * &lt;li&gt; ordinal 0 in Java Enum for 'ALTA' in DB
     * &lt;li&gt; ordinal 1 in Java Enum for 'MEDIA' in DB
     * &lt;li&gt; ordinal 2 in Java Enum for 'BAJA' in DB
     * &lt;li&gt;
     * &lt;/ul&gt;
     */
    public String getStrings() {
        return strings;
    }
    /**
     * String that corresponds with the valid values for this enum. &lt;p&gt;
     * 
     * For example, &quot;ALTA,MEDIA,BAJA&quot;, means:
     * &lt;ul&gt;
     * &lt;li&gt; ordinal 0 in Java Enum for 'ALTA' in DB
     * &lt;li&gt; ordinal 1 in Java Enum for 'MEDIA' in DB
     * &lt;li&gt; ordinal 2 in Java Enum for 'BAJA' in DB
     * &lt;li&gt;
     * &lt;/ul&gt;
     */
    public void setStrings(String strings) {
        this.strings = strings;
    }

}

