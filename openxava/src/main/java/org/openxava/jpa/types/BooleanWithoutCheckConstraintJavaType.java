package org.openxava.jpa.types;

import org.hibernate.dialect.*;
import org.hibernate.type.descriptor.converter.spi.*;
import org.hibernate.type.descriptor.java.*;
import org.hibernate.type.descriptor.jdbc.*;

/**
 * Boolean Java type that does not generate a <code>check</code> constraint in the DDL.
 * <p>
 * Since Hibernate 6.2 a <code>check</code> constraint is generated for each boolean
 * property with an {@link jakarta.persistence.AttributeConverter}, as it is the case of
 * all the boolean properties of OpenXava stored as 'Y'/'N'. That constraint is written
 * inline in the column definition, and although it works on <code>create table</code>,
 * some databases, HSQLDB among them, do not support it on
 * <code>alter table ... add column</code>, thus adding a new boolean property to an
 * existing entity fails on start up. Given that the conversion is done by OpenXava
 * itself, the constraint adds no value.
 *
 * @since 8.0
 * @author Javier Paniza
 */
public class BooleanWithoutCheckConstraintJavaType extends BooleanJavaType {

	@Override
	public String getCheckCondition(String columnName, JdbcType jdbcType, BasicValueConverter<Boolean, ?> converter, Dialect dialect) {
		return null;
	}

}
