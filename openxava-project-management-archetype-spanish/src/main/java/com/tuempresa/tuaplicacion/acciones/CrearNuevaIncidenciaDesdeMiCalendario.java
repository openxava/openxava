package com.tuempresa.tuaplicacion.acciones;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.tuempresa.tuaplicacion.modelo.*;

// TMR ME QUEDÉ POR AQUÍ: YA ESTÁ TERMINADO, SEGUIR CON LO SIGUIENTE
public class CrearNuevaIncidenciaDesdeMiCalendario extends NewAction {

	private boolean irALista = false;
	
	public void execute() throws Exception {
		if ("true".equals(getRequest().getParameter("firstRequest"))) {
			irALista = true;
			return;
		}
		super.execute();
		calcularValorDefectoPlan();	
		calcularValorDefectoEstado();
		calcularValorDefectoTipo();
	}

	public String getNextMode() {
		return irALista?IChangeModeAction.LIST:IChangeModeAction.DETAIL;
	}

	private void calcularValorDefectoPlan() {
		LocalDate planificadoPara = (LocalDate) getView().getValue("planificadoPara"); 		
		if (planificadoPara == null) {
			planificadoPara = LocalDate.now();
			getView().setValue("planificadoPara", planificadoPara);
		}
		Query query = XPersistence.getManager().createQuery(
			"from Plan p where p.trabajador.nombreUsuario = :nombreUsuario and :planificadoPara between p.periodo.fechaInicio and p.periodo.fechaFin");
		query.setParameter("planificadoPara", planificadoPara);
		query.setParameter("nombreUsuario", Users.getCurrent());
		List<Plan> planes = query.getResultList(); 
		if (planes.isEmpty()) {
			addError("no_plan_para_ususario_fecha", "asignadoA", planificadoPara, "'" + Users.getCurrent() + "'"); 
			return;
		}
		Plan plan = planes.get(0);
		getView().setValue("assignedTo.id", plan.getId());
	}
	
	private void calcularValorDefectoEstado() {
		EstadoIncidencia estadoIncidencia = EstadoIncidencia.findLaDePorDefectoParaMiCalendario();
		if (estadoIncidencia != null) {
			getView().setValue("estado.id", estadoIncidencia.getId());
		}
	}
	
	private void calcularValorDefectoTipo() {
		TipoIncidencia tipoIncidencia = TipoIncidencia.findLaDePorDefectoParaMiCalendario();
		if (tipoIncidencia != null) {
			getView().setValue("tipo.id", tipoIncidencia.getId());
		}
	}	
	
}
