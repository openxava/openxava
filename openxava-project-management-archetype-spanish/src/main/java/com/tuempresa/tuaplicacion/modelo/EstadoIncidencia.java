package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class EstadoIncidencia extends IconableConUsarComoValorPorDefectoParaMiCalendario {
			
	boolean usarComoValorPorDefecto;
			
	public static EstadoIncidencia findById(String id) { 
		return (EstadoIncidencia) XPersistence.getManager().find(EstadoIncidencia.class, id);
	}
	
	public static EstadoIncidencia findLaDePorDefecto() {
		return (EstadoIncidencia) findLaDePorDefecto("EstadoIncidencia", "usarComoValorPorDefecto");
	}
		
	public static EstadoIncidencia findLaDePorDefectoParaMiCalendario() {
		return (EstadoIncidencia) findLaDePorDefecto("EstadoIncidencia", "usarComoValorPorDefectoParaMiCalendario");
	}	

	public void setUsarComoValorPorDefecto(boolean usarComoValorPorDefecto) {
		if (this.usarComoValorPorDefecto == usarComoValorPorDefecto) return;
		unsetUsarComoValorPorDefectoParaTodos("usarComoValorPorDefecto");
		this.usarComoValorPorDefecto = usarComoValorPorDefecto;
	}		

}
