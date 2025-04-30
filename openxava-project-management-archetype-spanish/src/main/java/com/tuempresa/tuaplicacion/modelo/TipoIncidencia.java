package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class TipoIncidencia extends IconableConUsarComoValorPorDefectoParaMiCalendario {
	
	public static TipoIncidencia findById(String id) { 
		return (TipoIncidencia) XPersistence.getManager().find(TipoIncidencia.class, id);
	}
	
	public static TipoIncidencia findLaDePorDefectoParaMiCalendario() {
		return (TipoIncidencia) findLaDePorDefecto("TipoIncidencia", "usarComoValorPorDefectoParaMiCalendario");
	}

}
