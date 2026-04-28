package com.tuempresa.tuaplicacion.pruebas;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class PruebaPlan extends ModuleTestBase {

	public PruebaPlan(String nameTest) {
		super(nameTest, "tuaplicacion", "Plan");
	}
	
	public void testCrearNuevoPlan() throws Exception {
		login("admin", "admin");
		assertListRowCount(2);
		execute("CRUD.new");
		
		String [][] trabajadores = {
			{ "", "" },
			{ "2c976081901309200190130a11270000", "Javi" },
			{ "2c976081901309200190130a20280001", "Pedro" }
		};
		assertValidValues("trabajador.id", trabajadores);
		setValue("trabajador.id", "2c976081901309200190130a20280001"); // Pedro 
		
		String [][] periodos = {
			{ "", "" },
			{ "2c976081901305ad019013077d3c0000", "2024.10" },
			{ "2c976081901305ad019013078c3c0001", "2024.11" }
		};
		assertValidValues("periodo.id", periodos);
		setValue("periodo.id", "2c976081901305ad019013078c3c0001"); // 2024.11 
		
		execute("Collection.new", "viewObject=xava_view_incidencias");
		setValue("titulo", "El primer paso de mi gran plan");
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo 
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("incidencias", 1);
		
		execute("Mode.list");
		assertListRowCount(3);
		assertValueInList(1, 0, "Pedro");
		assertValueInList(1, 1, "2024.11");

		execute("List.viewDetail", "row=0");
		execute("Navigation.next");
		assertNoErrors(); // Para probar un fallo con el formateador de planificadoPara al navegar

		assertValue("trabajador.id", "2c976081901309200190130a20280001"); // Pedro
		assertValue("periodo.id", "2c976081901305ad019013078c3c0001"); // 2024.11		
		assertCollectionRowCount("incidencias", 1);
		assertValueInCollection("incidencias", 0, 0, "El primer paso de mi gran plan");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_incidencias");
		execute("CRUD.delete");
		assertNoErrors();
		
		changeModule("Incidencia");
		assertListRowCount(1);
		assertValueInList(0, 0, "El primer paso de mi gran plan");
		assertValueInList(0, 1, "Fallo");
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
	}

	
	public void testFormateadorFechaLimiteLista() throws Exception { 
		login("admin", "admin");
		
		// Obtener el primer plan
		execute("List.viewDetail", "row=0");
		
		// Calcular fechas de días laborables
		LocalDate hoy = LocalDate.now();
		LocalDate siguienteDiaLaboral = getSiguienteDiaLaboral(hoy);
		LocalDate segundoSiguienteDiaLaboral = getSiguienteDiaLaboral(siguienteDiaLaboral);
		LocalDate otraFecha = LocalDate.of(2025, 3, 15);
		
		DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		// Añadir incidencia para hoy
		execute("Collection.new", "viewObject=xava_view_incidencias");
		setValue("titulo", "Incidencia para hoy");
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo
		setValue("planificadoPara", hoy.format(formateador));
		execute("Collection.save");
		assertNoErrors();
		
		// Añadir incidencia para el siguiente día laboral (mañana)
		execute("Collection.new", "viewObject=xava_view_incidencias");
		setValue("titulo", "Incidencia para mañana");
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo
		setValue("planificadoPara", siguienteDiaLaboral.format(formateador));
		execute("Collection.save");
		assertNoErrors();
		
		// Añadir incidencia para el segundo siguiente día laboral (pasado mañana)
		execute("Collection.new", "viewObject=xava_view_incidencias");
		setValue("titulo", "Incidencia para pasado mañana");
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo
		setValue("planificadoPara", segundoSiguienteDiaLaboral.format(formateador));
		execute("Collection.save");
		assertNoErrors();
		
		// Añadir incidencia para otra fecha
		execute("Collection.new", "viewObject=xava_view_incidencias");
		setValue("titulo", "Incidencia para otra fecha");
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo
		setValue("planificadoPara", otraFecha.format(formateador));
		execute("Collection.save");
		assertNoErrors();
		
		// Verificar que la colección tiene 4 incidencias
		assertCollectionRowCount("incidencias", 4);
		
		// Obtener el HTML y verificar que las clases CSS se aplican correctamente a las fechas adecuadas
		String html = getHtml();
		
		// Formatear fechas como aparecen en el HTML (formato dd/MM/yyyy para locale español)
		DateTimeFormatter htmlFormateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String hoyFormateada = hoy.format(htmlFormateador);
		String mananaFormateada = siguienteDiaLaboral.format(htmlFormateador);
		String pasadoMananaFormateada = segundoSiguienteDiaLaboral.format(htmlFormateador);
		String otraFechaFormateada = otraFecha.format(htmlFormateador);
		
		// Verificar que la fecha de hoy tiene la clase 'deadline-today'
		assertTrue("La fecha de hoy (" + hoyFormateada + ") debe tener la clase 'deadline-today'", 
			html.contains("<span class=\"deadline-today\">" + hoyFormateada + "</span>") || 
			html.contains("<span class='deadline-today'>" + hoyFormateada + "</span>"));
		
		// Verificar que la fecha de mañana tiene la clase 'deadline-tomorrow'
		assertTrue("La fecha de mañana (" + mananaFormateada + ") debe tener la clase 'deadline-tomorrow'", 
			html.contains("<span class=\"deadline-tomorrow\">" + mananaFormateada + "</span>") || 
			html.contains("<span class='deadline-tomorrow'>" + mananaFormateada + "</span>"));
		
		// Verificar que la fecha de pasado mañana tiene la clase 'deadline-day-after-tomorrow'
		assertTrue("La fecha de pasado mañana (" + pasadoMananaFormateada + ") debe tener la clase 'deadline-day-after-tomorrow'", 
			html.contains("<span class=\"deadline-day-after-tomorrow\">" + pasadoMananaFormateada + "</span>") || 
			html.contains("<span class='deadline-day-after-tomorrow'>" + pasadoMananaFormateada + "</span>"));

		// Verificar que otra fecha no tiene ninguna clase especial (sin envoltorio span)
		assertFalse("La otra fecha (" + otraFechaFormateada + ") no debe tener ninguna clase deadline", 
			html.contains("<span class=\"deadline-today\">" + otraFechaFormateada + "</span>") ||
			html.contains("<span class=\"deadline-tomorrow\">" + otraFechaFormateada + "</span>") ||
			html.contains("<span class=\"deadline-day-after-tomorrow\">" + otraFechaFormateada + "</span>"));

		// Limpieza: eliminar todas las incidencias
		checkAllCollection("incidencias");
		execute("Collection.deleteSelected", "viewObject=xava_view_incidencias");
		
		assertCollectionRowCount("incidencias", 0);
	}
	
	private LocalDate getSiguienteDiaLaboral(LocalDate fecha) {
		LocalDate siguiente = fecha.plusDays(1);
		while (siguiente.getDayOfWeek() == DayOfWeek.SATURDAY || 
			   siguiente.getDayOfWeek() == DayOfWeek.SUNDAY) {
			siguiente = siguiente.plusDays(1);
		}
		return siguiente;
	}

}
