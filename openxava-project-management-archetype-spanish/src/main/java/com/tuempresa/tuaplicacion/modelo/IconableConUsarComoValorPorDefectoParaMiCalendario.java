package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@MappedSuperclass @Getter @Setter
public class IconableConUsarComoValorPorDefectoParaMiCalendario extends Iconable {
	
	boolean usarComoValorPorDefectoParaMiCalendario;
	
	protected static Object findLaDePorDefecto(String entidad, String propiedad) {
		List resultado = XPersistence.getManager()
			.createQuery("from " + entidad + " where " + propiedad + " = true")
			.getResultList();
		if (resultado.size() == 1) return resultado.get(0);
		return null;
	}
	
	protected void unsetUsarComoValorPorDefectoParaTodos(String propiedad) {
		XPersistence.getManager()
			.createQuery("update " + getClass().getSimpleName() + " set " + propiedad + " = false")
			.executeUpdate();
	}
	
	public void setUsarComoValorPorDefectoParaMiCalendario(boolean usarComoValorPorDefectoParaMiCalendario) {
		if (this.usarComoValorPorDefectoParaMiCalendario == usarComoValorPorDefectoParaMiCalendario) return;
		unsetUsarComoValorPorDefectoParaTodos("usarComoValorPorDefectoParaMiCalendario");
		this.usarComoValorPorDefectoParaMiCalendario = usarComoValorPorDefectoParaMiCalendario;
	}
	
}
