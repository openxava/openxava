package com.tuempresa.tuaplicacion.pruebas;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openqa.selenium.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.tuempresa.tuaplicacion.modelo.*;

/**
 * Prueba para el módulo Mi Calendario usando Selenium WebDriver.
 */
public class PruebaMiCalendario extends WebDriverTestBase {
	
	private int anyo = Dates.getYear(new Date());
	private int mes = Dates.getMonth(new Date());
	
	protected void setUp() throws Exception {
		super.setUp();
		XPersistence.setPersistenceUnit("junit");
	}
		
	public void testCrearIncidencia() throws Exception {
		goModule("MiCalendario");
		login("javi", "javi");

		// Probar crear una incidencia sin un plan
		assertNuevoSinPlan();		
		
		// Crear un plan para el mes actual
		crearPlanParaMes(anyo, mes);		
		
		// Probar crear una incidencia sin estado y tipo por defecto
		assertNuevoSinValoresPorDefectoParaEstadoYTipo();
		
		// Probar hacer clic en un día del calendario
		clickEnDia();
		assertNoErrors();
		assertValue("planificadaPara", mes + "/15/" + anyo);
		assertDescriptionValue("tipo.id", "Tarea");
		assertDescriptionValue("estado.id", "Planificada");
		assertDescriptionValue("asignadoA.id", "Javi " + anyo + "." + mes);	
		
		// Probar error de validación cuando falta el título
		execute("MiCalendario.save"); 
		assertError("Valor para Título en Incidencia es obligatorio");

		// Probar creación exitosa de una incidencia
		setValue("titulo", "Incidencia JUnit desde Mi calendario");
		execute("MiCalendario.save"); 
		assertNoErrors(); 
		
		// Verificar que la incidencia aparece en el calendario
		assertTextoDia(15, "Incidencia JUnit desde Mi calendario"); 
		
		// Probar crear una nueva incidencia con valores por defecto
		execute("MiCalendario.new");
		assertNoErrors();
		assertValue("planificadaPara", mes + "/" + Dates.getDay(new Date()) + "/" + anyo);
		assertDescriptionValue("tipo.id", "Tarea");
		assertDescriptionValue("estado.id", "Planificada");
		assertDescriptionValue("asignadoA.id", "Javi " + anyo + "." + mes);	
		
		// Probar visibilidad de incidencia para diferentes usuarios
		logout();
		login("pedro", "pedro");
		goModule("MiCalendario");
		assertTextoDia(15, "");
		
		// Limpiar datos de prueba
		borrarDatos("Incidencia JUnit desde Mi calendario");
	}

	private void assertNuevoSinValoresPorDefectoParaEstadoYTipo() throws Exception {
		// Encontrar y deshabilitar el estado de incidencia por defecto para Mi Calendario
		EstadoIncidencia estadoIncidencia = EstadoIncidencia.findLaDePorDefectoParaMiCalendario();
		estadoIncidencia.setUsarComoValorPorDefectoParaMiCalendario(false);
		String estadoIncidenciaId = estadoIncidencia.getId();
		
		// Encontrar y deshabilitar el tipo de incidencia por defecto para Mi Calendario
		TipoIncidencia tipoIncidencia = TipoIncidencia.findLaDePorDefectoParaMiCalendario();
		tipoIncidencia.setUsarComoValorPorDefectoParaMiCalendario(false);
		String tipoIncidenciaId = tipoIncidencia.getId();
		
		XPersistence.commit();
		
		// Hacer clic en un día y verificar que no se usan valores por defecto
		clickEnDia();
		
		assertNoErrors();
		assertValue("planificadaPara", mes + "/15/" + anyo);
		assertDescriptionValue("tipo.id", "");
		assertDescriptionValue("estado.id", "Pendiente");  
		assertDescriptionValue("asignadoA.id", "Javi " + anyo + "." + mes);
		
		// Restaurar valores por defecto
		EstadoIncidencia.findById(estadoIncidenciaId).setUsarComoValorPorDefectoParaMiCalendario(true);
		TipoIncidencia.findById(tipoIncidenciaId).setUsarComoValorPorDefectoParaMiCalendario(true);
		XPersistence.commit();
		
		execute("Mode.list");
	}

	private void assertNuevoSinPlan() throws Exception {
		// Hacer clic en un día cuando no hay plan
		clickEnDia();
		assertError("No hay plan para javi en la fecha " + fechaFormateada() + ". Cree uno y establézcalo en el campo Asignado a");
		assertValue("planificadaPara", mes + "/15/" + anyo);
		assertDescriptionValue("tipo.id", "Tarea");
		assertDescriptionValue("estado.id", "Planificada");
		assertDescriptionValue("asignadoA.id", "");
		execute("Mode.list");
	}
	
	private String fechaFormateada() {
		return String.format("%d-%02d-15", anyo, mes);
	}

	private void clickEnDia() throws Exception {
		// Encontrar y hacer clic en un elemento de día en el calendario
		WebElement elementoDia = getElementoDia();
		elementoDia.click();
		wait(getDriver());
	}
	
	private void assertTextoDia(int dia, String textoEsperado) throws Exception {
		// Verificar el texto mostrado en un día del calendario
		WebElement elementoDia = getElementoDia();
		String contenidoDiaEsperado = Is.emptyString(textoEsperado)?Integer.toString(dia): dia + "\n" + textoEsperado; 
		assertEquals(contenidoDiaEsperado, elementoDia.getText());
	}
	
	private WebElement getElementoDia() {
		// Obtener el elemento del día desde el calendario
		String fecha = fechaFormateada();
		WebElement elementoDia = getDriver().findElement(By.cssSelector("td[data-date='" + fecha + "']")); 
		return elementoDia;
	}	

	private void crearPlanParaMes(int anyo, int mes) throws Exception {
		// Crear un período para el mes
		Periodo periodo = new Periodo();
		periodo.setNombre(anyo + "." + mes);
		periodo.setFechaInicio(LocalDate.of(anyo, mes, 1));
		periodo.setFechaFin(YearMonth.now().atEndOfMonth()); 
		
		// Crear un plan para el trabajador
		Plan plan = new Plan();
		plan.setTrabajador(Trabajador.findById("2c94f081900875e80190088fd8f60004")); // Javi
		plan.setPeriodo(periodo);
		
		// Persistir las entidades
		XPersistence.getManager().persist(periodo);
		XPersistence.getManager().persist(plan);
		
		XPersistence.commit();
	}
	
	private void borrarDatos(String tituloIncidencia) {
		// Borrar la incidencia y entidades relacionadas
		Incidencia incidencia = Incidencia.findByTitulo(tituloIncidencia);
		EntityManager em = XPersistence.getManager();
		Plan plan = incidencia.getAsignadoA();
		em.remove(incidencia);
		em.remove(plan);
		em.remove(plan.getPeriodo());
		XPersistence.commit();
	}
}
