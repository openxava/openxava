package com.tuempresa.tuaplicacion.cuadrosmando;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

@View(members=
	"numeroMaestros, numeroPersonas, sumaTotal; " +
	"evolucion;" +
	"personasDestacadas, mejoresAnyos"
)
public class CuadroMando {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getSumaTotal() { 
		return (BigDecimal) XPersistence.getManager().createQuery("select sum(m.total) from Maestro m").getSingleResult();
	}
	
	@LargeDisplay(icon="animation")
	public long getNumeroMaestros() { 		
		return contar("Maestro"); 
	}
		
	@LargeDisplay(icon="account-group")
	public long getNumeroPersonas() { 
		return contar("Persona");
	}
	
	@Chart
	public Collection<PorAnyo> getEvolucion() { 
		String jpql = "select new com.tuempresa.tuaplicacion.cuadrosmando.PorAnyo(m.anyo, sum(m.total), sum(m.iva)) " +
			"from Maestro m " +
			"group by m.anyo " +
			"order by m.anyo asc";
		TypedQuery<PorAnyo> query = XPersistence.getManager().createQuery(jpql, PorAnyo.class);
		return query.getResultList();
	}
	
	@SimpleList 
	public Collection<PorPersona> getPersonasDestacadas() { 
		String jpql = "select new com.tuempresa.tuaplicacion.cuadrosmando.PorPersona(m.persona.nombre, sum(m.total) as importe) " +
			"from Maestro m " +
			"group by m.persona.numero, importe " +
			"order by importe desc";
		TypedQuery<PorPersona> query = XPersistence.getManager().createQuery(jpql, PorPersona.class).setMaxResults(5);
		return query.getResultList();
	}
	
	@SimpleList @ListProperties("anyo, total")
	public Collection<PorAnyo> getMejoresAnyos() { 
		String jpql = "select new com.tuempresa.tuaplicacion.cuadrosmando.PorAnyo(m.anyo, sum(m.total) as importe, sum(m.iva)) " +
			"from Maestro m " +
			"group by m.anyo " +
			"order by importe desc";
		TypedQuery<PorAnyo> query = XPersistence.getManager().createQuery(jpql, PorAnyo.class).setMaxResults(5);
		return query.getResultList();
	}	
	
	private Long contar(String entity) {
		return (Long) XPersistence.getManager().createQuery("select count(*) from " + entity).getSingleResult();
	}
	
}
