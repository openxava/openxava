package com.tuempresa.tuaplicacion.tareas;

import java.time.format.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.quartz.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class TareaRecordatorioIncidenciaPlanificada implements Job {
	
	private static final Log log = LogFactory.getLog(TareaRecordatorioIncidenciaPlanificada.class);
    
    public void execute(JobExecutionContext context) {
    	String idIncidencia = "Desconocido";
    	String correoElectronicoTrabajador = "Desconocido";
    	try {
    		idIncidencia = context.getJobDetail().getJobDataMap().getString("incidencia.id");
			String esquema = context.getJobDetail().getJobDataMap().getString("esquema");
			if (esquema != null) {
				XPersistence.setDefaultSchema(esquema);
			}
    		Incidencia incidencia = Incidencia.findById(idIncidencia);
    		
    		if (incidencia == null) {
    			log.error(XavaResources.getString("error_incidencia_no_encontrada_recordatorio", idIncidencia));
    			return;
    		}
    		Plan plan = incidencia.getAsignadoA();
    		if (plan == null) {
    			log.error(XavaResources.getString("error_campo_faltante_recordatorio", idIncidencia, "plan"));
    			return;
    		}
    		Trabajador trabajador = plan.getTrabajador();
    		if (trabajador == null) {
    			log.error(XavaResources.getString("error_campo_faltante_recordatorio", idIncidencia, "worker"));
    			return;
    		}
    		correoElectronicoTrabajador = trabajador.getCorreoElectronico();
    		if (Is.emptyString(correoElectronicoTrabajador)) {
    			log.error(XavaResources.getString("error_campo_faltante_recordatorio", idIncidencia, "email"));
    			return;
    		}
    		if (incidencia.getPlanificadoPara() == null) {
    			log.error(XavaResources.getString("error_campo_faltante_recordatorio", idIncidencia, "plannedFor"));
    			return;
    		}
    		
    		// TMR ME QUEDÉ POR AQUÍ: FALTA PONER EN ESPAÑOL LAS VARIABLES DE ABAJO
    		// TMR YA ESTÁ TODO, SOLO FALTA PROBARLO. EL DE INGLÉS YA ESTÁ PROBADO.
    		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    		String formattedDate = incidencia.getPlanificadoPara().format(formatter);
    		
    		String subject = XavaResources.getString("asunto_recordatorio_incidencia_planificada", incidencia.getTitulo(), formattedDate);
    		String content = XavaResources.getString("contenido_recordatorio_incidencia_planificada", incidencia.getTitulo(), incidencia.getDescripcion(), formattedDate);
    		
    		Emails.send(correoElectronicoTrabajador, subject, content);
    	}
    	catch (Exception ex) {
    		log.error(XavaResources.getString("error_recordatorio_incidencia_planificada", idIncidencia, correoElectronicoTrabajador), ex);
    	}
    	finally {
    		XPersistence.commit();
    	}    	
    }
    
}
