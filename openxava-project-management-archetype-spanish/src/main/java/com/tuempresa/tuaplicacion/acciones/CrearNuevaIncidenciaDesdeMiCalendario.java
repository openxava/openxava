package com.tuempresa.tuaplicacion.acciones;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.tuempresa.tuaplicacion.modelo.*;

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
		// TMR ME QUEDÉ POR AQUÍ: TRADUCIENDO A ESPAÑOL
		LocalDate plannedFor = (LocalDate) getView().getValue("plannedFor"); 		
		if (plannedFor == null) {
			plannedFor = LocalDate.now();
			getView().setValue("plannedFor", plannedFor);
		}
		Query query = XPersistence.getManager().createQuery(
			"from Plan p where p.worker.userName = :userName and :plannedFor between p.period.startDate and p.period.endDate");
		query.setParameter("plannedFor", plannedFor);
		query.setParameter("userName", Users.getCurrent());
		List<Plan> plans = query.getResultList(); 
		if (plans.isEmpty()) {
			addError("no_plan_for_user_date", "assignedTo", plannedFor, "'" + Users.getCurrent() + "'"); 
			return;
		}
		Plan plan = plans.get(0);
		getView().setValue("assignedTo.id", plan.getId());
	}
	
	private void calcularValorDefectoEstado() {
		IssueStatus issueStatus = IssueStatus.findTheDefaultOneForMyCalendar();
		if (issueStatus != null) {
			getView().setValue("status.id", issueStatus.getId());
		}
	}
	
	private void calcularValorDefectoTipo() {
		IssueType issueType = IssueType.findTheDefaultOneForMyCalendar();
		if (issueType != null) {
			getView().setValue("type.id", issueType.getId());
		}
	}	
	
}
