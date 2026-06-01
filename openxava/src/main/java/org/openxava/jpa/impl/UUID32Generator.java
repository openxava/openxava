package org.openxava.jpa.impl;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.util.UUID;

/**
 * Custom identifier generator for 32-character hex UUIDs. <p>
 *
 * So you can have 32 chars columns for table created with Hibernate 5,
 * and still it does not give deprecated warnings.
 *
 * @since 8.0
 * @author Javier Paniza
 */
public class UUID32Generator implements IdentifierGenerator {

	@Override
	public Object generate(SharedSessionContractImplementor session, Object object) {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
