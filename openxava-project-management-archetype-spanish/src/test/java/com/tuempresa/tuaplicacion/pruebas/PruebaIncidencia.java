package com.tuempresa.tuaplicacion.pruebas;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class PruebaIncidencia extends ModuleTestBase {

	public PruebaIncidencia(String nameTest) {
		super(nameTest, "tuaplicacion", "Incidencia");
	}
	
	public void testCrearNuevaIncidencia() throws Exception {
		login("admin", "admin"); 
		setValue("titulo", "Incidencia JUnit");
		String [][] tipos = {
			{ "", "" },
			{ "2c94f081900875e801900896f25b0008", "Fallo" },
			{ "2c94f081900875e80190089701170009", "Característica" }
		};
		assertValidValues("tipo.id", tipos);
		setValue("tipo.id", "2c94f081900875e801900896f25b0008"); // Bug 
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
			{ "2c94f081900856030190085ec7cc0002", "2.0" },
			{ "2c94f081900856030190085eb1610001", "1.0" }
		};
		assertValidValues("version.id", versiones);
		setValue("version.id", "2c94f081900856030190085eb1610001"); // 1.0 
			
		String [][] planes = {
			{ "", "" },	
			{ "2c94f081900875e80190089c0080000a", "Javi 2024.10" },
			{ "2c94f081900875e80190089c1211000b", "Javi 2024.11" }
		};
		assertValidValues("asignadoA.id", planes);
		setValue("asignadoA.id", "2c94f081900875e80190089c1211000b"); // Javi 2024.11 
		
		String [][] estados = {
			{ "", "" },	
			{ "2c94f081900875e80190088afdc30001", "Hecho" },
			{ "2c94f081900875e80190088ce9d30002", "No reproducible" },
			{ "2c94f081900875e80190088a559a0000", "Pendiente" },
			{ "2c94f081900875e80190088d8fb90003", "Rechazada" }
		};
		
		assertValidValues("estado.id", estados);
		assertValue("estado.id", "2c94f081900875e80190088a559a0000"); // Pendiente 
		
		String [][] clientes = {
			{ "", "" },	
			{ "2c94f081900875e8019008a7349e0010", "Corporación Americana de Software" },
			{ "2c94f081900875e8019008a637e8000f", "Ministerio de industria" }			
		};
		
		assertValidValues("cliente.id", clientes);
		setValue("cliente.id", "2c94f081900875e8019008a637e8000f"); // Ministerio de industria 

		assertValue("horas", "");
		setValue("minutos", "90");
		assertValue("horas", "1.50");
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
		assertValue("tipo.id", "2c94f081900875e801900896f25b0008"); // Bug  
		assertValue("descripcion", "<p>Esto es una incidencia JUnit</p>");
		assertDescriptionValue("proyecto.id", "OpenXava"); 
		assertValue("creadoPor", "admin");
		assertValue("creadoEl", getFechaActual()); // Si falla revisa serverTimezone en la URL de MySQL
		assertValue("prioridad.nivel", "7"); 
		assertValue("version.id", "2c94f081900856030190085eb1610001"); // 1.0 
		assertValue("asignadoA.id", "2c94f081900875e80190089c1211000b"); // Javi 2024.11
		assertValue("estado.id", "2c94f081900875e80190088a559a0000"); // Pendiente  
		assertValue("cliente.id", "2c94f081900875e8019008a637e8000f"); // Ministerio de industria
		assertValue("minutos", "90");
		assertValue("horas", "1.50");		
		
		assertFile("adjuntos", 0, "text/plain");
		assertDiscussionCommentsCount("discusion", 1);
		assertDiscussionCommentContentText("discusion", 0, "Estoy de acuerdo");
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testIncidenciaMinima() throws Exception {
		login("admin", "admin"); 
		setValue("titulo", "Incidencia simple JUnit");
		String [][] types = {
			{ "", "" },
			{ "2c94f081900875e801900896f25b0008", "Fallo" },
			{ "2c94f081900875e80190089701170009", "Característica" }
		};
		assertValidValues("tipo.id", types);
		setValue("tipo.id", "2c94f081900875e801900896f25b0008"); // Fallo
		setValue("descripcion", "Esto una incidencia JUnit");
		
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		assertValueInList(0, 0, "Incidencia simple JUnit");
		execute("List.viewDetail", "row=0");
		
		assertValue("titulo", "Incidencia simple JUnit");
		assertValue("tipo.id", "2c94f081900875e801900896f25b0008"); 
		assertValue("descripcion", "<p>Esto una incidencia JUnit</p>");
		assertValue("creadoPor", "admin");
		assertValue("creadoEn", getFechaActual());

		execute("CRUD.delete");
		assertNoErrors();		
	}
	
	private String getFechaActual() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

}
