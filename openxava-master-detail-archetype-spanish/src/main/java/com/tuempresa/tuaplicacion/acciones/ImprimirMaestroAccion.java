package com.tuempresa.tuaplicacion.acciones;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.tuempresa.tuaplicacion.modelo.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

public class ImprimirMaestroAccion extends JasperReportBaseAction {

	private Maestro maestro;
	
	@Override
	protected JRDataSource getDataSource() throws Exception {
		return new JRBeanCollectionDataSource(getMaestro().getDetalles());
	}

	@Override
	protected String getJRXML() throws Exception {
		return "Maestro.jrxml";
	}

	@Override
	protected Map<String, Object> getParameters() throws Exception {
		// available-on-new="false" evita necesidad de validar
		Map<String, Object> p = new HashMap<>();
		p.put(JRParameter.REPORT_LOCALE, Locales.getCurrent());
		p.put("anyo", getMaestro().getAnyo());
		p.put("numero", getMaestro().getNumero());
		p.put("fecha", getMaestro().getFecha());
		p.put("personaNumero", getMaestro().getPersona().getNumero());
		p.put("personaNombre", getMaestro().getPersona().getNombre());
		p.put("personaDireccion", getMaestro().getPersona().getDireccion());
		p.put("personaCiudad", getMaestro().getPersona().getCiudad());
		p.put("personaPais", getMaestro().getPersona().getPais());
		p.put("porcentajeIVA", getMaestro().getPorcentajeIVA());
		p.put("iva", getMaestro().getIva());
		p.put("total", getMaestro().getTotal());
		p.put("observaciones", getMaestro().getObservaciones());
		return p;
	}

	private Maestro getMaestro() throws Exception {
		if (maestro == null) {
			String id = getView().getValueString("id");
			maestro = XPersistence.getManager().find(Maestro.class, id);
		}
		return maestro;
	}
}
