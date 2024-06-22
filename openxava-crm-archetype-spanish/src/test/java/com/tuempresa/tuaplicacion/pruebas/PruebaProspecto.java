package com.tuempresa.tuaplicacion.pruebas;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class PruebaProspecto extends ModuleTestBase {

	public PruebaProspecto(String nameTest) {
		super(nameTest, "tuaplicacion", "Prospecto");
	}
	
	public void testCrearNuevoProspecto() throws Exception {
		login("admin", "admin"); 
		setValue("nombre", "Prospecto JUnit");
		String [][] estados = {
			{ "", "" },	
			{ "2c9c10818fcaf2b7018fcaf34d4d0000", "A Confirmado" },
			{ "2c9c10818fcaf2b7018fcaf366470001", "B Pendiente" },
			{ "2c9c10818fcaf2b7018fcaf386760002", "X Descartado" },
			{ "2c9c10818fcaf2b7018fcaf39ec50003", "Z Hecho" }
		};
		
		assertValidValues("estado.id", estados);
		setValue("estado.id", "2c9c10818fcaf2b7018fcaf39ec50003"); // Hecho 
		setValue("correoElectronico", "antonio.rodolfo.valentino.smith@thelargestcompanyinworld.com");
		setValue("descripcion", "Esto es un Prospecto JUnit");
		
		execute("Sections.change", "activeSection=1");
		setValue("observaciones", "Esto es un comentario");
		
		execute("Sections.change", "activeSection=2");
		setValueInCollection("actividades", 0, "descripcion", "La primera actividad con Prospecto JUnit");
		
		execute("Sections.change", "activeSection=3");
		uploadFile("adjuntos", "test-files/notas.txt");
		
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		
		assertListRowCount(1);
		assertValueInList(0, 0, "Prospecto JUnit");
		assertValueInList(0, 4, "Terminado");
		execute("List.viewDetail", "row=0");
		
		assertValue("nombre", "Prospecto JUnit");
		assertValue("estado.id", "2c9c10818fcaf2b7018fcaf39ec50003"); // Hecho 
		assertValue("correoElectronico", "antonio.rodolfo.valentino.smith@thelargestcompanyinworld.com");
		assertValue("ultimoToque", getFechaActual()); // Si falla cambia GMT+x en serverTimezone de la URL de MySQL
		execute("Sections.change", "activeSection=0");
		assertValue("descripcion", "<p>Esto es un Prospecto JUnit</p>");
		execute("Sections.change", "activeSection=1");
		assertValue("observaciones", "<p>Esto es un comentario</p>");
		execute("Sections.change", "activeSection=2");
		assertCollectionColumnCount("actividades", 1);
		assertValueInCollection("actividades", 0, "descripcion", "La primera actividad con Prospecto JUnit");
		assertValueInCollection("actividades", 0, "fecha", getFechaActual());
		execute("Sections.change", "activeSection=3");
		assertFile("adjuntos", 0, "text/plain");
				
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private String getFechaActual() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
	}

}