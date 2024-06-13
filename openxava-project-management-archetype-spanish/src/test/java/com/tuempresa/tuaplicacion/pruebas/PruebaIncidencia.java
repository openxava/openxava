package com.tuempresa.tuaplicacion.pruebas;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class PruebaIncidencia extends ModuleTestBase {

	public PruebaIncidencia(String nameTest) {
		super(nameTest, "proyecto1", "Incidencia");
	}
	
	public void testCrearNuevaIncidencia() throws Exception {
		login("admin", "admin"); 
		setValue("titulo", "Incidencia JUnit");
		String [][] tipos = {
			{ "", "" },
			{ "2c976081901309200190130d3e560006", "Característica" },
			{ "2c976081901309200190130d0b150005", "Fallo" }
		};
		assertValidValues("tipo.id", tipos);
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo 
		setValue("descripcion", "Esto es una incidencia JUnit");
		assertValidValuesCount("proyecto.id", 2);
		assertDescriptionValue("proyecto.id", "OpenXava"); 		
		assertValue("creadoPor", "admin");
		assertNoEditable("creadoPor");
		assertValue("creadoEl", getFechaActual());

		String [][] prioridades = {
			{ "", "" },
			{ "7", "Alta" },
			{ "5", "Normal" },
			{ "3", "Baja" }
		};
		assertValidValues("prioridad.nivel", prioridades);
		assertValue("prioridad.nivel", "5");
		setValue("prioridad.nivel", "7");

		String [][] versiones = {
			{ "", "" },
			{ "2c97608190130fc2019013108e390001", "2.0" },
			{ "2c97608190130fc2019013106fd10000", "1.0" }
		};
		assertValidValues("version.id", versiones);
		setValue("version.id", "2c97608190130fc2019013106fd10000"); // 1.0 
			
		String [][] planes = {
			{ "", "" },	
			{ "2c976081901309200190130a54570002", "Javi 2024.10" },
			{ "2c976081901309200190130a69590003", "Javi 2024.11" }
		};
		assertValidValues("asignadoA.id", planes);
		setValue("asignadoA.id", "2c976081901309200190130a69590003"); // Javi 2024.11 
		
		String [][] estados = {
			{ "", "" },	
			{ "2c97608190130fc20190131e9c820003", "Hecho" },
			{ "2c97608190130fc20190131f26ad0004", "No reproducible" },
			{ "2c97608190130fc20190131e45000002", "Pendiente" },
			{ "2c97608190130fc20190131f91480005", "Rechazado" }
		};
		assertValidValues("estado.id", estados);
		assertValue("estado.id", "2c97608190130fc20190131e45000002"); // Pendiente 
		
		String [][] clientes = {
			{ "", "" },	
			{ "4028808d9012a3ef019012a72cd40000", "Corporación Americana de Software" },
			{ "4028808d9012a3ef019012a759910001", "Ministerio de industria" }			
		};		
		assertValidValues("cliente.id", clientes);
		setValue("cliente.id", "4028808d9012a3ef019012a759910001"); // Ministerio de industria 

		assertValue("horas", "");
		setValue("minutos", "90");
		assertValue("horas", "1,50");
		assertNoEditable("horas");
		
		uploadFile("adjuntos", "test-files/notas.txt");
		postDiscussionComment("discusion", "Estoy de acuerdo");
		
		execute("CRUD.save");
		execute("Mode.list");
		
		changeModule("Version");
		assertValueInList(1, 0, "OpenXava");
		assertValueInList(1, 1, "1.0");
		execute("List.viewDetail", "row=1");
		assertDescriptionValue("proyecto.id", "OpenXava");  
		assertValue("nombre", "1.0");
		assertCollectionRowCount("incidencias", 1);
		assertValueInCollection("incidencias", 0, 0, "Incidencia JUnit");
		
		changeModule("Incidencia");
		assertListRowCount(1);
		assertValueInList(0, 0, "Incidencia JUnit");
		execute("List.viewDetail", "row=0");
		
		assertValue("titulo", "Incidencia JUnit");
		assertValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo  
		assertValue("descripcion", "<p>Esto es una incidencia JUnit</p>");
		assertDescriptionValue("proyecto.id", "OpenXava"); 
		assertValue("creadoPor", "admin");
		assertValue("creadoEl", getFechaActual()); // Si falla revisa serverTimezone en la URL de MySQL
		assertValue("prioridad.nivel", "7"); 
		assertValue("version.id", "2c97608190130fc2019013106fd10000"); // 1.0 
		assertValue("asignadoA.id", "2c976081901309200190130a69590003"); // Javi 2024.11
		assertValue("estado.id", "2c97608190130fc20190131e45000002"); // Pendiente  
		assertValue("cliente.id", "4028808d9012a3ef019012a759910001"); // Ministerio de industria
		assertValue("minutos", "90");
		assertValue("horas", "1,50");		
		
		assertFile("adjuntos", 0, "text/plain");
		assertDiscussionCommentsCount("discusion", 1);
		assertDiscussionCommentContentText("discusion", 0, "Estoy de acuerdo");
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testIncidenciaMinima() throws Exception {
		login("admin", "admin"); 
		setValue("titulo", "Incidencia simple JUnit");
		String [][] tipos = {
			{ "", "" },
			{ "2c976081901309200190130d3e560006", "Característica" },
			{ "2c976081901309200190130d0b150005", "Fallo" }
		};
		assertValidValues("tipo.id", tipos);
		setValue("tipo.id", "2c976081901309200190130d0b150005"); // Fallo
		setValue("descripcion", "Esto una incidencia JUnit");
		
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		assertValueInList(0, 0, "Incidencia simple JUnit");
		execute("List.viewDetail", "row=0");
		
		assertValue("titulo", "Incidencia simple JUnit");
		assertValue("tipo.id", "2c976081901309200190130d0b150005"); 
		assertValue("descripcion", "<p>Esto una incidencia JUnit</p>");
		assertValue("creadoPor", "admin");
		assertValue("creadoEl", getFechaActual());

		execute("CRUD.delete");
		assertNoErrors();		
	}
	
	private String getFechaActual() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

}
