package org.openxava.jpa.types;

import org.hibernate.boot.model.*;
import org.hibernate.service.*;

/**
 * Registers in Hibernate the Java types used by OpenXava. <p>
 *
 * It's discovered automatically by Hibernate using the standard
 * {@link java.util.ServiceLoader} mechanism, by means of the
 * META-INF/services/org.hibernate.boot.model.TypeContributor file.
 *
 * @since 8.0
 * @author Javier Paniza
 */
public class OpenXavaTypeContributor implements TypeContributor {

	@Override
	public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		typeContributions.contributeJavaType(new BooleanWithoutCheckConstraintJavaType());
	}

}
