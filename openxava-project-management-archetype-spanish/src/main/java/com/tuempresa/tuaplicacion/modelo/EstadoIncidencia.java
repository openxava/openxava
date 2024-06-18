package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class EstadoIncidencia extends Iconable {
			
	boolean usarComoValorPorDefecto;
	
	public static EstadoIncidencia findLaDePorDefecto() {
		List<EstadoIncidencia> estado = XPersistence.getManager()
			.createQuery("from EstadoIncidencia where usarComoValorPorDefecto = true")
			.getResultList();
		if (estado.size() == 1) return estado.get(0);
		return null;
	}
		
	private void unsetUsarComoValorPorDefectoParaTodos() {
		XPersistence.getManager().createQuery("update EstadoIncidencia set usarComoValorPorDefecto = false").executeUpdate();
	}

	public void setUsarComoValorPorDefecto(boolean usarComoValorPorDefecto) {
		if (this.usarComoValorPorDefecto == usarComoValorPorDefecto) return;
		unsetUsarComoValorPorDefectoParaTodos();
		this.usarComoValorPorDefecto = usarComoValorPorDefecto;
	}

}
