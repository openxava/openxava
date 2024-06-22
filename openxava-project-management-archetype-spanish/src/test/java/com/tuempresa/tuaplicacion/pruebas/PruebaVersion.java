package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.jpa.*;
import org.openxava.tests.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class PruebaVersion extends ModuleTestBase {

	public PruebaVersion(String nameTest) {
		super(nameTest, "tuaplicacion", "Version");
	}
	
	public void testVersionesDependeDeProyectoEnIncidencia_valorPorDefectoParaProyectoAlCrearIncidenciaDesdeVersion() throws Exception {
		login("admin", "admin");
		execute("CRUD.new");
		execute("Reference.createNew", "model=Proyecto,keyProperty=proyecto.id");
		setValue("nombre", "XavaProjects");
		execute("NewCreation.saveNew");
		setValue("nombre", "2024.12");
		execute("CRUD.save");

		Proyecto nuevoProyecto = Proyecto.findByNombre("XavaProjects"); 
		setValue("proyecto.id", nuevoProyecto.getId());
		setValue("nombre", "2025.01");

		assertNoAction("Collection.new");
		execute("IncidenciasVersion.new", "viewObject=xava_view_incidencias");
		assertNoEditable("proyecto");
		assertDescriptionValue("proyecto.id", "XavaProjects");
		closeDialog();

		execute("CRUD.save");
		
		XPersistence.commit();
		Version nuevaVersion1 = Version.findByNombre("2024.12").get(0);
		Version nuevaVersion2 = Version.findByNombre("2025.01").get(0);

		changeModule("Incidencia");
		execute("CRUD.new");
		assertValue("proyecto.id", "");
		
		Proyecto viejoProyecto = Proyecto.findByNombre("OpenXava");
		String [][] projects = {
			{"", ""},
			{viejoProyecto.getId(), viejoProyecto.getNombre() },
			{nuevoProyecto.getId(), nuevoProyecto.getNombre() }
		};
		
		String [][] empty = {
			{"", ""}
		};
		
		assertValidValues("proyecto.id", projects);
		assertValidValues("version.id", empty);
		
		setValue("proyecto.id", viejoProyecto.getId());
		String [][] viejasVersionesProyectos = {
			{ "", "" },
			{ "2c97608190130fc2019013108e390001", "2.0" },
			{ "2c97608190130fc2019013106fd10000", "1.0" }
		};
		assertValidValues("version.id", viejasVersionesProyectos);
		
		setValue("proyecto.id", nuevoProyecto.getId());
		String [][] nuevaVersionesProyectos = {
			{ "", "" },
			{ nuevaVersion2.getId(), nuevaVersion2.getNombre() },
			{ nuevaVersion1.getId(), nuevaVersion1.getNombre() }
			
		};
		assertValidValues("version.id", nuevaVersionesProyectos);
		
		changeModule("Version");
		execute("Mode.list");
		assertListRowCount(4);
		setConditionValues(nuevoProyecto.getNombre()); 
		assertListRowCount(2);
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
		
		nuevoProyecto = XPersistence.getManager(). merge(nuevoProyecto);
		XPersistence.getManager().remove(nuevoProyecto);
	}	

}
