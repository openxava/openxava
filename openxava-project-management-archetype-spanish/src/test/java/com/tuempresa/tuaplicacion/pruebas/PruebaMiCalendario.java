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
		login("javi", "javi999");

		// Probar crear una incidencia sin un plan
		assertNuevoSinPlan();		
		
		// Crear un plan para el mes actual
		crearPlanParaMes(anyo, mes);		
		
		// Probar crear una incidencia sin estado y tipo por defecto
		assertNuevoSinValoresPorDefectoParaEstadoYTipo();
		
		// Probar hacer clic en un dí­a del calendario
		clickEnDia();
		assertNoErrors();
		assertValue("planificadoPara", String.format("15/%02d/%d", mes, anyo));
		assertDescriptionValue("tipo.id", "Tarea");
		assertDescriptionValue("estado.id", "Planificado");
		assertDescriptionValue("asignadoA.id", "Javi " + anyo + "." + mes);	
		
		// Probar error de validación cuando falta el título
		execute("MiCalendario.save"); 
		assertError("Es obligado que Titulo en Incidencia tenga valor");

		// Probar creación exitosa de una incidencia
		setValue("titulo", "Incidencia JUnit desde Mi calendario");
		execute("MiCalendario.save"); 
		assertNoErrors(); 
		
		// Verificar que la incidencia aparece en el calendario
		assertTextoDia(15, "Incidencia JUnit desde Mi calendario"); 
		
		// Probar crear una nueva incidencia con valores por defecto
		execute("MiCalendario.new");
		assertNoErrors();
		assertValue("planificadoPara", String.format("%02d/%02d/%d", Dates.getDay(new Date()), mes, anyo));
		assertDescriptionValue("tipo.id", "Tarea");
		assertDescriptionValue("estado.id", "Planificado");
		assertDescriptionValue("asignadoA.id", "Javi " + anyo + "." + mes);	
		
		// Probar visibilidad de incidencia para diferentes usuarios
		logout();
		login("pedro", "pedro888");
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
		
		// Hacer clic en un dí­a y verificar que no se usan valores por defecto
		clickEnDia();
		
		assertNoErrors();
		assertValue("planificadoPara", String.format("15/%02d/%d", mes, anyo));
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
		// Hacer clic en un dí­a cuando no hay plan
		clickEnDia();
		assertError("No hay ningún plan para javi en la fecha " + fechaFormateada() + ". Crea uno y asígnalo en el campo Asignado a");
		assertValue("planificadoPara", String.format("15/%02d/%d", mes, anyo));
		assertDescriptionValue("tipo.id", "Tarea");
		assertDescriptionValue("estado.id", "Planificado");
		assertDescriptionValue("asignadoA.id", "");
		execute("Mode.list");
	}
	
	private String fechaFormateada() {
		return String.format("%d-%02d-15", anyo, mes);
	}

	private void clickEnDia() throws Exception {
		// Encontrar y hacer clic en un elemento de dÃ­a en el calendario
		WebElement elementoDia = getElementoDia();
		elementoDia.click();
		wait(getDriver());
	}
	
	private void assertTextoDia(int dia, String textoEsperado) throws Exception {
		// Verificar el texto mostrado en un dí­a del calendario
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
		// Crear un periodo para el mes
		Periodo periodo = new Periodo();
		periodo.setNombre(anyo + "." + mes);
		periodo.setFechaInicio(LocalDate.of(anyo, mes, 1));
		periodo.setFechaFin(YearMonth.now().atEndOfMonth()); 
		
		// Crear un plan para el trabajador
		Plan plan = new Plan();
		plan.setTrabajador(Trabajador.findById("2c976081901309200190130a11270000")); // Javi
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
