package org.openxava.jpa.converters;

import jakarta.persistence.*;

/**
 * JPA converter for Boolean to String (Y/N).
 * <p>
 * This converter is better than {@link org.hibernate.type.YesNoConverter} because
 * it trims the database value before conversion, making it tolerant to padded strings
 * from databases that use PAD SPACE collation (like HSQLDB). Hibernate 7.2's
 * YesNoConverter is strict and throws {@code CoercionException} when reading
 * VARCHAR(1) values with trailing spaces.
 * </p>
 *
 * @since 8.0
 * @author Javier Paniza
 */
@Converter
public class YesNoConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		if (attribute == null) return "N";
		return attribute ? "Y" : "N";
	}

	@Override
	public Boolean convertToEntityAttribute(String dbData) {
		if (dbData == null) return false;
		return "Y".equalsIgnoreCase(dbData.trim());
	}

}
