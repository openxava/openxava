package org.openxava.converters.typeadapters;


import org.apache.commons.logging.*;
import org.hibernate.usertype.*;
import org.openxava.converters.*;
import org.openxava.util.*;

/**
 * Adapter for using Hibernate composite types as converters in OpenXava. <p>
 * 
 * It works with <code>org.hibernate.usertype.CompositeUserType</code>.<br>
 * 
 * @author Javier Paniza
 */

public class HibernateCompositeTypeConverter extends HibernateTypeBaseConverter implements IMultipleConverter {
	
	private static Log log = LogFactory.getLog(HibernateCompositeTypeConverter.class);
	private final static String [] fields = {"f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10" }; 
		
	private Object [] data;
	
	public Object toJava() throws ConversionException {
		try {		
			CompositeUserType userType = (CompositeUserType) getHibernateType();
			// In Hibernate 7.2, CompositeUserType no longer has nullSafeGet
			// We need to use instantiate() with ValueAccess, but that requires
			// a different approach. For now, we'll try to use reflection
			// to call the old method if it still exists in the implementation
			try {
				java.lang.reflect.Method method = userType.getClass().getMethod(
					"nullSafeGet", 
					java.sql.ResultSet.class, 
					String[].class, 
					org.hibernate.engine.spi.SharedSessionContractImplementor.class, 
					Object.class
				);
				return method.invoke(userType, new ArrayOneRowResultSetAdapter(data), fields, null, null);
			}
			catch (NoSuchMethodException e) {
				// The old method doesn't exist, this is a true Hibernate 7.2 type
				throw new ConversionException("composite_usertype_hibernate72_not_supported", userType.getClass());
			}
		} 
		catch (Exception ex) {
			log.error(XavaResources.getString("hibernate_type_conversion_error", getType()), ex); 
			throw new ConversionException(ex.getMessage()); 
		}
	}

	public void toDB(Object o) throws ConversionException {		
		// It's not needed to implement because this converter is only used in list mode
	}
	
	
	public void setValuesCount(int count) {
		this.data = new Object[3];
	}
	
	public void setValue0(Object v) {		
		data[0] = v;
	}
	public void setValue1(Object v) {
		data[1] = v;
	}
	public void setValue2(Object v) {
		data[2] = v;
	}	
	public void setValue3(Object v) {
		data[3] = v;
	}
	public void setValue4(Object v) {
		data[4] = v;
	}
	public void setValue5(Object v) {
		data[5] = v;
	}
	public void setValue6(Object v) {
		data[6] = v;
	}
	public void setValue7(Object v) {
		data[7] = v;
	}
	public void setValue8(Object v) {
		data[8] = v;
	}
	public void setValue9(Object v) {
		data[9] = v;
	}
		
}
